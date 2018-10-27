(ns woolybear.packs.forms.registration-form
  (:require [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [defn-traced]]
            [vimsical.re-frame.cofx.inject :as inject]
            [woolybear.ad.buttons :as buttons]
            [woolybear.ad.containers :as containers]
            [woolybear.tools.interceptors :as interceptors]
            [woolybear.ad.layout :as layout]
            [woolybear.ad.forms :as forms]))

;;;
;;; DB initialization
;;;

(defn db-init
  "Return initialization data for registration form."
  []
  {:last-name  ""
   :first-name ""
   :email      ""
   :nickname   ""
   :password   ""
   :confirm    ""})

;;;
;;; Utility fns
;;;

(defn path-to
  "Given the key for a form field, return the complete component data path
  for that field."
  [field]
  [:forms :registration-form field])

(defn mk-field-attributes
  "Given the key for a form field, return the common set of form field attributes
  for the text-input component. Sets :type to :password for :password and :confirm
  fields."
  [field]
  (let [field-name (name field)
        attrs
        (cond-> {:component-data-path         (path-to field)
                 :subscribe-to-component-data [(keyword "registration-form" field-name)]
                 :id                          field-name
                 :name                        field-name
                 :on-enter-key                [:registration-form/submit-if-valid]}

                (contains? #{:email :password :confirm} field)
                (assoc :subscribe-to-errors [(keyword "registration-form" (str field-name "-errors"))])

                (contains? #{:password :confirm} field) (assoc :type :password))]
    #_(cljs.pprint/pprint {:field field
                           :attrs attrs})
    attrs))

;;;
;;; Subscriptions
;;;

(re-frame/reg-sub
  :forms/registration-form
  :<- [:db/forms]
  (fn [forms]
    (:registration-form forms)))

;; TODO make a macro to automate form-field registrations
;; (adu/register-fields <form-key> <field-key> ... <field-key)

(re-frame/reg-sub
  :registration-form/last-name
  :<- [:forms/registration-form]
  (fn [reg-form]
    (:last-name reg-form)))

(re-frame/reg-sub
  :registration-form/first-name
  :<- [:forms/registration-form]
  (fn [reg-form]
    (:first-name reg-form)))

(re-frame/reg-sub
  :registration-form/email
  :<- [:forms/registration-form]
  (fn [reg-form]
    (:email reg-form)))

(re-frame/reg-sub
  :registration-form/nickname
  :<- [:forms/registration-form]
  (fn [reg-form]
    (:nickname reg-form)))

(re-frame/reg-sub
  :registration-form/password
  :<- [:forms/registration-form]
  (fn [reg-form]
    (:password reg-form)))

(re-frame/reg-sub
  :registration-form/confirm
  :<- [:forms/registration-form]
  (fn [reg-form]
    (:confirm reg-form)))

;;; Validations: email, password, and confirm are required, passwords must match

(re-frame/reg-sub
  :registration-form/email-errors
  :<- [:registration-form/email]
  (fn [email-data]
    (let [{:keys [value visited?]} email-data]
      (when visited?
        (cond
          (nil? (seq value)) [{:msg "Email is required" :class :error}]
          (nil? (re-find #".+@.+\..+" (or value ""))) [{:msg "Unrecognized email format" :class :warning}]
          :else nil)))))

(re-frame/reg-sub
  :registration-form/password-errors
  :<- [:registration-form/password]
  :<- [:registration-form/confirm]
  ;; Gotcha to watch out for: if you have more than one input signal, first arg is a vector of the values!
  ;; If you do (fn [password-data confirm-data] ...) it won't work
  (fn [[password-data confirm-data]]
    (let [pw-visited? (:visited? password-data)
          cf-visited? (:visited? confirm-data)
          pw-value (:value password-data)
          cf-value (:value confirm-data)
          mismatch? (and pw-visited? cf-visited?
                         (seq pw-value) (seq cf-value)
                         (not= pw-value cf-value))
          missing? (and pw-visited? (nil? (seq pw-value)))]
      (cond
        missing? [{:msg "You must provide a password" :class :error}]
        mismatch? [{:msg "Password and Confirm must match" :class :error}]
        :else nil))))

(re-frame/reg-sub
  :registration-form/confirm-errors
  :<- [:registration-form/password]
  :<- [:registration-form/confirm]
  (fn [[password-data confirm-data]]
    (let [pw-visited? (:visited? password-data)
          cf-visited? (:visited? confirm-data)
          pw-value (:value password-data)
          cf-value (:value confirm-data)
          mismatch? (and pw-visited? cf-visited?
                         (seq pw-value) (seq cf-value)
                         (not= pw-value cf-value))
          missing? (and cf-visited? (nil? (seq cf-value)))]
      (cond
        missing? [{:msg "You must provide a confirmation" :class :error}]
        mismatch? [{:msg "Password and Confirm must match" :class :error}]
        :else nil))))

;;; Overall form validation -- returns true when form is ok to submit, or false if any errors

(re-frame/reg-sub
  :registration-form/required-fields-present?
  :<- [:registration-form/email]
  :<- [:registration-form/password]
  :<- [:registration-form/confirm]
  ;; Here we take advantage of the fact that multiple input signals come in as a vector of values
  (fn [required-fields]
    (every? :visited? required-fields)))

(re-frame/reg-sub
  :registration-form/has-no-errors?
  :<- [:registration-form/email-errors]
  :<- [:registration-form/password-errors]
  :<- [:registration-form/confirm-errors]
  (fn [fields-to-check]
    ;; inner predicate to check that "errors" contains no values of class :error
    (let [has-no-errors? (fn [errors] (-> (filter #(= :error (:class %)) errors)
                                          seq
                                          nil?))]
      (every? #(or (nil? %) (has-no-errors? %)) fields-to-check))))

(re-frame/reg-sub
  :registration-form/disabled?
  :<- [:registration-form/required-fields-present?]
  :<- [:registration-form/has-no-errors?]
  (fn [[required-fields? no-errors?]]
    (not (and required-fields? no-errors?))))

;;;
;;; Event Handlers
;;;

(defn-traced handle-submit-if-valid
  "Event handler function to check if registration form contains valid data,
  and if so, to submit the form."
  [{:keys [db disabled?] :as ctx} [_]]
  ;; TODO replace this with real submit code
  (if disabled?
    (println "Form is disabled, event silently ignored")
    (js/alert "This is where the form contents would be submitted to the backend, if we had one.")))

(re-frame/reg-event-fx
  :registration-form/submit-if-valid
  [interceptors/throw-on-nil-db
   ;; need to inject-sub the form-valid? sub
   (re-frame/inject-cofx ::inject/sub [:registration-form/disabled?])]
  handle-submit-if-valid)

(defn-traced handle-cancel-registration
  "Event handler function to clear the form and navigate back to the login screen."
  [{:keys [db]} [_]]
  (let [db (assoc-in db [:forms :registration-form] (db-init))]
    {:dispatch [:navigation/go-to-page :home]}))

(re-frame/reg-event-fx
  :registration-form/cancel-registration
  handle-cancel-registration)

;;;
;;; View
;;;

(defn registration-form
  "Simple registration form for new users."
  []
  (let [fields {:last-name  "Last Name"
                :first-name "First Name"
                :email      "Email Address"
                :nickname   "Nickname"
                :password   "Password"
                :confirm    "Confirm Password"}
        required? #{:email :password :confirm}]
    [layout/zero-pad {:extra-classes :container}
     (->
       (into [layout/padded]
             (for [fld (keys fields)]
               [forms/field-group {:extra-classes :is-horizontal}
                [forms/label {:extra-classes :field-label
                              :required?     (required? fld)
                              :for           (name fld)} (fld fields)]
                [forms/text-input (mk-field-attributes fld)]]))
       (conj [layout/caption [:span.is-required-mark "*"] " Required field"])
       (conj [containers/bar {:extra-classes #{:field :is-grouped :is-grouped-right}}
              [buttons/cancel-button {:on-click [:registration-form/cancel-registration]}]
              [buttons/save-button {:on-click               [:registration-form/submit-if-valid]
                                    :subscribe-to-disabled? [:registration-form/disabled?]}]]))]))
