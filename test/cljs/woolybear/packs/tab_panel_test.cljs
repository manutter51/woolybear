(ns woolybear.packs.tab-panel-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [woolybear.packs.tab-panel :as sut]))

(deftest test-sub-panel
  (testing "subpanel options"
    (let [r (sut/sub-panel [:foo])]
      (is (= [:div {:class "wb-tab-sub-panel"} [:foo]]
             (r [:foo]))
          "renders correctly when options omitted"))))
