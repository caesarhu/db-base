(ns db-base.malli.address
  (:require [db-base.config :as config]
            [clojure.java.io :as io]
            [clojure.test.check.generators :as gen]
            [clojure.string :as string]))

(def addr-seq
  (let [addr-file (io/resource (:address-path @config/config))]
    (-> addr-file
        (slurp :encoding "utf16")
        str
        (string/split #"\r\n"))))

(defn fake-number
  []
  (inc (rand-int 500)))

(defn fake-address-number
  []
  (let [lane (when (> (fake-number) 250)
               (str (fake-number) "巷"))
        alley (when (and lane (> (fake-number) 400))
                (str (inc (rand-int 15)) "弄"))
        sub (when (> (fake-number) 400)
              (str "之" (inc (rand-int 15))))
        number (str (fake-number) sub "號")
        floor (when (> (fake-number) 200)
                (str (inc (rand-int 20)) "樓"))]
    (str lane alley number floor)))

(defn fake-address
  []
  (let [addr-head (apply str (-> (rand-nth addr-seq)
                                 (string/split #",")
                                 drop-last
                                 rest))]
    (str addr-head (fake-address-number))))

(def gen-address
  (gen/fmap (fn [_]
              (fake-address))
            gen/large-integer))

