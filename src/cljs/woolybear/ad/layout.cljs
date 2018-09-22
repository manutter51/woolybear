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
