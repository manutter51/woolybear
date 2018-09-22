(ns woolybear.ad.containers
  "
  Container components are the next step up from simple layout components. Container
  components take subscriptions and may fire events.
  "
  (:require [re-frame.core :as re-frame]
            [cljs.spec.alpha :as s]
            [woolybear.ad.utils :as adu]))

(s/def :container/subscribe-to-visible? :ad/subscription?)
(s/def :shy-block/options (s/keys :req-un [:container/subscribe-to-visible?]
                                  :opt-un [:ad/extra-classes]))

(defn shy-block
  "A container that may or may not be visible, depending on the current value of
  its `visible?` subscription. If you wish you can pass in extra CSS classes via
  the :extra-classes option."
  [opts & _]
  (let [{:keys [subscribe-to-visible?]} opts
        visible?-sub (adu/subscribe-to subscribe-to-visible?)]
    (fn [opts & children]
      (let [{:keys [extra-classes]} opts
            visible? @visible?-sub
            vis-class (if visible? :visible :hidden)]
        (into [:div {:class (adu/css->str :shy vis-class extra-classes)}]
              children)))))
