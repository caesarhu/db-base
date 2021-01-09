(ns db-base.postgres.enum
  (:require [malli.core :as m]
            [db-base.postgres.utils :as utils]
            [camel-snake-kebab.core :as csk]
            [db-base.config :as config]
            [honeysql-postgres.util :refer [comma-join-args]]))

(defn get-enum-name
  [enum]
  (-> enum
      m/properties
      :enum-name))

(defn create-enum
  [enum]
  (let [values (m/children enum)
        values-str (->> values
                        (map utils/quotation-str)
                        (map keyword)
                        comma-join-args)]
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
  (let [base {:up (vector (create-enum enum))
              :down (vector (drop-enum enum))}
        id (get (m/properties enum) :id)]
    (if id
      (assoc base :id id)
      base)))

(defn spit-enum-edn
  ([path enum]
   (spit path (utils/pretty-format (generate-enum-edn enum))))
  ([enum]
   (let [dir (str "resources/" (:migration-dir @config/config))
         path (str dir "/" (get (m/properties enum) :id) ".edn")]
     (spit-enum-edn path enum))))