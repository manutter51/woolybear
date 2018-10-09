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
  [:div
   (demo "Page"
     "The page component is a simple wrapper designed to contain an entire
     \"page\" (not including the site header and footer). Use this container
     to wrap each page, then edit the AD page component whenever you want to
     make changes that apply to the page as a whole."
     [layout/page
      [:div "Something much more complex would go here."]])

   (demo "Page Title"
     "Use the page-title component instead of an h1 tag to contain the main
     title at the top of each page."
     [layout/page-title "Demonstration Page"])

   (demo "Section"
     "Use sections within a page to center the contents (space permitting).
     Multiple sections on a page will be separated by vertical spacing as
     well, to create a clear distinction between sections."
     [:div
      [layout/section "This is Section One. " acu/lorem]
      [layout/section "This is Section Two. " acu/lorem]])

   (demo "Text Block"
     "Use text blocks for ordinary blocks of text and generic text content."
     [layout/text-block acu/lorem])

   (demo "Frame"
     "Use a frame container to enclose other components inside a bordered frame
     and a drop-shadow."
     [layout/frame
      [layout/text-block "Here is a text block"]
      [layout/text-block acu/lorem]])

   ])
