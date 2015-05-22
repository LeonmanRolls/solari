(ns solari.views.residential
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(def res-atom (atom {:text "Homes are personal projects and we love that. When we take on a residential project we take on the thoughts, feelings, personality and unique circumstances of the client. We work closely with you to ensure that your home is exactly that – yours. You’re with us every step of the way, this not only makes absolute sense but undoubtedly delivers the best results. We share the challenges and successes with you and make you the expert of your own project by going at a pace that promotes attention to detail and clarity of thought from start to finish."
                     :projects [{:id "project-01"
                                 :title "Wadestown Renovation"
                                 :thumbail "/img/wadestown.jpg"}

                                {:id "project-02"
                                 :title "Lyall bay renovation"
                                 :thumbnail "/img/lyall.jpg"}

                                {:id "project-03"
                                 :title "Catline Lane Subdivision"
                                 :thumbnail: "/img/another.jpg"}]}))

(defn project-tumbnail [data owner]
  (reify
    om/IRender
    (render [this]
      (do
       (println "data: " data)
       (dom/li nil
              (dom/figure nil
                          (dom/div nil
                                   (dom/img #js {:src (:thumbnail data)})
                                   (dom/figcaption nil
                                                   (dom/h3 nil (:title data))
                                                   (dom/a #js {:href "#modal-02" #_(str "#" (:id data) "-modal")} "Take a look")))))))))


(defn project-modal [data owner]
  (reify
    om/IDidMount
    (did-mount [this]
      (do
        (js/alert "did mount")
        (.-animatedModal (js/$ (str "#" (:id data) "-modal")))))
    om/IRender
    (render [this]
      (dom/div #js {:id (str (:id data) "-modal")}
               (dom/div #js {:id "btn-close-modal" :className (str "close-" (:id data) "-modal")})
               (dom/div #js {:className "modal-content"})))))


(defn residential-page [data owner]
  (reify
    om/IRender
    (render [this]

      (apply dom/div #js {:id "modals"}
             (om/build-all project-modal (:projects data)))

      (dom/div nil
               (dom/p #js {:className "text-area"} (:text data))
               (dom/div #js {:className "row"}

                        (apply dom/ul #js {:className "grid cs-style-4"}
                                       (om/build-all project-tumbnail (:projects data))))))))

(defn residential-init []
  (om/root residential-page res-atom
                     {:target (. js/document (getElementById "main-content-container"))}))


