(ns the-sleeping-barber.core)

(defn initial-customer-state []
  nil)

(defn make-customer []
  (agent nil))

(defn shaggy [customer]
  true)

(defn initial-barber-state []
  nil)

(defn make-barber []
  (agent nil))

(defn sleeping [barber]
  true)

