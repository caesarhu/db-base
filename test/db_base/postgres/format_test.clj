(ns db-base.postgres.format-test
  (:require
    [clojure.test :as test]
    [db-base.postgres.format] ; must require for honeysql format
    [db-base.schema :as db-schema]
    [db-base.test-utils :refer [my-fixtures]]
    [honeysql-postgres.helpers :as psqlh]
    [honeysql.core :as sql]))

(test/use-fixtures
  :once
  my-fixtures)


(test/deftest generated-test
  (test/testing "testing fn-handler generated"
    (test/is (= ["GENERATED ALWAYS AS IDENTITY"]
                (-> (sql/call :generated)
                    sql/format)))
    (test/is (= ["GENERATED always AS IDENTITY"]
                (-> (sql/call :generated "always")
                    sql/format)))
    (test/is (= ["GENERATED BY DEFAULT AS IDENTITY"]
                (-> (sql/call :generated "BY DEFAULT")
                    sql/format)))))


(test/deftest references-test
  (test/testing "testing fn-handler references"
    (test/is (= ["REFERENCES employee(id)"]
                (-> (sql/call :references :table :employee :column :id)
                    sql/format)))
    (test/is (= ["REFERENCES employee(id, id2)"]
                (-> (-> (sql/call :references :table :employee :column [:id :id2]))
                    sql/format)))
    (test/is (= ["REFERENCES employee(id) ON DELETE CASCADE"]
                (-> (sql/call :references :table :employee :column :id :on-delete "CASCADE")
                    sql/format)))
    (test/is (= ["REFERENCES employee(id) ON DELETE RESTRICT ON UPDATE CASCADE"]
                (-> (sql/call :references :table :employee :column :id :on-delete "RESTRICT"  :on-update "CASCADE")
                    sql/format)))))


(test/deftest create-index-test
  (test/testing "testing fn-handler create-index"
    (test/is (= ["CREATE INDEX idx_employee_by_name ON employee(name);"]
                (-> (sql/call :create-index :index-name :idx_employee_by_name
                              :table :employee :column :name)
                    sql/format)))
    (test/is (= ["CREATE INDEX idx_employee_by_name ON employee(unit_id, name);"]
                (-> (sql/call :create-index :index-name :idx_employee_by_name
                              :table :employee :column [:unit-id :name])
                    sql/format)))
    (test/is (= ["CREATE UNIQUE INDEX idx_employee_by_name ON employee(unit_id, name);"]
                (-> (sql/call :create-index :index-name :idx_employee_by_name
                              :table :employee :column [:unit-id :name]
                              :unique true)
                    sql/format)))))

