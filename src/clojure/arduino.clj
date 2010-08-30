(ns arduino
  (:import [gnu.io CommPortIdentifier SerialPort])
  (:require [clojure.java.io :as io]
	    [clojure.stacktrace :as ss])
  (:gen-class))

(defn get-port [^String name]
  (let [port-enum (CommPortIdentifier/getPortIdentifiers)
	port-list (loop [p port-enum
			 list [] ]
		    (if-not (. p hasMoreElements)
		      list			     
		      (recur p (conj list (. p nextElement)))))
	port  (first (filter #(= (.getName %1) name) port-list))]
    
    (if-not port
      (do (println "Port" name "not found."
		   "Here is a list of known ports:\n"
		   "--------------------")
	  (doseq [i (map #(.getName %) port-list)] (println i))
	  (println "--------------------")		   
	  (throw (new Exception "Port not found.")))      
      
      ;;    Else, Return new configured,open port      
      (doto (.open port "Arduino" 100)
	(. setSerialPortParams 9600 SerialPort/DATABITS_8
	   SerialPort/STOPBITS_1
	   SerialPort/PARITY_NONE)))))

(defn -main []
  (with-open
      [port (get-port "COM3")
       in (io/reader (.getInputStream port))]
    
    ))

(-main)