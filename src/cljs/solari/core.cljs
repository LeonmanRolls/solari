(ns solari.core
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
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
 #_(routes/dispatch-route "/")
 (sb/nav-init data/nav-map)
  )
