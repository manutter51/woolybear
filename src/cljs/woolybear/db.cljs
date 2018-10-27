(ns woolybear.db
  (:require [woolybear.ad.catalog :as ad-catalog]
            [woolybear.packs.forms :as forms]))

(def default-db
  {:name       "re-frame"
   :ad-catalog ad-catalog/init-db
   :forms      (forms/db-init)})
