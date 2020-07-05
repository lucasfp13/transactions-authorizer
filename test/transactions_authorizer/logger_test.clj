(ns transactions-authorizer.logger-test
    (:require [clojure.test :refer :all]
              [clojure.java.io :as io]
              [transactions-authorizer.logger :refer :all]))
  
  (deftest logger
    (def global-account {:account {:activeCard true, :availableLimit 100}, :violations []})

    (testing "log transactions"
      (log-transaction global-account)
      (let [generated? (.exists (io/as-file "resources/operations-output"))]
        (is (= true generated?)))))
