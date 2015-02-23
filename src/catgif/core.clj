(ns catgif.core
  (:gen-class)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [feedparser-clj.core :as parser])
  (:use org.httpkit.server))

(def url "http://dailycatgif.tumblr.com/rss")

(defroutes app
  (GET "/" []
       (-> url
           (parser/parse-feed)
           (:entries)
           (rand-nth)
           (:description)
           (:value)))
  (route/not-found "<h1>Page not found</h1>"))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server []
  (reset! server (run-server app {:port 8008})))

(defn restart-server []
  (stop-server)
  (start-server))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (start-server))
