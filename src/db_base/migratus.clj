(ns db-base.migratus
  (:require
    [db-base.config :as config]
    [migratus.core :as migratus]
    [redelay.core :as redelay]))

;;; database migrations

(defn migrate
  []
  (migratus/migrate (:migratus @config/config)))


(defn rollback
  []
  (migratus/rollback (:migratus @config/config)))


(defn reset-db
  []
  (migratus/reset (:migratus @config/config)))
