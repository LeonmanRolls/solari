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

(defn update-value [data owner target key]
  (let [new-contact (-> (om/get-node owner target)
                        .-value)]
    (om/transact! data key (fn [x] new-contact))))

(def colors {:transparent-grey "rgba(29,29,27,0.4)"})

(def categories ["cat-residential" "cat-multi-residential" "cat-commercial"])

(def project-schema {:id "non-user" :year "text-input" :projectid "text-input" :link "non-user" :category "user-limited"
                     :title "text-input" :thumbnail "user-upload" :gallery-images "editable-list-upload"
                     :accordion "non-user" :bold "text-input" :paragraph "text-area" :placeholder "text-input"})

(defmulti input-partial (fn [data] ((first data) project-schema)))

(defn map->vector [data]
  (map (fn [x] (into [] x)) data))

(defmulti admin-li (fn [data] (type data)))

(defmethod admin-li (type "") [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/li #js {:style #js {:color "white"} :onClick (:callback state)} data)
               (dom/button nil (:button-label state))))))

(defmethod admin-li (type {}) [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/li #js {:style #js {:color "white"} :onClick (:callback state)} (:title data))
               (dom/li #js {:style #js {:color "white"} :onClick (:callback state)} (:content data))
               (dom/button nil (:button-label state))))))



(defn short-simple-input-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/label #js {:for (val data)} (name (key data)))
               (dom/input #js {:placeholder (val data) :type "text" :ref (val data)})))))

(defn short-input-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (let [keyv (name (key data)) valv (val data)]
        (dom/div nil
                 (dom/label #js {:for valv} keyv)
                 (dom/input #js {:placeholder valv :type "text" :ref valv})
                 (dom/button #js {:onClick #(update-value (:data state) owner valv (key data))
                                  :className "cbp-mc-submit"} "Update Site"))))))

(defn long-input-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (let [keyv (name (key data)) valv (val data)]
        (dom/div nil
                 (dom/label #js {:for valv} keyv)
                 (dom/textarea #js {:placeholder valv :type "text" :ref valv})
                 (dom/button #js {:onClick #(update-value (:data state) owner valv (key data))
                                  :className "cbp-mc-submit"} "Update Site"))))))

(defn radio-input-quark [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/input #js {:type "radio" :name data :value data} data))))

(defn radio-input-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (apply dom/div nil
             (om/build-all radio-input-quark categories)))))

(defn user-upload-partial [data owner]
  (let [almost-unique (str  (rand-int 1000000))]
    (reify

    om/IDidMount
    (did-mount [this]
      (.dropzone (js/$ (str "#" almost-unique) ) #js {:url "/imgupload/"}))

    om/IRenderState
    (render-state [this state]
      (dom/div #js {:id almost-unique :style #js {:margin-top "20px" :height "200" :color "white"
                                                     :background "rgba(29,29,27,0.4)"}}
               "Drop files here or click to upload")))))

(defn accordion-partial [data owner]
  (reify

    om/IRender
    (render [this]
      (dom/div nil
               (println "accordion: " data)
               (dom/dt nil
                       (dom/a #js {:href "#accordion1" :aria-expanded "false" :aria-controls "accordion1"
                                   :className "accordion-title accordionTitle js-accordionTrigger"}
                              (:title data)
                              #_(om/build short-input-partial (:title data))))

               (dom/dd #js {:className "accordion-content accordionItem is-collapsed" :id "accordion1"
                            :aria-hidden "true"}
                       (dom/p nil (:content data)))))))

(defn p-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/p #js {:style #js {:color (:color state)}}
             (dom/b nil (:bold data))
             (:paragraph data)))))

(defn paragraph-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (let [local (get data (:key state))]
        (dom/div nil
                 (om/build p-partial local {:state {:color (:color state)}})
                 (dom/div #js {:className "cbp-mc-form"}
                          (apply dom/div #js {:className "cbp-mc-column"}
                                 (om/build-all input-partial (map->vector local) {:state {:data local}}))))))))


;link, category, id, thumbnail, title
(defn gallery-partial [data owner]

  (reify

    om/IRender
    (render [this]
      (dom/a #js {:href (str "/#/" (:link data)) :className (str "mega-entry cat-all" (:category data))  :id (:id data)
                  :data-src (:thumbnail data) :data-bgposition "50% 50%" :data-width "320" :data-height "240"}
             (println "data data: " data)
             (dom/div #js {:className "mega-hover"}
                      #_(println "data: " data)
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
                   :onClick (:callback state)}
              data))))

(defn editable-list-upload-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]

      (dom/div nil

               (apply dom/ul nil
                      (om/build-all admin-li (last data)))

               (om/build user-upload-partial data)))))

(defn editable-list-text-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]

      (dom/div nil

               (apply dom/ul nil
                      (om/build-all admin-li (last data)))

               #_(apply dom/div nil
                               (om/build-all short-input-partial (:accordion data)))))))



(defmethod input-partial "text-input"
  [data owner] (short-input-partial data owner))

(defmethod input-partial "text-area"
  [data owner] (long-input-partial data owner))

(defmethod input-partial "user-limited"
  [data owner] (radio-input-partial data owner))

(defmethod input-partial "editable-list-upload"
  [data owner] (editable-list-upload-partial data owner))

(defmethod input-partial "editable-list-text"
  [data owner] (editable-list-text-partial data owner))

(defmethod input-partial "user-upload"
  [data owner] (user-upload-partial data owner))

