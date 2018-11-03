(ns woolybear.ad.forms
  "
  Form fields are moderately-complex components in that they have a lot of user interaction
  and need to support validation. Much of the basic functionality of form fields is provided
  by the browser (fortunately), so we don't need to build as much custom code as we might,
  but we still want to expand our components a bit in order to make our components more
  flexible and useful.
  "
  (:require [re-frame.core :as re-frame]
            [cljs.spec.alpha :as s]
            [woolybear.ad.utils :as adu]))

;;;
;;; Labels
;;;

(defn required-mark
  "
  Simple red asterisk for adding to form field labels to mark the corresponding form
  field as required. Takes no options.
  "
  []
  [:span.is-required-mark "*"])

(s/def :label/required? boolean?)
(s/def :label/for string?)
(s/def :label/options (s/keys :opt-un [:ad/extra-classes
                                       :ad/subscribe-to-classes
                                       :label/required?]))

(defn label
  "
  Simple label component for use with form fields. Supports the standard :extra-classes and
  :subscribe-to-classes options, plus an optional :required? option that, if present and
  truthy, will add a red asterisk to the label. You can also pass in a :for option containing
  the DOM ID of the element that the label describes.
  "
  [& args]   ;; opts & children
  (let [[opts _] (adu/extract-args args)
        {:keys [extra-classes subscribe-to-classes
                required? for]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub
            attribs (cond-> {:class (adu/css->str :label :wb-label
                                                  extra-classes
                                                  dynamic-classes)}
                            for (assoc :for for))]
        (cond->
          (into [:label attribs]
                children)
          required? (conj [required-mark]))))))

(s/fdef label
  :args (s/cat :opts :label/options
               :children (s/+ any?))
  :ret vector?)

(defn mk-field-data
  "
  Generates the internal fields used by self-aware form field components. Takes a vector
  of keywords to be used by `assoc-in` or `update-in` to update the value in the app-db.
  The optional second value is the default value to set the field value to. Returns a map
  of appropriate values to be added to the app-db in the appropriate location.
  "
  ([path] (mk-field-data path nil))
  ([path default]
   {:value          default
    :path           path
    :visited?       false
    :active?        false
    :original-value default
    }))

(s/fdef mk-field-data
  :args (s/cat :path :ad/component-path)
  :ret vector?)

(defn mk-dispatchers
  "Given a component-data-path and a map with the keys :on-enter-key, :on-escape-key, and :on-change,
  return a map with each of those keys mapped to a dispatcher function suitable for use in text-input,
  select-input, and so on. Note that this us used for select-input as well as text-input, so we need
  to check for the :multiple? option in the on-change handler."
  ([component-data-path opts] (mk-dispatchers component-data-path opts :form-field/change))
  ([component-data-path {:keys [on-enter-key on-escape-key on-change]} dispatch-key]
   (let [path component-data-path ;; alias for convenience
         on-key-dispatchers (cond-> {}
                                    on-enter-key (assoc "Enter"
                                                        (adu/append-to-dispatcher on-enter-key path))
                                    on-escape-key (assoc "Escape"
                                                         (adu/append-to-dispatcher on-escape-key path)))
         on-key-down-dispatcher (when (seq on-key-dispatchers)
                                  (adu/mk-keydown-dispatcher on-key-dispatchers))
         blur-dispatcher (adu/mk-dispatcher [:form-field/blur component-data-path])
         change-dispatcher* (when on-change
                              (adu/mk-dispatcher (adu/append-to-dispatcher on-change component-data-path)))
         change-dispatcher (if change-dispatcher*
                             (fn [e]
                               (re-frame/dispatch [dispatch-key component-data-path (adu/js-event-val e)])
                               (change-dispatcher* e))
                             ;; else
                             (fn [e]
                               (re-frame/dispatch [dispatch-key component-data-path (adu/js-event-val e)])))]
     (cond-> {:on-change change-dispatcher
              :on-blur blur-dispatcher}
             on-key-down-dispatcher (assoc :on-key-down on-key-down-dispatcher)))))

(s/fdef mk-dispatchers
  :args (s/cat :path vector?
               :dispatchers (s/keys :opt-un [:input/on-enter-key
                                             :input/on-escape-key
                                             :input/on-change])
               :dispatch-key (s/? keyword?))
  :ret map?)

;;;
;;; Built-in handlers for input field events
;;;

;; Field initialization

(defn handle-form-field-init
  "Handler function for :form-field/init event. The event should be dispatched with
  two additional arguments: the path to the component, and the default value, if any."
  [db [_ path & [default]]]
  (assoc-in
    db path
    (mk-field-data path default)))

(re-frame/reg-event-db
  :form-field/init
  handle-form-field-init)

;; Field focus

