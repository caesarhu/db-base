(ns db-base.schema
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [gungnir.model :as model]
    [malli.core :as m]
    [malli.registry :as mr]
    [db-base.malli.malli-time :as time]))

(def registry*
  (atom (merge
          (m/predicate-schemas)
          (m/class-schemas)
          (m/comparator-schemas)
          (m/type-schemas)
          (m/base-schemas)
          {:local-date time/local-date
           :local-date-time time/local-date-time})))



(defn register! [type ?schema]
  (swap! registry* assoc type ?schema))

(defn register-map! [m]
  (swap! registry* merge m))

(mr/set-default-registry!
  (mr/mutable-registry registry*))

(def schema-edn "schema.edn")

(defn db-schema
  []
  (let [schema (aero/read-config (io/resource schema-edn))]
    schema))

(defn db-enums
  []
  (:enum (db-schema)))

(register-map! (db-enums))

(defn db-models
  []
  (:model (db-schema)))

(defn register-model!
  []
  (model/register! (db-models)))
