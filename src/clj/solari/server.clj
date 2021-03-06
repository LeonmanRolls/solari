(ns solari.server
  (:require [clojure.java.io :as io]
            [solari.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [compojure.core :refer [GET defroutes ANY POST]]
            [compojure.route :refer [resources]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [solari.model :as m]
            [net.cgrand.reload :refer [auto-reload]]
            [ring.middleware.reload :as reload]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]))
(use 'ring.middleware.multipart-params)

(deftemplate page (io/resource "index.html") []
  [:body] (if is-dev? inject-devmode-html identity))

(defn upload-file
  [file]
  #_(ds/copy (file :tempfile) (ds/file-str "file.out"))
  #_(render (upload-success)))

(defroutes routes
           (resources "/")
           (ANY "/alldata/" request m/all-data-resource)
           (ANY "/twitter/" request m/twitter-resource)
           (wrap-multipart-params
             (POST "/imgupload/"
                   {{{tempfile :tempfile filename :filename} :file} :params :as params}
               (do (println "params: " (:params params) #_(:filename (get (:params params) :file)))
                   #_(println "tempfile: " params)
                   (io/copy (:tempfile (get (:params params) :file))
                            (io/file "resources" "public" "img" "leaderboards"
                                     "group_photo_everyday.jpg")))
               "success"))
           (resources "/react" {:root "react"})
           (GET "/*" req (page)))

(def http-handler
  (if is-dev?
    (wrap-gzip (reload/wrap-reload (wrap-defaults #'routes api-defaults)))
    (wrap-gzip (wrap-defaults routes api-defaults))))

(defn run-web-server [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (print "Starting web server on port" port ".\n")
    (run-jetty http-handler {:port port :join? false})))

(defn run-auto-reload [& [port]]
  (auto-reload *ns*)
  (start-figwheel))

(defn run [& [port]]
  (when is-dev?
    (run-auto-reload))
  (run-web-server port))

(defn -main [& [port]]
  (run port))

