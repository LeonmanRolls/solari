(ns solari.views.common
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [clojure.set :as set]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(def colors {:transparent-grey "rgba(29,29,27,0.4)"})

(defn p-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [color]}]
      (dom/p #js {:style #js {:color color}}
             (dom/b nil (:bold data))
             (:paragraph data)))))

(defn update-value [data owner target key]
  (let [new-contact (-> (om/get-node owner target)
                        .-value)]
    (om/transact! data key (fn [x] new-contact))))

(defn short-input-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/label #js {:for (:placeholder data)} (:label state))
               (dom/input #js {:placeholder (:placeholder data) :type "text" :ref (:placeholder data)})
               (dom/button #js {:onClick #(update-value data owner (:placeholder data) (:key state))
                                :className "cbp-mc-submit"} "Update Site")))))

(defn long-input-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/label #js {:for (:placeholder data)} (:label state))
               (dom/textarea #js {:type "text" :ref (:placeholder data) :placeholder (:placeholder data)})
               (dom/button #js {:onClick #(update-value data owner (:placeholder data) (:key state))
                                :className "cbp-mc-submit"} "Update Site")))))

(defn accordion-partial [data owner]
  (reify

    om/IRender
    (render [this]
      (dom/div nil
               (dom/dt nil
                       (dom/a #js {:href "#accordion1" :aria-expanded "false" :aria-controls "accordion1"
                                   :className "accordion-title accordionTitle js-accordionTrigger"}
                              (:title data)
                              (om/build short-input-partial (:title data))))

               (dom/dd #js {:className "accordion-content accordionItem is-collapsed" :id "accordion1"
                            :aria-hidden "true"}
                       (dom/p nil (:content data)))))))

#_(defmulti admin-partial (fn [x] (type x)))

;(defmethod admin-partial cljs.core/PersistentArrayMap)

;(defmethod admin-partial cljs.core/PersistentVector)



(defn paragraph-partial [data owner]
  (reify

    om/IRenderState
    (render-state [this {:keys [color]}]

      (dom/div nil

               (println "textext " data)

               (om/build p-partial data {:init-state {:color color}})

               (dom/form #js {:action "/file-upload" :class "dropzone" :id "my-awesome-dropzone"
                              :style #js {:width "400" :height "400"}})

               (dom/div #js {:className "cbp-mc-form"}

                        (dom/div #js {:className "cbp-mc-column"}
                                 (om/build short-input-partial (set/rename-keys data {:bold :placeholder})
                                           {:state {:label "Bold text" :key :bold}}))

                        (dom/div #js {:className "cbp-mc-column"}
                                 (om/build long-input-partial (set/rename-keys data {:paragraph :placeholder})
                                           {:state {:label "Paragraph" :key :paragraph}})))))))

;link, category, id, thumbnail, title
(defn gallery-partial [data owner]
  (reify

    om/IInitState
    (init-state [this]
      #_(println "hi there: " data))

    om/IRender
    (render [this]
      (dom/a #js {:href (str "/#/" (:link data)) :className (str "mega-entry " (:category data))  :id (:id data)
                  :data-src (:thumbnail data) :data-bgposition "50% 50%" :data-width "320" :data-height "240"}
             (dom/div #js {:className "mega-hover"}
                      (dom/div #js {:className "mega-hovertitle" :style #js {:left 0 :width "100%" :top "40%"}}
                               (:title data)
                               (dom/div #js {:className "mega-hoversubtitle"} "Click for info")))))))

;key
(defn simple-li [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/li #js {:style #js {:cursor "pointer" :height "50px" :width "140px" :letterSpacing "1px" :color "white"
                               :textTransform "uppercase" :fontSize "80%" :position "relative" :textAlign "center"
                               :backgroundColor (:transparent-grey colors) :display "block" :textDecoration "none"
                               :padding "16px" :outline "none" :marginLeft "-2px" :marginRight "-1px"
                               :borderTop "1px solid white" :borderLeft "1px solid white"}
                   :onClick (:callback state)}
              data))))


(defn admin-li [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/li #js {:style #js {:color "white"} :onClick (:callback state)} data)
               (dom/button nil (:button-label state))))))

