(ns db-base.postgres.utils
  (:refer-clojure :exclude [format partition-by])
  (:require [honeysql.format :as sqlf]))

(def quote-symbol "'")

(defn quotation-str
  [s]
  (str quote-symbol s quote-symbol))

(defn to-sql
  [x]
  (if (string? x)
    (quotation-str x)
    (sqlf/to-sql x)))

(defn comma-join-args
  "Returns the args comma-joined after applying to-sql to them"
  [args]
  (if (nil? args)
    ""
    (->> args
         (map to-sql)
         sqlf/comma-join
         sqlf/paren-wrap)))
