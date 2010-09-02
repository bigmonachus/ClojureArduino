(ns arduino
  (:import [gnu.io CommPortIdentifier SerialPort SerialPortEvent])
  (:require [clojure.java.io :as io]
	    [clojure.stacktrace :as ss])
  (:gen-class))

(def raw-read (atom "")) ;;Points to data being read.

(def accel (atom [0 0 0]))

(defn parse-input
  "Called by the event listener when a newline is found."
  [s]
  (println s))

(defn get-port [^String name]
  (let [port-enum (CommPortIdentifier/getPortIdentifiers)
	port-list (loop [p port-enum
			 list [] ]
		    (if-not (. p hasMoreElements)
		      list			     
		      (recur p (conj list (. p nextElement)))))
	^CommPortIdentifier port-id  (first (filter #(= (.getName %1) name) port-list))]
    
    (if-not port-id
      (do (println "Port" name "not found."
		   "Here is a list of known ports:\n"
		   "--------------------")
	  (doseq [i (map #(.getName %) port-list)] (println i))
	  (println "--------------------")		   
	  (throw (new Exception "Port not found.")))      
      
      ;;    Else, Return new configured,open port      
      (let [^SerialPort port (.open port-id "Arduino" 2000)
	    listener (proxy [gnu.io.SerialPortEventListener
			     Runnable] []
		       (serialEvent [^gnu.io.SerialPortEvent ev]
				    (when (= (.getEventType ev)
					   SerialPortEvent/DATA_AVAILABLE)
				      ;;read data available.
				      (let [input-stream (.getInputStream port)
					    char (char (.read input-stream))]
					(swap! raw-read #(str % char))
					(when (= \newline char)
					  ;;call parse-input and reset raw-read
					  (parse-input @raw-read)
					  (swap! raw-read (fn [a] ""))
					  )))]
				      )))]
	(doto port
	  (. addEventListener listener)
	  (. notifyOnDataAvailable true)	    
	  (. setSerialPortParams 9600 SerialPort/DATABITS_8
	     SerialPort/STOPBITS_1
	     SerialPort/PARITY_NONE))))))

(defn -main []
  (with-open
      [port (get-port "COM3")
       ]
    (prn port)
    (Thread/sleep 5000)
    (println "Done.")
    ))

(-main)