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
      (js/megafolioInit))

    om/IRender
    (render [this]

    (dom/div #js {:className "container"}
            (dom/div #js {:className "megafolio-container"}
                    (dom/div #js {:className "mega-entry" :id "entry-1" :data-src "/img/lyall.jpg"
                                  :data-bgposition "50% 50%" :data-width "320" :data-height "240"})
                     (dom/div #js {:className "mega-entry" :id "entry-2" :data-src "/img/lyall.jpg"
                                   :data-bgposition "0% 50%" :data-width "504" :data-height "400"})
                     (dom/div #js {:className "mega-entry" :id "entry-3" :data-src "/img/lyall.jpg"
                                   :data-bgposition "50% 100%" :data-width "504" :data-height "400"})
                     ))

      )))


(defn all-projects-init [project-atom]
  (do (om/root all-projects-page project-atom
               {:target (. js/document (getElementById "main-content-container"))})
      (ef/at ".context" (ef/content (:title @project-atom)))))

