(ns woolybear.ad.catalog.utils
  "
  Misc utilities for rendering demonstrations in the AD Catalog, including
  a utility for pop-up source code blocks.
  "
  (:require [re-frame.core :as re-frame]
            [woolybear.ad.utils :as adu]
            [woolybear.ad.layout :as layout]
            [woolybear.ad.buttons :as buttons]
            [woolybear.ad.containers :as containers]))

;; normally we'd define code-block as a layout component, but
;; we're putting it here because it's only used in the AD
;; catalog, and isn't intended for public consumption.

