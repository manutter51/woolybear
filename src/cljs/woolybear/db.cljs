(ns woolybear.db
  (:require [woolybear.ad.catalog :as ad-catalog]))

(def default-db
  {:name "re-frame"
   :ad-catalog ad-catalog/init-db})
