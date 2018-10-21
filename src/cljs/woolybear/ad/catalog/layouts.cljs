(ns woolybear.ad.catalog.layouts
  "Catalog and acu/demonstrations of available layout components."
  (:require [woolybear.ad.catalog.utils :as acu]
            [woolybear.ad.layout :as layout]))

(defn catalog
  []
  [:div
   (acu/demo "Page"
     "The page component is a simple wrapper designed to contain an entire
     \"page\" (not including the site header and footer). Use this container
     to wrap each page, then edit the AD page component whenever you want to
     make changes that apply to the page as a whole."
     [layout/page
      [:div "Something much more complex would go here."]]
     '[layout/page
       [:div "Something much more complex would go here."]])

   (acu/demo "Page Header"
     "The page header component is intended for use as the top section of a
     page, containing the page title, navigation, bread crumbs, etc. Use
     inside a [layout/page] component."
     [layout/page
      [layout/page-header "Put header stuff here."]]
     '[layout/page
       [layout/page-header "Put header stuff here."]])

   (acu/demo "Page Title"
     "Use the page-title component instead of an h1 tag to contain the main
     title at the top of each page."
     [layout/page
      [layout/page-header
       [layout/page-title "Demonstration Page"]]]
     '[layout/page
       [layout/page-header
        [layout/page-title "Demonstration Page"]]])

   (acu/demo "Section"
     "Use sections within a page to center the contents (space permitting).
     Multiple sections on a page will be separated by vertical spacing as
     well, to create a clear distinction between sections."
     [:div
      [layout/section "This is Section One. " acu/lorem]
      [layout/section "This is Section Two. " acu/lorem]]
     '[:div
       [layout/section "This is Section One. " acu/lorem]
       [layout/section "This is Section Two. " acu/lorem]])

   (acu/demo "Text Block"
     "Use text blocks for ordinary blocks of text and generic text content."
     [layout/text-block acu/lorem]
     '[layout/text-block acu/lorem])

   (acu/demo "Frame"
     "Use a frame container to enclose other components inside a bordered frame
     and a drop-shadow."
     [layout/frame
      [layout/text-block "Here is a text block"]
      [layout/text-block acu/lorem]]
     '[layout/frame
       [layout/text-block "Here is a text block"]
       [layout/text-block acu/lorem]])

   ])
