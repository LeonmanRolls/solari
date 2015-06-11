(ns solari.views.project
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [solari.views.common :as common]
            [solari.data :as data])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn img-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/img #js {:className "rsImg" :src (str "/img/" data ) :data-rsTmb (str "/img/" data )}))))


;(defmulti project-page (fn [x] ))

(defn project-page [data owner]
  (reify

    om/IDidMount
    (did-mount [this]
      (do
        (.royalSlider (js/$ ".royalSlider") #js {:keyboardNavEnabled true :controlNavigation "none"
                                                 :autoScaleSlider true :autoScaleSliderWidth 900 :autoScaleSliderHeight 400
                                                 :fullscreen #js {:enabled true :nativeFS true}
                                                 :visibleNearby #js {:enabled false :centerArea 0.5 :center true
                                                                     :breakpoint 650 :breakpointCenterArea 0.64
                                                                     :navigateByCenterClick true}})
        (js/accordion)

        #_(.dropzone (js/$ "#image-dropzone") #js {:url "/file-upload"}) ))

    om/IRenderState
    (render-state [this state]
     (let [local (get data (:key state))
           project (first (filter (fn [x] (= (:filter state) (:projectid x))) local))]

       (dom/div #js {:id "project-container"}

                (println "all member ids" (data/all-memberids))
                (println "data: "  (:accordion project))

                (apply dom/div #js {:className "royalSlider rsDefault"}
                       (doall (om/build-all img-page (:gallery-images project))))

               #_(println "filter: "
                        (nth (filter (fn [x] (not= "non-user" ((key x) common/project-schema))) data) 2)  )

               #_(om/build
                 common/input-partial
                 (nth (filter (fn [x] (not= "non-user" ((key x) common/project-schema))) project) 4))

               (doall
               (om/build-all common/input-partial (filter (fn [x] (not= "non-user" ((key x) common/project-schema))) project))
                 )

               (dom/div #js {:className "accordion"}
                        (apply dom/dl nil
                               (om/build-all common/accordion-partial (:accordion project)))))
       )
      )))





