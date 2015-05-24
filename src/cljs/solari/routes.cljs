(ns solari.routes
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [solari.views.overview :as overview]
            [solari.views.project :as project]
            [solari.views.admin :as admin]
            [ajax.core :refer [GET POST PUT]]
            [cljs.core.async :refer [put! chan <! >! take! close!]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [enfocus.core :as ef]
            [solari.data :as data])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]])
  (:import goog.History))

(def route-chan (chan))
(defn dispatch-route [route] (sec/dispatch! route))

(go
 (loop []
  (dispatch-route (<! route-chan))
  ;(println "loop: " (<! route-chan))
   (recur)))

;Fallback for browsers without html5 history support
(sec/set-config! :prefix "#")


(defroute admin "/admin" []
          (do
            (om/root admin/admin-page data/projects-atom
                     {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "home"))
            (ef/at ".context" (ef/content ""))
            (js/blabla)))


(defn home-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:id "spider"}
               (dom/canvas #js {:id "demo-canvas" :width "100%" :height "100%"})
               (dom/div #js {:className "main-title"}
                        (dom/p #js {:className "title"} "Come on in...")
                        (dom/strong nil "We're Solari architects.")
                        (dom/p nil " Our studio is based in Wellington and our thoughts, projects and experiences span New Zealand, Australia and beyond."))))))

(defroute "/" []
          (do
            (om/root home-page {}
                     {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "home"))
            (ef/at ".context" (ef/content "Welcome"))
            (js/blabla)))


;How is this residiential atom working?
(defroute residential "/residential" []
          (do
            (overview/overview-init (atom (get-in @data/projects-atom [:projects 0])) route-chan)
            (ef/at ".context" (ef/content "Residential"))
            (ef/at "body" (ef/set-attr :background "for-you"))))

(defroute "/wadestown" []
          (do
            (project/project-init (atom (get-in @data/projects-atom [:projects 0 :projects 0])))
            (ef/at ".context" (ef/content ""))
            (ef/at "body" (ef/set-attr :background "for-you"))))

(defroute "/lyall" []
          (do
            (project/project-init (atom (get-in @data/projects-atom [:projects 0 :projects 1])))
            (ef/at ".context" (ef/content ""))
            (ef/at "body" (ef/set-attr :background "for-you"))))

(defroute "/catline" []
          (do
            (project/project-init (atom (get-in @data/projects-atom [:projects 0 :projects 2])))
            (ef/at ".context" (ef/content ""))
            (ef/at "body" (ef/set-attr :background "for-you"))))

(defn multi-residential-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the multi-residential page"))))

(defroute "/multi-residential" {:as params}
          (do
            (overview/overview-init data/projects-atom route-chan)
            (ef/at ".context" (ef/content "multi"))
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
            (ef/at ".context" (ef/content "commerical"))
            (ef/at "body" (ef/set-attr :background "for-you"))))

(defn our-process-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/ul #js {:className "cbp_tmtimeline"}
              (dom/li nil
                      (dom/time #js {:className "cbp_tmtime"} (dom/span nil "One"))
                      (dom/div #js {:className "cbp_tmicon cbp_tmicon-phone"})
                      (dom/div #js {:className "cbp_tmlabel"}
                               (dom/h2 nil "Riceban black-eyed pea")
                               (dom/p nil "Winter purslane sdfdas
                               asfasdf sdafsadf sfaddsa")))

              (dom/li nil
                      (dom/time #js {:className "cbp_tmtime"} (dom/span nil "Two"))
                      (dom/div #js {:className "cbp_tmicon cbp_tmicon-phone"})
                      (dom/div #js {:className "cbp_tmlabel"}
                               (dom/h2 nil "Riceban black-eyed pea")
                               (dom/p nil "Winter purslane...")))

              (dom/li nil
                      (dom/time #js {:className "cbp_tmtime"} (dom/span nil "Three"))
                      (dom/div #js {:className "cbp_tmicon cbp_tmicon-phone"})
                      (dom/div #js {:className "cbp_tmlabel"}
                               (dom/h2 nil "Riceban black-eyed pea")
                               (dom/p nil "Winter purslane...")))
              ))))

(defroute "/our-process" {:as params}
          (do
            (om/root our-process-page {}
                     {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "for-you"))))

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
          (do
            (om/root your-career-page {}
                   {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "for-architects"))))

(defn meet-the-team-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the meet your team page"))))

(defroute "/meet-the-team" {:as params}
          (om/root meet-the-team-page {}
                   {:target (. js/document (getElementById "main-content-container"))}))

(defn contact-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the meet your contact page"))))

(defroute "/contact" {:as params}
          (do
            (om/root contact-page {}
                   {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "from-us"))))

;; Quick and dirty history configuration.
(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(sec/dispatch! (.-token %)))
  (doto h (.setEnabled true)))
