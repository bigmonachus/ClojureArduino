(ns arduino
  (:import [gnu.io CommPortIdentifier SerialPort SerialPortEvent])
  (:require [clojure.java.io :as io]
	    [clojure.stacktrace :as ss])
  (:gen-class))

(def last-line (atom nil));last line read.
(def raw-read (atom [])) ;;Points to data being read.

(def accel (atom [0 0 0]))

(defn parse-input
    ;;Input should be of the form xxyyzz. xx should coerce into a 16-bit int.
  [s]

  (if (= (count s) 6)
    (let [[x-l x-h
	   y-l y-h
	   z-l z-h] s
	   coerce (fn [a b]
		    (let [num (+ (bit-shift-left (int a) 8) (int b))]
		      (if (> num (dec (Math/pow 2 16))) ;;16 bit signed integer within a 32 bit integer... fix this
			(- num (Math/pow 2 16))
			num)))
	   x (coerce x-h x-l)
	   y (coerce y-h y-l)
	   z (coerce z-h z-l)]
      (swap! accel (fn [a] [x y z])))
    (print s)))

(defn get-port [^String name]
  (let [port-enum (CommPortIdentifier/getPortIdentifiers)
	port-list (loop [p port-enum
			 list []]
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
					    bytes-avail (.available input-stream)
					    buffer (byte-array bytes-avail)
					    bytes-read (.read input-stream buffer)]					
					(swap! raw-read concat (seq buffer))
					(try (swap! last-line (fn [a] (->> @raw-read
								     (reverse)
								     (drop-while #(not= (int \newline) %))
								     (drop 2)
								     (take-while #(not= (int \newline)  %))
								     (reverse))))
					     ;;Exception: no problem.. it was a negative char
					     (catch RuntimeException e ))))))]
	(doto port
	  (. addEventListener listener)
	  (. notifyOnDataAvailable true)	    
	  (. setSerialPortParams 9600 SerialPort/DATABITS_8
	     SerialPort/STOPBITS_1
	     SerialPort/PARITY_NONE))))))

(defn -main []
  (with-open [port (get-port "COM3")]
    ;;wait for line
    (loop [line @last-line]
      (when-not line
	(Thread/sleep 100)
	(recur @last-line)))
    
    (doseq [i (range 200)]
      (parse-input @last-line)
      (println @accel)
      (Thread/sleep 25)) ;;40hz approx
    (println "Done.")))

(-main)