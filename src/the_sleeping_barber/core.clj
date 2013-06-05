(ns the-sleeping-barber.core
   (:require [clojure.stacktrace :refer :all]))

;; Person
(defn make-person [state]
  (assoc state :self (atom :soulless)))

(defn self [person-state] 
  @(:self person-state))

(defn set-self [person-state new-self] 
  (reset! (:self person-state) new-self)
  person-state)

;; Shop 
(defn make-shop-with [sitting-in-barber-chair waiting-people]
   {:barber-chair (ref sitting-in-barber-chair) :waiting-chairs (map ref waiting-people)})

(defn make-shop [barber count-chairs]
   (make-shop-with barber (repeat  count-chairs nil)))

(defn barber-chair [shop]
  (deref (:barber-chair shop)))

(defn sit-barber-chair [shop person]
  (ref-set (:barber-chair shop) person))

(defn waiting-chairs [shop]
  (map deref (:waiting-chairs shop)))

(defn first-empty-waiting-chair [shop]
  (some (fn [x] (if (nil? @x) x)) (:waiting-chairs shop)))

(defn waiting-customer [shop]
  (some (fn [x] (if @x x)) (:waiting-chairs shop)))

(defn sit-waiting-chair [shop person]
  (dosync
   (ref-set (first-empty-waiting-chair shop) person)))

;; Barber

(defn make-barber []
  (let [barber (agent (make-person {:id :barber}))]
    (set-error-handler! barber (fn [agent exception] (print-cause-trace exception)))
    (send barber set-self barber)
    (await barber)
    barber))
  
(defn is-barber? [person]
  (= :barber (:id @person)))

;; Customer

(defn make-customer [id]
  (let [customer (agent (make-person {:type :customer :id id :hair-length :long}))]
    (set-error-handler! customer (fn [agent exception] (print-cause-trace exception)))
    (send customer set-self customer)
    (await customer)
    customer))

(defn shaggy [customer-state]
  (not (= :short (:hair-length customer-state))))

(defn hair-scissors [customer-state]
  (assoc customer-state :hair-length :short))

;; Actions

(defn cut-hair [customer]
  (send customer hair-scissors))

(defn tend-customers [barber-state shop]
    (let [customer (barber-chair shop)]
      (cut-hair customer))
    (dosync
      (let [waiting-customer-seat (waiting-customer shop)]
        (if waiting-customer-seat
          (let [customer @waiting-customer-seat]
            (ref-set waiting-customer-seat nil)
            (sit-barber-chair shop customer)
            (send (self barber-state) tend-customers shop))
          (sit-barber-chair shop (self barber-state)))))
  barber-state)

(defn enter-shop [customer-state shop]
  (dosync
    (let [maybe-barber (barber-chair shop)]
      (if (is-barber? maybe-barber)
        (do 
          (send maybe-barber tend-customers shop)
          (sit-barber-chair shop (self customer-state)))
        (sit-waiting-chair shop (self customer-state)))))
  customer-state)

