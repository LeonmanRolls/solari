(ns solari.routes
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [solari.views.overview :as overview]
            [solari.views.project :as project]
            [solari.views.allprojects :as allprojects]
            [solari.views.yourteam :as yourteam]
            [solari.views.home :as home]
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
   (let [route (<! route-chan)]
     (dispatch-route route))
  ;(println "loop: " (<! route-chan))
   (recur)))

;Fallback for browsers without html5 history support
(sec/set-config! :prefix "#")

(defroute all-projects "/all-projects" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (allprojects/all-projects-init data/individual-projects-atom)))

(defroute admin "/admin" []
          (admin/admin-init data/home-page-atom))

(defroute "/" []
          (home/home-init data/home-page-atom))

(defroute "/home" []
          (home/home-init data/home-page-atom))

;How is this residiential atom working?
(defroute residential "/residential" []
          (do
            (overview/overview-init (atom (get-in @data/projects-atom [:projects 0])) route-chan)
            (ef/at "#nav-hint-inner" (ef/content "Residential"))
            (ef/at "body" (ef/set-attr :background "for-you"))))

(defroute "/wadestown" []
          (do
            (project/project-init (atom (get-in @data/projects-atom [:projects 0 :projects 0])))
            (ef/at "#nav-hint-inner" (ef/content ""))
            (ef/at "body" (ef/set-attr :background "grey"))))

(defroute "/lyall" []
          (do
            (project/project-init (atom (get-in @data/projects-atom [:projects 0 :projects 1])))
            (ef/at "#nav-hint-inner" (ef/content ""))
            (ef/at "body" (ef/set-attr :background "grey"))))

(defroute "/catline" []
          (do
            (project/project-init (atom (get-in @data/projects-atom [:projects 0 :projects 2])))
            (ef/at "#nav-hint-inner" (ef/content ""))
            (ef/at "body" (ef/set-attr :background "grey"))))

(defn multi-residential-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the multi-residential page"))))

(defroute "/multi-residential" {:as params}
          (do
            (overview/overview-init data/projects-atom route-chan)
            (ef/at "#nav-hint-inner" (ef/content "multi"))
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
            (ef/at "#nav-hint-inner" (ef/content "commerical"))
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

            (yourteam/your-team-init (atom {}))

            (ef/at "body" (ef/set-attr :background "for-you"))))

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

