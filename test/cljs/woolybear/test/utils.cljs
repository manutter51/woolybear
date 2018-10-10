(ns woolybear.test.utils
  (:require [clojure.test :as t]
            [clojure.string :as string]
            [cljs.pprint :as pp]
            [clojure.walk :as w]))

(defn classes-match?
  "Given a set of expected classes and a hiccup DOM structure,
  returns true if the classes specified on the DOM structure match
  the list of expected classes, independent of order. The expected
  classes should be given as a set of keywords."
  [classes elem]
  (let [raw-classes (:class (second elem))
        found-classes (->> (string/split raw-classes #" ")
                           (map keyword)
                           (into #{}))]
    (when-not (= classes found-classes)
      (pp/pprint {:expected-classes (map name classes)
                  :actual-classes (map name found-classes)}))
    (= classes found-classes)))

(def fake-event
  #js {"stopPropagation" (fn [] fake-event)
       "preventDefault" (fn [] fake-event)})

(defn- run-dispatchers
  "Called by realize-dispatchers to look at one specific element or attribute map. If item is an
  attribute map, runs any function values in the map, passing in fake-event as the sole argument.
  See docstring for realize-dispatchers function."
  [item]
  (if-not (map? item)
    item
    (reduce (fn [m k]
              (let [v (k item)
                    v (if (fn? v)
                        (v fake-event)
                        v)]
                (assoc m k v)))
            item
            (keys item))))

(defn realize-dispatchers
  "Recursively evaluates a Hiccup-flavored DOM structure. If any element has an attribute
  map, scans all the values looking for function values. If function values are found,
  runs the function, passing in a fake event as the sole argument. For testing purposes
  only. Functions should return strings or keywords for easier testing."
  [elem]
  (w/prewalk (fn [e] (run-dispatchers e)) elem))

