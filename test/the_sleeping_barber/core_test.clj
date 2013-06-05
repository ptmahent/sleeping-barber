(ns the-sleeping-barber.core-test
  (:require [clojure.test :refer :all]
            [the-sleeping-barber.core :refer :all]))

(set! *print-level* 5)

(deftest self-test 
    (testing "self reads back set-self"  
    (let [customer-state (initial-customer-state)]
      (set-self customer-state :new-self)
      (is (= :new-self (self customer-state))))))

(deftest customer-test
  (testing "customers can be sent to"
    (let [customer (make-customer)]
      (is (= customer (send customer (fn []))))))
  (testing "Initial customer state is shaggy"
    (let [customer (make-customer)]
      (is (shaggy @customer))))
  (testing "A customer's state has access to the customer using self"  
    (let [customer (make-customer)]
      (is (= customer (self @customer))))))

(deftest barber-test
  (testing "barber can be sent to"
    (let [barber (make-barber)]
    (is (= barber (send barber (fn []))))))
  (testing "Initial barber state is sleeping"
    (let [barber (make-barber)]
    (is (sleeping @barber)))))

(deftest shop-test
  (testing "The shop starts with a barber sleeping in the barber chair"
    (let [barber (make-barber)
          shop (make-shop barber)]
    (is (= barber (barber-chair shop))))))

(deftest enter-shop-test
  (testing "When a customer enters a shop with a sleeping barber chair the customer sits in the barber chair"
    (let [dummy-barber (agent "dummy-barber")
          shop (make-shop dummy-barber)
          customer (make-customer)]
      
    (send customer enter-shop shop)
    (await customer)
    (is (= customer (barber-chair shop))))))

