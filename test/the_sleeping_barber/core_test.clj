(ns the-sleeping-barber.core-test
  (:require [clojure.test :refer :all]
            [the-sleeping-barber.core :refer :all]))



; Limit the depth of printing, because we have circular data structures 
(set! *print-level* 5) 

(deftest self-test 
    (testing "self reads back set-self"  
    (let [person-state (make-person {})]
      (set-self person-state :new-self)
      (is (= :new-self (self person-state))))))

(deftest customer-test
  (testing "customers can be sent to"
    (let [customer (make-customer :one)]
      (is (= customer (send customer (fn [customer-state]))))))
  (testing "Initial customer state is shaggy"
    (let [customer (make-customer :one)]
      (is (shaggy @customer))))
  (testing "Hair scissors remove shaggyness from customer state"
    (let [customer (make-customer :one)]
      (is (not (shaggy (hair-scissors @customer))))))
  (testing "A customer's state has access to the customer using self"  
    (let [customer (make-customer :one)]
      (is (= customer (self @customer))))))

(deftest barber-test
  (testing "barber can be sent to"
    (let [barber (make-barber)]
    (is (= barber (send barber (fn [barber-state]))))))
  (testing "a barber is a barber"
    (let [barber (make-barber)]
    (is (is-barber? barber)))))
  (testing "A barber's state has access to the barber using self"  
    (let [barber (make-barber)]
      (is (= barber (self @barber)))))

(deftest shop-test
  (testing "The shop starts with a barber sleeping in the barber chair and no one waiting"
    (let [the-barber (make-barber)
          shop (make-shop the-barber 2)]
    (is (= the-barber (barber-chair shop)))
    (is (nil? (waiting-customer shop))) ))
  (testing "A customer can sit in the barber chair"
    (let [the-barber (make-barber)
          shop (make-shop the-barber 2)
          customer (make-customer :one)]
      (dosync (sit-barber-chair shop customer)
      (is (= customer (barber-chair shop)))) ))
  (testing "Customers can sit in waiting chairs"
    (let [the-barber (make-barber)
          shop (make-shop the-barber 2)
          customerOne (make-customer :one)
          customerTwo (make-customer :two)]
      (sit-waiting-chair shop customerOne)
      (await-for 1000 customerOne)
      (is (some #(= customerOne %) (waiting-chairs shop)))
      (is (= customerOne @(waiting-customer shop)))
      (sit-waiting-chair shop customerTwo)
      (is (some #(= customerOne %) (waiting-chairs shop)))
      (is (some #(= customerTwo %) (waiting-chairs shop))) )))

(deftest enter-shop-test
    (testing "When a customer enters a shop with a sleeping barber chair the 
              customer wakes the barber and sits in the barber chair"
        (let [barber (make-barber)
              shop (make-shop barber 2)
              customer (make-customer :one)
              barber-woken (atom false)]
          (with-redefs [tend-customers (fn [barber-state shop] (reset! barber-woken true))]
            (send customer enter-shop shop)
            (await-for 1000 customer barber)
            (is @barber-woken)
            (is (= customer (barber-chair shop))))))
    (testing "When another customer is in the chair a customer sits on a waiting chair"
      (let [barber (make-barber)
            customerOne (make-customer :one)
            shop (make-shop-with customerOne [nil])
            customerTwo (make-customer :two)]
        (send customerTwo enter-shop shop)
        (await-for 1000 customerTwo)
        (is (= customerOne (barber-chair shop)))
        (is (some #(= customerTwo %) (waiting-chairs shop))))))

(deftest cut-hair-test
  (testing "When the barber cuts hair a customers hair, they aren't shaggy"
    (let [customer (make-customer :one)]
    (is (shaggy @customer))
    (cut-hair customer)
    (await-for 1000 customer)
    (is (not (shaggy @customer))))))

(deftest tend-customers-test
  (testing "The barber cuts hair until the shop is empty"
    (let [barber (make-barber)
            customerOne (make-customer :one)
            customerTwo (make-customer :two)
            customerThree (make-customer :three)
            shop (make-shop-with customerOne [customerTwo customerThree])
          ]
      (send barber tend-customers shop)
      
      (await-for 1000 barber customerOne customerTwo customerThree) 
      (is (= barber (barber-chair shop)))
      (is (= [nil nil] (waiting-chairs shop)))
      (are [customer] (not (shaggy @customer))
        customerOne customerTwo customerThree))))