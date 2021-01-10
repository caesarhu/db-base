(ns db-base.postgres.core
  (:require
    [db-base.schema :as db-schema]
    [db-base.postgres.enum :as enum]
    [db-base.postgres.table :as table]
    [gungnir.model :as gm]))

(defn spit-enum-all
  []
  (mapv enum/spit-enum-edn (vals (db-schema/db-enums))))

(defn spit-table-all
  []
  (mapv table/spit-table-edn (vals @gm/models)))

(defn spit-edn-all
  []
  (spit-enum-all)
  (spit-table-all))
