(ns dev
  (:require [fipp.edn :refer [pprint]]
            [clojure.spec.alpha :as spec.alpha]
            [aero.core :as aero]
            [expound.alpha :as expound]
            [orchestra.spec.test :as stest]
            [db-base.config :as config]
            [clojure.tools.gitlibs :as gl]
            [db-base.ragtime :refer [migrate rollback reset-db]]
            [juxt.clip.repl :refer [start stop set-init! reset system]]
            [malli.core :as m]
            [malli.error :as me]
            [gungnir.model :as gm]
            [db-base.schema :as dbs]))

(set-init! (fn [] @config/config))

(comment
  (start)
  (reset)
  (stop)
  system)


;;; expound and Orchestra

(defn unstrument
  []
  (stest/unstrument))


(defn instrument
  []
  (set! spec.alpha/*explain-out* expound/printer)
  (stest/instrument))

(instrument)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;