(defn handle-form-field-focus
  "Handler function for :form-field/focus event. Updates the :visited? and :active?
  keys in the field data map. Takes the path to the component to update."
  [db [_ path]]
  (update-in db path assoc :visited? true :active? true))

(re-frame/reg-event-db
  :form-field/focus
  handle-form-field-focus)

;; Field blur

(defn handle-form-field-blur
  "Handler function for the :form-field/blur event. Updates the :active? key in the
  field data map. Takes the path to the component to update."
  [db [_ path]]
  (update-in db path
             assoc :active? false :visited? true))

(re-frame/reg-event-db
  :form-field/blur
  handle-form-field-blur)

;; Field change

(defn handle-form-field-change
  "Handler function for the :form-field/change event. Updates the :value key in the
  field data map. Takes the path to the component to update, and the new value for
  the field."
  [db [_ path new-val]]
  (update-in db path assoc :value new-val))

(re-frame/reg-event-db
  :form-field/change
  handle-form-field-change)

(defn handle-multi-select-change
  "Handler function for the :multi-select/change event. Updates the :value key in
  the field data map, allowing for multiple selections. Takes the path to the component
  to update, and the new value to toggle in or out of the set of selected options."
  [db [_ path change-val]]
  (if (seq change-val)
    (let [current-selection (into #{} (:value (get-in db path)))
          new-selection (if (current-selection change-val)
                          (disj current-selection change-val)
                          (conj current-selection change-val))]
      (update-in db path assoc :value (into [] new-selection)))
    db))

(re-frame/reg-event-db
  :multi-select/change
  handle-multi-select-change)

(defn handle-checkbox-change
  "Handler function for the :checkbox/change event. Toggles the boolean value
  of the :value key in the component data."
  [db [_ path]]
  (let [value-path (conj path :value)]
    (update-in db value-path not)))

(re-frame/reg-event-db
  :checkbox/change
  handle-checkbox-change)

;;;
;;; Form field components
;;;

(s/def :error/msg string?)
(s/def :error/class #{:error :warning :info})

(s/def :form-field/error (s/keys :req-un [:error/msg]
                                 :opt-un [:error/class]))

(defn errors-list
  "
  Given a list of validation errors, in the form of maps with :msg and :class keys,
  display a suitably-formatted list of error messages.
  "
  [errors]
  (when (seq errors)
    (into [:div.wb-errors]
          (mapv (fn [err]
                  (let [classes (adu/css->str :wb-error (:class err))]
                    [:div {:class classes} (:msg err)]))
                errors))))

(s/fdef errors-list
  :args (s/cat :errors (s/coll-of :form-field/error))
  :ret vector?)

(s/def :input/id string?)
(s/def :input/name string?)
(s/def :input/type #{:text :password})
(s/def :input/default any?)
(s/def :input/placeholder string?)
(s/def :input/subscribe-to-disabled? :ad/subscription)
(s/def :input/subscribe-to-errors :ad/subscription)
(s/def :input/on-change :ad/event-dispatcher)
(s/def :input/on-enter-key :ad/event-dispatcher)
(s/def :input/on-escape-key :ad/event-dispatcher)
(s/def :input/autofocus? boolean?)
(s/def :input/component-data-path (s/coll-of keyword? :kind vector?))

(s/def :input/options (s/keys :req-un [:ad/subscribe-to-component-data
                                       :input/component-data-path]
                              :opt-un [:input/id
                                       :input/name
                                       :input/type
                                       :input/default
                                       :input/subscribe-to-disabled?
                                       :input/placeholder
                                       :input/subscribe-to-errors
                                       :input/on-change
                                       :input/on-enter-key
                                       :input/on-escape-key
                                       :input/autofocus?
                                       :ad/extra-classes
                                       :ad/subscribe-to-classes]))

(defn- get-text-input-attribs
  "Utility function just to keep text-input from getting too big. Builds the component
  attributes map given the options."
  [opts]
  (let [{:keys [component-data-path id name type placeholder autofocus?]} opts
        type (or type :text)  ;; set default value for optional :text key
        dispatchers (mk-dispatchers component-data-path opts)]
    (cond-> (merge dispatchers {:type type})
            id (assoc :id id)
            name (assoc :name name)
            placeholder (assoc :placeholder placeholder)
            autofocus? (assoc :autofocus autofocus?))))

(defn text-input
  "
  Text-input field component. Includes built-in functionality for updating its
  its field data in response to user interaction. Takes a single `opts` argument,
  which should be a map of option keys and values. Accepts the standard extra-classes
  and subscribe-to-classes options, as well as the following options:

    * :id  A string to be used as the DOM ID for this field
    * :name The `name` attribute for this field
    * :type Either :text or :password, default :text
    * :default Default value for the field.
    * :subscribe-to-disabled? Subscription to a boolean value that returns true if the field should be disabled.
    * :placeholder A placeholder string to display in the field when empty
    * :subscribe-to-errors A validation subscription returning any error messages to display below the field.
    * :on-change An event dispatcher to dispatch when the user changes the value of the input
                 Note that the text-input automatically updates app-db when the value changes; the on-change
                 dispatcher here is for any *additional* events you wish to fire when the value changes.
    * :on-enter-key An event dispatcher to dispatch when the user hits Enter in this field
    * :on-escape-key An event dispatcher to dispatch when the user hits Escape in this field
    * :autofocus? If present, and true, sets the `autofocus` attribute on this field
    * :component-data-path Path to field data in app-db

  Text inputs have two required options: :component-data-path and :subscribe-to-component-data.
  The :component-data-path must be a vector of keywords, suitable for use with assoc-in and update-in,
  defining where the component data for this field lives in the app-db. The subscribe-to-component-data
  option must be a subscription returning this data.
  "
  [opts]
  (let [{:keys [subscribe-to-component-data
                component-data-path
                default subscribe-to-disabled?
                subscribe-to-errors extra-classes
                subscribe-to-classes]} opts
        component-data-sub (adu/subscribe-to
                             subscribe-to-component-data)
        disabled?-sub (adu/subscribe-to subscribe-to-disabled?)
        errors-sub (adu/subscribe-to subscribe-to-errors)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        attribs (get-text-input-attribs opts)]
    ;; dispatch init event at component definition time
    (re-frame/dispatch [:form-field/init
                        component-data-path default])
    (fn [_]
      (let [{:keys [value]} @component-data-sub
            dynamic-classes @classes-sub
            disabled? @disabled?-sub
            errors @errors-sub
            attribs (cond->
                      (assoc attribs :class (adu/css->str :input :wb-text-input
                                                          extra-classes
                                                          dynamic-classes)
                                     :value value)
                      disabled? (assoc :disabled "disabled"))]
        [:div.control
         [:input attribs]
         [errors-list errors]]))))

(s/fdef text-input
  :args (s/cat :opts :input/options)
  :ret vector?)

(defn field-group
  "
  A component to group together an input field and a label. Accepts standard :extra-classes and
  :subscribe-to-classes options.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :field :wb-field-group
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef field-group
  :args (s/cat :opts :label/options
               :children (s/+ any?))
  :ret vector?)

(s/def :select/subscribe-to-option-items :ad/subscription)
(s/def :select/none-value string?)
(s/def :select/multiple? boolean?)
(s/def :select/size int?)
(s/def :select/get-label-fn fn?)
(s/def :select/get-value-fn fn?)

(s/def :select/options (s/keys :req-un [:select/subscribe-to-option-items
                                        :ad/subscribe-to-component-data
                                        :input/component-data-path]
                               :opt-un [:input/id
                                        :input/name
                                        :input/default
                                        :input/autofocus?
                                        :input/subscribe-to-disabled?
                                        :input/subscribe-to-errors
                                        :input/on-change
                                        :input/on-enter-key
                                        :input/on-escape-key
                                        :select/none-value
                                        :select/multiple?
                                        :select/size
                                        :select/get-label-fn
                                        :select/get-value-fn
                                        :ad/extra-classes
                                        :ad/subscribe-to-classes]))

(defn- get-select-input-attribs
  [opts]
  (let [{:keys [component-data-path id name autofocus? multiple? size]} opts
        dispatchers (if multiple?
                      (mk-dispatchers component-data-path opts :multi-select/change)
                      (mk-dispatchers component-data-path opts))]
    (cond-> dispatchers
            id (assoc :id id)
            name (assoc :name name)
            autofocus? (assoc :autofocus autofocus?)
            multiple? (assoc :multiple true)
            size (assoc :size size))))

(defn select-input
  "
  A drop-down/select-menu component for use in forms. Options to display in the select input are
  passed in via the :subscribe-to-option-items key in the opts argument. If you wish to specify a
  \"none\" selection to display at the top of the select list, pass it in via the :none-value key.
  As a self-referential input component, select input requires the component-data-path option and
  the subscribe-to-component-data subscription, as with the text-input component. The value of the
  :component-data-path must be a vector of keywords, suitable for use with assoc-in and update-in,
  defining where the component data for this field lives in the app-db. The subscribe-to-component-data
  option must be a subscription returning this data.

  The select-input component supports the standard :extra-classes and :subscribe-to-classes options,
  as well as the following:

    * :id The DOM ID for this element (e.g. for use with [label] components)
    * :name The form field name attribute for this component
    * :default A default value for the select
    * :autofocus? If present and true, sets the \"autofocus\" attribute
    * :subscribe-to-disabled? Subscription returning true if this component should be disabled
    * :multiple? If present and true, enables multiple selections (e.g. checklist)
    * :size If present, sets the \"size\" attribute on the select element
    * :on-change An event dispatcher to dispatch when the user changes the value of the input
                 Note that select-input automatically updates app-db when the value changes; the
                 on-change handler is for any *additional* events you wish to fire when the value changes.
    * :on-enter-key An event dispatcher to dispatch when the user hits Enter in this field
    * :on-escape-key An event dispatcher to dispatch when the user hits Escape in this field
    * :get-label-fn (See below)
    * :get-value-fn (See below)

  The option-items list returned by :subscribe-to-option-items can be in any format as long as you
  provide values for the keys :get-label-fn and/or get-value-fn. For each item in the option-items
  list, a label will be generated by calling the get-label-fn with the option item as an argument.
  Default action is to use the value of the :label key in the option item if there is one, or to
  use the entire option item, in string form. The default value is to return the entire option item.
  "
  [opts]
  (let [{:keys [extra-classes subscribe-to-classes subscribe-to-option-items subscribe-to-disabled?
                component-data-path subscribe-to-component-data default none-value multiple?
                subscribe-to-errors get-label-fn get-value-fn]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)
        option-items-sub (adu/subscribe-to subscribe-to-option-items)
        disabled?-sub (adu/subscribe-to subscribe-to-disabled?)
        component-data-sub (adu/subscribe-to subscribe-to-component-data)
        errors-sub (adu/subscribe-to subscribe-to-errors)
        default (or default (if multiple? [] ""))
        get-label-fn (if (fn? get-label-fn)
                       get-label-fn
                       (fn [item]
                         (or (:label item) (str item))))
        get-value-fn (if (fn? get-value-fn)
                       get-value-fn
                       str)
        attribs (get-select-input-attribs opts)]

    ;; dispatch init event at component definition time
    (re-frame/dispatch [:form-field/init component-data-path default])

    (fn [_]
      (let [{:keys [value]} @component-data-sub
            value (or value (if multiple? [] ""))
            dynamic-classes @classes-sub
            option-items @option-items-sub
            disabled? @disabled?-sub
            errors @errors-sub
            children (if none-value [[:option {:value ""} none-value]]
                                    [])
            children (into children (mapv (fn [item]
                                            [:option {:value (get-value-fn item)} (get-label-fn item)])
                                          option-items))
            attribs (cond-> (assoc attribs :class (adu/css->str :input :select :wb-select
                                                                (when multiple? :is-multiple)
                                                                extra-classes
                                                                dynamic-classes)
                                           :value value)
                            disabled? (assoc :disabled "disabled"))]
        [:div.control
         [:div.select {:class (adu/css->str (when multiple? :is-multiple))}
          (into [:select attribs] children)]
         [errors-list errors]]))))

(s/fdef select-input
  :args (s/cat :opts :select/options)
  :ret vector?)

(s/def :checkbox/options (s/keys :req-un [:input/component-data-path
                                          :ad/subscribe-to-component-data]
                                 :opt-un [:input/default
                                          :input/id
                                          :input/name
                                          :input/on-change
                                          :input/subscribe-to-disabled?
                                          :ad/extra-classes
                                          :ad/subscribe-to-classes]))

(defn checkbox
  "
  Standard checkbox component, takes standard options :component-data-path, :subscribe-to-component-data,
  :default, :id, :name, :on-change, :subscribe-to-disabled? :extra-classes, and :subscribe-to-classes.
  "
  [opts & children]
  (let [{:keys [component-data-path default subscribe-to-component-data id name
                subscribe-to-disabled? extra-classes subscribe-to-classes]} opts
        component-data-sub (adu/subscribe-to subscribe-to-component-data)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        disabled?-sub (adu/subscribe-to subscribe-to-disabled?)
        dispatchers (mk-dispatchers component-data-path opts :checkbox/change)
        attribs (cond-> dispatchers
                        id (assoc :id id)
                        name (assoc :name name))]
    (re-frame/dispatch [:form-field/init component-data-path default])
    (fn [_]
      (let [{:keys [value]} @component-data-sub
            disabled? @disabled?-sub
            dynamic-classes @classes-sub
            attribs (cond-> (assoc attribs :type "checkbox"
                                           :value value
                                           :checked value)
                            disabled? (assoc :disabled "disabled"))]
        [:span.control
         (into [:label {:class (adu/css->str :checkbox :wb-checkbox
                                             (when disabled? :disabled)
                                             extra-classes
                                             dynamic-classes)}
                [:input attribs]]
               children)]))))

(s/fdef checkbox
  :args (s/cat :opts :checkbox/options)
  :ret vector?)
