(ns woolybear.packs.tab-panel
  (:require [re-frame.core :as re-frame]
            [reagent.ratom :as ratom]
            [cljs.spec.alpha :as s]
            [woolybear.ad.utils :as adu]
            [woolybear.ad.buttons :as buttons]
            [woolybear.ad.containers :as containers]
            [woolybear.tools.interceptors :as interceptors]))

;;; Event Handlers
;; Generic event handlers for "self-aware" components that know how
;; to handle their own internal events.

(defn tab-panel-on-click-handler
  "Given the data path to the component data for the tab panel,
  and a new value, update the component data with the new value."
  [db [_ data-path new-value]]
  (assoc-in db data-path new-value))

(re-frame/reg-event-db
  :tab-panel/on-click
  [interceptors/throw-on-nil-db]
  tab-panel-on-click-handler)


(s/def :tab-sub-panel/options (s/keys :opt-un [:ad/extra-classes
                                               :ad/subscribe-to-classes]))

(defn sub-panel
  "
  Child component to be used inside a tab-panel. One of the child elements should
  be a woolybear.ad.buttons/tab-button, which will be moved into a tab bar at the
  top of the tab panel. Subscriptions and click handlers will automatically be
  added so that clicking on the tab button activates this panel and hides the other
  panels in this tab-panel. Accepts the standard options for extra-events and
  subscribe-to-events.
  "
  [& args]
  (let [[{:keys [extra-classes subscribe-to-classes]} _] (adu/extract-opts args)
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [& args]
      (let [[_ children] (adu/extract-opts args)
            dynamic-classes @classes-sub]
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

(defn- tab-button?
  [elem]
  (= (first elem) buttons/tab-button))

(defn- extract-tab-button
  "Given the child of a tab-panel, determine if it is a tab button or a sub-panel.
  If it is a tab button, return it inside a 2-item vector with nil as the second
  item. Otherwise extract the tab button from the sub-panel, and return a 2-item
  vector where the first item is the tab button and the 2nd item is everything
  else. Note that any additional tab buttons will be ignored."
  [child]
  (if (tab-button? child)
    [child nil]
    (let [[_ grand-kids] (adu/extract-opts (rest child))
          {buttons true non-buttons false} (group-by tab-button? grand-kids)
          sub-panel (adu/replace-children child non-buttons)]
      [(first buttons) sub-panel])))

(defn- link-button->panel
  "Given a tab button and a (possibly nil) sub-panel, create a unique ID and
  assign it to a :tab-panel/pair-id option on both the button and the sub-panel,
  assuming the latter is not nil."
  [button panel]
  (if (nil? panel)
    [button panel]
    (let [id (adu/unique-id)]
      [(adu/add-option button :tab-panel/pair-id id)
       (adu/add-option panel :tab-panel/pair-id id)])))

(defn- mk-click-handler
  "
  Given the state data for a tab-panel component, return a click-handler function that
  will update the state correctly, both when storing state in the app-db and when it
  is local. Call once for each tab-button, passing in the :tab-panel/pair-id for that
  button as the item-id.
  "
  [state-data item-id]
  (let [{:keys [data-path local-state]} state-data]
    (if data-path
      (let [data-path (conj data-path :current-panel)]
        (fn [_] (re-frame/dispatch [:tab-panel/on-click data-path item-id])))
      ;; else if we're local, just update local state
      (fn [_] (swap! local-state assoc :current-panel item-id)))))

(s/def :tab-panel/options (s/keys :opt-un [:ad/extra-classes
                                           :ad/subscribe-to-classes
                                           :ad/subscribe-to-component-data]))

(defn- build-tab-panel
  "
  Given the child elements and state data of a tab-panel, iterate and set up the
  click handlers for each tab button that is linked to a tab panel. Returns a map
  with the keys :buttons (a vector of buttons to put in the tab bar) and :panels
  (a map of sub-panels keyed by their :tab-panel/pair-id).
  "
  [children state-data]
  (reduce (fn [result child]
            (let [;; if it's a sub-panel with a tab-button in it, pull out the button
                  ;; otherwise it's a button, just get it and set `panel` to nil
                  [button panel] (extract-tab-button child)
                  ;; if the panel is not nil, assign pair ids to both panel and button
                  ;; each button/panel pair will be given the same pair-id
                  [button panel] (link-button->panel button panel)
                  ;; get the pair id (it will be in the opts which will be the 2nd item in the button vector)
                  pair-id (:tab-panel/pair-id (second button))
                  ;; if the pair-id exists, we need to set up a click handler on the button
                  click-handler (when pair-id (mk-click-handler state-data pair-id))
                  button (if click-handler
                           (adu/add-option button :on-click click-handler)
                           button)]
              (-> result
                  (update :buttons conj button)
                  (update :panels assoc pair-id panel))))
          {:buttons []
           :panels  {}}
          children))

(defn tab-panel
  "
  A tabbed panel UI component with a tab bar at the top and a content area below.
  The tab bar contains buttons that usually activate specific sub-panels (though
  you can also include general purpose buttons that aren't linked to any specific
  sub-panel). You can put two kinds of child elements inside a tab-panel. The
  most common type will be a woolybear.ad.tab-panel/sub-panel, which contains
  whatever you want to display in the content area whenever that panel is the
  active panel. Each sub-panel should contain its own woolybear.ad.buttons/tab-button,
  which will be automatically moved up into the tab bar. (It's inside the sub-
  panel so that the tab-panel knows which button belongs to which sub-panel.)
  The other type of child element you can pass to a tab-panel is a tab-button
  outside of any sub-panel. This allows you to add special-purpose buttons
  that are not linked to any sub-panel. The tab-panel component supports the
  standard extra-classes, subscribe-to-classes options, and the subscribe-to-
  component-data option. If you need to have other components know which tab
  is currently active, passing in a subscribe-to-component-data value will
  tell the tab panel to use the app-db for its state management, otherwise it
  will use an internal Reagent atom.
  "
  [& args]
  (let [[opts children] (adu/extract-opts args)
        {:keys [extra-classes subscribe-to-classes
                subscribe-to-component-data]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)
        local-state (when-not subscribe-to-component-data
                      (ratom/atom {:current-panel nil}))
        _ (swap! local-state assoc :local-state local-state)
        data-sub (if local-state
                   local-state
                   (adu/subscribe-to subscribe-to-component-data))
        {:keys [buttons panels]} (build-tab-panel children @data-sub)
        buttons (map (fn [b i] (adu/add-option b :key i)) buttons (range))
        ]
    (fn [& args]
      (let [dynamic-classes @classes-sub
            component-data @data-sub
            ;; current panel id is whatever is in the component state (default: first panel)
            current-panel-id (or (:current-panel component-data)
                                 (first (sort (keys panels))))
            current-panel (get panels current-panel-id)]
        [:div {:class (adu/css->str :wb-tab-panel
                                    :container
                                    extra-classes
                                    dynamic-classes)}
         (into [containers/bar {:extra-classes :wb-tab-panel-tab-bar}]
               (for [button buttons :let [active? (= current-panel-id
                                                     (adu/get-option button :tab-panel/pair-id))]]
                 (if active?
                   ^{:key (str "tab-panel-tab-button-" (adu/get-option :key button) "-" active?)}
                   (adu/add-option button :active? true)
                   button)))
         [:div {:class (adu/css->str :wb-tab-panel-subpanel)}
          current-panel]]))))

(s/fdef tab-panel
  :args (s/cat :opts (s/? :tab-panel/options)
               :children (s/+ any?))
  :ret vector?)
