(ns dev
  (:require
    [aero.core :as aero]
    [clojure.spec.alpha :as spec.alpha]
    [clojure.tools.gitlibs :as gl]
    [db-base.config :as config]
    [db-base.ragtime :refer [migrate rollback clear-db reset-db]]
    [db-base.schema :as db-schema]
    [expound.alpha :as expound]
    [fipp.edn :refer [pprint]]
    [gungnir.changeset :as gc]
    [gungnir.field :as gf]
    [gungnir.model :as gm]
    [juxt.clip.repl :refer [start stop set-init! reset system]]
    [kaocha.repl :as k]
    [malli.core :as m]
    [malli.error :as me]
    [malli.generator :as mg]
    [orchestra.spec.test :as stest]))


(set-init! (fn [] @config/config))


(comment
  (start)
  (reset)
  (stop)
  system)


(defn unit-test
  []
  (k/run :unit))


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
