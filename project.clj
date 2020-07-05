(defproject transactions-authorizer "0.1.0-SNAPSHOT"
  :description "Nubank's Transactions Authorizer Code Exercise"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [cheshire "5.9.0"]
                 [clj-time "0.15.2"]]
  :uberjar-name "transactions-authorizer-standalone.jar"
  :main ^:skip-aot transactions-authorizer.parser
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
