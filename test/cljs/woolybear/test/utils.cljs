(ns woolybear.test.utils
  (:require [clojure.test :as t]
            [clojure.string :as string]
            [cljs.pprint :as pp]))

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
