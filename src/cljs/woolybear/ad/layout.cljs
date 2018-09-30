(ns woolybear.ad.layout
  "
  These components are the simplest AD components to write, since they do not
  require subscriptions or event handlers. They are simple, basic wrappers to
  put things in--a tool for structuring pages and complex components. Because
  they are so simple, we're combining several of them in a single namespace
  instead of breaking them out into their own packs. Many of these components
  rely on the classes defined in the Bulma CSS library, which are documented
  at https://bulma.io/documentation/layout/.
  "
  (:require [re-frame.core :as re-frame]
            [woolybear.ad.utils :as adu]))

(defn page
  "Very top-level block, designed to wrap all other elements on the page, with the
  exception of the page header and footer. Centers content horizontally (space
  permitting)."
  [& children]
  (into [:div.container] children))

(defn page-title
  "Simple page title, designed to appear exactly once at the top of a page."
  [title-text]
  [:div {:class (adu/css->str :wb-page-title
                              :container
                              :is-size-1)} title-text])

(defn text-block
  "Simple container for one or more paragraphs of text. Automatically adds a
  1.5rem margin between itself and the next child element, unless it is the
  last child element."
  [& children]
  (into [:div.content] children))

(defn frame
  "Encloses contents inside a frame with rounded corners and a slight drop
  shadow."
  [& children]
  (into [:div.box children]))
