(ns db-base.postgres.core
  (:require
    [db-base.postgres.enum :as enum]
    [db-base.postgres.table :as table]
    [db-base.schema :as db-schema]
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

(defn generate-enum-edn-all
  []
  (map enum/generate-enum-edn (vals (db-schema/db-enums))))

(defn generate-table-edn-all
  []
  (map table/generate-table-edn (vals @gm/models)))

(defn generate-edn-all
  []
  (->> (concat (generate-enum-edn-all) (generate-table-edn-all))
       (sort-by :id)))
