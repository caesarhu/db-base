(defproject db-base "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Apache License, Version 2.0."
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :deploy-repositories [["releases" :clojars
                         :creds :gpg]
                        ["snapshots" :clojars
                         :creds :gpg]]
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [juxt/clip "0.20.0"]
                 [aero/aero "1.1.6"]
                 [caesarhu/aave "0.1.0"]
                 [caesarhu/hodur-translate "0.1.15"]
                 [seancorfield/next.jdbc "1.1.613"]
                 [org.postgresql/postgresql "42.2.18"]
                 [hikari-cp "2.13.0"]
                 [com.taoensso/timbre "5.1.0"]
                 [com.fzakaria/slf4j-timbre "0.3.20"]]
  :main ^:skip-aot db-base.core
  :target-path "target/%s"
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile"]
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :repl-options {:init-ns user}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[fipp "0.6.23"]
                                   [org.clojure/tools.namespace "1.1.0"]
                                   [expound "0.8.7"]
                                   [orchestra "2020.09.18-1"]
                                   [org.clojure/test.check "1.1.0"]
                                   [com.gfredericks/test.chuck "0.2.10"]
                                   [lambdaisland/kaocha "1.0.732"]
                                   [migratus "1.3.3"]]}})
