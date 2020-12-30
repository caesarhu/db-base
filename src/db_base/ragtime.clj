(ns db-base.ragtime
  (:require
    [db-base.config :as config]
    [ragtime.jdbc]
    [ragtime.repl :as repl]))

(defn migrate
  []
  (repl/migrate (eval (:ragtime-config @config/config))))

(defn rollback
  []
  (repl/rollback (eval (:ragtime-config @config/config))))