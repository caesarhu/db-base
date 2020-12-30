(ns db-base.schema)

(def gender
  [:enum
   {:error/message "should be: 男|女"}
   "男" "女"])