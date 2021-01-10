(ns db-base.postgres.format
  (:require
    [db-base.postgres.utils :refer [comma-join-args to-sql-arg]]
    [honeysql.format :as sqlf :refer [fn-handler format-clause format-modifiers]]))

(defmethod fn-handler "generated" [_ & args]
  (let [generate-type (or (first args)
                          "ALWAYS")]
    (str "GENERATED " generate-type " AS IDENTITY")))

(defmethod fn-handler "references" [_ & args]
  (let [args-map (apply hash-map args)
        {:keys [table column on-delete on-update]} args-map
        base (str "REFERENCES " (sqlf/to-sql table) (if (coll? column)
                                                      (comma-join-args column)
                                                      (comma-join-args [column])))]
    (cond-> base
      on-delete (str " ON DELETE " (to-sql-arg on-delete))
      on-update (str " ON UPDATE " (to-sql-arg on-update)))))

(defmethod fn-handler "create-index" [_ index-name unique? table & columns]
  (let [unique (when (or (= :unique unique?)
                         (true? unique?))
                 "UNIQUE ")]
    (str "CREATE " unique "INDEX "
         (sqlf/to-sql index-name)
         " ON "
         (sqlf/to-sql table)
         " "
         (comma-join-args columns)
         ";")))