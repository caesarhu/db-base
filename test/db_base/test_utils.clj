(ns db-base.test-utils
  (:require
    [clojure.spec.alpha :as s]
    [orchestra.spec.test :as stest]
    [expound.alpha :as expound]))

(defn instrument-specs
  [f]
  (set! s/*explain-out* expound/printer)
  (stest/instrument)
  (f))