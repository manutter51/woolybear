(ns woolybear.ad.catalog
  (:require [re-frame.core :as re-frame]
            [woolybear.ad.buttons :as buttons]
            [woolybear.ad.containers :as containers]
            [woolybear.ad.layout :as layout]
            [woolybear.packs.tab-panel :as tab-panel]))

(defn page
  "Top-level AD Catalog page"
  []
  [layout/page
   [layout/page-title "AD Catalog"]
   [layout/text-block "Click a tab to see the Atomic Design components defined for that category."]

   [tab-panel/tab-panel {:extra-classes :ad-catalog}
    [tab-panel/sub-panel
     [buttons/tab-button "Layout"]
     [layout/text-block "This is where the layout components will appear"]]
    [tab-panel/sub-panel
     [buttons/tab-button "Containers"]
     [layout/text-block "This is where the container components will appear"]]
    [tab-panel/sub-panel
     [buttons/tab-button "Buttons"]
     [layout/text-block "This is where the button components will appear"]]
    ]])
