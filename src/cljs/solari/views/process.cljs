(ns solari.views.process
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [solari.views.common :as common])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(def hipster-data [{:href "" :text "Short version" :callback #(do (ef/at "#short-version" (ef/remove-class "hidden"))
                                                                (ef/at "#long-version" (ef/add-class "hidden")))}
                   {:href "" :text "Long version" :callback #(do (ef/at "#long-version" (ef/remove-class "hidden"))
                                                                (ef/at "#short-version" (ef/add-class "hidden"))) }])

(defn accordion-partial [data owner]
  (reify

    om/IInitState
    (init-state [this]
      (println "accordion: " (:paragraphs data)))

    om/IRender
    (render [this]
      (dom/div nil
               (dom/dt nil
                       (dom/a #js {:href "#accordion1" :aria-expanded "false" :aria-controls "accordion1"
                                   :className "accordion-title accordionTitle js-accordionTrigger"}
                              (:heading data)))

               (apply dom/dd #js {:className "accordion-content accordionItem is-collapsed" :id "accordion1"
                            :aria-hidden "true"}
                       (om/build-all
                         (fn [data owner]
                           (reify om/IRender
                             (render [this] (dom/p nil data)))) (:paragraphs data)))))))

(def box-style #js {:style #js {:color "white" :marginTop "10px" :padding "20px" :border "2px solid #c0392b"
                                :textTransform "uppercase"}})

(defn process-page [data owner]
  (reify

    om/IInitState
    (init-state [this])

    om/IDidMount
    (did-mount [this]
      (js/accordion))

    om/IRender
    (render [this]
      (dom/div nil

               (apply dom/ul #js {:style #js {:top "100px" :width "140px" :right "0px" :position "fixed"
                                              :listStyle "none" :borderBottom "1px solid white" :padding "0px" }}
                      (om/build-all common/simple-li hipster-data))

               (om/build common/p-partial-white (:text data))

               (dom/div #js {:id "short-version"}
                        (dom/div box-style
                                 "1. We listen to your goals and objectives")
                        (dom/div box-style
                                 "2. We translate your ideas, inspiration and words into design. That goes back and forth until we are all speaking the same language.")
                        (dom/div box-style
                                 "3. We communicate the solution to the right team of colaborators to actualise your vision."))

               (dom/div #js {:id "long-version" :className "accordion hidden"}
                        (apply dom/dl nil
                               (om/build-all accordion-partial (:long data))))))))


(defn process-init [atom]
  (do
    (om/root process-page atom
             {:target (. js/document (getElementById "main-content-container"))})
    (ef/at "body" (ef/set-attr :background "home"))
    (ef/at "#nav-hint-inner" (ef/content "Welcome"))))

