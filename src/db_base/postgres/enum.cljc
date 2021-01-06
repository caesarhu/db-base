(ns db-base.postgres.enum
  (:require [malli.core :as m]
            [db-base.postgres.utils :as utils]
            [camel-snake-kebab.core :as csk]))

(defn create-enum
  [m enum-key]
  (when-let [enum (and (= :enum (m/type (get m enum-key)))
                       (get m enum-key))]
    (let [values (m/children enum)
          values-str (utils/comma-join-args values)]
      (str "CREATE TYPE "
           (csk/->snake_case_string enum-key)
           " AS ENUM "
           values-str
           ";"))))

(defn drop-enum
  [enum-key]
  (str "DROP TYPE IF EXISTS "
       (csk/->snake_case_string enum-key)
       " CASCADE;"))

(defn generate-enum-edn
  [m enum-key]
  {:up (vector (create-enum m enum-key))
   :down (vector (drop-enum enum-key))})