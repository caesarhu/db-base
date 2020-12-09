(ns dev
  (:require [fipp.edn :refer [pprint]]
            [clojure.spec.alpha :as spec.alpha]
            [clojure.java.io :as io]
            [expound.alpha :as expound]
            [orchestra.spec.test :as stest]
            [db-base.config :as config]
            [juxt.clip.repl :refer [start stop set-init! reset system]]
            [hodur-translate.core :as hodur]))

(set-init! (fn [] (config/config :dev)))

(comment
  (start)
  (reset)
  (stop)
  system)

;;; code start

(defn meta-db
  []
  (-> (str (:schema-path (config/config)))
      hodur/read-schema
      hodur/init-db))

(defn spit-sql
  []
  (-> (str "resources/" (:migration-dir (config/config)))
      (hodur/spit-db-sql (meta-db))))

;;; expound and Orchestra

(defn unstrument
  []
  (stest/unstrument))


(defn instrument
  []
  (set! spec.alpha/*explain-out* expound/printer)
  (stest/instrument))

(instrument)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;