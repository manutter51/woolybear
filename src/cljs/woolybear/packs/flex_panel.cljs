(ns woolybear.packs.flex-panel
  "
  A container for scrolling content with fixed elements and the top and/or
  bottom. Dynamically re-sizes itself to fit onto screens with any arbitrary
  dimensions and/or any sized browser window.
  "
  (:require [reagent.core :as reagent]
            [reagent.ratom :as ratom]
            [woolybear.ad.utils :as adu]
            [woolybear.ad.containers :as containers]
            [cljs.spec.alpha :as s]))

(defn flex-fixed
  "
  A utility component used to build flex-top and flex-bottom. Uses an on-size-change
  callback fn provided by the parent to report its height back to the parent container.
  "
  [type-class & args]
  (let [[{:keys [extra-classes subscribe-to-classes
                 on-size-change]} _] (adu/extract-opts args)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        size-change-handler (fn [comp]
                              (on-size-change (-> comp
                                                  (reagent/dom-node)
                                                  .-offsetHeight)))
        render-fn (fn [& args]
                    (let [[_ children] (adu/extract-opts args)
                          dynamic-classes @classes-sub]
                      (into [:div {:class (adu/css->str type-class
                                                        extra-classes
                                                        dynamic-classes)}]
                            children)))]
    (reagent/create-class
      {:display-name         "flex-top"
       :component-did-mount  size-change-handler
       :component-did-update size-change-handler
       :reagent-render       render-fn
       })))

(s/fdef flex-fixed
  :args (s/cat :type keyword?
               :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn flex-top
  "
  A container to be used inside a flex-panel. Sticks to the top of the panel and
  subtracts its own height from the height of the height of the flexible part of
  the flex panel so that the flex panel fits neatly inside its enclosing space.
  "
  [& args]
  (apply flex-fixed :wb-flex-top args))

(s/fdef flex-top
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn flex-bottom
  "
  A container to be used inside a flex-panel. Sticks to the bottom of the panel and
  subtracts its own height from the height of the height of the flexible part of
  the flex panel so that the flex panel fits neatly inside its enclosing space.
  "
  [& args]
  (apply flex-fixed :wb-flex-bottom args))

(s/fdef flex-bottom
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn flex-content
  "
  The 'flexible' part of a flex-panel. The height of the flex-content component is managed
  by the parent flex-panel, so that it is always equal to the height of the enclosing
  container, minus the height(s) any flex-top and/or flex-bottom components in the same
  flex panel.
  "
  [opts & children]
  (let [{:keys [height]} opts]
    (into [containers/v-scroll-pane {:height height}] children)))

(s/fdef flex-content
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)

(defn- flex-type
  "Given a child element, return :flex-top if it is a flex-top, :flex-bottom if it is a
  flex-bottom, or :other if it is anything else."
  [child]
  (condp = (first child)
    flex-top :flex-top
    flex-bottom :flex-bottom
    :other))

(defn- get-fixed-child
  "Search a list of children and pull out the first instance of flex-top or flex-bottom,
  properly prepared for use in a flex panel."
  [children target-component-type height-atom]
  (let [targets (filter #(= target-component-type (flex-type %)) children)
        target (first targets)]
    (when target
      (adu/add-option target :on-size-change (fn [h]
                                               (reset! height-atom h))))))

(defn flex-panel
  "
  A container that creates a flexible, autosizing panel to fill the entire height of the
  viewport, reserving space for any enclosed flex-top and/or flex-bottom components.
  Takes a :height option, specified in standard CSS units (180px, 50vh, 8rem, etc). Also
  takes the standard :extra-classes and :subscribe-to-classes options.

  NOTE: You can have both a flex-top and flex-bottom as a child element, but not more
  than one of each.
  "
  [& args]
  (let [[{:keys [height extra-classes subscribe-to-classes]} _] (adu/extract-opts args)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        flex-height (ratom/atom 0)
        flex-top-height (ratom/atom 0)
        flex-bottom-height (ratom/atom 0)
        size-change-handler (fn [comp]
                              (reset! flex-height (-> comp
                                                      (reagent/dom-node)
                                                      .-offsetHeight)))
        render-fn (fn [& args]
                    (let [[_ children] (adu/extract-opts args)
                          flex-contents (filter #(= :other (flex-type %)) children)
                          the-flex-top (get-fixed-child children :flex-top flex-top-height)
                          the-flex-bottom (get-fixed-child children :flex-bottom flex-bottom-height)
                          dynamic-classes @classes-sub
                          content-height (- @flex-height (+ @flex-top-height @flex-bottom-height))
                          content-height (str content-height "px")]
                      [:div {:style {:height height}
                             :class (adu/css->str :wb-flex-panel
                                                  extra-classes
                                                  dynamic-classes)}
                       the-flex-top
                       ^{:key (str "flex-content-" content-height)}
                       (into [flex-content {:height content-height
                                            :date-content-height content-height}] flex-contents)
                       the-flex-bottom
                       ]))]
    (reagent/create-class
      {:display-name         "flex-panel"
       :component-did-mount  size-change-handler
       :component-did-update size-change-handler
       :reagent-render       render-fn})))

(s/fdef flex-panel
  :args (s/cat :opts (s/keys :opt-un [:ad/extra-classes
                                      :ad/subscribe-to-classes])
               :children (s/+ any?))
  :ret vector?)
