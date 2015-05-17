(ns solari.core
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [solari.views.sidebar :as sb]
            [solari.views.home :as home]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [enfocus.core :as ef])
  (:require-macros [enfocus.macros :as em])
  (:import goog.History))

;Fallback for browsers without html5 history support
(sec/set-config! :prefix "#")

;enable html5 history
(let [history (History.)
      navigation EventType/NAVIGATE]
  (goog.events/listen history
                      navigation
                      #(-> % .-token sec/dispatch!))
  (doto history (.setEnabled true)))

(enable-console-print!)

(defn home-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:id "spider"}
               (dom/canvas #js {:id "demo-canvas" :width "100%" :height "100%"
                                })
               (dom/h1 #js {:className "main-title"} "hello there!"))


      #_(dom/div #js {:className "row home-row no-margin"}
               (dom/div #js {:className "col-xs-1"})
               (dom/div #js {:className "col-xs-10"}

                        (dom/div #js {:className "front-page-container"}

                                 (dom/p nil
                                        (dom/strong #js {:className "front-page-title"} "Come on in..."))

                                 (dom/p nil
                                        (dom/strong nil "We're Solari Architects.")
                                        (dom/p nil "Our studio is base in Wellington and our thoughts, projects and experiences span New Zealand, Australia and beyond."))

                                 (dom/div #js {:className "col-xs-1"})))))))

(defroute "/" {:as params}
          (do
            (om/root home-page {}
                   {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "home"))
            (js/blabla)))

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
                                                      (dom/img #js {:src "/img/project.jpg"})
                                                      (dom/figcaption nil
                                                                      (dom/h3 nil "Wadestown Renovation")
                                                                      (dom/a #js {:href "#"} "Take a look"))
                                                      )
                                             ))

                         (dom/li nil
                                 (dom/figure nil
                                             (dom/div nil
                                                      (dom/img #js {:src "/img/project.jpg"})
                                                      (dom/figcaption nil
                                                                      (dom/h3 nil "Catlina Lane Subdivision")
                                                                      (dom/a #js {:href "#"} "Take a look"))
                                                      )
                                             ))

                         )
                 )
        )
      )))

(defroute "/residential" {:as params}
          (do
            (om/root residential-page {}
                   {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "for-you"))))

(defn multi-residential-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the multi-residential page"))))

(defroute "/multi-residential" {:as params}
          (do
            (om/root multi-residential-page {}
                   {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "for-you"))))

(defn commercial-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the commercial page"))))

(defroute "/commercial" {:as params}
          (do
            (om/root commercial-page {}
                   {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "for-you"))
            )
          )

(defn our-process-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the our process page"))))

(defroute "/our-process" {:as params}
          (do
          (om/root our-process-page {}
                   {:target (. js/document (getElementById "main-content-container"))})
          (ef/at "body" (ef/set-attr :background "for-you"))
            )
          )

(defn faqs-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the faqs page"))))

(defroute "/faqs" {:as params}
          (do
          (om/root faqs-page {}
                   {:target (. js/document (getElementById "main-content-container"))})
          (ef/at "body" (ef/set-attr :background "for-you"))
            )
          )

(defn your-team-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the your team page"))))

(defroute "/your-team" {:as params}
          (do
            (om/root your-team-page {}
                   {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "for-you"))
            )
          )

(defn your-career-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the your career page"))))

(defroute "/your-career" {:as params}
          (om/root your-career-page {}
                   {:target (. js/document (getElementById "main-content-container"))}))

(defn meet-the-team-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the meet your team page"))))

(defroute "/meet-the-team" {:as params}
          (om/root meet-the-team-page {}
                   {:target (. js/document (getElementById "main-content-container"))}))


