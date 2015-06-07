(ns solari.views.project
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [solari.views.common :as common])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn img-page [data owner]
  (reify

    om/IRender
    (render [this]
      (dom/img #js {:className "rsImg" :src (str "/img/" data ) :data-rsTmb (str "/img/" data )}))))


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
        (.dropzone (js/$ "#image-dropzone") #js {:url "/file-upload"}) ))

    om/IRender
    (render [this]
      (dom/div #js {:id "project-container"}

               (apply dom/div #js {:className "royalSlider rsDefault"}
                      (om/build-all img-page (:gallery-images data)))


               (dom/div #js {:id "image-dropzone" :style #js {:margin-top "20px" :height "200" :color "white"
                                                              :background "rgba(29,29,27,0.4)"}}
                        "Drop files here or click to upload")


                (dom/form #js {:className "cbp-mc-form" :action "/upload" :method "post" :enctype "multipart/form-data"}

               (dom/div #js {:className "cbp-mc-column"}

                        (dom/div nil

                       (apply dom/ul nil (om/build-all common/admin-li (:gallery-images data)
                                                       {:state {:button-label "Remove"}}))

                                (dom/input #js {:type "file" :name "fileToUpload" :id "fileToUpload"})
                                 (dom/input #js {:type "button" :value "Upload Image" :name "submit"})
                                 )
                        )
                        )


               (dom/div #js {:className "accordion"}

                        (apply dom/dl nil
                               (om/build-all common/accordion-partial (:accordion data))))))))


(defn project-init [project-atom]
  (do (om/root project-page project-atom
               {:target (. js/document (getElementById "main-content-container"))})
      (ef/at ".context" (ef/content (:title @project-atom)))))

