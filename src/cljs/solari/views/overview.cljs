(ns solari.views.overview
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [cljs.core.async :refer [put! chan <! >! take! close!]]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defn project-tumbnail [data owner]
  (reify
    om/IInitState
    (init-state [this]
      (println "Project" data))

    om/IDidMount
    (did-mount [this]
      (ef/at (str "#" (:projectid data))
             (ev/listen :click
                        #(go (>! (:route-chan (om/get-state owner)) (str "/" (:projectid data)))))))

    om/IRender
    (render [this]
      (do
       (println "data: " data)
       (dom/li nil
              (dom/figure nil
                          (dom/div nil
                                   (dom/img #js {:src (:thumbnail data)})
                                   (dom/figcaption nil
                                                   (dom/h3 nil (:title data))
                                                   (dom/a #js {:id (:projectid data)} "Take a look")))))))))

(defn overview-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil
               (dom/p #js {:className "text-area"} (:text data))
               (dom/div #js {:className "row"}

                        (apply dom/ul #js {:className "grid cs-style-4"}
                                       (om/build-all project-tumbnail (:projects data)
                                                     {:init-state {:route-chan (:route-chan (om/get-state owner))}})))))))

(defn overview-init [overview-atom route-chan]
  (println "overview: " overview-atom)
  (om/root overview-page overview-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :init-state {:route-chan route-chan}}))

