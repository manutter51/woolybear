(ns woolybear.ad.images
  "
  Simple images, including images with placeholders for display while loading.
  "
  (:require [cljs.spec.alpha :as s]
            [woolybear.ad.utils :as adu]))

(s/def :image/src string?)

(s/def :image/options (s/keys :req-un [:image/src]
                              :opt-un [:ad/extra-classes
                                       :ad/subscribe-to-classes]))

(defn image
  "
  Standard image component with support for placeholder graphics to display while
  image is loading. Pass the source URL via the :src key in the opts argument.
  Supports the standard :extra-classes and :subscribe-to-classes. For Bulma classes
  that control image sizes and ratios, see

     https://bulma.io/documentation/elements/image/
  "
  [opts]
  (let [{:keys [src extra-classes subscribe-to-classes]} opts
        classes-sub (adu/subscribe-to subscribe-to-classes)]
    (fn [_]
      (let [dynamic-classes @classes-sub]
        [:figure {:class (adu/css->str :image :wb-image
                                       extra-classes
                                       dynamic-classes)}
         [:img {:src src}]]))))
