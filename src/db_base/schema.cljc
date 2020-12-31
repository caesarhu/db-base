(ns db-base.schema
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [gungnir.model :as model]))

(def schema-edn "schema.edn")

(defn db-schema
  []
  (let [schema (aero/read-config (io/resource schema-edn))]
    schema))