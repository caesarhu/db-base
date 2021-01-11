(ns db-base.postgres.enum-test
  (:require
    [clojure.test :as test]
    [db-base.postgres.enum :as enum]
    [db-base.schema :as db-schema]
    [db-base.test-utils :refer [instrument-specs]]))


(test/use-fixtures
  :once
  instrument-specs)


(def enum-employee
  {:up ["CREATE TYPE enum_employee AS ENUM ('回聘', '契約正式', '契約工讀', '計時', '計件');"],
   :down ["DROP TYPE IF EXISTS enum_employee CASCADE;"],
   :id "003-enum-employee"})


(test/deftest enum-test
  (test/testing "testing postgres/enum"
    (test/is (= enum-employee
                (enum/generate-enum-edn (:enum-employee (db-schema/db-enums)))))))
