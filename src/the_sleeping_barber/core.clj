(ns the-sleeping-barber.core)

;; Person
(defn self [person-state] 
  @(:self person-state))

(defn set-self [person-state new-self] 
  (reset! (:self person-state) new-self)
  person-state)

;; Shop 
(defn make-shop [barber]
   {:barber-chair (ref barber)})

(defn barber-chair [shop]
  (deref (:barber-chair shop)))

(defn sit-barber-chair [shop person]
  (dosync
   (ref-set (:barber-chair shop) (self person))))

;; Customer

(defn initial-customer-state []
  {:type :customer :self (atom :soulless)})

(defn make-customer []
  (let [customer (agent (initial-customer-state))]
    (send customer set-self customer)
    (await customer)
    customer))

(defn shaggy [customer]
  true)

(defn enter-shop [customer-state shop]
  (sit-barber-chair shop customer-state))

;; Barber

(defn initial-barber-state []
  nil)

(defn make-barber []
  (agent nil))

(defn sleeping [barber]
  true)

