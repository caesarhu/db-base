(ns db-base.malli.translate
  (:require
    [clojure.set]
    [gungnir.field :as gf]
    [gungnir.model :as gm]
    [malli.core :as m]))


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


(defn model-key-dict
  [model-key]
  (when-let [locale-table (get-locale model-key)]
    (let [pairs (->> (map entry-pair (gm/keys model-key))
                     (filter some?))
          table (gm/table model-key)
          half-map (->> (map #(hash-map (first %) (keyword locale-table (last %))) pairs)
                        (apply merge (hash-map table (keyword locale-table))))]
      (merge half-map (clojure.set/map-invert half-map)))))


(defn model-dict
  [model]
  (-> model
      gm/table
      model-key-dict))


(defn translate
  [dict-map k]
  (or (get dict-map k)
      k))
