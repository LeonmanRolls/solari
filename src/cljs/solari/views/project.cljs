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
      #_(println "Project" data))

    om/IDidMount
    (did-mount [this]
      (do
        (.royalSlider (js/$ ".royalSlider") #js {:keyboardNavEnabled true :controlNavigation "thumbnails"
                                               :fullscreen #js {:enabled true :nativeFS false}
                                               :visibleNearby #js {:enabled true :centerArea 0.5 :center true
                                                                   :breakpoint 650 :breakpointCenterArea 0.64
                                                                   :navigateByCenterClick true}})
        (js/accordion)
        )
      )

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


               (dom/p #js {:className "text-area"} "Some text")

              (dom/div #js {:className "accordion"}
                       (dom/dl nil

                               (dom/dt nil
                                       (dom/a #js {:href "#accordion1" :aria-expanded "false" :aria-controls "accordion1"
                                                   :className "accordion-title accordionTitle js-accordionTrigger"}
                                              "First Accordion header"))

                               (dom/dd #js {:className "accordion-content accordionItem is-collapsed" :id "accordion1"
                                            :aria-hidden "true"}
                                       (dom/p nil "bla bla bla")
                                       (dom/p nil "And some more blas")

                                       )))


               ))))

(defn project-init [project-atom]
  (om/root project-page project-atom
           {:target (. js/document (getElementById "main-content-container"))}))

