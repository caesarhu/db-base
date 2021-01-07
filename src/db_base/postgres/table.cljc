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
  [field]
  (loop [schema (last field)]
    (let [f-type (or (some-> schema
                             m/properties
                             :postgres/type)
                     (m/type schema))]
      (cond
        (symbol? f-type) f-type
        (contains? type-keys f-type) f-type
        (= :re f-type) f-type
        (= :enum f-type) (enum/get-enum-name schema)
        (= :malli.core/schema f-type) (recur (->> schema m/form (get @db-schema/registry*)))
        (= :maybe f-type) (recur (->> schema m/children first))
        :else (throw (ex-info "field type parse error!"
                              {:cause ::field-type
                               :schema schema}))))))

(def ->postgres-table
  [[(set [:re :string 'string?]) :text]
   [(set ['integer?, 'int?, 'pos-int?, 'neg-int?, 'nat-int?, :int]) :bigint]
   [(set ['float?, 'double?, 'decimal?, :double]) :decimal]
   [(set [:local-date]) :date]
   [(set [:local-date-time 'inst?]) :timestamp]
   [(set [:boolean 'boolean?]) :boolean]
   [(set ['bytes?]) :bytea]])

(defn type->postgres
  [type]
  (let [type-fn (fn [tv]
                  (let [[ts pt] tv]
                    (when (contains? ts type)
                      pt)))]
    (if-let [postgres-type (some type-fn ->postgres-table)]
      postgres-type
      (when (keyword? type)
        type))))

(defn field->postgres-type
  [field]
  (-> field  field-type type->postgres))

(defn field-types
  [model]
  (->> model
       gm/keys
       (map #(gm/child model %))
       (map field->postgres-type)))

(defn create-table
  [model])