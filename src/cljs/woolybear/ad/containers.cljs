(ns woolybear.ad.containers
  "
  Container components are the next step up from simple layout components. Container
  components take subscriptions and may fire events.
  "
  (:require [reagent.ratom :as ratom]
            [cljs.spec.alpha :as s]
            [woolybear.ad.utils :as adu]
            [woolybear.ad.buttons :as buttons]))

(s/def :shy-block/options (s/keys :req-un [:ad/active?]
                                  :opt-un [:ad/extra-classes :ad/subscribe-to-classes]))

(defn shy-block
  "A container that may or may not be visible, depending on the current value of
  its `active?` subscription. If you wish you can pass in extra CSS classes via
  the :extra-classes option. For extra classes that change dynamically at run-time,
  pass in :subscribe-to-classes instead."
  [opts & _]
  (let [{:keys [extra-classes subscribe-to-classes]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [{:keys [active?]} & children]
      (let [dynamic-classes @classes-sub
            vis-class (if active? :visible :hidden)]
        (into [:div {:class (adu/css->str :wb-shy
                                          vis-class
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef shy-block
  :args (s/cat :opts (s/? :shy-block/options)
               :children (s/+ any?))
  :ret vector?)

(s/def :scroll-pane-header/options (s/keys :opt-un [:ad/extra-classes
                                                    :ad/subscribe-to-classes]))

(defn scroll-pane-header
  "A component with no bottom margin. If a scroll-pane-header is passed in as
  a top-level child of a v-scroll-pane, it will remain fixed in place at the
  top while the other components scroll beneath it. Accepts an optional opts
  map as the first argument, with the following options:

  * :extra-classes        - static CSS classes to apply to the footer
  * :subscribe-to-classes - subscription to dynamic CSS classes to apply at runtime.
  "
  [& args]
  (let [[opts _] (adu/extract-args args)
        {:keys [extra-classes subscribe-to-classes]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :wb-scroll-pane-header
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(s/fdef scroll-pane-header
  :args (s/cat :opts (s/? :scroll-pane-header/options)
               :children (s/+ any?))
  :ret vector?)

(s/def :scroll-pane-footer/options (s/keys :opt-un [:ad/extra-classes
                                                    :ad/subscribe-to-classes]))

(defn scroll-pane-footer
  "A component with no top margin. If a scroll-pane-footer is passed in as
  a top-level child of a v-scroll-pane, it will remain fixed in place at the
  bottom while the other components scroll above it. Accepts an optional opts
  map as the first argument, with the following options:

  * :extra-classes        - static CSS classes to apply to the footer
  * :subscribe-to-classes - subscription to dynamic CSS classes to apply at runtime.
  "
  [& args]
  (let [[opts _] (adu/extract-args args)
        {:keys [extra-classes subscribe-to-classes]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :wb-scroll-pane-footer extra-classes dynamic-classes)}]
              children)))))

(s/fdef scroll-pane-footer
  :args (s/cat :opts (s/? :scroll-pane-footer/options)
               :children (s/+ any?))
  :ret vector?)

(defn- get-header-footer-body-type
  "Given a child element, return :header if it is a scroll-pane-header, or
  :footer if it is a scroll-pane-footer, or :body if it is anything else."
  [child]
  (condp = (first child)
    scroll-pane-header :header
    scroll-pane-footer :footer
    :body))

(s/def :v-scroll-pane/height string?)
(s/def :v-scroll-pane/options (s/keys :req-un [:v-scroll-pane/height]
                                      :opt-un [:ad/extra-classes
                                               :ad/subscribe-to-classes]))

(defn v-scroll-pane
  "A component that sets overflow-y to auto so that if its contents exceed the
  component height, a scrollbar will appear. If any of the child elements are
  scroll-pane-header elements, and are not nested inside any other child elements,
  they will be placed at the top, above the scrolling portion. Any scroll-pane-footer
  child elements are similarly locked to the bottom of the scroll area. Requires an
  options map as the first argument, with the following options. The :height option
  is required and the others are optional.

  * :height (required)    - standard CSS height value (e.g. 60vh)
  * :extra-classes        - static CSS classes to apply to the footer
  * :subscribe-to-classes - subscription to dynamic CSS classes to apply at runtime.
  "
  [opts & _]
  (let [{:keys [extra-classes subscribe-to-classes]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [{:keys [height]} & children]
      (let [{:keys [header footer body]} (group-by get-header-footer-body-type children)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :wb-v-scroll-pane-container
                                          extra-classes
                                          dynamic-classes)}]
              (remove nil?
                      [(when header
                         (into [:div.wb-v-scroll-pane-header] header))
                       ^{:key (str "v-scroll-pane-overflow-" height)}
                       (into [:div.wb-v-scroll-pane-overflow {:style {:height height}}] body)
                       (when footer
                         (into [:div.wb-v-scroll-pane-footer] footer))]))))))

(s/fdef v-scroll-pane
  :args (s/cat :opts (s/? :v-scroll-pane/options)
               :children (s/+ any?))
  :ret vector?)

(s/def :bar/options (s/keys :opt-un [:ad/extra-classes
                                     :ad/subscribe-to-classes]))

(defn bar
  "
  Generic bar component, suitable for use as a toolbar, button bar, etc.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :level :wb-bar
                                          extra-classes dynamic-classes)}]
              children)))))

(s/fdef bar
  :args (s/cat :opts (s/? :bar/options)
               :children (s/* any?))
  :ret vector?)

(s/def :spoiler/show-label string?)
(s/def :spoiler/hide-label string?)

(s/def :spoiler/options (s/keys :opt-un [:spoiler/show-label
                                         :spoiler/hide-label
                                         :ad/extra-classes
                                         :ad/subscribe-to-classes]))

(defn spoiler
  "
  A component with a show-hide button and other content. When the button is clicked,
  it toggles the visibility of the other contents. Supports the following options:

  * :show-label - Button label when contents hidden, default 'Show'
  * :hide-label - Button label when contents shown, default 'Hide'
  * :extra-classes, :subscribe-to-classes - Standard CSS options

  NOTE: stores the show/hide state internally, and not in the app-db.
  "
  [& args]
  (let [[{:keys [show-label hide-label
                 extra-classes subscribe-to-classes]} _] (adu/extract-args args)
        visible? (ratom/atom false)
        show-label (or show-label 'Show')
        hide-label (or hide-label 'Hide')
        classes-sub (adu/subscribe-to subscribe-to-classes)
        click-dispatcher (fn [_] (swap! visible? not))]
    (fn [& args]
      (let [[_ children] (adu/extract-args args)
            visible? @visible?
            button-label (if visible? hide-label show-label)
            dynamic-classes @classes-sub]
        [:div.level {:class (adu/css->str :wb-spoiler extra-classes dynamic-classes)}
         [:div.level-item-left [buttons/button {:on-click click-dispatcher}
                                button-label]]
         [:div.level-item
          (into [shy-block {:active? visible?}] children)]]))))

(s/fdef spoiler
  :args (s/cat :opts (s/? :spoiler/options)
               :children (s/+ any?))
  :ret vector?)
