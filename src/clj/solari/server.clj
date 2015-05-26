(ns solari.server
  (:require [clojure.java.io :as io]
            [solari.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [compojure.core :refer [GET defroutes ANY]]
            [compojure.route :refer [resources]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [solari.model :as m]
            [net.cgrand.reload :refer [auto-reload]]
            [ring.middleware.reload :as reload]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]))

(deftemplate page (io/resource "index.html") []
  [:body] (if is-dev? inject-devmode-html identity))


(defroutes routes
           (resources "/")
           (ANY "/faqs/" request m/faqs)
           (ANY "/team/" request m/team)
           (ANY "/process/" request m/process)
           (ANY "/projects/" request m/projects)
           (ANY "/home/" request m/home)
           (resources "/react" {:root "react"})
           (GET "/*" req (page)))

(def http-handler
  (if is-dev?
    (reload/wrap-reload (wrap-defaults #'routes api-defaults))
    (wrap-defaults routes api-defaults)))

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
