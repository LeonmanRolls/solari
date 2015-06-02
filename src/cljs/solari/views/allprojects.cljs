(ns solari.views.allprojects
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [solari.views.common :as common]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn gallery-partial [data owner]
  (reify

    om/IInitState
    (init-state [this])

    om/IRender
    (render [this]
      (dom/div #js {:className (str "mega-entry " (:category data))  :id (:id data) :data-src (:thumbnail data)
                    :data-bgposition "50% 50%" :data-width "320" :data-height "240"}
               (dom/div #js {:className "mega-hover"}
                        (dom/div #js {:className "mega-hovertitle"} (:title data)
                                 (dom/div #js {:className "mega-hoversubtitle"} "subtitle"))

                        (dom/a #js {:href (str "/#/" (:projectid data))}
                               (dom/div #js {:className "mega-hoverlink"})))))))


(defn all-projects-page [data owner]
  (reify

    om/IInitState
    (init-state [this])

    om/IDidMount
    (did-mount [this]
      (js/megafolioInit))

    om/IRenderState
    (render-state [this {:keys [text]}]

      (dom/div #js {:className "container"}

               (om/build common/p-partial-white text)

               (apply dom/div #js {:className "megafolio-container"}
                      (om/build-all common/gallery-partial data {:key :id}))))))

(defn all-projects-init [project-atom filter text-atom]
  (do (om/root all-projects-page project-atom
               {:target (. js/document (getElementById "main-content-container"))
                :init-state {:text @text-atom}})
      (ef/at ".context" (ef/content (:title @project-atom)))
      (.megafilter js/api filter)))

