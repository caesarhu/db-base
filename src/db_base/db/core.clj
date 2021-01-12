(ns db-base.db.core
  (:require [clojure.spec.alpha :as s]
            [next.jdbc :as jdbc]
            [next.jdbc.date-time :refer [read-as-local]]
            [honeysql.core :as sql]
            [db-base.config :as config]
            [honeysql-postgres.helpers :as psqlh]))

(read-as-local)

(let [kebab-case# (requiring-resolve 'camel-snake-kebab.core/->kebab-case)
      snake-case# (requiring-resolve 'camel-snake-kebab.core/->snake_case)]
  (def auto-opts
    "A hash map of options that will convert Clojure identifiers to
snake_case SQL entities (`:table-fn`, `:column-fn`), and will convert
SQL entities to qualified kebab-case Clojure identifiers (`:builder-fn`)."
    {:column-fn  snake-case# :table-fn     snake-case#
     :label-fn   kebab-case# :qualifier-fn kebab-case#
     :builder-fn (resolve 'next.jdbc.result-set/as-kebab-maps)
     :return-keys true})
  (def unqualified-auto-opts
    "A hash map of options that will convert Clojure identifiers to
snake_case SQL entities (`:table-fn`, `:column-fn`), and will convert
SQL entities to unqualified kebab-case Clojure identifiers (`:builder-fn`)."
    {:column-fn  snake-case# :table-fn     snake-case#
     :label-fn   kebab-case# :qualifier-fn kebab-case#
     :builder-fn (resolve 'next.jdbc.result-set/as-unqualified-kebab-maps)
     :return-keys true}))

(defn honey-format
  "map? : honeysql map, default set :namespace-as-table? true
  seq? or vector? : first arg is honeysql map, others is opts, ex: :namespace-as-table? false"
  [map-or-seq-or-vector]
  (if (map? map-or-seq-or-vector)
    (sql/format map-or-seq-or-vector)
    (apply sql/format map-or-seq-or-vector)))

(defn honey!
  ([sql-map opts db]
   (jdbc/execute! db (honey-format sql-map) (merge auto-opts opts)))
  ([sql-map opts]
   (honey! sql-map opts @config/db))
  ([sql-map]
   (honey! sql-map {} @config/db)))

(defn honey-one!
  ([sql-map opts db]
   (jdbc/execute-one! db (honey-format sql-map) (merge auto-opts opts)))
  ([sql-map opts]
   (honey-one! sql-map opts @config/db))
  ([sql-map]
   (honey-one! sql-map {} @config/db)))

(defn upsert-one
  [table row & conflicts]
  (let [base (sql/build :insert-into table
                        :values [row])]
    (psqlh/upsert base (apply psqlh/do-update-set
                              (apply psqlh/on-conflict {} conflicts)
                              (keys row)))))
