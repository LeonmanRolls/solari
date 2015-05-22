(ns solari.views.residential
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(def res-atom (atom {:projects [{:id "project-01"
                                 :title "Wadestown Renovation"
                                 :thumbail "/img/wadeston.jpg"}

                                {:id "project-02"
                                 :title "Lyall bay renovation"
                                 :thumbnail ""}

                                {:id "project-03"
                                 :title "Catline Lane Subdivision"
                                 :thumbnail: ""}

                                ] }))

(defn project-tumbnail [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/li nil
              (dom/figure nil
                          (dom/div nil
                                   (dom/img #js {:src (:thumbnail data)})
                                   (dom/figcaption nil
                                                   (dom/h3 nil (:title data))
                                                   (dom/a #js {:href "#"} "Take a look")))))
      )


    )

  )

(defn residential-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil
               (dom/p #js {:className "text-area"} "Homes are personal projects and we love that. When we take on a residential project we take on the thoughts, feelings, personality and unique circumstances of the client. We work closely with you to ensure that your home is exactly that – yours. You’re with us every step of the way, this not only makes absolute sense but undoubtedly delivers the best results. We share the challenges and successes with you and make you the expert of your own project by going at a pace that promotes attention to detail and clarity of thought from start to finish.")
               (dom/div #js {:className "row"}
                        (dom/ul #js{:className "grid cs-style-4"} nil
                                (dom/li nil
                                        (dom/figure nil
                                                    (dom/div nil
                                                             (dom/img #js {:src "/img/wadestown.jpg"})
                                                             (dom/figcaption nil
                                                                             (dom/h3 nil "Wadestown Renovation")
                                                                             (dom/a #js {:href "#"} "Take a look")))))

                                (dom/li nil
                                        (dom/figure nil
                                                    (dom/div nil
                                                             (dom/img #js {:src "/img/lyall.jpg"})
                                                             (dom/figcaption nil
                                                                             (dom/h3 nil "Lyall bay renovation")
                                                                             (dom/a #js {:href "#"} "Take a look")))))

                                (dom/li nil
                                        (dom/figure nil
                                                    (dom/div nil
                                                             (dom/img #js {:src "/img/lyall.jpg"})
                                                             (dom/figcaption nil
                                                                             (dom/h3 nil "Catlina Lane Subdivision")
                                                                             (dom/a #js {:href "#"} "Take a look")))))))))))

(defn residential-init []
  (om/root residential-page {}
                     {:target (. js/document (getElementById "main-content-container"))}))


