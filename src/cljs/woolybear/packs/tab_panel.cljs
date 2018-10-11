(ns woolybear.packs.tab-panel
  (:require [re-frame.core :as re-frame]
            [reagent.ratom :as ratom]
            [day8.re-frame.tracing :refer-macros [defn-traced]]
            [cljs.spec.alpha :as s]
            [woolybear.ad.utils :as adu]
            [woolybear.ad.buttons :as buttons]
            [woolybear.ad.containers :as containers]
            [woolybear.tools.interceptors :as interceptors]))

;;; Factory Functions

(defn mk-tab-panel-data
  "
  Set up the data map for a tab-panel/tab-bar combination.
  "
  [path-in-db]
  {:value     nil
   :data-path path-in-db})

;;; Event Handlers
;; Generic event handlers for "self-aware" components that know how
;; to handle their own internal events.

(defn-traced tab-panel-on-click-handler
  "Given the data path to the component data for the tab panel,
  and a new value, update the component data with the new value."
  [db [_ data-path new-value]]
  (assoc-in db (conj data-path :value) new-value))

(re-frame/reg-event-db
  :tab-panel/on-click
  [interceptors/throw-on-nil-db]
  tab-panel-on-click-handler)


;;; Views

(s/def :tab-bar/options (s/keys :req-un [:ad/subscribe-to-component-data]
                                :opt-un [:ad/extra-classes
                                         :ad/subscribe-to-classes]))

(defn prep-buttons
  "Used to map over the buttons in a tab bar, setting up the on-click dispatchers
  appropriately."
  [data-path child]
  (if-not (= (first child) buttons/tab-button)
    child                     ;; if not a tab-button, just pass thru
    (let [[tag opts & more] child
          _ (if-not (map? opts)
              (throw (ex-info "Missing opts map on tab button" child)))
          panel-id (or (:panel-id opts)
                       (throw (ex-info "Missing panel-id on tab button" child)))
          on-click (:on-click child)
          on-click (if on-click
                     (adu/append-to-dispatcher on-click data-path panel-id)
                     [:tab-panel/on-click data-path panel-id])
          on-click (adu/mk-dispatcher on-click)
          opts (assoc opts :on-click on-click)]
      (into [tag opts] more))))

(defn tab-bar
  "
  A component used with a tab-panel to display the row of tabs/buttons above
  the panel.
  "
  [opts & children]
  (let [{:keys [subscribe-to-component-data extra-classes subscribe-to-classes]} opts
        data-sub (adu/subscribe-to subscribe-to-component-data)
        ;; NOTE subscribe-to-classes just gets passed through to the containers/bar component for rendering
        data-path (:data-path @data-sub)
        buttons (mapv (partial prep-buttons data-path) children)]
    (fn [_ & _]
      (let [data @data-sub
            current-panel-id (:value data)]
        (into [containers/bar {:extra-classes           (adu/css+css :wb-tab-bar extra-classes)
                               :ad/subscribe-to-classes subscribe-to-classes}]
              (for [button buttons :let [active? (= current-panel-id
                                                    (adu/get-option button :panel-id))]]
                (if active?
                  ^{:key (str "tab-bar-button-" (adu/get-option :key button) "-" active?)}
                  (adu/add-option button :active? true)
                  button)))))))

(s/fdef tab-bar
  :args (s/cat :opts :tab-bar/options
               :children (s/+ any?)))

(s/def :tab-sub-panel/panel-id keyword?)
(s/def :tab-sub-panel/options (s/keys :req-un [:tab-sub-panel/panel-id]
                                      :opt-un [:ad/extra-classes
                                               :ad/subscribe-to-classes]))

(defn sub-panel
  "
  Child component to be used inside a tab-panel. The `opts` argument must contain
  a :panel-id key whose value is used to distinguish this sub-panel from other
  sub-panels in the same tab-panel. Also accepts the standard options for extra-events
  and subscribe-to-events.
  "
  [opts & _]
  ;; NOTE: the :panel-id value is not used by the sub-panel component directly;
  ;; it is managed by the parent tab-panel.
  (let [{:keys [extra-classes subscribe-to-classes]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [_ & children]
      (let [dynamic-classes @classes-sub]
        [:div {
               :class (adu/css->str :wb-tab-sub-panel
                                    extra-classes
                                    dynamic-classes)}
         (into [containers/v-scroll-pane {:height "100%"}]
               children)]))))

(s/fdef sub-panel
  :args (s/cat :opts (s/? :tab-sub-panel/options)
               :children (s/+ any?))
  :ret vector?)

(defn- attach-subpanel
  "Internal reducing panel used to add sub-panels to a map keyed by :panel-id"
  [m panel]
  (if-not (= (first panel) sub-panel)
    (throw (ex-info "All children of a tab-panel must be sub-panels" panel)))
  (if-let [id (:panel-id (second panel))]
    (assoc m id panel)
    (throw (ex-info "Missing :panel-id in sub-panel" panel))))

(s/def :tab-panel/subscribe-to-selected-tab :ad/subscription)
(s/def :tab-panel/options (s/keys :req-un [:tab-panel/subscribe-to-selected-tab]
                                  :opt-un [:ad/extra-classes
                                           :ad/subscribe-to-classes]))

(defn tab-panel
  "
  A panel component that contains multiple panels, only one of which is visible at any given
  time. Takes the standard :extra-classes and :subscribe-to-classes keys, plus a specific
  :subscribe-to-selected-tab key used to select the sub-panel to display. Each sub-panel must
  have a :panel-id key whose value is unique relative to other sub-panels in the same tab-panel.
  Setting the :subscribe-to-selected-tab subscription value to this :panel-id key will cause
  that sub-panel to be the one that is displayed in the tab panel.
  "
  [opts & children]
  (let [{:keys [extra-classes subscribe-to-classes
                subscribe-to-selected-tab]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)
        selected-tab-sub (adu/subscribe-to subscribe-to-selected-tab)
        panels (reduce attach-subpanel {} children)
        ]
    (fn [_ & _]
      (let [dynamic-classes @classes-sub
            ;; If selected-tab-sub returns a nil, use arbitrary key
            selected-tab (or @selected-tab-sub
                             (first (keys panels)))
            current-panel (get panels selected-tab)]
        [:div {:class (adu/css->str :wb-tab-panel
                                    :container
                                    extra-classes
                                    dynamic-classes)}
         ^{:key selected-tab}
         current-panel]))))

(s/fdef tab-panel
  :args (s/cat :opts (s/? :tab-panel/options)
               :children (s/+ any?))
  :ret vector?)
