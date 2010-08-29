(defproject arduino "1.0"
  :description "Communicate"
  :dependencies [[org.clojure/clojure "1.2.0"]
		 [org.clojure/clojure-contrib "1.2.0"]
		 [javax.comm/comm "2.1-7"]]
  :dev-dependencies [[swank-clojure "1.2.1"]]
  :warn-on-reflection true
  :uberjar-name "arduino.jar"
  :jvm-opts []
  :main arduino)
		 