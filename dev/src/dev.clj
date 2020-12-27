(ns dev
  (:require [fipp.edn :refer [pprint]]
            [clojure.spec.alpha :as spec.alpha]
            [clojure.java.io :as io]
            [expound.alpha :as expound]
            [orchestra.spec.test :as stest]
            [db-base.config :as config]
            [clojure.tools.gitlibs :as gl]
            [juxt.clip.repl :refer [start stop set-init! reset system]]
            [gungnir.model :as gm]))

(set-init! (fn [] (config/config :dev)))

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