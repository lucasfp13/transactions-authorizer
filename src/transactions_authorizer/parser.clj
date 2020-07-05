(ns transactions-authorizer.parser
  (:gen-class)
  (:require [cheshire.core :as cheshire]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [transactions-authorizer.authorizer :as authorizer]))

  (defn parse-input [reader]
    ;; Receives the input reader to parse each operation in the input file
    (doseq [line (line-seq reader)]
      (if (str/includes? line "account")
        (authorizer/process-account line)
        (authorizer/process-transaction line))))

  (defn read-input [file]
    (let [rdr (io/reader file)]
         (parse-input rdr)))

  (defn -main
    [& args]
    (read-input *in*))
