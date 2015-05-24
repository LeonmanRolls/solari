(ns solari.views.project
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
      (dom/img #js {:className "rsImg" :src (str "/img/wadestown/" data )}))))

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

(defn project-page [data owner]
  (reify

    om/IInitState
    (init-state [this]
      (println "Project: " data ))

    om/IDidMount
    (did-mount [this]
      (do
        (.royalSlider (js/$ ".royalSlider") #js {:keyboardNavEnabled true :controlNavigation "thumbnails"
                                                 :fullscreen #js {:enabled true :nativeFS false}
                                                 :visibleNearby #js {:enabled true :centerArea 0.5 :center true
                                                                     :breakpoint 650 :breakpointCenterArea 0.64
                                                                     :navigateByCenterClick true}})
        (js/accordion)))

    om/IRender
    (render [this]
      (dom/div nil

               (apply dom/div #js {:className "royalSlider rsDefault"}
                      (om/build-all img-page (:gallery-images data)))

               (dom/div #js {:className "accordion"}

                        (apply dom/dl nil
                               (om/build-all accordion-page (:accordion data))))))))


(defn project-init [project-atom]
  (do (om/root project-page project-atom
               {:target (. js/document (getElementById "main-content-container"))})
      (ef/at ".context" (ef/content (:title @project-atom)))))

