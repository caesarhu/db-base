(ns db-base.schema
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [gungnir.model :as model]
    [malli.core :as m]
    [malli.registry :as mr]
    [db-base.malli.malli-time :as time]
    [db-base.malli.employee :as employee]))

(def default-schema
  (merge
    (m/predicate-schemas)
    (m/class-schemas)
    (m/comparator-schemas)
    (m/type-schemas)
    (m/base-schemas)))

(def registry*
  (atom (merge
          default-schema
          time/time-schema)))

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

(defn db-models
  []
  (:model (db-schema)))

(defn register-model!
  []
  (model/register! (db-models)))

;;; 加入enum employee 的欄位 malli 定義及model定義

(defn register-all!
  []
  (register-map! (db-enums))
  (register-map! employee/employee-schema)
  (register-model!))

(register-all!)
