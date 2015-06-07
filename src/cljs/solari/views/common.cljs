(ns solari.views.common
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
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

(defn accordion-partial [data owner]
  (reify

    om/IRender
    (render [this]
      (dom/div nil
               (dom/dt nil
                       (dom/a #js {:href "#accordion1" :aria-expanded "false" :aria-controls "accordion1"
                                   :className "accordion-title accordionTitle js-accordionTrigger"}
                              (:title data)))

               (dom/dd #js {:className "accordion-content accordionItem is-collapsed" :id "accordion1"
                            :aria-hidden "true"}
                      (dom/p nil (:content data))
                       )))))

#_(defmulti admin-partial (fn [x] (type x)))

;(defmethod admin-partial cljs.core/PersistentArrayMap)

;(defmethod admin-partial cljs.core/PersistentVector)

(defn short-input-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               #_(dom/label #js {:for "home-page-title"} "Bold text")
               #_(dom/input #js {:placeholder (:placeholder data) :type "text" :ref "home-page-title"
                               :name "home-page-title"})
               #_(dom/button #js {:onClick #(update-value data owner "home-page-title" :bold)
                                :className "cbp-mc-submit"} "Update Site")))))

(defn paragraph-partial [data owner]
  (reify
   om/IInitState
    (init-state [this]
      (println "p partial init " data))

    om/IRenderState
    (render-state [this {:keys [color]}]
      (dom/div nil

               (om/build p-partial data {:init-state {:color color}})

               (dom/div #js {:className "cbp-mc-form"}

               (dom/div #js {:className "cbp-mc-column"}

                        (om/build short-input-partial (atom {:placeholder "hi there"}))

                        )

                        (dom/div #js {:className "cbp-mc-column"}

                                 (dom/label #js {:for "paragraph-one"} "Paragraph")
                                 (dom/textarea #js {:type "text" :ref "paragraph-one" :name "home-page-title"
                                                    :placeholder (:paragraph data)})
                                 (dom/button #js {:onClick #(update-value data owner "paragraph-one" :paragraph)
                                                  :className "cbp-mc-submit"} "Update Site")


                                 ))))))

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

