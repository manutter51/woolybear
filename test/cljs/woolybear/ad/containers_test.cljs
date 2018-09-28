(ns woolybear.ad.containers-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [woolybear.test.utils :as wtu]
            [woolybear.ad.containers :as sut]))

(deftest shy-block-test

  (testing "Rendering the :active? option."
    (let [r (sut/shy-block {:active? true} [:foo])]
      (is (wtu/classes-match? #{:wb-shy :visible}
                              (r {:active? true} [:foo]))
          "renders classes correctly when visible")
      (is (wtu/classes-match? #{:wb-shy :hidden}
                              (r {:active? false} [:foo]))
          "renders classes correctly when hidden")))

  (testing "Rendering the :subscribe-to-classes option."
    (let [cl (atom #{:foo})
          opts {:active? true
                :subscribe-to-classes cl}
          r (sut/shy-block opts [:foo])]
      (is (= (r opts [:foo])
               [:div {:class "wb-shy visible foo"} [:foo]])
            "renders correctly with dynamic classes")
      (reset! cl #{:bar :baz})
      (is (= (r opts [:foo])
               [:div {:class "wb-shy visible baz bar"} [:foo]])
            "renders correctly dynamic classes have changed")))

  (testing "Rendering the :extra-classes option."
    (let [opts {:active? true
                :extra-classes :foo}
          r (sut/shy-block opts [:foo])]
      (is (= (r opts [:foo])
             [:div {:class "wb-shy visible foo"} [:foo]])
          "renders correctly with extra classes")
      (is (= (r {:active? true
                 :extra-classes :bar} [:foo])
             [:div {:class "wb-shy visible foo"} [:foo]])
          "does not change classes at render time if extra-classes have changed"))))

(deftest scroll-pane-header-test
  (testing "Rendering with no opts"
    (let [r (sut/scroll-pane-header 1 2 3)
          result (r 1 2 3)]
      (is (= [:div {:class "wb-scroll-pane-header"} 1 2 3]
             result)
          "renders child elements correctly when no opts given.")))

  (testing "Rendering with :extra-classes option"
    (let [opts {:extra-classes :foo}
          r (sut/scroll-pane-header opts 1 2 3)
          result (r opts 1 2 3)]
      (is (= [:div {:class "wb-scroll-pane-header foo"} 1 2 3]
             result)
          "renders extra classes correctly"))
    (let [opts {:extra-classes :foo}
          r (sut/scroll-pane-header opts 1 2 3)
          result (r {:extra-classes :bar} 1 2 3)]
      (is (= [:div {:class "wb-scroll-pane-header foo"} 1 2 3]
             result)
          "renders extra classes correctly despite changes at render-time.")))

  (testing "Rendering with :subscribe-to-classes option"
    (let [cls (atom :foo)
          opts {:subscribe-to-classes cls}
          r (sut/scroll-pane-header opts 1 2 3)
          result (r opts 1 2 3)]
      (is (= [:div {:class "wb-scroll-pane-header foo"} 1 2 3]
             result)
          "renders extra classes correctly"))
    (let [cls (atom :foo)
          opts {:subscribe-to-classes cls}
          r (sut/scroll-pane-header opts 1 2 3)
          _ (reset! cls :bar)
          result (r opts 1 2 3)]
      (is (= [:div {:class "wb-scroll-pane-header bar"} 1 2 3]
             result)
          "renders extra classes correctly including changes at render-time."))))

(deftest scroll-pane-footer-test
  (testing "Rendering with no opts"
    (let [r (sut/scroll-pane-footer 1 2 3)
          result (r 1 2 3)]
      (is (= [:div {:class "wb-scroll-pane-footer"} 1 2 3]
             result)
          "renders child elements correctly when no opts given.")))

  (testing "Rendering with :extra-classes option"
    (let [opts {:extra-classes :foo}
          r (sut/scroll-pane-footer opts 1 2 3)
          result (r opts 1 2 3)]
      (is (= [:div {:class "wb-scroll-pane-footer foo"} 1 2 3]
             result)
          "renders extra classes correctly"))
    (let [opts {:extra-classes :foo}
          r (sut/scroll-pane-footer opts 1 2 3)
          result (r {:extra-classes :bar} 1 2 3)]
      (is (= [:div {:class "wb-scroll-pane-footer foo"} 1 2 3]
             result)
          "renders extra classes correctly despite changes at render-time.")))

  (testing "Rendering with :subscribe-to-classes option"
    (let [cls (atom :foo)
          opts {:subscribe-to-classes cls}
          r (sut/scroll-pane-footer opts 1 2 3)
          result (r opts 1 2 3)]
      (is (= [:div {:class "wb-scroll-pane-footer foo"} 1 2 3]
             result)
          "renders extra classes correctly"))
    (let [cls (atom :foo)
          opts {:subscribe-to-classes cls}
          r (sut/scroll-pane-footer opts 1 2 3)
          _ (reset! cls :bar)
          result (r opts 1 2 3)]
      (is (= [:div {:class "wb-scroll-pane-footer bar"} 1 2 3]
             result)
          "renders extra classes correctly including changes at render-time."))))

(deftest v-scroll-pane-test

  (testing "with no header or footer"

    (testing "Rendering with no opts"
      (let [r (sut/v-scroll-pane [:foo [:bar]] [:baz {}])
            result (r [:foo [:bar]] [:baz {}])]
        (is (= [:div {:class "wb-v-scroll-pane-container"}
                [:div.wb-v-scroll-pane-overflow
                 [:foo [:bar]] [:baz {}]]]
               result)
            "renders child elements correctly when no opts given.")))

    (testing "Rendering with :extra-classes option"
      (let [opts {:extra-classes :foo}
            r (sut/v-scroll-pane opts [:foo [:bar]] [:baz {}])
            result (r opts [:foo [:bar]] [:baz {}])]
        (is (= [:div {:class "wb-v-scroll-pane-container foo"}
                [:div.wb-v-scroll-pane-overflow
                 [:foo [:bar]] [:baz {}]]]
               result)
            "renders extra classes correctly"))
      (let [opts {:extra-classes :foo}
            r (sut/v-scroll-pane opts [:foo [:bar]] [:baz {}])
            result (r {:extra-classes :bar} [:foo [:bar]] [:baz {}])]
        (is (= [:div {:class "wb-v-scroll-pane-container foo"}
                [:div.wb-v-scroll-pane-overflow
                 [:foo [:bar]] [:baz {}]]]
               result)
            "renders extra classes correctly despite changes at render-time.")))

    (testing "Rendering with :subscribe-to-classes option"
      (let [cls (atom :foo)
            opts {:subscribe-to-classes cls}
            r (sut/v-scroll-pane opts [:foo [:bar]] [:baz {}])
            result (r opts [:foo [:bar]] [:baz {}])]
        (is (= [:div {:class "wb-v-scroll-pane-container foo"}
                [:div.wb-v-scroll-pane-overflow
                 [:foo [:bar]] [:baz {}]]]
               result)
            "renders extra classes correctly"))
      (let [cls (atom :foo)
            opts {:subscribe-to-classes cls}
            r (sut/v-scroll-pane opts [:foo [:bar]] [:baz {}])
            _ (reset! cls :bar)
            result (r opts [:foo [:bar]] [:baz {}])]
        (is (= [:div {:class "wb-v-scroll-pane-container bar"}
                [:div.wb-v-scroll-pane-overflow
                 [:foo [:bar]] [:baz {}]]]
               result)
            "renders extra classes correctly including changes at render-time.")))

    (testing "Rendering with header"
      (let [h [sut/scroll-pane-header {} [:foo]]
            r (sut/v-scroll-pane [:foo] h [:bar])
            result (r [:foo] h [:bar])]
        (is (= [:div {:class "wb-v-scroll-pane-container"}
                [:div.wb-v-scroll-pane-header h]
                [:div.wb-v-scroll-pane-overflow
                 [:foo] [:bar]]]
               result)
            "puts header above body")))

    (testing "Rendering with footer"
      (let [f [sut/scroll-pane-footer {} [:foo]]
            r (sut/v-scroll-pane [:foo] f [:bar])
            result (r [:foo] f [:bar])]
        (is (= [:div {:class "wb-v-scroll-pane-container"}
                [:div.wb-v-scroll-pane-overflow
                 [:foo] [:bar]]
                [:div.wb-v-scroll-pane-footer f]]
               result)
            "puts footer below body")))

    (testing "Rendering with header and footer"
      (let [h [sut/scroll-pane-header {} [:foo]]
            f [sut/scroll-pane-footer [:baz]]
            r (sut/v-scroll-pane [:foo] f h [:bar])
            result (r [:foo] f h [:bar])]
        (is (= [:div {:class "wb-v-scroll-pane-container"}
                [:div.wb-v-scroll-pane-header h]
                [:div.wb-v-scroll-pane-overflow
                 [:foo] [:bar]]
                [:div.wb-v-scroll-pane-footer f]]
               result)
            "puts header above body and footer below body")))))

(println "start local tests")
(run-tests)
(println "end local tests")
