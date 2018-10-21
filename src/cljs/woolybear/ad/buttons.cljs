(ns woolybear.ad.buttons
  "
  Button components support simple, configurable interactions and styles. Use
  button components instead of rolling your own [:button] elements in order to
  promote a consistent UX, and to promote a standard API for things like a
  dynamic 'disabled' attribute.
  "
  (:require [reagent.ratom :as ratom]
            [cljs.spec.alpha :as s]
            [woolybear.ad.utils :as adu]))

(s/def :button/subscribe-to-disabled? :ad/subscription)
(s/def :button/on-click :ad/event-dispatcher)

(s/def :button/options (s/keys :req-un [:button/on-click]
                               :opt-un [:button/subscribe-to-disabled?
                                        :ad/extra-classes
                                        :ad/subscribe-to-classes]))

(defn button
  "
  A simple button component that fires a specified event when clicked.
  Note that the opts argument is mandatory, since you must specify an
  :on-click dispatcher. You can also pass a :subscribe-to-disabled? option
  to dynamically enable/disable the button at runtime, and either (or
  both) of the :extra-classes and :subscribe-to-classes options to set
  additional CSS classes on the button.
  "
  [opts & _]
  (let [{:keys [on-click subscribe-to-disabled?
                extra-classes subscribe-to-classes]} opts
        disabled?-sub (adu/subscribe-to subscribe-to-disabled?)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        click-dispatcher (adu/mk-dispatcher on-click)]
    (fn [_ & children]
      (let [disabled? @disabled?-sub
            dynamic-classes @classes-sub
            attrs (cond-> {:on-click click-dispatcher
                           :class    (adu/css->str :button :wb-button extra-classes dynamic-classes)}
                          disabled? (assoc :disabled "disabled"))]
        (into [:button attrs] children)))))

(s/fdef button
  :args (s/cat :opt :button/options
               :children (s/+ any?))
  :ret vector?)

