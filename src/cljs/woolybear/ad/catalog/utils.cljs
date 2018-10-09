(ns woolybear.ad.catalog.utils
  "
  Misc utilities for rendering demonstrations in the AD Catalog, including
  a utility for pop-up source code blocks.
  "
  (:require
            [re-frame.core :as re-frame]
            [woolybear.ad.utils :as adu]
            [woolybear.ad.layout :as layout]
            [woolybear.ad.buttons :as buttons]
            [woolybear.ad.containers :as containers])
  ; get macros from cljc file
  (:require-macros [woolybear.ad.catalog.utils :refer [demo]]))

;; normally we'd define code-block as a layout component, but
;; we're putting it here because it's only used in the AD
;; catalog, and isn't intended for public consumption. It's a
;; textarea so that the code will be easier to select for copy
;; and paste.

(defn code-block
  [code]
  (into [:textarea.code-block {:value code}]))

(def lorem
  "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
