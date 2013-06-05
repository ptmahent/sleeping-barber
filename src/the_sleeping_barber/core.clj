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

(defn waiting-customer [shop]
  (some (fn [x] (if (not @x) x)) (:waiting-chairs shop)))

(defn sit-waiting-chair [shop person]
  (dosync
   (ref-set (first-empty-waiting-chair shop) person)))

;; Customer

(defn initial-customer-state [id]
  {:type :customer :id id :hair-length :long :self (atom :soulless)})

(defn make-customer [id]
  (let [customer (agent (initial-customer-state id))]
    (send customer set-self customer)
    (await customer)
    customer))

(defn shaggy [customer-state]
  (= :long (:hair-length customer-state)))

(defn hair-scissors [customer-state]
  (assoc customer-state :hair-length :short))

(defn enter-shop [customer-state shop]
  (if (= (barber shop) (barber-chair shop))
    (sit-barber-chair shop (self customer-state))
    (sit-waiting-chair shop (self customer-state))))


;; Barber

(defn make-barber []
  (agent nil))

(defn cut-hair [barber customer]
  (send customer hair-scissors))
