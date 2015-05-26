(ns solari.views.process
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn p-partial [data owner]
   (reify
    om/IRender
    (render [this]
      (dom/p nil data))))


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
                              (:heading data)))

#_(apply dom/ul #js {:className "nav-ul-left"}
                               (om/build-all nav-menu-item-left (:root menu-atom)
                                             {:init-state {:clicked clicked}}))

               (apply dom/dd #js {:className "accordion-content accordionItem is-collapsed" :id "accordion1"
                            :aria-hidden "true"}
                       (om/build-all p-partial (:paragraphs data)))))))

(defn text-partial [data owner]
  (reify

    om/IRender
    (render [this]
      (dom/p #js {:className "text-box"} data))))


(defn process-page [data owner]
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
                               (om/build-all accordion-partial (:long data))))))))





(defn process-init [atom]
  (do
    (om/root process-page atom
             {:target (. js/document (getElementById "main-content-container"))})
    (ef/at "body" (ef/set-attr :background "home"))
    (ef/at "#nav-hint-inner" (ef/content "Welcome"))))
