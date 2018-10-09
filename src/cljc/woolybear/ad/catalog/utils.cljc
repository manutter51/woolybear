(ns woolybear.ad.catalog.utils
  (:require [clojure.string :as string]))

(declare parse)

(defn pad
  [n]
  (apply str (repeat n " ")))

(defn with-rows
  [coll]
  (map-indexed (fn [i item] [i item]) coll))

(defn parse-map
  [m indent row]
  (let [k-indent (apply max (doall (map #(count (str %)) (keys m))))
        prefix (if (pos? row)
                 (str "\n" (pad indent) "{")
                 "{")
        pairs (doall (for [[inner-row [k v]] (with-rows m)
                           :let [v (parse v (+ indent k-indent 3) inner-row)]]
                       (str k " " v)))]
    (str prefix (string/join (str "\n" (pad (inc indent))) pairs) "}")))

(defn parse-children
  [children indent]
  (let [parsed-children (doall (for [[row child] (with-rows children)]
                                 (parse child indent row)))]
    (string/join " " parsed-children)))

(defn parse-vector
  [vct indent row]
  (str (when (pos? row)
         (str "\n" (pad indent)))
       "[" (parse-children vct (inc indent)) "]"))

(defn parse-seq
  [sq indent row]
  (prn {:parse-seq sq :indent indent})
  (str (when (pos? row)
         (str "\n" (pad indent)))
       "(" (parse-children sq (inc indent)) ")"))

(defn parse
  [v & [indent row]]
  (let [indent (or indent 0)
        row (or row 0)]
    (cond
      (string? v) (pr-str v)
      (keyword? v) (str v)
      (char? v) (pr-str v)
      (symbol? v) (pr-str v)
      (map? v) (parse-map v (+ 2 indent) row)
      (vector? v) (parse-vector v (+ 2 indent) row)
      (seq? v) (parse-seq v (+ 2 indent) row)
      :else (pr-str v))))

(defmacro src->str
  [src]
  (parse src))

(defmacro demo
  [name & children]
  (let [[notes# children#] (if (string? (first children))
                             [(first children) (rest children)]
                             [nil children])
        src# (if (string? (first children))
               (parse (second children))
               (parse (first children)))]
    `[:div.demo-container
      [:div.demo-name ~name]
      ~(if notes#
         [:div.demo-notes notes#]
         "")
      [:div.demo-display ~@children#]
      [woolybear.ad.containers/spoiler {:show-label "Show Code"
                                        :hide-label "Hide Code"}
       [woolybear.ad.catalog.utils/code-block ~src#]]]))
