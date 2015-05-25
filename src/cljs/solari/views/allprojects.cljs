(ns solari.views.allprojects
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)


(defn all-projects-page [data owner]
  (reify

    om/IInitState
    (init-state [this]
      )

    om/IDidMount
    (did-mount [this]
      (.-init js/Grid))

    om/IRender
    (render [this]
      (dom/ul #js {:id "org-rid" :className "og-grid"}
              (dom/li nil
                      (dom/a #js {:href "http://cargocollective.com/jaimemartinez/"
                                  :data-largesrc "/img/lyall.jpg"
                                  :data-title "The title"
                                  :data-description "The Data description"}
                             (dom/img #js {:src "/img/lyall.jpg"}))

                      )

              (dom/li nil
                      (dom/a #js {:href "http://cargocollective.com/jaimemartinez/"
                                  :data-largesrc "/img/lyall.jpg"
                                  :data-title "The title"
                                  :data-description "The Data description"}
                             (dom/img #js {:src "/img/lyall.jpg"}))



                      )

               ))))


(defn all-projects-init [project-atom]
  (do (om/root all-projects-page project-atom
               {:target (. js/document (getElementById "main-content-container"))})
      (ef/at ".context" (ef/content (:title @project-atom)))))

