(ns solari.core
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cemerick.url :refer (url url-encode)]
            [solari.routes :as routes]
            [solari.data :as data]
            [solari.views.sidebar :as sb]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [enfocus.core :as ef])
  (:require-macros [enfocus.macros :as em])
  (:import goog.History))

(enable-console-print!)

(defn main []
  (data/data-init)
 (sb/nav-init data/nav-map)
  #_(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(sec/dispatch! (:anchor (url (.-token %)))))
  (doto h (.setEnabled true)))
  )
