(ns transactions-authorizer.authorizer-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [transactions-authorizer.authorizer :refer :all]))

(deftest authorizer
  (def new-authorized-transactions (atom []))
  (def account-default {:account {:activeCard true, :availableLimit 100}, :violations []})
  (def account-card-not-active {:account {:activeCard false, :availableLimit 100}, :violations []})
  (def account-insufficient-limit {:account {:activeCard true, :availableLimit 5}, :violations []})
  (def new-account-command "{ \"account\": { \"activeCard\": true, \"availableLimit\": 100 } }")
  (def new-transaction-command "{ \"transaction\": { \"merchant\": \"Mc Donalds\", \"amount\": 6, \"time\": \"2019-02-13T11:01:15.000Z\" } }")
  (def new-transaction-command2 "{ \"transaction\": { \"merchant\": \"Burguer King\", \"amount\": 10, \"time\": \"2019-02-13T11:01:49.300Z\" } }")
  (def new-transaction-command3 "{ \"transaction\": { \"merchant\": \"Pizza Hut\", \"amount\": 8, \"time\": \"2019-02-13T11:02:05.000Z\" } }")
  (def new-transaction-command4 "{ \"transaction\": { \"merchant\": \"Bobs\", \"amount\": 5, \"time\": \"2019-02-13T11:02:18.000Z\" } }")
  (def repeated-transaction-command "{ \"transaction\": { \"merchant\": \"Bobs\", \"amount\": 5, \"time\": \"2019-02-13T11:02:55.000Z\" } }")

  (testing "if new account was created (reset! atom)"
    (let [new-account (cheshire/parse-string new-account-command true)]
      (create-account new-account)
      (is (= new-account @global-account))))
  
  (testing "if account's limit was changed after a transaction's debit"
    (let [amount-debit 25
          account-limit (get-in @global-account [:account :availableLimit])
          new-limit (- account-limit amount-debit)]
      (authorize-transaction global-account new-limit)
      (is (= (get-in @global-account [:account :availableLimit]) new-limit))))
  
  (testing "if a processed transaction was saved"
    (let [new-transaction (cheshire/parse-string new-transaction-command true)]
      (save-transaction new-transaction new-authorized-transactions)
      (is (= true (.contains @new-authorized-transactions new-transaction)))))

  (testing "if a transaction throws a violation when the account hasn't active card"
    (let [command new-transaction-command]
      (reset! global-account account-card-not-active)
      (process-transaction command)
      (is (= (get-in @global-account [:violations 0]) "card-not-active"))))

  (testing "if a transaction throws a violation when the account hasn't available limit"
    (let [command new-transaction-command]
      (reset! global-account account-insufficient-limit)
      (process-transaction command)
      (is (= (get-in @global-account [:violations 0]) "insufficient-limit"))))
  
  (testing "if there are more than 3 transactions in 2 minutes interval"
    (let [command1 new-transaction-command
          command2 new-transaction-command2
          command3 new-transaction-command3
          command4 new-transaction-command4]
      (reset! global-account account-default)
      (process-transaction command1)
      (process-transaction command2)
      (process-transaction command3)
      (process-transaction command4)
      (is (= (get-in @global-account [:violations 0]) "high-frequency-small-interval"))))
      
  (testing "if there is a doubled transaction in a 2 minutes interval"
    (let [command1 new-transaction-command4
          command2 repeated-transaction-command]
      (reset! global-account account-default)
      (reset! authorized-transactions [])
      (process-transaction command1)
      (process-transaction command2)
      (is (= (get-in @global-account [:violations 0]) "doubled-transaction"))))
  )
