(ns woolybear.ad.catalog.layouts
  ""
  (:require [re-frame.core :as re-frame]
            [woolybear.ad.catalog.utils :as acu]
            [woolybear.ad.layout :as layout]
            [woolybear.ad.containers :as containers])
  ; get macros from cljc file
  (:require-macros [woolybear.ad.catalog.utils :refer [demo]]))

(defn catalog
  []
  [layout/section
   [containers/v-scroll-pane
    (demo
      "Page"
      "The page component is a simple wrapper designed to contain an entire
      \"page\" (not including the site header and footer)."
      [layout/page
       [:div "Something much more complex would go here."]])]])
