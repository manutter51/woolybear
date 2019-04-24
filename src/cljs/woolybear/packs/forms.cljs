(ns woolybear.packs.forms
  (:require [woolybear.packs.forms.registration-form :as registration-form]
            [re-frame.core :as re-frame]))

;;;
;;; db
;;;

(defn db-init
  []
  {:registration-form (registration-form/db-init)})

;;;
;;; Subscriptions
;;;

(re-frame/reg-sub
  :db/forms
  (fn [db [_]]
    (:forms db)))

