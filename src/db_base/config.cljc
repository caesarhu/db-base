(ns db-base.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [migratus.core :as migratus]))

(defn config
  ([profile]
   (aero/read-config (io/resource "config.edn") {:profile profile}))
  ([]
   (config :dev)))

;;; database migrations

(defn migratus-config
  ([tag]
   (:migratus (config tag)))
  ([]
   (migratus-config :dev)))

(defn migrate
  ([tag]
   (migratus/migrate (migratus-config tag)))
  ([]
   (migrate :dev)))

(defn rollback
  ([tag]
   (migratus/rollback (migratus-config tag)))
  ([]
   (rollback :dev)))

