(ns db-base.test-utils
  (:require
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [db-base.schema :as db-schema]
    [orchestra.spec.test :as stest]))


(defn my-fixtures
  [f]
  (set! s/*explain-out* expound/printer)
  (stest/instrument)
  (db-schema/register-all!)
  (f))
