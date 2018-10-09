(ns woolybear.packs.flex-panel
  "
  A container for scrolling content with fixed elements and the top and/or
  bottom. Dynamically re-sizes itself to fit onto screens with any arbitrary
  dimensions and/or any sized browser window.
  "
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [reagent.ratom :as ratom]
            [woolybear.ad.utils :as adu]
            ))

(defn flex-top
  "
  A container to be used inside a flex-panel. Sticks to the top of the panel.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes
                 on-size-change]} _] (adu/extract-opts args)
        classes-sub (adu/subscribe-to subscribe-to-classes)
        size-change-handler (fn [comp]
                              (println (-> comp
                                                  (reagent/dom-node)
                                                  .-offsetHeight)))]
    (reagent/create-class
      {:display-name "flex-top"
       :component-did-mount size-change-handler
       :component-did-update size-change-handler
       :reagent-render
       (fn [& args]
         (let [[_ children] (adu/extract-opts args)
               dynamic-classes @classes-sub]
           (into [:div {:class (adu/css->str :wb-flex-top
                                             extra-classes
                                             dynamic-classes)}]
                 children)))})))

(defn flex-bottom
  "
  A container to be used inside a flex-panel. Sticks to the bottom of the panel.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes
                 on-size-change]} _] (adu/extract-opts args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-opts args)
            dynamic-classes @classes-sub]
        (into [:div {:class (adu/css->str :wb-flex-bottom
                                          extra-classes
                                          dynamic-classes)}]
              children)))))

(defn flex-panel
  "
  A container that creates a flexible, autosizing panel to fill the entire height of the
  viewport, reserving space for any enclosed flex-top and/or flex-bottom components.
  "
  [& args]
  )
