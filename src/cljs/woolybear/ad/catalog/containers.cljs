(ns woolybear.ad.catalog.containers
  "Catalog and demonstrations of available container components."
  (:require [reagent.ratom :as ratom]
            [woolybear.ad.catalog.utils :as acu]
            [woolybear.ad.layout :as layout]
            [woolybear.ad.containers :as containers]
            [woolybear.ad.buttons :as buttons]
            [woolybear.packs.flex-panel :as flex]))

(defn shy-block-demo
  []
  (let [shy-block-active? (ratom/atom true)
        click-dispatcher (fn [_]
                           (swap! shy-block-active? not))]
    (fn []
      (let [active? @shy-block-active?]
        [layout/frame
         [layout/columns
          [layout/column {:extra-classes :is-one-fifth}
           [layout/padded
            [buttons/button {:on-click click-dispatcher} "Toggle"]]]
          [:div.column
           [containers/shy-block {:active? active?}
            [layout/text-block acu/lorem]]]]]))))

(defn catalog
  []
  [:div

   (acu/demo "Vertical scroll pane"
     "Use a v-scroll-pane to wrap oversized content inside a container with a vertical
     scroll bar with a fixed height specified by the :height option. Takes the standard
     :extra-classes and :subscribe-to-classes option. If the v-scroll-pane contains a
     component of type scroll-pane-header, it will be displayed above the scrolling
     portion. Similarly, a scroll-pane-footer will be displayed below.

     Note that the :height parameter applies only to the scrolling portion of the
     container; any header and/or footer will add additional height to the v-scroll-pane
     as a whole."
     [containers/v-scroll-pane {:height "12rem"}
      [containers/scroll-pane-header "This line does not scroll."]
      [containers/scroll-pane-footer "This line also does not scroll."]
      [layout/text-block acu/lorem]
      [layout/text-block acu/lorem]
      [layout/text-block acu/lorem]]

     '[containers/v-scroll-pane {:height "12rem"}
       [containers/scroll-pane-header "This line does not scroll."]
       [containers/scroll-pane-footer "This line also does not scroll"]
       [layout/text-block acu/lorem]
       [layout/text-block acu/lorem]
       [layout/text-block acu/lorem]])

   (acu/demo "Shy block"
     "A 'shy' block is a container that is only visible when its :active? option
     is true. Used as a sub-component of other components such as the 'spoiler'
     panel. Takes standard :extra-classes and :subscribe-to-classes options."
     [shy-block-demo]

     '[layout/frame
       [buttons/button {:on-click click-dispatcher} "Toggle"]
       [containers/shy-block {:active? active?}
        [layout/text-block acu/lorem]]])

   (acu/demo "Flex panel"
     "A flex panel is designed primarily to implement a container that fills the
     entire viewport, with a scrolling content section between an (optional) flex-top
     and/or flex-bottom component. Specify the :height as 100vh to fill the entire
     screen, or use any valid CSS height specification to set a different size."
     [flex/flex-panel {:height "33vh"}
      [flex/flex-top "This line should be fixed to the top of the flex panel."]
      [flex/flex-bottom "This line should be fixed to the bottom of the flex panel."]
      [layout/text-block acu/lorem]
      [layout/text-block acu/lorem]
      [layout/text-block acu/lorem]]
     '[flex/flex-panel {:height "33vh"}
       [flex/flex-top "This line should be fixed to the top of the flex panel."]
       [flex/flex-bottom "This line should be fixed to the bottom of the flex panel."]
       [layout/text-block acu/lorem]
       [layout/text-block acu/lorem]
       [layout/text-block acu/lorem]])

   ])
