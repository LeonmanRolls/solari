(ns solari.views.admin.allprojects
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [cljs.core.async :refer [put! chan <! >! take! close!]]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(defn update-value [data owner target key]
  (let [new-contact (-> (om/get-node owner target)
                        .-value)]
    (om/transact! data key (fn [x] new-contact))))

#_(defn handle-change [e owner {:keys [text]}]
  (om/set-state! owner :text (.. e -target -value)))

#_(defn project-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:className "cbp-mc-column"}
               (dom/label #js {:for (:projectid data)} (:title data))
               (dom/input #js {:placeholder "Input new value..." :type "text" :ref "new-contact" :name (:projectid data)})
               (dom/button #js {:onClick #(update-value data owner) :className "cbp-mc-submit"} "Update Site")
               ))))

#_(defn category-view [data owner]
  (reify
    om/IRender
    (render [this]
      (apply dom/div #js {:className "cbp-mc-form"}
             (om/build-all project-view (:projects data))))))

#_(defn admin-page [data owner]
  (reify
    om/IDidMount
    (did-mount [this]
      #_(println "Data" data))
    om/IRender
    (render [this]
      (dom/div nil
               (apply dom/h1 nil "Projects"
                      (om/build-all category-view (:projects data)))))))

(defn home-page-edit [data owner]
  (reify

    om/IInitState
    (init-state [this]
      (:text "Hi there"))

    om/IDidMount
    (did-mount [this]
      (println "DAta: " (om/get-state owner))
      )

    om/IRenderState
    (render-state [this state]
      (dom/div #js {:className "cbp-mc-form"}

               (dom/div #js {:className "cbp-mc-column"}

                        (dom/label #js {:for "home-page-title"} "Main Title")
                        (dom/input #js {:placeholder (:main-title data) :type "text" :ref "home-page-title"
                                        :name "home-page-title"})
                        (dom/button #js {:onClick #(update-value data owner "home-page-title" :main-title)
                                         :className "cbp-mc-submit"} "Update Site")

                        (dom/label #js {:for "home-page-sub-title"} "Sub Title")
                        (dom/input #js {:placeholder (:sub-title data) :type "text" :ref "home-page-sub-title" :name "home-page-title"})
                        (dom/button #js {:onClick #(update-value data owner "home-page-sub-title" :sub-title)
                                         :className "cbp-mc-submit"} "Update Site")

                        )


               (dom/div #js {:className "cbp-mc-column"}

                        (dom/label #js {:for "paragraph-one"} "Paragraph One")
                        (dom/textarea #js {:type "text" :ref "paragraph-one" :name "home-page-title"
                                           :placeholder (:paragraph-one data)})
                        (dom/button #js {:onClick #(update-value data owner "paragraph-one" :paragraph-one)
                                         :className "cbp-mc-submit"} "Update Site")

                        (dom/label #js {:for "paragraph-two"} "Paragraph Two")
                        (dom/textarea #js {:placeholder (:paragraph-two data) :type "text" :ref "paragraph-two" :name "home-page-title"})
                        (dom/button #js {:onClick #(update-value data owner "paragraph-two" :sub-title)
                                         :className "cbp-mc-submit"} "Update Site")

                        )


               )




      )


    ))



(defn admin-init [atom]
  (do
    (om/root home-page-edit atom
             {:target (. js/document (getElementById "main-content-container"))})
    (ef/at "body" (ef/set-attr :background "admin"))
    (ef/at "#nav-hint-inner" (ef/content "admin"))))

