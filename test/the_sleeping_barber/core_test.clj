(ns the-sleeping-barber.core-test
  (:require [clojure.test :refer :all]
            [the-sleeping-barber.core :refer :all]))

(deftest customer-test
  (testing "customers can be sent to"
    (let [customer (make-customer)]
    (is (= customer (send customer (fn []))))))
  (testing "A customer that hasn't been sent to starts in the initial state"
    (let [customer (make-customer)]
    (is (= (initial-customer-state) (deref customer)))))
  (testing "Initial customer state is shaggy"
    (let [customer (make-customer)]
    (is (shaggy (initial-customer-state))))))

