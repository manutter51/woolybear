(ns woolybear.ad.buttons
  "
  Button components support simple, configurable interactions and styles. Use
  button components instead of rolling your own [:button] elements in order to
  promote a consistent UX, and to promote a standard API for things like a
  dynamic 'disabled' attribute.
  "
  (:require [re-frame.core :as re-frame]
            [cljs.spec.alpha :as s]
            [woolybear.ad.utils :as adu]))

(s/def :button/subscribe-to-disabled? :ad/subscription)
(s/def :button/on-click :ad/event-handler)

(s/def :button-options (s/keys :req-un [:button/on-click]
                               :opt-un [:button/subscribe-to-disabled?
                                        :ad/extra-classes
                                        :ad/subscribe-to-classes]))

(s/fdef button
  :args (s/cat :opt :button-options
               :children (s/+ any?))
  :ret vector?)

(defn button
  "
  A simple button component that fires a specified event when clicked.
  Note that the opts argument is mandatory, since you must specify an
  :on-click handler. You can also pass a :subscribe-to-disabled? option
  to dynamically enable/disable the button at runtime, and either (or
  both) of the :extra-classes and :subscribe-to-classes options to set
  additional CSS classes on the button.
  "
  [opts & _]
  (let [{:keys [on-click subscribe-to-disabled?
                extra-classes subscribe-to-classes]} opts
        disabled?-sub (adu/subscribe-to subscribe-to-disabled?)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        click-handler (adu/mk-handler on-click)]
    (fn [_ & children]
      (let [disabled? @disabled?-sub
            dynamic-classes @classes-sub
            attrs (cond-> {:on-click click-handler
                           :class    (adu/css->str :button extra-classes dynamic-classes)}
                          disabled? (assoc :disabled "disabled"))]
        (into [:button attrs] children)))))

(s/def :tab-button/active? boolean?)
(s/def :tab-button/options (s/keys :req-un [:button/on-click :tab-button/active?]
                                   :opt-un [:button/subscribe-to-disabled?
                                            :ad/extra-classes
                                            :ad/subscribe-to-classes]))

(s/fdef tab-button
  :args (s/cat :opt :tab-button/options
               :children (s/+ any?))
  :ret vector?)

(defn tab-button
  "
  A button intended for use as part of a tab bar or tab-panel. Takes all the same
  options as a regular button, plus an :active? option. Notice that :active? is a
  direct boolean value rather than a subscription. The assumption here is that
  each tab button will be part of a group of buttons managed by a parent container,
  and the parent container will take care of managing which tab button is active.
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
        click-handler (adu/mk-handler on-click)]
    ;; we want the render-time value of the :active? opt, so we destructure it here.
    (fn [{:keys [active?]} & children]
      (let [disabled? @disabled?-sub
            dynamic-classes @classes-sub
            attrs (cond-> {:on-click click-handler
                           :class    (adu/css->str :button :tab-button (when active? :active)
                                                   extra-classes dynamic-classes)}
                          disabled? (assoc :disabled "disabled"))]
        (into [:button attrs] children)))))
