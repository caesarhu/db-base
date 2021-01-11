(ns db-base.postgres.table
  (:require
    [db-base.config :as config]
    [db-base.postgres.enum :as enum]
    [db-base.postgres.utils :as utils]
    [db-base.schema :as db-schema]
    [gungnir.field :as gf]
    [gungnir.model :as gm]
    [honeysql-postgres.helpers :as psqlh]
    [honeysql.core :as sql]
    [db-base.postgres.format] ; must require for honeysql format
    [malli.core :as m]))


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


(def ->postgres-type
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
    (if-let [postgres-type (some type-fn ->postgres-type)]
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


(defn column-auto
  [field]
  (let [properties (gf/properties field)
        f-type (field->postgres-type field)
        get-property #(get properties %)]
    (when (get-property :auto)
      (cond
        (and (get-property :primary-key)
             (= :bigint f-type)) (sql/call :generated)
        (= :date f-type) (sql/call :default :CURRENT_DATE)
        (= :timestamp f-type) (sql/call :default :CURRENT_TIMESTAMP)))))


(def column-call-set
  (set [:primary-key :unique :default :references]))


(defn sql-call
  [property-key property]
  (when property
    (let [s-key (-> property-key name keyword)]
      (cond
        (true? property) (sql/call s-key)
        (coll? property) (apply sql/call s-key property)
        :else (sql/call s-key property)))))


(defn column-sql-call
  [field]
  (let [properties (gf/properties field)
        get-property #(get properties %)
        column-attr (fn [property-key]
                      (let [property (get-property property-key)]
                        (when (contains? column-call-set property-key)
                          (sql-call property-key property))))]
    (->> (map column-attr (keys properties))
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
  (let [sql-map (-> (psqlh/create-table {} (gm/table model))
                    (psqlh/with-columns (model-columns model)))]
    (-> (sql/format sql-map :parameterizer :none)
        first
        (str ";"))))


(defn drop-table
  [model]
  (str "DROP TABLE IF EXISTS "
       (-> model gm/table utils/to-sql-arg)
       " CASCADE;"))


(defn create-index
  [model]
  (when-let [index-property (-> (gm/properties model)
                                :create-index)]
    (if (coll? (first index-property))
      (->> (map #(apply sql/call :create-index %) index-property)
           (map sql/format)
           flatten)
      (->> (apply sql/call :create-index index-property)
           sql/format))))


(defn generate-table-edn
  [model]
  (let [base {:up (->> (vector (create-table model) (create-index model))
                       flatten
                       (filter some?)
                       vec)
              :down (vector (drop-table model))}
        id (get (gm/properties model) :id)]
    (if id
      (assoc base :id id)
      base)))


(defn spit-table-edn
  ([path model]
   (spit path (utils/pretty-format (generate-table-edn model))))
  ([model]
   (let [dir (str "resources/" (:migration-dir @config/config))
         path (str dir "/" (get (m/properties model) :id) ".edn")]
     (spit-table-edn path model))))
