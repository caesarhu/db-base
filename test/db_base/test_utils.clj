(ns db-base.test-utils
  (:require
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [orchestra.spec.test :as stest]))


(defn instrument-specs
  [f]
  (set! s/*explain-out* expound/printer)
  (stest/instrument)
  (f))
