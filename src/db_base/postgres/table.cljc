(ns db-base.postgres.table
  (:require [malli.core :as m]
            [db-base.postgres.utils :as utils]
            [camel-snake-kebab.core :as csk]
            [honeysql.core :as sql]
            [honeysql.helpers :as sqlh]
            [honeysql-postgres.helpers :as psqlh]
            [gungnir.model :as gm]
            [gungnir.field :as gf]
            [db-base.postgres.enum :as enum]
            [db-base.schema :as db-schema]))

(def type-keys
  (set (concat (keys (m/type-schemas))
               [:local-date :local-date-time])))

(defn field-type
  [column]
  (loop [schema (last column)]
    (let [f-type (m/type schema)]
      (cond
        (symbol? f-type) f-type
        (contains? type-keys f-type) f-type
        (= :re f-type) f-type
        (= :enum f-type) (enum/get-enum-name schema)
        (= :malli.core/schema f-type) @db-schema/registry*
        (= :maybe f-type) (recur (->> schema m/children first))
        :else f-type))))

(defn field-types
  [model]
  (->> model
       gm/keys
       (map #(gm/child model %))
       (map field-type)))

(defn create-table
  [model])