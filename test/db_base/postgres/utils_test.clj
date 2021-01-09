(ns db-base.postgres.utils-test
  (:require [clojure.test :refer :all]
            [db-base.postgres.utils :refer :all]))

(deftest sql-command-test
  (testing "testing sql-command"
    (is (= "INSERT INTO properties (name, surname, age) VALUES ('Jon', 'Smith', 34), ('Andrew', 'Cooper', 12), ('Jane', 'Daniels', 56)"
           (sql-command ["INSERT INTO properties (name, surname, age) VALUES (?, ?, ?), (?, ?, ?), (?, ?, ?)" "Jon" "Smith" 34 "Andrew" "Cooper" 12 "Jane" "Daniels" 56])))))