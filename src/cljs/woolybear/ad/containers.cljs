(ns woolybear.ad.containers
  "
  Container components are the next step up from simple layout components. Container
  components take subscriptions and may fire events.
  "
  (:require [re-frame.core :as re-frame]
            [cljs.spec.alpha :as s]
            [woolybear.ad.utils :as adu]))

(s/def :container/subscribe-to-visible? :ad/subscription?)
;; :ad/extra-classes defined in woolybear.ad.utils
(s/def :shy-block/options (s/keys :req-un [:container/subscribe-to-visible?]
                                  :opt-un [:ad/extra-classes :ad/subscribe-to-classes]))
(s/fdef shy-block
        :args (s/cat :opts (s/? :shy-block/options)
                     :children (s/+ any?))
        :ret vector?)

(defn shy-block
  "A container that may or may not be visible, depending on the current value of
  its `visible?` subscription. If you wish you can pass in extra CSS classes via
  the :extra-classes option. For extra classes that change dynamically at run-time,
  pass in :subscribe-to-classes instead."
  [opts & _]
  (let [{:keys [extra-classes subscribe-to-visible? subscribe-to-classes]} opts
        visible?-sub (adu/subscribe-to subscribe-to-visible?)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [_ & children]
      (let [visible? @visible?-sub
            dynamic-classes @classes-sub
            vis-class (if visible? :visible :hidden)]
        (into [:div {:class (adu/css->str :shy
                                          vis-class
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/def :scroll-pane-header/options (s/keys :opt-un [:ad/extra-classes
                                                    :ad/subscribe-to-classes]))
(s/fdef scroll-pane-header
        :args (s/cat :opts (s/? :scroll-pane-header/options)
                     :children (s/+ any?))
        :ret vector?)
(defn scroll-pane-header
  "A component with no bottom margin. If a scroll-pane-header is passed in as
  a top-level child of a v-scroll-pane, it will remain fixed in place at the
  top while the other components scroll beneath it. Accepts an optional opts
  map as the first argument, with the following options:

  * :extra-classes        - static CSS classes to apply to the footer
  * :subscribe-to-classes - subscription to dynamic CSS classes to apply at runtime.
  "
  [& args]
  (let [[opts _] (adu/extract-opts args)
        {:keys [extra-classes subscribe-to-classes]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-opts args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :scroll-pane-header
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/def :scroll-pane-footer/options (s/keys :opt-un [:ad/extra-classes
                                                    :ad/subscribe-to-classes]))
(s/fdef scroll-pane-footer
        :args (s/cat :opts (s/? :scroll-pane-footer/options)
                     :children (s/+ any?))
        :ret vector?)

(defn scroll-pane-footer
  "A component with no top margin. If a scroll-pane-footer is passed in as
  a top-level child of a v-scroll-pane, it will remain fixed in place at the
  bottom while the other components scroll above it. Accepts an optional opts
  map as the first argument, with the following options:

  * :extra-classes        - static CSS classes to apply to the footer
  * :subscribe-to-classes - subscription to dynamic CSS classes to apply at runtime.
  "
  [& args]
  (let [[opts _] (adu/extract-opts args)
        {:keys [extra-classes subscribe-to-classes]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-opts args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :scroll-pane-footer extra-classes dynamic-classes)}]
              children)))))

(defn- header-footer-body?
  "Given a child element, return :header if it is a scroll-pane-header, or
  :footer if it is a scroll-pane-footer, or :body if it is anything else."
  [child]
  (condp = (first child)
    scroll-pane-header :header
    scroll-pane-footer :footer
    :body))

(s/def :v-scroll-pane/options (s/keys :opt-un [:ad/extra-classes
                                               :ad/subscribe-to-classes]))
(s/fdef v-scroll-pane
        :args (s/cat :opts (s/? :v-scroll-pane/options)
                     :children (s/+ any?))
        :ret vector?)

(defn v-scroll-pane
  "A component that sets overflow-y to auto so that if its contents exceed the
  component height, a scrollbar will appear. If any of the child elements are
  scroll-pane-header elements, and are not nested inside any other child elements,
  it will be placed at the top, above the scrolling portion. Any scroll-pane-footer
  child elements are similarly locked to the bottom of the scroll area. Accepts an
  optional opts map as the first argument, with the following options:

  * :extra-classes        - static CSS classes to apply to the footer
  * :subscribe-to-classes - subscription to dynamic CSS classes to apply at runtime.
  "
  [& args]
  (let [[opts _] (adu/extract-opts args)
        {:keys [extra-classes subscribe-to-classes]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-opts args)
            {:keys [header footer body]} (group-by header-footer-body? children)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :v-scroll-pane-container
                                          extra-classes
                                          dynamic-classes)}]
              (remove nil?
                      [(when header
                         (into [:div.v-scroll-pane-header] header))
                       (into [:div.v-scroll-pane-overflow] body)
                       (when footer
                         (into [:div.v-scroll-pane-footer] footer))]))))))

