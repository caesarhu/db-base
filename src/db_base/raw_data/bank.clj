(ns db-base.raw-data.bank
  (:require [clojure.string :as string]
            [db-base.config :as config]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [db-base.db.bank :as db-bank]))

(def bank-fisc
  (first (:bank-data @config/config)))

(def banking
  (last (:bank-data @config/config)))

(def lost-banks
  [["0520498" "渣打國際商業銀行延平分行"]])

(defn convert-chinese-space
  [line]
  (let [chinese-space 12288]
    (string/split (->> line
                       seq
                       (map int)
                       (map #(if (= chinese-space %)
                               32
                               %))
                       (map char)
                       (apply str))
                  #"\s+")))

(defn read-bank-fisc
  [path]
  (->> (map convert-chinese-space (-> (slurp (io/resource path) :encoding "Big5")
                                      (string/split #"\r\n")))
       (map #(vector (first %) (last %)))
       (concat lost-banks)
       (map #(zipmap [:bank/bank-id :bank/name] %))))

(defn read-banking
  [path]
  (let [lines (-> (slurp (io/resource path))
                  (string/split #"\r\n"))]
    (->> (map #(string/split % #"\s+") lines)
         (map rest)
         (map (partial take 2))
         (filter not-empty)
         (map #(zipmap [:bank/bank-id :bank/name] %)))))

(defn local-bank-seed!
  ([db]
   (->> (read-banking banking)
        (concat (read-bank-fisc bank-fisc))
        (map #(db-bank/bank-upsert % db))
        dorun))
  ([]
   (local-bank-seed! @config/db)))

(defn init-bank-seed!
  ([f db]
   (when-not (= 5764 (db-bank/bank-count db))
     (f db)))
  ([f]
   (init-bank-seed! f @config/db))
  ([]
   (init-bank-seed! local-bank-seed!)))
