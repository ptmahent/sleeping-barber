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

(deftest barber-test
  (testing "barber can be sent to"
    (let [barber (make-barber)]
    (is (= barber (send barber (fn []))))))
  (testing "A barber that hasn't been sent to starts in the initial state"
    (let [barber (make-barber)]
    (is (= (initial-barber-state) (deref barber)))))
  (testing "Initial barber state is sleeping"
    (let [barber (make-barber)]
    (is (sleeping (initial-barber-state))))))

