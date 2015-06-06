(ns solari.views.allprojects
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [cljs.core.async :refer [put! chan <! >! take! close!]]
            [enfocus.effects :as eff]
            [solari.views.common :as common]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(def sort-chan (chan))
(def sorting-data [{:href "" :text "By name" :callback #(put! sort-chan "name")}
                   {:href "" :text "By year" :callback #(put! sort-chan "year")}])

(defn gallery-partial [data owner]
  (reify

    om/IInitState
    (init-state [this]
      )

    om/IRender
    (render [this]
      (dom/div #js {:className (str "mega-entry " (:category data))  :id (:id data) :data-src (:thumbnail data)
                    :data-bgposition "50% 50%" :data-width "320" :data-height "240"}
               (dom/div #js {:className "mega-hover"}
                        (dom/div #js {:className "mega-hovertitle"} (:title data)
                                 (dom/div #js {:className "mega-hoversubtitle"} "subtitle"))

                        (dom/a #js {:href (str "/#/" (:projectid data))}
                               (dom/div #js {:className "mega-hoverlink"})))



               )

      )))

(defn all-projects-page [data owner]
  (reify

    om/IInitState
    (init-state [this]
      (println "all projects: " data))

    om/IDidMount
    (did-mount [this]
      (do
        (js/megafolioInit)
        (go
        (while true
          (let [sort-type (<! sort-chan)]
            (cond
              (= sort-type "name") (do
                                     (om/transact! data (fn [cursor] (sort-by (fn [x] (:projectid x)) cursor)))
                                     (.megafilter js/api (:filter (om/get-state owner))))
              (= sort-type "year") (do
                                     (om/update! data (reverse (sort-by (fn [x] (:year x)) data)))
                                     (.megafilter js/api (:filter (om/get-state owner))))))))))

    om/IRenderState
    (render-state [this {:keys [text]}]

      (dom/div #js {:className "container"}

               (apply dom/ul #js {:style #js {:top "100px" :width "140px" :right "0px" :position "fixed"
                                              :listStyle "none" :borderBottom "1px solid white" :padding "0px" }}
                      (om/build-all common/simple-li sorting-data))

               (om/build common/home-page text)

               (apply dom/div #js {:className "megafolio-container"}
                      (om/build-all common/gallery-partial data {:key :id}))))))


(defn all-projects-init [project-atom filter text-atom]
  (do (om/root all-projects-page project-atom
               {:target (. js/document (getElementById "main-content-container"))
                :init-state {:text @text-atom :filter filter}})
      (ef/at ".context" (ef/content (:title @project-atom)))
      (.megafilter js/api filter)))

