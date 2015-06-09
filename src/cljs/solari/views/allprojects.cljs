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

(def project-schema [:id "non-user" :year "text-input" :projectid "text-input" :link "non-user" :category "user-limited" :title "text-input" :thumbnail "user-upload"
                     :gallery-images "editable-list-upload" :accordion "editable-list-text"])


(defn all-projects-page [data owner]
  (reify

    om/IInitState
    (init-state [this]
      #_(println "all projects: " data))

    om/IDidMount
    (did-mount [this]
      #_(do
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
    (render-state [this state]

      (dom/div #js {:className "container"}

               #_(apply dom/ul #js {:style #js {:top "100px" :width "140px" :right "0px" :position "fixed"
                                              :listStyle "none" :borderBottom "1px solid white" :padding "0px" }}
                      (om/build-all common/simple-li sorting-data))

               (println "safsd: " (:text data))

               (om/build common/paragraph-partial (:text data) {:init-state {:color "white"}})

               #_(apply dom/div #js {:className "megafolio-container"}
                      (om/build-all common/gallery-partial data))

               #_(dom/form #js {:className "cbp-mc-form"}

               (dom/div #js {:className "cbp-mc-column"}

                        (dom/div nil

                       (apply dom/ul nil (om/build-all common/admin-li (:gallery-images data)
                                                       {:state {:button-label "Remove"}}))

                                (dom/input #js {:type "file" :name "fileToUpload" :id "fileToUpload"})
                                 (dom/input #js {:type "button" :value "Upload Image" :name "submit"})
                                 )
                        )

                (dom/div #js {:className "cbp-mc-column"}


                         (om/build common/short-simple-input-partial (atom {:placeholder "Year"})
                                   {:state {:label "Year"}})

                         (om/build common/short-simple-input-partial (atom {:placeholder "Project Id"})
                                   {:state {:label "Project Id"}})

                        )

                        )

               ))))


(defn all-projects-init [project-atom filter text-atom]
  (do (om/root all-projects-page (atom {:project @project-atom :text @text-atom})
               {:target (. js/document (getElementById "main-content-container"))
                :state {:project project-atom :text text-atom}})
      (ef/at ".context" (ef/content (:title @project-atom)))
      #_(.megafilter js/api filter)))

