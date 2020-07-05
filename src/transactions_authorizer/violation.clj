(ns transactions-authorizer.violation
  (:require [transactions-authorizer.logger :as logger]))

(defn register-violation [account violation]
  (swap! account conj {:violations [violation]})
  (logger/log-transaction @account))