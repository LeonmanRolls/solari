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

(def sorting-data [{:href "" :label "By name" :callback #(put! sort-chan "name")}
                   {:href "" :label "By year" :callback #(put! sort-chan "year")}])

(def project-schema [:id "non-user" :year "text-input" :projectid "text-input" :link "non-user" :category "user-limited"
                     :title "text-input" :thumbnail "user-upload" :gallery-images "editable-list-upload"
                     :accordion "editable-list-text"])

(defn all-projects-page [data owner]
  (reify

    om/IDidMount
    (did-mount [this]
      (let [local (:key (om/get-state owner))]
        (do
          (js/megafolioInit)
          (.megafilter js/api (:cat (om/get-state owner)) )
          (if (not (empty? data))
            (go
              (while true
                (let [sort-type (<! sort-chan)
                      state (om/get-state owner)]
                  (cond

                    (= sort-type "name") (do
                                           (om/transact! (get data :all-projects) (fn [cursor] (sort-by (fn [x] (first (:projectid x))) cursor)))
                                           (sec/dispatch! (:route state)))

                    (= sort-type "year") (do
                                           (om/transact! (get data :all-projects) (fn [cursor] (reverse (sort-by (fn [x] (first (:year x))) cursor))))
                                           (sec/dispatch! (:route state)))))))))))

    om/IRenderState
    (render-state [this state]
      (let [local (get data (:key state))]
        (dom/div #js {:className "container"}

                 (apply dom/ul #js {:id "right-right-nav"
                                    :style #js {:top "100px" :width "140px" :right "0px" :position "fixed"
                                                :listStyle "none" :borderBottom "1px solid white" :padding "0px" }}
                        (om/build-all common/simple-li sorting-data {:state {:data local}}))

                 (om/build common/paragraph-partial data {:state {:key (:extra state)
                                                                  :admin (:admin state)
                                                                  :color "white"}})

                 #_(om/build common/gallery-partial (first local))

                 (println "local: " local )

                 #_(println "local: " (empty? local)  )

                 ;        (println "sort-by: " (sort-by (fn [x] (first (:year x))) local))

                 #_(if (not (empty? local))  (fn [cursor] (sort-by > (fn [x] (first (:year x))) cursor)) )

                 (apply dom/div #js {:className "megafolio-container"}
                        (om/build-all common/gallery-partial local {:state {:link :projectid :prelink "/#/projects/individual/"
                                                                            :subtitle (:subtitle state)}}))

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


                 )

        )

      )))


