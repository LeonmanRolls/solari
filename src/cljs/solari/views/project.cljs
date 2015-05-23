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

    om/IInitState
    (init-state [this]
      (println "Project" data))

    om/IDidMount
    (did-mount [this]
     (.royalSlider (js/$ ".royalSlider") #js {:keyboardNavEnabled true :controlNavigation "thumbnails"}))

    om/IRender
    (render [this]
      (dom/div nil
              (dom/div #js {:className "royalSlider rsDefault"}
                       (dom/img #js {:className "rsImg" :src "/img/lyall.jpg"})
                       (dom/img #js {:className "rsImg" :src "/img/lyall.jpg"})
                       (dom/img #js {:className "rsImg" :src "/img/lyall.jpg"})
                       (dom/img #js {:className "rsImg" :src "/img/lyall.jpg"})
                       (dom/img #js {:className "rsImg" :src "/img/lyall.jpg"})
                       (dom/img #js {:className "rsImg" :src "/img/lyall.jpg"}))


               (dom/p #js {:className "text-area"} "Some text")))))

(defn project-init [project-atom]
  (om/root project-page project-atom
           {:target (. js/document (getElementById "main-content-container"))}))

