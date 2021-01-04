(ns db-base.malli.malli-time
  (:require [malli.core :as m]
            [java-time :as jt]
            [clojure.test.check.generators :as gen]
            [malli.generator :as mg]))

(defn date->str
  [d]
  (if (jt/local-date? d)
    (jt/format :iso-local-date d)
    d))

(defn str->date
  [s]
  (if (string? s)
    (jt/local-date s)
    s))

(defn date-time->str
  [t]
  (if (jt/local-date-time? t)
    (jt/format :iso-local-date t)
    t))

(defn str->date-time
  [s]
  (if (string? s)
    (jt/local-date-time s)
    s))

(defn rand-local-date
  ([start-date end-date]
   (let [start (.toEpochDay start-date)
         end (.toEpochDay end-date)
         rand-epoch (+ (rand-int (- end start)) start)]
     (java.time.LocalDate/ofEpochDay rand-epoch)))
  ([]
   (rand-local-date (jt/local-date 1920 1 1) (jt/local-date))))

(def gen-local-date
  (let [start (.toEpochDay (jt/local-date 1920 1 1))
        end (.toEpochDay (jt/local-date))]
    (gen/fmap (fn [epoch]
                (java.time.LocalDate/ofEpochDay epoch))
              (gen/large-integer* {:min start :max end}))))

(defn rand-local-time
  ([start-time end-time]
   (let [start (.toSecondOfDay start-time)
         end (.toSecondOfDay end-time)
         rand-epoch (+ (rand-int (- end start)) start)]
     (java.time.LocalTime/ofSecondOfDay rand-epoch)))
  ([]
   (let [milli-property (jt/property (jt/local-time) :milli-of-day)]
     (rand-local-time (jt/with-min-value milli-property) (jt/with-max-value milli-property)))))

(def gen-local-date-time
  (gen/fmap #(-> (java.time.Instant/ofEpochMilli %)
                 (java.time.LocalDateTime/ofInstant java.time.ZoneOffset/UTC))
            gen/large-integer))

(def local-date
  (m/-simple-schema
    {:type :local-date
     :pred jt/local-date?
     :type-properties {:error/message "should be java.time.LocalDate"
                       :decode/string str->date
                       :encode/string date->str
                       :decode/json str->date
                       :encode/json date->str
                       :json-schema/type "string"
                       :json-schema/format "date"
                       :gen/gen gen-local-date}}))


(def local-date-time
  (m/-simple-schema
    {:type            :local-date-time
     :pred            jt/local-date-time?
     :type-properties {:error/message      "should be java.time.LocalDateTime"
                       :decode/string      str->date-time
                       :encode/string      date-time->str
                       :decode/json        str->date-time
                       :encode/json        date-time->str
                       :json-schema/type   "string"
                       :json-schema/format "date-time"
                       :gen/gen gen-local-date-time}}))