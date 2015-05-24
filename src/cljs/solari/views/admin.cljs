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
      (dom/div nil
               (dom/p nil (:title data))
               (dom/input #js {:placeholder "Input new value..." :type "text" :ref "new-contact"})
               (dom/button #js {:onClick #(update-value data owner)} "Add contact")))))

(defn category-view [data owner]
  (reify
    om/IRender
    (render [this]
      (apply dom/h1 nil (:category data)
             (om/build-all project-view (:projects data))))))

(defn admin-page [data owner]
  (reify
    om/IDidMount
    (did-mount [this]
      (let [values (ef/from "#read-form-test" (ef/read-form))]
        (ef/at "#read-form-demo" (ef/content (pr-str values))))
      )
    om/IRender
    (render [this]
      (dom/div nil
               (apply dom/h1 nil "Projects"
                      (om/build-all category-view (:projects data)))
               ))))

