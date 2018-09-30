(ns woolybear.tools.interceptors
  "
  Define the interceptors to use with reg-event-db and reg-event-fx so that we can have
  consistent and safe event handlers.
  "
  (:require [re-frame.core :as re-frame]))

;; SEE ALSO https://github.com/Day8/re-frame/blob/master/docs/FAQs/GlobalInterceptors.md

(def throw-on-nil-db
  "Throws an exception if the handler returns nil as the value of the db. Catches a
  not-uncommon bug that produces some weird symptoms when undetected."
  (re-frame/after
    (fn [context]
      (let [db (get-in context [:effects :db])
            handler-name (get-in context [:coeffects :event] "Unknown")]
        (when (nil? db)
          (throw (ex-info "Function handler returned nil" {:handler handler-name})))))))
