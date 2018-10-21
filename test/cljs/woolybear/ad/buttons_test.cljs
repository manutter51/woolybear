(ns woolybear.ad.buttons-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [woolybear.ad.utils :as adu]
            [woolybear.test.utils :as wtu]
            [woolybear.ad.buttons :as sut]))

(deftest button-test
  (testing "subscribe to disable"
    (let [disable (atom false)
          clicker (fn [_] "Clicked!")
          expected-click-dispatcher (adu/mk-dispatcher clicker)
          label "Click Me!"
          opts {:subscribe-to-disabled? disable
                :on-click               clicker}
          b (sut/button opts label)]
      (is (= (wtu/realize-dispatchers
               [:button {:on-click expected-click-dispatcher
                         :class    "button wb-button"} label])
             (wtu/realize-dispatchers
               (b opts label)))
          "Should not render 'disabled' attr when subscription value is false")
      (swap! disable not)
      (is (= (wtu/realize-dispatchers
               [:button {:on-click expected-click-dispatcher
                         :disabled "disabled"
                         :class    "button wb-button"} label])
             (wtu/realize-dispatchers
               (b opts label)))
          "Should render 'disabled' attr when subscription value is true")))

  (testing "extra classes"
    (let [clicker (fn [_] "Clicked!")
          expected-click-dispatcher (adu/mk-dispatcher clicker)
          label "Click Me!"
          opts {:extra-classes :foo
                :on-click      clicker}
          b (sut/button opts label)]
      (is (= (wtu/realize-dispatchers
               [:button {:on-click expected-click-dispatcher
                         :class    "button wb-button foo"} label])
             (wtu/realize-dispatchers
               (b opts label)))
          "Should add extra class to class attr")
      (is (= (wtu/realize-dispatchers
               [:button {:on-click expected-click-dispatcher
                         :class    "button wb-button foo"} label])
             (wtu/realize-dispatchers
               (b {:extra-classes :bar
                   :on-click      clicker} label)))
          "If render-time value of extra class changes, should render original value, not changed value.")))

  (testing "subscribe to classes"
    (let [dynamic-classes (atom #{:foo})
          clicker (fn [_] "Clicked!")
          expected-click-dispatcher (adu/mk-dispatcher clicker)
          label "Click Me!"
          opts {:subscribe-to-classes dynamic-classes
                :on-click             clicker}
          b (sut/button opts label)]
      (is (= (wtu/realize-dispatchers
               [:button {:on-click expected-click-dispatcher
                         :class    "button wb-button foo"} label])
             (wtu/realize-dispatchers
               (b opts label)))
          "Should add dynamic class to class attr")
      (swap! dynamic-classes conj :bar)
      (is (= (wtu/realize-dispatchers
               [:button {:on-click expected-click-dispatcher
                         :class    "button wb-button foo bar"} label])
             (wtu/realize-dispatchers
               (b opts label)))
          "If dynamic classes change at render time, should render updated classes"))))

(deftest tab-button-test
  (testing "active? flag"
    (let [clicker (fn [_] "Clicked!")
          expected-click-dispatcher (adu/mk-dispatcher clicker)
          label "Click Me!"
          opts {:id       :sut
                :panel-id :foo
                :active?  false
                :on-click clicker}
          b (sut/tab-button opts label)]
      (is (= (wtu/realize-dispatchers
               [:div.level-item
                [:button {:on-click expected-click-dispatcher
                          :class    "wb-button wb-tab-button button"} label]])
             (wtu/realize-dispatchers
               (b opts label)))
          "When active? is false, should render without 'active' CSS class")
      (is (= (wtu/realize-dispatchers
               [:div.level-item
                [:button {:on-click expected-click-dispatcher
                          :class    "wb-button wb-tab-button active button"} label]])
             (wtu/realize-dispatchers
               (b {:id       :sut
                   :active?  true
                   :on-click clicker} label)))
          "If :active? is true at render time, should render with 'active' CSS class")))
  (testing "subscribe to disable"
    (let [disable (atom false)
          clicker (fn [_] "Clicked!")
          expected-click-dispatcher (adu/mk-dispatcher clicker)
          label "Click Me!"
          opts {:subscribe-to-disabled? disable
                :id                     :sut
                :panel-id               :foo
                :active?                false
                :on-click               clicker}
          b (sut/tab-button opts label)]
      (is (= (wtu/realize-dispatchers
               [:div.level-item
                [:button {:on-click expected-click-dispatcher
                          :class    "wb-button wb-tab-button button"} label]])
             (wtu/realize-dispatchers
               (b opts label)))
          "Should not render 'disabled' attr when subscription value is false")
      (swap! disable not)
      (is (= (wtu/realize-dispatchers
               [:div.level-item
                [:button {:on-click expected-click-dispatcher
                          :disabled "disabled"
                          :class    "wb-button wb-tab-button button"} label]])
             (wtu/realize-dispatchers
               (b opts label)))
          "Should render 'disabled' attr when subscription value is true")))

  (testing "extra classes"
    (let [clicker (fn [_] "Clicked!")
          expected-click-dispatcher (adu/mk-dispatcher clicker)
          label "Click Me!"
          opts {:extra-classes :foo
                :id            :sut
                :panel-id      :foo
                :active?       false
                :on-click      clicker}
          b (sut/tab-button opts label)]
      (is (= (wtu/realize-dispatchers
               [:div.level-item
                [:button {:on-click expected-click-dispatcher
                          :class    "wb-button wb-tab-button button foo"} label]])
             (wtu/realize-dispatchers
               (b opts label)))
          "Should add extra class to class attr")
      (is (= (wtu/realize-dispatchers
               [:div.level-item
                [:button {:on-click expected-click-dispatcher
                          :class    "wb-button wb-tab-button button foo"} label]])
             (wtu/realize-dispatchers
               (b {:extra-classes :bar
                   :id            :sut
                   :panel-id      :foo
                   :active?       false
                   :on-click      clicker} label)))
          "If render-time value of extra class changes, should render original value, not changed value.")))

  (testing "subscribe to classes"
    (let [dynamic-classes (atom #{:foo})
          clicker (fn [_] "Clicked!")
          expected-click-dispatcher (adu/mk-dispatcher clicker)
          label "Click Me!"
          opts {:subscribe-to-classes dynamic-classes
                :id                   :sut
                :panel-id             :foo
                :active?              false
                :on-click             clicker}
          b (sut/tab-button opts label)]
      (is (= (wtu/realize-dispatchers
               [:div.level-item
                [:button {:on-click expected-click-dispatcher
                          :class    "wb-button wb-tab-button button foo"} label]])
             (wtu/realize-dispatchers
               (b opts label)))
          "Should add dynamic class to class attr")
      (swap! dynamic-classes conj :bar)
      (is (= (wtu/realize-dispatchers
               [:div.level-item
                [:button {:on-click expected-click-dispatcher
                          :class    "wb-button wb-tab-button button foo bar"} label]])
             (wtu/realize-dispatchers
               (b opts label)))
          "If dynamic classes change at render time, should render updated classes"))))

(println "Run local AD Buttons tests")
(run-tests)
(println "Finished local AD Buttons tests")
