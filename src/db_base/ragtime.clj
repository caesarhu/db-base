(ns db-base.ragtime
  (:require
    [db-base.config :as config]
    [ragtime.core :as ragtime]
    [ragtime.jdbc]
    [ragtime.repl :as repl]
    [db-base.schema :as db-schema]
    [db-base.postgres.core :refer [generate-edn-all]]))

(defn generate-migrations
  []
  (->> (generate-edn-all)
       (map ragtime.jdbc/sql-migration)))

(defn ragtime-config
  ([config?]
   (if config?
     (eval (:ragtime-config @config/config))
     (-> (dissoc (:ragtime-config @config/config) :migrations)
         eval
         (assoc :migrations (generate-migrations)))))
  ([]
   (ragtime-config nil)))

(defn migrate
  ([config]
   (repl/migrate config))
  ([]
   (migrate (ragtime-config))))

(defn rollback
  ([config n]
   (repl/rollback config n))
  ([n]
   (rollback (ragtime-config) n))
  ([]
   (repl/rollback (ragtime-config))))

(defn clear-db
  ([config]
   (let [migrations (count @repl/migration-index)]
     (rollback config migrations)))
  ([]
   (clear-db (ragtime-config))))

(defn reset-db
  ([config]
   (clear-db config)
   (reset! repl/migration-index {})
   (db-schema/register-all!)
   (migrate config))
  ([]
   (reset-db (ragtime-config))))

