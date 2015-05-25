(ns solari.views.admin
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [cljs.core.async :refer [put! chan <! >! take! close!]]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(defn update-value [data owner]
  (println "data owner" data (.-value (om/get-node owner "new-contact")))
  (let [new-contact (-> (om/get-node owner "new-contact")
                        .-value)]
    (om/transact! data :title (fn [x] new-contact))
    ))

(defn project-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:className "cbp-mc-column"}
               (dom/label #js {:for (:projectid data)} (:title data))
               (dom/input #js {:placeholder "Input new value..." :type "text" :ref "new-contact" :name (:projectid data)})
               (dom/button #js {:onClick #(update-value data owner) :className "cbp-mc-submit"} "Update Site")))))

(defn category-view [data owner]
  (reify
    om/IRender
    (render [this]
      (apply dom/div #js {:className "cbp-mc-form"}
             (om/build-all project-view (:projects data))))))

(defn admin-page [data owner]
  (reify
    om/IDidMount
    (did-mount [this]
      #_(println "Data" data))
    om/IRender
    (render [this]
      (dom/div nil
               (apply dom/h1 nil "Projects"
                      (om/build-all category-view (:projects data)))))))

