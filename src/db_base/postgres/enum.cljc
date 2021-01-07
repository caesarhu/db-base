(ns db-base.postgres.enum
  (:require [malli.core :as m]
            [db-base.postgres.utils :as utils]
            [camel-snake-kebab.core :as csk]))

(defn get-enum-name
  [enum]
  (-> enum
      m/properties
      :enum-name))

(defn create-enum
  [enum]
  (let [values (m/children enum)
        values-str (utils/comma-join-args values)]
    (str "CREATE TYPE "
         (csk/->snake_case_string (get-enum-name enum))
         " AS ENUM "
         values-str
         ";")))

(defn drop-enum
  [enum]
  (str "DROP TYPE IF EXISTS "
       (csk/->snake_case_string (get-enum-name enum))
       " CASCADE;"))

(defn generate-enum-edn
  [enum]
  {:up (vector (create-enum enum))
   :down (vector (drop-enum enum))})
