(ns woolybear.ad.catalog.buttons
  "Catalog and demonstrations of available button components."
  (:require [re-frame.core :as re-frame]
            [reagent.ratom :as ratom]
            [woolybear.ad.catalog.utils :as acu]
            [woolybear.ad.layout :as layout]
            [woolybear.ad.containers :as containers]
            [woolybear.ad.icons :as icons]
            [woolybear.ad.buttons :as buttons]))

(defn- tattle
  "Utility for making a fake dispatcher that just reports to the js/console"
  [evt]
  (fn [_]
    (js/console.log "Dispatched %o" evt)))

(defn catalog
  []

  [:div

   (acu/demo "Simple button"
     "The generic [buttons/button] component is the base component for a number of common,
     more-specialized buttons like the OK button, Save button, and Cancel button. Takes the
     standard extra-classes and subscribe-to-classes options, plus a required on-click
     option, which specifies the event to dispatch when the button is clicked."
     [layout/padded
      [buttons/button {:on-click (tattle
                                   [:button-demo/click :ooo/you-actually-clicked-it!])}
       "Click me!"]]
     '[layout/padded
       [buttons/button {:on-click [:button-demo/click :ooo/you-actually-clicked-it!]}]])

   (acu/demo "Specialized buttons"
     "The OK, Save, Cancel, Delete, and Close buttons are all simple wrappers around the simple
     [buttons/button] component, each one supplying a default label and related classes so that
     the indicated button can be implemented with the minimum of ceremony."
     [layout/padded {:extra-classes :level}
      [buttons/ok-button {:on-click (tattle [:button-demo/click :ok-button])}]
      [buttons/save-button {:on-click (tattle [:button-demo/click :save-button])}]
      [buttons/cancel-button {:on-click (tattle [:button-demo/click :cancel-button])}]
      [buttons/delete-button {:on-click (tattle [:button-demo/click :delete-button])}]
      [buttons/close-button {:on-click (tattle [:button-demo/click :close-button])}]
      ]
     '[layout/padded {:extra-classes :level}
       [buttons/ok-button {:on-click (tattle [:button-demo/click :ok-button])}]
       [buttons/save-button {:on-click (tattle [:button-demo/click :save-button])}]
       [buttons/cancel-button {:on-click (tattle [:button-demo/click :cancel-button])}]
       [buttons/delete-button {:on-click (tattle [:button-demo/click :delete-button])}]
       [buttons/close-button {:on-click (tattle [:button-demo/click :close-button])}]
       ])

   (acu/demo "Buttons with icons"
     "By default, buttons take simple strings as child elements, which are rendered
     as the button label. But you're not limited to strings--button labels can include
     icons as well."
     [layout/padded {:extra-classes :level}
      [buttons/button {:on-click (tattle [:button-demo/click :icon-edit-button])}
       [icons/icon {:icon "edit"}] "Edit"]
      [buttons/button {:on-click (tattle [:button-demo/click :icon-share-button])}
       [icons/icon {:icon "share"}] "Share"]
      [buttons/button {:on-click (tattle [:button-demo/click :icon-twitter-button])}
       [icons/icon {:icon "twitter" :brand? true :extra-classes :has-text-info}] "Tweet"]]
     '[layout/padded {:extra-classes :level}
       [buttons/button {:on-click (tattle [:button-demo/click :icon-edit-button])}
        [icons/icon {:icon "edit"}] "Edit"]
       [buttons/button {:on-click (tattle [:button-demo/click :icon-share-button])}
        [icons/icon {:icon "share"}] "Share"]
       [buttons/button {:on-click (tattle [:button-demo/click :icon-twitter-button])}
        [icons/icon {:icon "twitter" :brand? true :extra-classes :has-text-info}] "Tweet"]])

   ])
