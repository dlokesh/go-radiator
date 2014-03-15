(ns go-radiator.main
	(:require [cemerick.url :refer [query->map]]
			  [clojure.walk :refer [keywordize-keys]]
			  [clojure.set :refer [difference]]
			  [clojure.string :refer [split]]))

(defn get-params []
	(-> (subs (-> js/window .-location .-search) 1)
		query->map keywordize-keys))

(defn ^:export jobs-to-display [data]
	(into-array (difference (set (split (:include_jobs (get-params)) #",")) 
				(set data))))

(.write js/document 
	(str "Hello, ClojureScript!" (get-params)))