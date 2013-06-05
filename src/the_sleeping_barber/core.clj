(ns the-sleeping-barber.core)

;; Person
(defn self [person-state] 
  @(:self person-state))

(defn set-self [person-state new-self] 
  (reset! (:self person-state) new-self)
  person-state)

;; Shop 
(defn make-shop [barber]
   {:barber barber :barber-chair (ref barber) :waiting-chairs [(ref nil)]})

(defn barber-chair [shop]
  (deref (:barber-chair shop)))

(defn barber [shop]
  (:barber shop))

(defn waiting-chairs [shop]
  (map deref (:waiting-chairs shop)))

(defn sit-barber-chair [shop person]
  (dosync
   (ref-set (:barber-chair shop) (self person))))

(defn sit-waiting-chair [shop person]
  (dosync
   (ref-set (first (:waiting-chairs shop)) (self person))))



;; Customer

(defn initial-customer-state [id]
  {:type :customer :id id :self (atom :soulless)})

(defn make-customer [id]
  (let [customer (agent (initial-customer-state id))]
    (send customer set-self customer)
    (await customer)
    customer))

(defn shaggy [customer]
  true)

(defn enter-shop [customer shop]
  (if (= (barber shop) (barber-chair shop))
    (sit-barber-chair shop customer)
    (sit-waiting-chair shop customer)))

;; Barber

(defn initial-barber-state []
  nil)

(defn make-barber []
  (agent nil))

(defn sleeping [barber]
  true)

