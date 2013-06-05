(ns the-sleeping-barber.core)

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
    (send barber set-self barber)
    (await barber)
    barber))
  
(defn is-barber? [person]
  (= :barber (:id @person)))

;; Customer

(defn make-customer [id]
  (let [customer (agent (make-person {:type :customer :id id :hair-length :long}))]
    (send customer set-self customer)
    (await customer)
    customer))

(defn shaggy [customer-state]
  (= :long (:hair-length customer-state)))

(defn hair-scissors [customer-state]
  (assoc customer-state :hair-length :short))

;; Actions

(defn enter-shop [customer-state shop]
  (if (is-barber? (barber-chair shop))
    (dosync (sit-barber-chair shop (self customer-state)))
    (sit-waiting-chair shop (self customer-state)))
  customer-state)

(defn cut-hair [barber-state customer]
  (send customer hair-scissors)
  barber-state)

(defn seat-waiting-customer-or-sleep [barber-state shop]
  (dosync
    (let [waiting-customer-seat (waiting-customer shop)]
      (if waiting-customer-seat
        (let [customer @waiting-customer-seat]
          (ref-set waiting-customer-seat nil)
          (sit-barber-chair shop customer))
        (sit-barber-chair shop (self barber-state))))))
