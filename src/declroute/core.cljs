(ns declroute.core
  (:require [reagent.core :as r]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [clojure.string :as str])
  (:import goog.History))


(enable-console-print!)

(defonce uri-to-page (atom {}))
(defonce page-to-uri (atom {}))

(defn set-page-uri [page uri]
  (swap! uri-to-page assoc uri page)
  (swap! page-to-uri assoc page uri))

(defn parse-hash [uri]
  (let [func-map @uri-to-page]
    (let [[uri & args] (->> (str/split uri #"[/]")
                            (map js/decodeURIComponent))]
      (-> (cond (contains? func-map uri)
                (func-map uri)
                :else
                (:default func-map))
          (cons args)
          vec))))

(defn build-uri [[func & args]]
  (str "#"
       (->> (cons (@page-to-uri func) args)
            (map js/encodeURIComponent)
            (str/join "/"))))

(defn watch-uri [page-atom]
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (let [hash (.-token event)]
         (reset! page-atom (parse-hash hash)))))
    (.setEnabled true)))

