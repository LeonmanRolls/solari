(ns solari.views.common
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn p-partial [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/p #js {:style #js {}}
             (dom/b nil (:bold data))
             (:paragraph data)))))


(defn p-partial-white [data owner]
  (reify
    om/IInitState
    (init-state [this]
      (println "data" data))

    om/IRender
    (render [this]
      (dom/p #js {:style #js {:color "white" :marginBottom "30px"}}
             (dom/b nil (:bold data))
             (:paragraph data)))))

