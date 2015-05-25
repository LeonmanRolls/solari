(ns solari.views.yourteam
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn img-page [data owner]
  (reify

    om/IInitState
    (init-state [this]
      (println "img: " data))

    om/IRender
    (render [this]
      (dom/img #js {:className "rsImg" :src (str "/img/" data )}))))

(defn accordion-page [data owner]
  (reify

    om/IInitState
    (init-state [this]
      #_(println "accordion: " data))

    om/IRender
    (render [this]
      (dom/div nil
               (dom/dt nil
                       (dom/a #js {:href "#accordion1" :aria-expanded "false" :aria-controls "accordion1"
                                   :className "accordion-title accordionTitle js-accordionTrigger"}
                              (:title data)))

               (dom/dd #js {:className "accordion-content accordionItem is-collapsed" :id "accordion1"
                            :aria-hidden "true"}
                       (dom/p nil (:content data)))))))

(defn polaroid-page [data owner]
  (reify

    om/IInitState
    (init-state [this]
      (println "Project: " data ))

    om/IDidMount
    (did-mount [this]
      (do
        (js/photostack js/window)))

    om/IRender
    (render [this]
      (dom/section #js {:id "photostack-3" :className "photostack"}
                   (dom/div nil
                            (dom/figure nil
                                        (dom/a #js {:href "#" :className "photostack-img"}
                                               (dom/img #js {:src "/img/lyall.jpg"}))
                                        (dom/figcaption nil
                                                        (dom/h2 #js {:className "photostact-title"} "Happy Days")
                                                        (dom/div #js {:className "photostack-back"}
                                                                 (dom/p nil "Hello there"))))

                            (dom/figure nil
                                        (dom/a #js {:href "#" :className "photostack-img"}
                                               (dom/img #js {:src "/img/lyall.jpg"}))
                                        (dom/figcaption nil
                                                        (dom/h2 #js {:className "photostact-title"} "Happy Days")
                                                        (dom/div #js {:className "photostack-back"}
                                                                 (dom/p nil "Hello there"))))

                            (dom/figure nil
                                        (dom/a #js {:href "#" :className "photostack-img"}
                                               (dom/img #js {:src "/img/lyall.jpg" :className "polaroid-img"}))
                                        (dom/figcaption nil
                                                        (dom/h2 #js {:className "photostact-title"} "Happy Days")
                                                        (dom/div #js {:className "photostack-back"}
                                                                 (dom/p nil "Hello there"))))


                            )


                  (dom/nav nil )


               ))))


(defn your-team-init [team-atom]
  (do (om/root polaroid-page team-atom
               {:target (. js/document (getElementById "main-content-container"))})
      (ef/at ".context" (ef/content (:title @team-atom)))))

