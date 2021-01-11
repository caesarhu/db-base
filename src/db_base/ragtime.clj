(ns db-base.ragtime
  (:require
    [db-base.config :as config]
    [ragtime.jdbc]
    [ragtime.core :as ragtime]
    [ragtime.repl :as repl]))

(defn migrate
  []
  (repl/migrate (eval (:ragtime-config @config/config))))

(defn rollback
  ([]
   (repl/rollback (eval (:ragtime-config @config/config))))
  ([n]
   (repl/rollback (eval (:ragtime-config @config/config)) n)))

(defn clear-db
  []
  (let [migrations (count @repl/migration-index)]
    (rollback migrations)))

(defn reset-db
  []
  (clear-db)
  (reset! repl/migration-index {})
  (migrate))