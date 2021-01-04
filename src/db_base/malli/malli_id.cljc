(ns db-base.malli.malli-id
  (:require [taiwan-id.core :as id]
            [malli.core :as m]))

(def taiwan-id
  (m/-simple-schema
    {:type :taiwan-id
     :pred id/some-id?
     :type-properties {:error/message "必須是合法身分證號或外籍證號"
                       :json-schema/type "string"
                       :json-schema/format "string"
                       :gen/gen id/taiwan-gen}}))

(def company-id
  [:re {:error/message "必須是合法員工編號-5位數字或Z開頭4位數字"} #"^[Z\d]\d{4}$"])

(def bank-id
  [:re {:error/message "必須是合法銀行代號-7位數字"} #"^\d{7}$"])

(def account
  [:re {:error/message "必須是合法銀行帳號-10位以上數字"} #"^\d{9}\d+$"])