(defn ok-button
  [opts & children]
  (let [extra-classes (adu/css+css (:extra-classes opts) #{:wb-ok-button :is-primary})
        label (or children ["OK"])]
    (into [button (assoc opts :extra-classes extra-classes)] label)))

(s/fdef ok-button
  :args (s/cat :opt :button/options
               :children (s/* any?))
  :ret vector?)

(defn save-button
  [opts & children]
  (let [extra-classes (adu/css+css (:extra-classes opts) #{:wb-save-button :is-primary})
        label (or children ["Save"])]
    (into [button (assoc opts :extra-classes extra-classes)] label)))

(s/fdef save-button
  :args (s/cat :opt :button/options
               :children (s/* any?))
  :ret vector?)

(defn delete-button
  [opts & children]
  (let [extra-classes (adu/css+css (:extra-classes opts) #{:wb-delete-button :is-danger})
        label (or children ["Delete"])]
    (into [button (assoc opts :extra-classes extra-classes)] label)))

(s/fdef delete-button
  :args (s/cat :opt :button/options
               :children (s/* any?))
  :ret vector?)

(defn cancel-button
  [opts & children]
  (let [extra-classes (:extra-classes opts)
        extra-classes (adu/css+css extra-classes :wb-cancel-button)
        label (or children ["Cancel"])
        opts (assoc opts :extra-classes extra-classes)]
    (into [button opts] label)))

(s/fdef cancel-button
  :args (s/cat :opt :button/options
               :children (s/* any?))
  :ret vector?)

(defn close-button
  "Button with an X in it, for use as a close button"
  [opts]
  (let [extra-classes (:extra-classes opts)
        extra-classes (adu/css+css extra-classes :wb-close-button)]
    [button (assoc opts :extra-classes extra-classes) "✖︎"]))

(s/fdef close-button
  :args (s/cat :opts :button/button-config)
  :ret vector?)


(s/def :tab-button/active? boolean?)
(s/def :tab-button/panel-id keyword?)
(s/def :tab-button/options (s/keys :req-un [:tab-button/panel-id
                                            :tab-button/active?]
                                   :opt-un [:button/on-click
                                            :button/subscribe-to-disabled?
                                            :ad/extra-classes
                                            :ad/subscribe-to-classes]))

(defn tab-button
  "
  A button intended for use as part of a tab bar or tab-panel. Takes all the same
  options as a regular button, plus a :panel-id keyword and an :active? option. Notice
  that :active? is a direct boolean value rather than a subscription. The assumption
  here is that each tab button will be part of a group of buttons managed by a
  parent container, and the parent container will take care of managing which tab
  button is active, using the :panel-id value to track which button is active.
  "
  ;; We're re-implementing most of the functionality of button here because it makes
  ;; it simpler to work with the additional "tab-button" and "active" classes we
  ;; want to add when rendering the button. Also we need to manage the render-time
  ;; destructuring of the :active? flag, which would be difficult to do if we were
  ;; trying to make tab-button a mere wrapper around a call to button.
  [opts & _]
  (let [{:keys [on-click subscribe-to-disabled?
                extra-classes subscribe-to-classes]} opts
        disabled?-sub (adu/subscribe-to subscribe-to-disabled?)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        click-dispatcher (adu/mk-dispatcher on-click)]
    ;; we want the render-time value of the :active? opt, so we destructure it here.
    (fn [{:keys [active?]} & children]
      (let [disabled? @disabled?-sub
            dynamic-classes @classes-sub
            attrs (cond-> {:on-click click-dispatcher
                           :class    (adu/css->str :wb-button :wb-tab-button
                                                   (when active? :active)
                                                   :button
                                                   extra-classes dynamic-classes)}
                          disabled? (assoc :disabled "disabled"))]
        [:div.level-item
         (into [:button attrs] children)]))))

(s/fdef tab-button
  :args (s/cat :opt :tab-button/options
               :children (s/+ any?))
  :ret vector?)

(s/def :toggle/on? boolean?)
(s/def :toggle/subscribe-to-on? :ad/subscription)
(s/def :toggle/class-for-on keyword?)
(s/def :toggle/class-for-off (s/nilable keyword?))
(s/def :toggle/on-click :ad/event-dispatcher)

(s/def :toggle/options (s/keys :req-un [:toggle/on-click]
                               :opt-un [:toggle/subscribe-to-on?
                                        :toggle/class-for-on
                                        :toggle/class-for-off]))

(defn toggle-button
  "
  A button that has different appearances depending on whether it is \"on\" or \"off\".
  This is a wrapper component for the base button component that uses the value of :on?
  or :subscribe-to-on? in order to (possibly) add a CSS class to the :extra-classes option.
  If the button is :on?, adds the :class-for-on CSS class, otherwise adds the optional
  class-for-off CSS class, if any.
  "
  [opts & _]
  (let [{:keys [subscribe-to-on? subscribe-to-classes]} opts
        on?-sub (adu/subscribe-to subscribe-to-on?)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        button-classes-state (ratom/atom nil)]
    (fn [opts & children]
      ;; Note: required :on-click option passes thru to base button component.
      (let [{:keys [class-for-on class-for-off]} opts
            on? @on?-sub
            dynamic-classes @classes-sub
            button-classes (cond
                             on? (adu/css+css dynamic-classes #{:wb-toggle-button class-for-on})
                             (nil? class-for-off) (adu/css+css dynamic-classes :wb-toggle-button)
                             :else (adu/css+css dynamic-classes #{:wb-toggle-button class-for-off}))]
        (reset! button-classes-state button-classes)
        ^{:key (str "toggle-button-" (if on? "on" "off") (pr-str children))}
        (into [button (assoc opts :subscribe-to-classes button-classes-state)] children)))))

(s/fdef toggle-button
  :args (s/cat :opt :toggle-button/options
               :children (s/+ any?))
  :ret vector?)




