(ns db-base.config
  (:require
    [aero.core :as aero]
    [clojure.java.io :as io]
    [redelay.core :as redelay]
    [taoensso.timbre :as timbre]
    [taoensso.timbre.appenders.3rd-party.rolling :as rolling]))

(def config-edn "config.edn")

(defn read-edn-config
  ([profile]
   (aero/read-config (io/resource config-edn) {:profile profile}))
  ([]
   (let [config-file (io/resource config-edn)
         system-profile (:system-profile (aero/read-config config-file))]
     (aero/read-config config-file {:profile system-profile}))))

(def config
  (redelay/state (read-edn-config)))

;;; timbre

(defn taipei-zone
  []
  (java.util.TimeZone/getTimeZone "Asia/Taipei"))

(defn rolling-appender
  [opts]
  (rolling/rolling-appender opts))

(defn set-timbre-config!
  [m]
  (timbre/set-config! timbre/default-config)
  (timbre/merge-config! m))


