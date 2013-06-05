(ns the-sleeping-barber.core)

;; Customer

(defn initial-customer-state []
  nil)

(defn make-customer []
  (agent nil))

(defn shaggy [customer]
  true)

;; Barber

(defn initial-barber-state []
  nil)

(defn make-barber []
  (agent nil))

(defn sleeping [barber]
  true)

;; Shop 
(defn make-shop [barber]
   barber)

(defn barber-chair [shop]
  shop)

