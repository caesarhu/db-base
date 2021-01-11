(ns db-base.postgres.table-test
  (:require
    [clojure.test :as test]
    [db-base.postgres.table :as table]
    [db-base.schema :as db-schema]
    [db-base.test-utils :refer [my-fixtures]]
    [gungnir.model :as gm]))

(test/use-fixtures
  :once
  my-fixtures)

(def table-employee
  {:up ["CREATE TABLE employee (id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY, taiwan_id text NOT NULL UNIQUE, company_id text NOT NULL UNIQUE, name text NOT NULL, birthday date NOT NULL, gender enum_gender NOT NULL, direct_kind enum_direct NOT NULL, employee_kind enum_employee NOT NULL, price_kind enum_price NOT NULL, reg_addr text NOT NULL, mail_addr text, unit_id text NOT NULL, bank_id bigint REFERENCES bank(id) ON DELETE RESTRICT ON UPDATE CASCADE, account text, work_place text, factory text, job_title text, job_title_2 text, phone text, mobile text, education text, education_period text, exception bytea, memo text, created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP);"
        "CREATE INDEX idx_employee_by_name ON employee(name);"],
   :down ["DROP TABLE IF EXISTS employee CASCADE;"],
   :id "101-employee"})

(def table-change
  {:up ["CREATE TABLE change (id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY, employee_id bigint NOT NULL REFERENCES employee(id) ON DELETE CASCADE ON UPDATE CASCADE, change_kind enum_change NOT NULL, change_day date NOT NULL DEFAULT CURRENT_DATE, created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, memo text);"
        "CREATE INDEX idx_change_by_employee_id ON change(employee_id);"],
   :down ["DROP TABLE IF EXISTS change CASCADE;"],
   :id "102-change"})

(def table-bank
  {:up ["CREATE TABLE bank (id bigint NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY, bank_id text NOT NULL UNIQUE, name text NOT NULL, memo text);"],
   :down ["DROP TABLE IF EXISTS bank CASCADE;"],
   :id "100-bank"})

(test/deftest table-test
  (test/testing "testing postgres/table"
    (test/is (= table-employee
                (table/generate-table-edn (:employee @gm/models))))
    (test/is (= table-change
                (table/generate-table-edn (:change @gm/models))))
    (test/is (= table-bank
                (table/generate-table-edn (:bank @gm/models))))))