(ns woolybear.test.runner
  (:require cljs.test
            woolybear.test.utils
            woolybear.ad.containers-test))

(cljs.test/run-all-tests #"^woolybear\..*")
