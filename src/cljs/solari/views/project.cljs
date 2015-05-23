(ns solari.views.project
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn project-page [data owner]
  (reify
    om/IRender
    (render [this]

      (dom/div nil
               (dom/p #js {:className "text-area"} "Some text")))))

(defn project-init [project-atom]
  (om/root project-page project-atom
           {:target (. js/document (getElementById "main-content-container"))}))

