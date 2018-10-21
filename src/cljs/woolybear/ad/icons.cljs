(ns woolybear.ad.icons
  "
  Simple icons based on FontAwesome (free version), suitable for use in buttons,
  dialogs, etc.
  "
  (:require [cljs.spec.alpha :as s]
            [woolybear.ad.utils :as adu]
            [clojure.string :as str]))

;; Wrapper for Font Awesome based icons
;; https://fontawesome.com/icons?d=gallery&m=free

(s/def :icon/icon string?)
(s/def :icon/brand? boolean?)
(s/def :icon/tooltip string?)
(s/def :icon/extra-classes :ad/extra-classes)
(s/def :icon/subscribe-to-classes :ad/subscription)
(s/def :icon/size #{:small :medium :large})
(s/def :icon/on-click :ad/event-dispatcher)

(s/def :icon/opts (s/keys :req-un [:icon/icon]
                          :opt-un [:icon/brand?
                                   :icon/tooltip
                                   :icon/extra-classes
                                   :icon/subscribe-to-classes
                                   :icon/size
                                   :icon/on-click
                                   ]))
(defn icon
  "
  Given a map of options, returns an icon component based on Bulma's
  FontAwesome-based icons. The options are as follows:
  :icon (req)            name of the icon, as specified on the FontAwesome gallery page
  :brand?                if present and true, uses 'fab' icons instead of 'fas'.
  :tooltip               Short tooltip string (for title attribute)
  :extra-classes         extra CSS classes to add to the icon
  :subscribe-to-classes  dynamic CSS classes to add to the icon
  :size                  :small, :medium, :large (or none). Default size is between :small and :medium
  :on-click              re-frame event handler when user clicks on icon
  "
  [opts]
  (if-not (contains? opts :icon)
    (throw (ex-info "Missing :icon/icon parameter." opts)))
  (let [{:keys [icon brand? tooltip classes size on-click extra-classes subscribe-to-classes
                caption subscribe-to-caption]} opts
        icon-class (if (str/starts-with? icon "fa-") icon
                                                     (str "fa-" icon))
        size-class (when size (str "is-" (name size)))
        brand-class (if brand? "fab" "fas")
        fa-size (case size
                  nil :fa-lg
                  :medium :fa-2x
                  :large :fa-3x
                  "")
        fa-classes #{brand-class icon-class fa-size}
        span-attrs (cond-> {:class (adu/css->str :icon size-class classes)}
                           tooltip (assoc :title tooltip)
                           on-click (assoc :on-click (adu/mk-no-default-dispatcher on-click)))
        classes-sub (adu/subscribe-to subscribe-to-classes)
        caption-sub (adu/subscribe-to subscribe-to-caption)]
    (fn [_]
      (let [dynamic-classes @classes-sub
            dynamic-caption @caption-sub]
        [:span span-attrs
         [:i {:class (adu/css->str :wb-icon
                                   fa-classes
                                   extra-classes
                                   dynamic-classes)}]
         (when (or caption dynamic-caption)
           [:span.wb-icon-caption caption dynamic-caption])]))))

(s/fdef icon
  :args (s/cat :opts :icon/opts)
  :ret vector?)
