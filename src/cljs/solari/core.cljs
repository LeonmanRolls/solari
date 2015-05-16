(ns solari.core
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [solari.views.sidebar :as sb]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [enfocus.core :as ef])
  (:require-macros [enfocus.macros :as em])
  (:import goog.History))

;Fallback for browsers without html5 history support
(sec/set-config! :prefix "#")

;enable html5 history
(let [history (History.)
      navigation EventType/NAVIGATE]
  (goog.events/listen history
                      navigation
                      #(-> % .-token sec/dispatch!))
  (doto history (.setEnabled true)))

(enable-console-print!)

(defroute "/users/:id" {:as params}
          (js/console.log (str "User: " (:id params))))

(defn home-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "isdfsd")
      )))

(defroute "/" {:as params}
          (om/root home-page {}
                   {:target (. js/document (getElementById "main-content-container"))}))

(sec/dispatch! "/")

