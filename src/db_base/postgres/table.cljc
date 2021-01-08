(ns db-base.postgres.table
  (:require [malli.core :as m]
            [honeysql.core :as sql]
            [honeysql.format :as sqlf :refer [fn-handler format-clause format-modifiers]]
            [honeysql-postgres.helpers :as psqlh]
            [honeysql-postgres.format]
            [gungnir.model :as gm]
            [gungnir.field :as gf]
            [db-base.postgres.enum :as enum]
            [db-base.schema :as db-schema]
            [camel-snake-kebab.core :as csk]
            [sql-formatter.core :as sf]
            [db-base.config :as config]))

(def type-keys
  (set (concat (keys (m/type-schemas))
               [:local-date :local-date-time])))

(defn field-type
  [field]
  (loop [schema (last field)]
    (let [f-type (or (some-> schema
                             m/properties
                             :postgres/type)
                     (m/type schema))]
      (cond
        (symbol? f-type) f-type
        (contains? type-keys f-type) f-type
        (= :re f-type) f-type
        (= :enum f-type) (enum/get-enum-name schema)
        (= :malli.core/schema f-type) (recur (->> schema m/form (get @db-schema/registry*)))
        (= :maybe f-type) (recur (->> schema m/children first))
        :else (throw (ex-info "field type parse error!"
                              {:cause ::field-type
                               :schema schema}))))))

(def ->postgres-table
  [[(set [:re :string 'string?]) :text]
   [(set ['integer?, 'int?, 'pos-int?, 'neg-int?, 'nat-int?, :int]) :bigint]
   [(set ['float?, 'double?, 'decimal?, :double]) :decimal]
   [(set [:local-date]) :date]
   [(set [:local-date-time 'inst?]) :timestamp]
   [(set [:boolean 'boolean?]) :boolean]
   [(set ['bytes?]) :bytea]])

(defn type->postgres
  [type]
  (let [type-fn (fn [tv]
                  (let [[ts pt] tv]
                    (when (contains? ts type)
                      pt)))]
    (if-let [postgres-type (some type-fn ->postgres-table)]
      postgres-type
      (when (keyword? type)
        type))))

(defn field->postgres-type
  [field]
  (-> field field-type type->postgres))

(defn column-null
  [field]
  (when-not (= :maybe (m/type (last field)))
    (sql/call :not nil)))

(defmethod fn-handler "generate" [_ & args]
  (let [generate-type (or (first args)
                          "ALWAYS")]
    (str "GENERATED " generate-type " AS IDENTITY")))

(defn column-auto
  [field]
  (let [properties (gf/properties field)
        f-type (field->postgres-type field)
        get-property #(get properties %)]
    (when (get-property :auto)
      (cond
        (and (get-property :primary-key)
             (= :bigint f-type)) (sql/call :generate)
        (= :date f-type) (sql/call :default :CURRENT_DATE)
        (= :timestamp f-type) (sql/call :default :CURRENT_TIMESTAMP)))))

(def column-call-set
  (set [:primary-key :unique :default]))

(defn column-sql-call
  [field]
  (let [properties (gf/properties field)
        get-property #(get properties %)
        sql-call (fn [property-key]
                   (let [property (get-property property-key)]
                     (when (and (contains? column-call-set property-key)
                                property)
                       (if (true? property)
                         (sql/call property-key)
                         (sql/call property-key property)))))]
    (->> (map sql-call (keys properties))
         (filter some?))))

(defn field->column
  [field]
  (let [args-fn (fn [field]
                  (->> ((juxt column-null column-auto column-sql-call) field)
                       flatten
                       (filter some?)))]
    (->> ((juxt first field->postgres-type args-fn) field)
         flatten
         vec)))


(defn model-columns
  [model]
  (->> model
       gm/keys
       (map #(gm/child model %))
       (map field->column)))

(defn create-table
  [model]
  (-> (psqlh/create-table {} (gm/table model))
      (psqlh/with-columns (model-columns model))
      sql/format
      sf/sql-command
      (str ";")))

(defn drop-table
  [model]
  (str "DROP TABLE IF EXISTS "
       (csk/->snake_case_string (gm/table model))
       " CASCADE;"))

(defn generate-table-edn
  [model]
  (let [base {:up (vector (create-table model))
              :down (vector (drop-table model))}
        id (get (gm/properties model) :id)]
    (if id
      (assoc base :id id)
      base)))

(defn spit-table-edn
  ([path model]
   (spit path (generate-table-edn model)))
  ([model]
   (let [dir (str "resources/" (:migration-dir @config/config))
         path (str dir "/" (get (m/properties model) :id) ".edn")]
     (spit-table-edn path model))))
