(ns woolybear.test.runner
  (:require [cljs.test :as t]
            [cljs.spec.alpha :as s]
            [cljs.spec.test.alpha :as stest]
            [expound.alpha :as expound]
            woolybear.test.utils
    ;; Now list all test ns's so they're loaded when tests run
    ;; This has the happy side effect of running all tests whenever
    ;; you make a change to a file that has related tests.
            woolybear.ad.buttons-test
            woolybear.ad.containers-test
            ))

(def report-stats (atom {}))

(defn bump [k]
  (swap! report-stats update k inc))

(defn summary
  []
  (let [{:keys [files tests passes fails errors]} @report-stats
        tested-nses (:with-tests @report-stats)]
    (println)
    (println "Ran" tests "tests in" files "namepaces," (count tested-nses) "with tests.")
    (println "Passed:" passes ", Failed:" fails ", Errors:" errors)
    (println)
    (if (and (zero? fails)
               (zero? errors))
      (println "✅✅✅✅✅ \uD83D\uDE01")
      (println "❌❌❌❌❌ \uD83D\uDE21"))))

(defmulti report :type)

(defmethod report :default [_] (fn [& _]))

(defmethod report :begin-test-ns [_] (bump :files))

(defmethod report :begin-test-var [report]
  (bump :tests)
  (swap! report-stats update :with-tests conj ((comp :ns meta) (:var report)))
  #_(println "\n" (:var report)))

(defmethod report :pass [_]
  (bump :passes)
  (when (zero? (mod (:tests @report-stats) 40))
    (println))
  (print "✅"))

(defmethod report :fail [report]
  (bump :fails)
  (println "❌" (:message report))
  (cljs.pprint/pprint (select-keys report [:expected :actual])))

(defmethod report :error [report]
  (println "🔥 😱" (:message report))
  (bump :errors)
  (js/console.log report)
  (println (.-message (:actual report))))

(defmethod report :end-run-tests [_]
  (summary))

(set! s/*explain-out* expound/printer)
(stest/instrument)
(reset! report-stats {:files      0
                      :with-tests #{}
                      :tests      0
                      :passes     0
                      :fails      0
                      :errors     0})
(with-redefs [t/report report]
             (t/run-all-tests #"^woolybear\..*"))
