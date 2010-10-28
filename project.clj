(defproject arduino "1.0"
  :description "Communicate"
  :dependencies [[org.clojure/clojure "1.2.0"]
		 [org.clojure/clojure-contrib "1.2.0"]
		 ;;		 [javax.comm/comm "2.1-7"] ;;win
		 [javax.comm/comm "2.2"]
		 [penumbra "0.6.0-SNAPSHOT"]]
;  :native-dependencies [[penumbra/lwjgl "2.4.2"]]
  :dev-dependencies [[native-deps "1.0.3"]
		     [swank-clojure "1.2.1"]]
  :warn-on-reflection true
  :source-path "src/clojure/"
  :uberjar-name "arduino.jar"
  :jvm-opts ["-Xbootclasspath/a:lib/clojure-1.2.0.jar" ;;improve startup time..
	     "-Xms25M"
	     "-Xmx128M"] 
  :main arduino)
		 