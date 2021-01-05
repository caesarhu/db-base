(ns db-base.malli.translate
  (:require [malli.core :as m]
            [clojure.set]))

(defn get-locale
  [v]
  (let [m (second v)]
    (when (map? m)
      (:locale/zh-tw m))))

(defn entry-pair
  [v]
  (when-let [locale (get-locale v)]
    (when (qualified-keyword? (first v))
      [(first v) locale])))

(defn model-dict
  [model]
  (when-let [locale-table (get-locale model)]
    (let [pairs (->> (map entry-pair (m/children model))
                     (filter some?))
          table (->> (ffirst pairs) namespace keyword)
          half-map (->> (map #(hash-map (first %) (keyword locale-table (last %))) pairs)
                        (apply merge (hash-map table (keyword locale-table))))]
      (merge half-map (clojure.set/map-invert half-map)))))

(defn translate
  [dict-map k]
  (or (get dict-map k)
      k))