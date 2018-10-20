(ns woolybear.ad.forms
  "
  Form fields are moderately-complex components in that they have a lot of user interaction
  and need to support validation. Much of the basic functionality of form fields is provided
  by the browser (fortunately), so we don't need to build as much custom code as we might,
  but we still want to expand our components a bit in order to make our components more
  flexible and useful.
  "
  (:require [re-frame.core :as re-frame]
            [reagent.ratom :as ratom]
            [cljs.spec.alpha :as s]
            [woolybear.ad.utils :as adu]
            [woolybear.ad.buttons :as buttons]
            [woolybear.ad.icons :as icons]
            [woolybear.ad.layout :as layout]))

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
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes required? for]} _] (adu/extract-opts args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-opts args)
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

;;;
;;; Built-in handlers for input field events
;;;

;; Field initialization

(defn handle-form-field-init
  "Handler function for :form-field/init event. The event should be dispatched with
  two additional arguments: the path to the component, and the default value, if any."
  [db [_ path & [default]]]
  (assoc-in db path (mk-field-data path default)))

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
  (update-in db path assoc :active? false))

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
                                       :input/placeholder
                                       :input/subscribe-to-errors
                                       :input/on-change
                                       :input/on-enter-key
                                       :input/on-escape-key
                                       :input/autofocus?
                                       :ad/extra-classes
                                       :ad/subscribe-to-classes]))

(defn- get-attribs
  "Utility function just to keep text-input from getting too big. Builds the component
  attributes map given the options."
  [opts]
  (let [{:keys [component-data-path id name type placeholder on-change on-enter-key
                on-escape-key autofocus?]} opts
        type (or type :text)  ;; set default value for optional :text key
        path component-data-path ;; alias for convenience
        on-key-dispatchers (cond-> {}
                                   on-enter-key (assoc "Enter"
                                                       (adu/append-to-dispatcher on-enter-key path))
                                   on-escape-key (assoc "Escape"
                                                        (adu/append-to-dispatcher on-escape-key path)))
        on-key-down-dispatcher (when (seq on-key-dispatchers)
                                 (adu/mk-keydown-dispatcher on-key-dispatchers))
        change-dispatcher* (when on-change
                             (adu/mk-dispatcher (adu/append-to-dispatcher on-change path)))
        change-dispatcher (if change-dispatcher*
                            (fn [e]
                              (change-dispatcher* e)
                              (re-frame/dispatch [:form-field/change path (adu/js-event-val e)]))
                            ;; else
                            (fn [e]
                              (re-frame/dispatch [:form-field/change path (adu/js-event-val e)])))]
    (cond-> {:type      type
             :on-change change-dispatcher}
            id (assoc :id id)
            name (assoc :name name)
            placeholder (assoc :placeholder placeholder)
            autofocus? (assoc :autofocus autofocus?)
            on-key-down-dispatcher (assoc :on-key-down on-key-down-dispatcher))))

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
    * :placeholder A placeholder string to display in the field when empty
    * :subscribe-to-errors A validation subscription returning any error messages to display below the field.
    * :on-change An event dispatcher to dispatch when the user changes the value of the input
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
  (let [{:keys [subscribe-to-component-data component-data-path default
                subscribe-to-errors extra-classes subscribe-to-classes]} opts
        path component-data-path ;; alias for convenience
        component-data-sub (adu/subscribe-to subscribe-to-component-data)
        errors-sub (adu/subscribe-to subscribe-to-errors)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        attribs (get-attribs opts)]
    ;; dispatch init event at component definition time
    (re-frame/dispatch [:form-field/init path default])
    (fn [_]
      (let [{:keys [value]} @component-data-sub
            dynamic-classes @classes-sub
            errors @errors-sub
            attribs (assoc attribs :class (adu/css->str :input :wb-text-input
                                                        extra-classes
                                                        dynamic-classes)
                                   :value value)]
        [:div
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
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-opts args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-opts args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :wb-field-group
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef field-group
  :args (s/cat :opts :label/options
               :children (s/+ any?))
  :ret vector?)
