(ns solari.views.faqs
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)


(defn accordion-partial [data owner]
  (reify

    om/IInitState
    (init-state [this]
      #_(println "accordion: " data))

    om/IRender
    (render [this]
      (dom/div nil
               (dom/dt nil
                       (dom/a #js {:href "#accordion1" :aria-expanded "false" :aria-controls "accordion1"
                                   :className "accordion-title accordionTitle js-accordionTrigger"}
                              (:q data)))

               (dom/dd #js {:className "accordion-content accordionItem is-collapsed" :id "accordion1"
                            :aria-hidden "true"}
                       (dom/p nil (:a data)))))))

(defn text-partial [data owner]
  (reify

    om/IRender
    (render [this]
      (dom/p #js {:className "text-box"} data)
      )))


(defn faqs-page [data owner]
  (reify

    om/IInitState
    (init-state [this]
      (println "Project: " data ))

    om/IDidMount
    (did-mount [this]
      (js/accordion))

    om/IRender
    (render [this]
      (dom/div nil

              (om/build text-partial (:text data))

               (dom/div #js {:className "accordion"}
                        (apply dom/dl nil
                               (om/build-all accordion-partial (:questions data))))))))





(defn faqs-init [atom]
  (do
    (om/root faqs-page atom
             {:target (. js/document (getElementById "main-content-container"))})
    (ef/at "body" (ef/set-attr :background "home"))
    (ef/at "#nav-hint-inner" (ef/content "Welcome"))))
