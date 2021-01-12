(ns db-base.raw-data.bank
  (:require [clojure.string :as string]
            [db-base.config :as config]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [db-base.db.bank :as db-bank]))

(def bank-data
  (:bank-data @config/config))

(def bank-txt
  (first bank-data))

(def bank-csv
  (last bank-data))

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

(defn read-bank-txt
  [path]
  (->> (map convert-chinese-space (-> (slurp (io/resource path) :encoding "Big5")
                                      (string/split #"\r\n")))
       (map #(vector (first %) (last %)))
       (concat lost-banks)
       (map #(zipmap [:bank/bank-id :bank/name] %))))

(defn read-bank-csv
  [path]
  (let [csv-str (-> (slurp (io/resource path) :encoding "Utf16")
                    (string/replace #"=" ""))]
    (->> (csv/read-csv csv-str :separator \tab :quote \")
         (map rest)
         (map #(vector (first %) (second %)))
         (map #(zipmap [:bank/bank-id :bank/name] %)))))

(defn local-bank-seed!
  ([db]
   (->> (read-bank-csv bank-csv)
        (concat (read-bank-txt bank-txt))
        (map #(db-bank/bank-upsert % db))
        dorun))
  ([]
   (local-bank-seed! @config/db)))
