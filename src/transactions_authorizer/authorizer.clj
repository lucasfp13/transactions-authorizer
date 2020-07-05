(ns transactions-authorizer.authorizer
  (:require [cheshire.core :as cheshire]
            [clj-time.core :as tt]
            [clj-time.coerce :as tc]
            [transactions-authorizer.logger :as logger]
            [transactions-authorizer.violation :as violation]))
  
  (def global-account (atom {}))
  (def authorized-transactions (atom []))
  (def doubled-transaction (atom false))

  (defn create-account [new-account]
    ;; Creates the global account that will be used throughout the program execution until its end
    ;; and log it to the terminal and output file
    (reset! global-account new-account)
    (logger/log-transaction @global-account))

  (defn authorize-transaction [account new-limit]
    ;; Authorizes the transaction and perform all account updates
    ;; and log it to the terminal and output file
    (swap! account update-in [:account] assoc :availableLimit new-limit)
    (swap! account assoc :violations [])
    (logger/log-transaction @global-account))

  (defn save-transaction [transaction last-transactions]
    ;; Save all processed transactions
    (swap! last-transactions conj transaction))

  (defn time-checker [transaction last-transactions]
    ;; Checks all time-related business logic violation conditions
    (def occurrences (atom 0))
    ;; Get transaction time and converts to long to make checks with the last 3 authorized transactions 
    (let [transaction-time (tc/to-long (get-in transaction [:transaction :time]))
          last-three-transactions (take-last 3 @last-transactions)]
      (doseq [l last-three-transactions]
        ;; Checks whether the transaction to be performed is within the 2 minute interval
        ;; 120000 = 2 minutes
        (if (< transaction-time (+ 120000 (tc/to-long (get-in l [:transaction :time]))))
          (do
            (swap! occurrences inc)
            ;; Checks if the doubled transaction condition happens within this 2 minute interval
            (if (= (get-in transaction [:transaction :merchant]) (get-in l [:transaction :merchant]))
              (if (= (get-in transaction [:transaction :amount]) (get-in l [:transaction :amount]))
                (reset! doubled-transaction true))))
          (do 
            (reset! occurrences 0)
            (reset! doubled-transaction false)))))
    ;; returns the number of occurrences
    @occurrences)
            
  ;; Receives the operation to be processed and check if the account was already initialized
  (defn process-account [command]
    (cond
      (seq @global-account) (violation/register-violation global-account "account-already-initialized")
      :else
        (let [operation (cheshire/parse-string command true)
              new-account (conj operation {:violations []})]
          (create-account new-account))))
  
  (defn process-transaction [command]
    ;; Receives the transaction to be processed and checks for any business logic violation
    (let [transaction (cheshire/parse-string command true)
          amount (get-in transaction [:transaction :amount])
          card-status (get-in @global-account [:account :activeCard])
          available-limit (get-in @global-account [:account :availableLimit])
          new-limit (- available-limit amount)]
      (cond
        ;; checks if account's card is active
        (= card-status false)
          (violation/register-violation global-account "card-not-active")
        ;; checks if account has available limit for the transaction
        (> amount available-limit)
          (violation/register-violation global-account "insufficient-limit")
        ;; using seq (idiomatic way) to verify if there are any past transactions processed
        ;; and calling the time-checker method to perform time validations
        (and (seq @authorized-transactions) (= (time-checker transaction authorized-transactions) 3))
          (violation/register-violation global-account "high-frequency-small-interval")
        ;; checks if there was an attempt to make a repeated transaction in the 2 minutes interval
        (= @doubled-transaction true)
          (violation/register-violation global-account "doubled-transaction")
        :else 
          (do                    
            (authorize-transaction global-account new-limit)
            (save-transaction transaction authorized-transactions)))))