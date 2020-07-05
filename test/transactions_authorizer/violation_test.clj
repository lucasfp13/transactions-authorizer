(ns transactions-authorizer.violation-test
  (:require [clojure.test :refer :all]
            [transactions-authorizer.violation :refer :all]))

(deftest violation
  (def account (atom {:account {:activeCard true, :availableLimit 100}, :violations []}))

  (testing "if register violation ok"
    (let [account-param account
          changed-account (register-violation account-param "some-violation")]
      (is (not= account-param changed-account)))))
