(ns solari.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [solari.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'solari.core-test))
    0
    1))
