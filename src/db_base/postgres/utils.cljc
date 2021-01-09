(ns db-base.postgres.utils
  (:refer-clojure :exclude [format partition-by])
  (:require [honeysql.format :as sqlf]
            [fipp.edn :refer [pprint]]
            [clojure.string :as string])
  (:import (java.io StringWriter)))

(def quote-symbol "'")

(defn quotation-str
  ([s quote]
   (if (string? s)
     (str quote s quote)
     (str s)))
  ([s]
   (quotation-str s quote-symbol)))

(defn sql-command
  ([sql-v quote]
   (loop [sql-str (first sql-v)
          params (rest sql-v)]
     (if (empty? params)
       sql-str
       (let [param (first params)
             new-str (string/replace-first sql-str #"\?" (quotation-str param quote))]
         (recur new-str (rest params))))))
  ([sql-v]
   (sql-command sql-v quote-symbol)))

(defn pretty-format
  [obj]
  (with-open [w (StringWriter.)]
    (binding [*out* w]
      (pprint obj)
      (str w))))