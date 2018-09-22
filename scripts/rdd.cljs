(ns rdd)

;; REPL-Driven Development

(require '[cljs.spec.alpha :as s])
(require '[clojure.string :as string])
(require '[re-frame.core :as re-frame])
(require '[reagent.ratom :as ra])
(require '[woolybear.ad.containers :as ct])

(defn get-args
  [& args]
  (prn args))

(into #{}
      (flatten '(:foo #{:bar} [:baz [:quux]])))

(def args '(:foo #{:bar} [:baz [:quux] nil]))

