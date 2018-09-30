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
(s/def :ad/subscription (s/or :ratom #(= (type %) reagent.ratom/RAtom)
                              :atom #(= (type %) cljs.core/Atom)
                              :fn fn?
                              :vec vector?))

;; An event handler argument can be passed in as either a standard
;; re-frame event vector, or as a function (i.e. for running tests
;; and/or for display in the AD catalog). Call the adu/mk-handler fn,
;; below, to convert this into a handler function as follows: if the
;; value is a vector, make a function that appends the JS event to
;; the vector and dispatches the event, or if the value is a function,
;; just return the function, which is assumed to take the JS event as
;; its sole argument. SEE ALSO the adu/mk-no-default-handler function
;; below to make an event handler that calls stopPropagation and
;; preventDefault on the event before passing it to the event handler.
(s/def :ad/event-handler (s/or :vec vector?
                               :fn fn?))

;; Some components let you pass in extra classes. By convention, we
;; specify CSS classes as either keywords or sets of keywords, but
;; they can also be strings or sets of strings or collections of either
;; strings or keywords. (The reason for preferring keywords is sheer
;; laziness--one less character to type!)
(s/def :ad/extra-classes (s/or :set set?
                               :coll coll?
                               :kw keyword?
                               :str string?))

;; Some components let you pass in CSS classes that can change at
;; render time. Pass in a subscription that returns the current
;; class or set of classes to render
(s/def :ad/subscribe-to-classes :ad/subscription)

;; Some components are meant to be used as a direct subcomponent of
;; a parent that controls whether or not they are visible, enabled,
;; or otherwise active. The parent will pass in a boolean value to
;; the :ad/active? option to either enable or disable it.
(s/def :ad/active? boolean?)

;; Some components have internal data that they need to track and manage
;; without "manual intervention". The :subscribe-to-component-data
;; option lets you tell the component what to subscribe to in order to
;; retrieve its current state
(s/def :ad/subscribe-to-component-data :ad/subscription)

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

(defn get-option
  "Given a hiccup-style vector representing a DOM element, and a key,
  return the value (if any) from the opts map of the element. Returns
  woolybear.ad.utils/not-found if the key does not exist in the options."
  [elem k]
  (get (second elem) k ::not-found))

(defn add-option
  "Given a hiccup-style vector representing a DOM element, and a key/value pair,
  add the k/v pair to the element's opts map, creating that map if needed"
  [elem k v]
  (let [[opts kids] (extract-opts (rest elem))
        opts (or opts {})
        result [(first elem) (assoc opts k v)]]
    (into result kids)))

(defn replace-children
  "Given a hiccup-style vector representing a DOM element with an optional
  opts map, and a list of new children, replace the element's children by
  the new children, preserving any opts value that may be present."
  [elem new-kids]
  (let [[opts _] (extract-opts (rest elem))]
    (if opts
      (into [(first elem) opts] new-kids)
      (into [(first elem)] new-kids))))

(def function-type (type #()))
(def always-nil (atom nil))

(defn subscribe-to
  "Given a value that may be either a re-frame subscription ratom, or a vector
  specifying a re-frame subscription, return a valid re-frame subscription ratom.
  If the value is nil, returns an atom that always dereferences to nil.
  The value can also be a zero-arity function that returns a subscription, in
  which case subscribe-to will return the result of calling that function."
  [sub]
  (condp = (type sub)
    reagent.ratom/RAtom sub
    cljs.core/Atom sub
    function-type (sub)
    nil always-nil
    ;; else
    (re-frame/subscribe sub)))

(defn mk-handler
  "Given an :ad/event-handler argument, return a handler function suitable
  for passing to (for example) the :on-click attribute. If the argument is
  a function, returns the function (which is assumed to take a single argument,
  the JS event that triggered the handler). If the :ad/event-handler is a
  vector, returns a function that appends the JS event to the vector, and
  calls the standard re-frame dispatch function."
  [handler]
  (cond
    (fn? handler) handler
    (vector? handler) (fn [e]
                        (let [with-e (conj handler e)]
                          (re-frame/dispatch with-e)))
    :else (throw
            (ex-info "First argument to woolybear.ad.utils/mk-handler must be a vector or fn."
                     handler))))

(defn mk-no-default-handler
  "Same as mk-handler, except returns a handler that calls stopPropagate and
  preventDefault on the JS event before dispatching."
  [handler]
  (let [handler (mk-handler handler)]
    (fn [e]
      (let [e (.. e
                  preventDefault
                  stopPropagation)]
        (handler handler e)))))

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

(def unique-id
  (let [counter (atom 0)]
    (fn []
      (swap! counter inc))))
