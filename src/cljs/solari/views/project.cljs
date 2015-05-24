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

(defn project-page [data owner]
  (reify

    om/IInitState
    (init-state [this]
      (println "Project: " (:gallery-images data) ))

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
                        (om/build-all img-page (:galley-images data)))

               (dom/div #js {:className "accordion"}
                        (dom/dl nil

                                (dom/dt nil
                                        (dom/a #js {:href "#accordion1" :aria-expanded "false" :aria-controls "accordion1"
                                                    :className "accordion-title accordionTitle js-accordionTrigger"}
                                               "First Accordion header"))

                                (dom/dd #js {:className "accordion-content accordionItem is-collapsed" :id "accordion1"
                                             :aria-hidden "true"}
                                        (dom/p nil "bla bla bla")
                                        (dom/p nil "And some more blas"))))))))

#_(defn get-specific-project [project-atom project]
  (loop [idx 0]
  (when (< idx (count (:projects projects-atom)))
    (let [current-category-projects (get-in project-atom [:projects idx :projects])]
      (loop [idxx 0]
        (when (< idxx (count current-category-projects))
        (if )

          )
        (recur (inc idxx)))

      )

   ) (recur (inc idx))))

(defn project-init [project-atom]
  (do (om/root project-page project-atom
           {:target (. js/document (getElementById "main-content-container"))})
      (ef/at ".context" (ef/content (:title @project-atom)))))


