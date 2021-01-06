(ns db-base.malli.translate
  (:require [malli.core :as m]
            [clojure.set]
            [gungnir.model :as gm]
            [gungnir.field :as gf]))

(defn get-locale
  [k]
  (let [properties (if (simple-keyword? k)
                     (gm/properties k)
                     (gf/properties k))]
    (get properties :locale/zh-tw)))

(defn entry-pair
  [field-key]
  (when-let [locale (get-locale field-key)]
    [field-key locale]))

(defn model-dict
  [model-key]
  (when-let [locale-table (get-locale model-key)]
    (let [pairs (->> (map entry-pair (gm/keys model-key))
                     (filter some?))
          table (gm/table model-key)
          half-map (->> (map #(hash-map (first %) (keyword locale-table (last %))) pairs)
                        (apply merge (hash-map table (keyword locale-table))))]
      (merge half-map (clojure.set/map-invert half-map)))))

(defn translate
  [dict-map k]
  (or (get dict-map k)
      k))