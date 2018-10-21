(ns woolybear.ad.catalog.utils
  "
  Misc utilities for rendering demonstrations in the AD Catalog, including
  a utility for pop-up source code blocks.
  "
  (:require [clojure.string :as string]
            [woolybear.ad.containers :as containers]))

;; normally we'd define code-block as a layout component, but
;; we're putting it here because it's only used in the AD
;; catalog, and isn't intended for public consumption. It's a
;; textarea so that the code will be easier to select for copy
;; and paste.

(defn code-block
  [code]
  (into [:textarea.code-block {:defaultValue code}]))

(def lorem
  "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")


(declare pps)

(defn pp-vector
  [v prefix]
  (string/trimr (reduce (fn [result child]
                          (str result (pps child prefix) " "))
                        ""
                        v)))

(defn pp-map
  [m prefix]
  (let [short-prefix (apply str (butlast prefix))]
    (string/trimr (reduce (fn [result [k v]]
                            (str result k " " (pps v prefix) \newline short-prefix))
                          ""
                          m))))

(defn pps
  ([src] (pps src ""))
  ([src prefix]
   (cond
     (instance? cljs.core/PersistentVector src) (str \newline prefix "[" (pp-vector src (str prefix "  ")) "]")
     (instance? cljs.core/PersistentArrayMap src) (str \newline prefix "{" (pp-map src (str prefix "  ")) "}")
     (instance? cljs.core/PersistentHashSet src) (str \newline prefix "#{" (pp-vector src (str prefix "  ")) "}")
     :else (pr-str src))))

(defn demo
  [name & children]
  (let [[notes children] (if (string? (first children))
                           [(first children) (rest children)]
                           [nil children])
        [component src] children]
    [:div.demo-container
     [:div.demo-name name]
     (if notes
       [:div.demo-notes notes]
       "")
     [:div.demo-display component]
     [containers/spoiler {:show-label "Show Code"
                          :hide-label "Hide Code"}
      [code-block (string/triml (pps src))]]]))
