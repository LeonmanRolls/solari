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

(defn home-page [data owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [color]}]
      (dom/div nil

               (om/build p-partial data {:init-state {:color color}})

               (dom/div #js {:className "cbp-mc-form"}

               (dom/div #js {:className "cbp-mc-column"}
                        (dom/label #js {:for "home-page-title"} "Bold text")
                        (dom/input #js {:placeholder (:bold data) :type "text" :ref "home-page-title"
                                        :name "home-page-title"})
                        (dom/button #js {:onClick #(update-value data owner "home-page-title" :bold)
                                         :className "cbp-mc-submit"} "Update Site"))

                        (dom/div #js {:className "cbp-mc-column"}
                                 (dom/label #js {:for "paragraph-one"} "Paragraph")
                                 (dom/textarea #js {:type "text" :ref "paragraph-one" :name "home-page-title"
                                                    :placeholder (:paragraph data)})
                                 (dom/button #js {:onClick #(update-value data owner "paragraph-one" :paragraph)
                                                  :className "cbp-mc-submit"} "Update Site")))))))

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


(defn simple-li [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/li #js {:style #js {:cursor "pointer" :height "50px" :width "140px" :letterSpacing "1px" :color "white"
                                      :textTransform "uppercase" :fontSize "80%" :position "relative" :textAlign "center"
                                      :backgroundColor (:transparent-grey colors) :display "block" :textDecoration "none"
                                      :padding "16px" :outline "none" :marginLeft "-2px" :marginRight "-1px"
                                      :borderTop "1px solid white" :borderLeft "1px solid white"}
                   :onClick (:callback data)}
                     (:text data)))))


;Required href and text
(defn clear-li [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/li #js {:style #js {:cursor "pointer" :height "50px" :width "140px" :letterSpacing "1px" :color "white"
                                      :textTransform "uppercase" :fontSize "80%" :position "relative" :textAlign "center"
                                      :backgroundColor (:transparent-grey colors) :display "block" :textDecoration "none"
                                      :padding "16px" :outline "none" :marginLeft "-2px" :marginRight "-1px"
                                      :borderTop "1px solid white" :borderLeft "1px solid white"}
                   :onClick #(do (.megafilter js/api (:cat data))
                                 (ef/at "#group_photo" (ef/set-attr :src (:img data))))}
                     (:text data)))))




(defn p-partial-white [data owner]
  (reify
    om/IInitState
    (init-state [this]
      (println "ppartial" data))

    om/IRender
    (render [this]
      (dom/p #js {:style #js {:color "white" :marginBottom "30px"}}
             (dom/b nil (:bold data))
             (:paragraph data)))))

