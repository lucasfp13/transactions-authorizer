(ns transactions-authorizer.logger
  (:require [cheshire.core :as cheshire]))

  (defn append-to-file [output]
    (spit "resources/operations-output" (str (apply str (repeat 50 "-")) "\n" output "\n") :append true))

  (defn log-transaction [account]
    (println (cheshire/generate-string account))
    (append-to-file (cheshire/generate-string account {:pretty true})))
      