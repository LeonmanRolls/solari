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

(def box-style #js {:style #js {:color "white" :marginTop "10px" :padding "20px" :border "2px solid #c0392b"
                                :textTransform "uppercase" :fontWeight "700" :backgroundColor "rgba(29,29,27,0.4)"}})

(def hipster-data [{:href "" :label "Short version" :id "short-li"
                    :callback #(do (ef/at "#short-version" (ef/remove-class "hidden"))
                                   (ef/at "#long-version" (ef/add-class "hidden"))
                                   (ef/at "#short-li" (ef/set-attr :color "red"))
                                   (ef/at "#long-li" (ef/set-attr :color "none"))
                                   )}

                   {:href "" :label "Long version" :id "long-li"
                    :callback #(do (ef/at "#short-version" (ef/add-class "hidden"))
                                   (ef/at "#long-version" (ef/remove-class "hidden"))
                                   (ef/at "#short-li" (ef/set-attr :color "none"))
                                   (ef/at "#long-li" (ef/set-attr :color "red"))
                                   )}])

(defn process-page [data owner]
  (reify

    om/IDidMount
    (did-mount [this]
      (js/accordion)
      (ef/at "#short-li" (ef/set-attr :color "red"))
      (ef/at "#long-li" (ef/set-attr :color "none")))

    om/IRenderState
    (render-state [this state]
      (let [local (get data (:key state))]
        (dom/div nil
                 (apply dom/ul #js {:style #js {:top "100px" :width "140px" :right "0px" :position "fixed"
                                                :listStyle "none" :borderBottom "1px solid white" :padding "0px" }}
                        (om/build-all common/simple-li hipster-data))

                 (om/build common/paragraph-partial local {:state {:color "white" :key :text}})

                 (let [locall (get local :short)]
                   (dom/div #js {:id "short-version"}
                            (dom/div box-style (first (:step1 locall)) )
                            (if (:admin state) (om/build common/input-partial (:step1 locall)))
                            (dom/div box-style (first (:step2 locall)))
                            (if (:admin state) (om/build common/input-partial (:step2 locall)))
                            (dom/div box-style (first (:step3 locall)))
                            (if (:admin state) (om/build common/input-partial (:step3 locall)))))

                 (println "state" state)
                 (dom/div #js {:id "long-version" :className "accordion hidden"}
                          (apply dom/dl nil
                                 (om/build-all common/accordion-partial  (get local :long) {:state state}))))))))


