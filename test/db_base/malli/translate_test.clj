(ns db-base.malli.translate-test
  (:require
    [clojure.test :as test]
    [db-base.test-utils :refer [instrument-specs]]
    [db-base.schema :as db-schema]
    [db-base.malli.translate :as mt]
    [gungnir.model :as gm]))

(test/use-fixtures
  :once
  instrument-specs)

(db-schema/register-all!)

(def dict
  (mt/model-dict (:change @gm/models)))

(test/deftest translate-test
  (test/testing "testing postgres/translate"
    (test/is (= :員工異動檔
                (mt/translate dict :change)))
    (test/is (= :change
                (mt/translate dict :員工異動檔)))
    (test/is (= :員工異動檔/id
                (mt/translate dict :change/id)))
    (test/is (= :change/id
                (mt/translate dict :員工異動檔/id)))
    (test/is (= :員工異動檔/員工基本資料檔id
                (mt/translate dict :change/employee-id)))
    (test/is (= :change/employee-id
                (mt/translate dict :員工異動檔/員工基本資料檔id)))
    (test/is (= :員工異動檔/異動類別
                (mt/translate dict :change/change-kind)))
    (test/is (= :change/change-kind
                (mt/translate dict :員工異動檔/異動類別)))
    (test/is (= :change/change-day
                (mt/translate dict :員工異動檔/異動日期)))
    (test/is (= :員工異動檔/異動日期
                (mt/translate dict :change/change-day)))
    (test/is (= :change/created-at
                (mt/translate dict :員工異動檔/建立時間)))
    (test/is (= :員工異動檔/建立時間
                (mt/translate dict :change/created-at)))
    (test/is (= :change/memo
                (mt/translate dict :員工異動檔/備註)))
    (test/is (= :員工異動檔/備註
                (mt/translate dict :change/memo)))
    (test/is (= :not-found
                (mt/translate dict :not-found)))))