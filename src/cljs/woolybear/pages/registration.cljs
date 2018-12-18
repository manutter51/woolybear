(ns woolybear.pages.registration
  (:require [woolybear.ad.layout :as layout]
            [woolybear.packs.forms.registration-form :as registration]
            [woolybear.ad.images :as images]))

(defn page
  []
  [layout/page
   [layout/page-header
    [images/image {:src           "/img/logo.png"
                   :extra-classes #{:width-20 :is-4by1 :is-pulled-left}}]
    [layout/padded
     [layout/page-title "Sign up for 'pillar"]
     #_[layout/text-block "To buy and sell pedigreed Woolybear Caterpillars, you need to create
    an account. First name, last name, and nickname fields are optional."]]]

   [layout/page-body
    [layout/centered {:extra-classes :width-50}
     [registration/registration-form]]]])
