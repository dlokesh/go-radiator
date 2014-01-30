(ns go-radiator.core
  (:require [cheshire.core :as json]
            [clojure.xml :as xml]
            [clojure.java.browse :refer [browse-url]])
  (:import [org.webbitserver WebServer WebServers WebSocketHandler]
           [org.webbitserver.handler EmbeddedResourceHandler])
  (:gen-class))

(def connections (atom {}))
(def cctray-feed (atom ""))

(defn pipeline-name [{{name :name} :attrs}]
  (first (clojure.string/split name #"::")))

(defn pipelines-by-name [build-data]
  (group-by pipeline-name (-> build-data :content)))

(defn derive-status [stages]
  (cond
    (some #(-> % :attrs :activity (= "Building")) stages) "building"
    (some #(-> % :attrs :lastBuildStatus (= "Failure")) stages) "failed"
    :else "passed"))

(defn fetch-build-status []
  (let [build-data (xml/parse @cctray-feed)
        pipelines (pipelines-by-name build-data)]
    (into {} (for [[k v] pipelines] [k (derive-status v)]))))

(defn send-messages []
  (future (loop []
            (try
              (let [data (fetch-build-status)]
                (doseq [connection @connections]
                  (.send (key connection) (json/generate-string data))))
              (Thread/sleep 10000)
              (catch Exception e (println (.getMessage e))))
            (flush)
            (recur))))

(defn -main [& args]
  (swap! cctray-feed str (first args))
  (send-messages)
  (doto (WebServers/createWebServer 6734)
    (.add "/"
          (proxy [WebSocketHandler] []
            (onOpen [c] (swap! connections assoc c true))
            (onClose [c] (swap! connections dissoc c))))
    (.add (EmbeddedResourceHandler. "public"))
    (.start))
  (browse-url "http://localhost:6734/index.html"))
