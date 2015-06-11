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


(def box-style #js {:style #js {:color "white" :marginTop "10px" :padding "20px" :border "2px solid #c0392b"
                                :textTransform "uppercase"}})

(defn process-page [data owner]
  (reify

    om/IDidMount
    (did-mount [this]
      (js/accordion))

    om/IRenderState
    (render-state [this state]
      (let [local (get data (:key state))]
        (dom/div nil

                 (println "process: " (:long local))

                 #_(apply dom/ul #js {:style #js {:top "100px" :width "140px" :right "0px" :position "fixed"
                                                :listStyle "none" :borderBottom "1px solid white" :padding "0px" }}
                        (om/build-all common/simple-li hipster-data))

                 (om/build common/paragraph-partial local {:state {:color "white" :key :text}})

                 (let [locall (get local :short)]
                   (dom/div #js {:id "short-version"}
                            (dom/div box-style (:step1 locall))
                            (om/build common/input-partial [:step1 (:step1 locall)] {:state {:data locall :key :step1}})
                            (dom/div box-style (:step2 locall))
                            (om/build common/input-partial [:step2 (:step2 locall)] {:state {:data locall :key :step2}})
                            (dom/div box-style (:step3 locall))
                            (om/build common/input-partial [:step3 (:step3 locall)] {:state {:data locall :key :step3}})))

                 (dom/div #js {:id "long-version" :className "accordion"}
                          (apply dom/dl nil
                                 (om/build-all common/accordion-partial (get local :long)))))))))


