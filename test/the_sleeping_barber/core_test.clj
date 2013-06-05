(ns the-sleeping-barber.core-test
  (:require [clojure.test :refer :all]
            [the-sleeping-barber.core :refer :all]))

(set! *print-level* 5)

(deftest self-test 
    (testing "self reads back set-self"  
    (let [customer-state (initial-customer-state :one)]
      (set-self customer-state :new-self)
      (is (= :new-self (self customer-state))))))

(deftest customer-test
  (testing "customers can be sent to"
    (let [customer (make-customer :one)]
      (is (= customer (send customer (fn []))))))
  (testing "Initial customer state is shaggy"
    (let [customer (make-customer :one)]
      (is (shaggy @customer))))
  (testing "A customer's state has access to the customer using self"  
    (let [customer (make-customer :one)]
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
    (let [the-barber (make-barber)
          shop (make-shop the-barber)]
    (is (= the-barber (barber shop)))
    (is (= the-barber (barber-chair shop))) )))

(deftest enter-shop-test
  (testing "When a customer enters a shop with a sleeping barber chair the customer sits in the barber chair"
    (let [dummy-barber (agent "dummy-barber")
          shop (make-shop dummy-barber)
          customer (make-customer :one)]
    (send customer enter-shop shop)
    (await customer)
    (is (= customer (barber-chair shop)))))
  (testing "When another customer is in the chair a customer sits on a waiting chair"
    (let [dummy-barber (agent "dummy-barber")
          shop (make-shop dummy-barber)
          customerOne (make-customer :one)
          customerTwo (make-customer :two)]
    (send customerOne enter-shop shop)
    (await customerOne)
    (send customerTwo enter-shop shop)
    (await customerTwo)
    (is (= customerOne (barber-chair shop)))
    (is (some #(= customerTwo %) (waiting-chairs shop))))))

