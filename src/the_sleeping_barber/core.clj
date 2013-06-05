(ns the-sleeping-barber.core)

;; Person
(defn self [person-state] 
  @(:self person-state))

(defn set-self [person-state new-self] 
  (reset! (:self person-state) new-self)
  person-state)

;; Shop 
(defn make-shop [barber count-chairs]
   {:barber barber :barber-chair (ref barber) :waiting-chairs (repeatedly count-chairs #(ref nil))})

(defn barber [shop]
  (:barber shop))

(defn barber-chair [shop]
  (deref (:barber-chair shop)))

(defn sit-barber-chair [shop person]
  (dosync
   (ref-set (:barber-chair shop) person)))

(defn waiting-chairs [shop]
  (map deref (:waiting-chairs shop)))

(defn first-empty-waiting-chair [shop]
  (some (fn [x] (if (nil? @x) x)) (:waiting-chairs shop)))

(defn sit-waiting-chair [shop person]
  (dosync
   (ref-set (first-empty-waiting-chair shop) person)))

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

(defn enter-shop [customer-state shop]
  (if (= (barber shop) (barber-chair shop))
    (sit-barber-chair shop (self customer-state))
    (sit-waiting-chair shop (self customer-state))))

;; Barber

(defn initial-barber-state []
  nil)

(defn make-barber []
  (agent nil))

(defn sleeping [barber]
  true)

