(ns dev
  (:require [fipp.edn :refer [pprint]]
            [clojure.spec.alpha :as spec.alpha]
            [aero.core :as aero]
            [kaocha.repl :as k]
            [expound.alpha :as expound]
            [orchestra.spec.test :as stest]
            [db-base.config :as config]
            [clojure.tools.gitlibs :as gl]
            [db-base.ragtime :refer [migrate rollback clear-db reset-db]]
            [juxt.clip.repl :refer [start stop set-init! reset system]]
            [malli.core :as m]
            [malli.error :as me]
            [malli.generator :as mg]
            [gungnir.model :as gm]
            [gungnir.field :as gf]
            [db-base.schema :as db-schema]))

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