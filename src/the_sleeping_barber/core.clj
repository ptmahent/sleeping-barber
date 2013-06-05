(ns the-sleeping-barber.core)

(defn initial-customer-state []
  nil)

(defn make-customer []
  (agent nil))

(defn shaggy [customer]
  true)