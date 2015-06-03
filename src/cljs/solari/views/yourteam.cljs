(ns solari.views.yourteam
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn polaroid-partial [data owner]
 (reify
   om/IInitState
   (init-state [this]
     (println "polaroid data: " data ))

   om/IRender
   (render [this]
     (dom/figure #js {:className "flippable" :onClick ""}
                 (dom/a #js {:href "#" :className "photostack-img"}
                        (dom/img #js {:src (:hipster (:profilepics data))}))
                 (dom/figcaption nil
                                 (dom/h2 #js {:className "photostact-title"} (:name data))
                                 (dom/div #js {:className "photostack-back"}
                                          (dom/p #js {:style #js {:fontFamily "GiveYouGlory"}} (:polaroid data))))))))

(defn polaroid-page [data owner]
  (reify

    om/IInitState
    (init-state [this])

    om/IDidMount
    (did-mount [this]
      (do
        (js/photostack js/window)))

    om/IRender
    (render [this]
      (dom/section #js {:id "photostack-3" :className "photostack"
                        :style #js {:marginLeft "-350px"
                                    :marginRight "-350px"
                                    :background "url(/img/polaroid_background.jpg) no-repeat center center fixed;"}}
                   (apply dom/div nil
                          (om/build-all polaroid-partial (:team-members data)))
                   (dom/nav nil )))))

(defn your-team-init [team-atom]
  (do (om/root polaroid-page team-atom
               {:target (. js/document (getElementById "main-content-container"))})
      (ef/at ".context" (ef/content (:title @team-atom)))))

