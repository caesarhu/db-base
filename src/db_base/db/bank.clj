(ns db-base.db.bank
  (:require [db-base.db.core :as db]
            [db-base.config :as config]
            [gungnir.changeset :as gc]
            [taoensso.timbre :as timbre]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]))

(defn bank-upsert-sql
  [row]
  (db/upsert-one :bank row :bank-id))

(defn bank-upsert
  ([row db]
   (if-let [errors (:changeset/errors (gc/create row))]
     (timbre/log :error
                 errors
                 {:from ::bank-upsert
                  :data row})
     (db/honey-one! (bank-upsert-sql row) {} db)))
  ([row]
   (bank-upsert row @config/db)))

(defn bank-count
  ([db]
   (-> (sqlh/select :%count.*)
       (sqlh/from :bank)
       (db/honey-one!)
       :count))
  ([]
   (bank-count @config/db)))

