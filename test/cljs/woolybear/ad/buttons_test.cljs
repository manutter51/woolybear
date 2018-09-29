(ns woolybear.ad.buttons-test
  (:require [cljs.spec.alpha :as s]
            [cljs.test :refer-macros [deftest is testing run-tests]]
            [woolybear.ad.utils :as adu]
            [woolybear.test.utils :as wtu]
            [woolybear.ad.buttons :as sut]))

(deftest button-test
  (testing "subscribe to disable"
    (let [disable (atom false)
          clicker (fn [_] "Clicked!")
          expected-click-handler (adu/mk-handler clicker)
          label "Click Me!"
          opts {:subscribe-to-disabled? disable
                :on-click               clicker}
          b (sut/button opts label)]
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button"} label])
             (wtu/realize-handlers
               (b opts label)))
          "Should not render 'disabled' attr when subscription value is false")
      (swap! disable not)
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :disabled "disabled"
                         :class    "wb-button"} label])
             (wtu/realize-handlers
               (b opts label)))
          "Should render 'disabled' attr when subscription value is true")))

  (testing "extra classes"
    (let [clicker (fn [_] "Clicked!")
          expected-click-handler (adu/mk-handler clicker)
          label "Click Me!"
          opts {:extra-classes :foo
                :on-click      clicker}
          b (sut/button opts label)]
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button foo"} label])
             (wtu/realize-handlers
               (b opts label)))
          "Should add extra class to class attr")
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button foo"} label])
             (wtu/realize-handlers
               (b {:extra-classes :bar
                   :on-click      clicker} label)))
          "If render-time value of extra class changes, should render original value, not changed value.")))

  (testing "subscribe to classes"
    (let [dynamic-classes (atom #{:foo})
          clicker (fn [_] "Clicked!")
          expected-click-handler (adu/mk-handler clicker)
          label "Click Me!"
          opts {:subscribe-to-classes dynamic-classes
                :on-click             clicker}
          b (sut/button opts label)]
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button foo"} label])
             (wtu/realize-handlers
               (b opts label)))
          "Should add dynamic class to class attr")
      (swap! dynamic-classes conj :bar)
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button foo bar"} label])
             (wtu/realize-handlers
               (b opts label)))
          "If dynamic classes change at render time, should render updated classes"))))

(deftest tab-button-test
  (testing "active? flag"
    (let [clicker (fn [_] "Clicked!")
          expected-click-handler (adu/mk-handler clicker)
          label "Click Me!"
          opts {:id       :sut
                :active?  false
                :on-click clicker}
          b (sut/tab-button opts label)]
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button wb-tab-button"} label])
             (wtu/realize-handlers
               (b opts label)))
          "When active? is false, should render without 'active' CSS class")
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button wb-tab-button active"} label])
             (wtu/realize-handlers
               (b {:id       :sut
                   :active?  true
                   :on-click clicker} label)))
          "If :active? is true at render time, should render with 'active' CSS class")))
  (testing "subscribe to disable"
    (let [disable (atom false)
          clicker (fn [_] "Clicked!")
          expected-click-handler (adu/mk-handler clicker)
          label "Click Me!"
          opts {:subscribe-to-disabled? disable
                :id                     :sut
                :active?                false
                :on-click               clicker}
          b (sut/tab-button opts label)]
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button wb-tab-button"} label])
             (wtu/realize-handlers
               (b opts label)))
          "Should not render 'disabled' attr when subscription value is false")
      (swap! disable not)
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :disabled "disabled"
                         :class    "wb-button wb-tab-button"} label])
             (wtu/realize-handlers
               (b opts label)))
          "Should render 'disabled' attr when subscription value is true")))

  (testing "extra classes"
    (let [clicker (fn [_] "Clicked!")
          expected-click-handler (adu/mk-handler clicker)
          label "Click Me!"
          opts {:extra-classes :foo
                :id            :sut
                :active?       false
                :on-click      clicker}
          b (sut/tab-button opts label)]
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button wb-tab-button foo"} label])
             (wtu/realize-handlers
               (b opts label)))
          "Should add extra class to class attr")
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button wb-tab-button foo"} label])
             (wtu/realize-handlers
               (b {:extra-classes :bar
                   :id            :sut
                   :active?       false
                   :on-click      clicker} label)))
          "If render-time value of extra class changes, should render original value, not changed value.")))

  (testing "subscribe to classes"
    (let [dynamic-classes (atom #{:foo})
          clicker (fn [_] "Clicked!")
          expected-click-handler (adu/mk-handler clicker)
          label "Click Me!"
          opts {:subscribe-to-classes dynamic-classes
                :id                   :sut
                :active?              false
                :on-click             clicker}
          b (sut/tab-button opts label)]
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button wb-tab-button foo"} label])
             (wtu/realize-handlers
               (b opts label)))
          "Should add dynamic class to class attr")
      (swap! dynamic-classes conj :bar)
      (is (= (wtu/realize-handlers
               [:button {:on-click expected-click-handler
                         :class    "wb-button wb-tab-button foo bar"} label])
             (wtu/realize-handlers
               (b opts label)))
          "If dynamic classes change at render time, should render updated classes"))))

(println "Run local AD Buttons tests")
(run-tests)
(println "Finished local AD Buttons tests")
