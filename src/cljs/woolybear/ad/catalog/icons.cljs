(ns woolybear.ad.catalog.icons
  "Catalog and demonstrations of available icon components."
  (:require [re-frame.core :as re-frame]
            [reagent.ratom :as ratom]
            [woolybear.ad.catalog.utils :as acu]
            [woolybear.ad.layout :as layout]
            [woolybear.ad.containers :as containers]
            [woolybear.ad.buttons :as buttons]
            [woolybear.ad.icons :as icons]
            [woolybear.packs.flex-panel :as flex]))

(defn catalog
  []

  [:div

   (acu/demo "Standard Icons"
     "Simple, FontAwesome-based icons."
     [containers/bar
      [icons/icon {:icon "check"}]
      [icons/icon {:icon "edit"}]
      [icons/icon {:icon "save"}]
      [icons/icon {:icon "share"}]]
     '[containers/bar
       [icons/icon {:icon "check"}]
       [icons/icon {:icon "edit"}]
       [icons/icon {:icon "save"}]
       [icons/icon {:icon "share"}]])

   (acu/demo "Colored Icons"
     "FontAwesome icons are treated as text by the browser, so you can use
     Bulma text classes like has-text-success to color your icons."
     [containers/bar
      [icons/icon {:icon "check" :extra-classes :has-text-success}]
      [icons/icon {:icon "edit" :extra-classes :has-text-danger}]
      [icons/icon {:icon "save" :extra-classes :has-text-info}]
      [icons/icon {:icon "share" :extra-classes :has-text-primary}]]
     '[containers/bar
       [icons/icon {:icon "check" :extra-classes :has-text-success}]
       [icons/icon {:icon "edit" :extra-classes :has-text-danger}]
       [icons/icon {:icon "save" :extra-classes :has-text-info}]
       [icons/icon {:icon "share" :extra-classes :has-text-primary}]])

   (acu/demo "Small Icons"
     [containers/bar
      [icons/icon {:icon "check" :size :small}]
      [icons/icon {:icon "edit" :size :small}]
      [icons/icon {:icon "save" :size :small}]
      [icons/icon {:icon "share" :size :small}]]
     '[containers/bar
       [icons/icon {:icon "check" :size :small}]
       [icons/icon {:icon "edit" :size :small}]
       [icons/icon {:icon "save" :size :small}]
       [icons/icon {:icon "share" :size :small}]])

   (acu/demo "Medium Icons"
     [containers/bar
      [icons/icon {:icon "music" :size :medium}]
      [icons/icon {:icon "globe-americas" :size :medium}]
      [icons/icon {:icon "microphone" :size :medium}]
      [icons/icon {:icon "ellipsis-h" :size :medium}]]
     '[containers/bar
       [icons/icon {:icon "music" :size :medium}]
       [icons/icon {:icon "globe-americas" :size :medium}]
       [icons/icon {:icon "microphone" :size :medium}]
       [icons/icon {:icon "ellipsis-h" :size :medium}]])

   (acu/demo "Large Icons"
     [containers/bar
      [icons/icon {:icon "backward" :size :large}]
      [icons/icon {:icon "stop" :size :large}]
      [icons/icon {:icon "play" :size :large}]
      [icons/icon {:icon "forward" :size :large}]]
     '[containers/bar
       [icons/icon {:icon "backward" :size :large}]
       [icons/icon {:icon "stop" :size :large}]
       [icons/icon {:icon "play" :size :large}]
       [icons/icon {:icon "forward" :size :large}]])

   (acu/demo "Brand Icons"
     "Brand icons from the (free) FontAwesome collection."
     [containers/bar
      [icons/icon {:icon "google" :brand? true}]
      [icons/icon {:icon "jenkins" :brand? true}]
      [icons/icon {:icon "facebook" :brand? true}]
      [icons/icon {:icon "amazon" :brand? true}]
      ]
     '[containers/bar
       [icons/icon {:icon "google" :brand? true}]
       [icons/icon {:icon "jenkins" :brand? true}]
       [icons/icon {:icon "facebook" :brand? true}]
       [icons/icon {:icon "amazon" :brand? true}]
       ])

   (acu/demo "Clickable Icons"
     "Watch the JS console for messages when clicking icons."
     [containers/bar
      [icons/icon {:icon "comment"
                   :on-click (fn [_] (js/console.log "Comment icon clicked."))}]
      [icons/icon {:icon "gamepad"
                   :on-click (fn [_] (js/console.log "Gamepad icon clicked."))}]
      [icons/icon {:icon "frog"
                   :on-click (fn [_] (js/console.log "Frog icon clicked."))}]
      [icons/icon {:icon "helicopter"
                   :on-click (fn [_] (js/console.log "Helicopter icon clicked."))}]
      ]
     '[containers/bar
       [icons/icon {:icon "comment"
                    :on-click (fn [_] (js/console.log "Comment icon clicked."))}]
       [icons/icon {:icon "gamepad"
                    :on-click (fn [_] (js/console.log "Gamepad icon clicked."))}]
       [icons/icon {:icon "frog"
                    :on-click (fn [_] (js/console.log "Frog icon clicked."))}]
       [icons/icon {:icon "helicopter"
                    :on-click (fn [_] (js/console.log "Helicopter icon clicked."))}]
       ])

   ])
