(ns woolybear.ad.utils
  "Specs and utility functions for use with AD components."
  (:require [cljs.spec.alpha :as s]
            [clojure.string :as string]
            [re-frame.core :as re-frame]))

;; A subscription argument can be passed in via one of three ways:
;; as an atom/ratom directly, as a function that returns a subscription,
;; or as a vector that can be passed to a re-frame/subscribe call. Use
;; the subscribe-to function, below, to convert this value into a valid
;; ratom/subscription that can be dereferenced at runtime.
(s/def :ad/subscription? (s/or :ratom #(= (type %) reagent.ratom/RAtom)
                               :atom #(= (type %) cljs.core/Atom)
                               :fn fn?
                               :vec vector?))

;; Some components let you pass in extra classes. By convention, we
;; specify CSS classes as either keywords or sets of keywords, but
;; they can also be strings or sets of strings or collections of either
;; strings or keywords. (The reason for preferring keywords is sheer
;; laziness--one less character to type!)
(s/def :ad/extra-classes (s/or :set set?
                               :coll coll?
                               :kw keyword?
                               :str string?))

(defn extract-opts
  "Given a vector of arguments, checks to see if the first argument is a map of options.
  Returns a 2-element vector containing the options (if any) and a vector of the remaining
  arguments.

  Example:
      (defn foo [& args]
        (let [[opts children] (extract-opts args)
              ...]
          ...))"
  [args]
  (if (map? (first args))
    [(first args) (rest args)]
    [nil args]))

(def function-type (type #()))

(defn subscribe-to
  "Given a value that may be either a re-frame subscription ratom, or a vector
  specifying a re-frame subscription, return a valid re-frame subscription ratom.
  The value can also be a zero-arity function that returns a subscription."
  [sub]
  (condp = (type sub)
    reagent.ratom/RAtom sub
    cljs.core/Atom sub
    function-type  (sub)
    ;; else
    (re-frame/subscribe sub)))

(defn subscribe2
  [sub]
  (prn {:sub sub}))

(prn {:test (subscribe-to (atom true))})

(defn- to-name
  "Given a value that may be a string, symbol, or keyword, return the
  name of the value, or the original value if it's not a symbol or keyword."
  [v]
  (cond
    (keyword? v) (name v)
    (symbol? v) (name v)
    :else v))

(defn css->str
  "Given one or more css classes, return a string of space-separated class
  names suitable for use as the :class value on a hiccup DOM element."
  [& classes]
  (->> classes
       (mapv #(if (set? %) (into [] %) %)) ;; flatten doesn't flatten sets
       flatten
       (remove nil?)          ;; filter out nils
       (map to-name)
       (into #{})             ;; make css classes unique
       (string/join " ")))
