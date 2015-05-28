(ns solari.routes
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cemerick.url :refer (url url-encode)]
            [solari.views.overview :as overview]
            [solari.views.project :as project]
            [solari.views.allprojects :as allprojects]
            [solari.views.theteam :as theteam]
            [solari.views.yourteam :as yourteam]
            [solari.views.home :as home]
            [solari.views.foryou :as foryou]
            [solari.views.admin :as admin]
            [solari.views.faqs :as faqs]
            [solari.views.process :as process]
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
   (recur)))

;Fallback for browsers without html5 history support
(sec/set-config! :prefix "#")


(defroute all-projects "/all-projects/admin" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "All Projects - Admin"))
            (allprojects/all-projects-init data/individual-projects-atom "cat-all" data/for-architects-atom)))

(defroute for-you "/for-you" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "for you"))
            (home/for-you-init data/for-you-atom)))

(defroute for-you "/for-architects" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "for architects"))
            (home/for-architects-init data/for-architects-atom )))

(defroute for-you "/from-us" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "from us"))
            (home/from-us-init data/from-us-atom )))


(defroute the-team "/your-team" []
          (do
            (ef/at "body" (ef/set-attr :background "from-us"))
            (ef/at "#nav-hint-inner" (ef/content "Your Team"))
            (theteam/the-team-init data/the-team-atom "cat-architect")))

(defroute all-projects "/all-projects" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "All Projects"))
            (allprojects/all-projects-init data/individual-projects-atom "cat-all" data/for-you-atom)))

(defroute residential "/residential" []
          (do
            (allprojects/all-projects-init data/individual-projects-atom "cat-residential" data/residential-atom)
            (ef/at "#nav-hint-inner" (ef/content "Residential"))
            (ef/at "body" (ef/set-attr :background "grey"))))

(defroute "/multi-residential" {:as params}
          (do
            (allprojects/all-projects-init data/individual-projects-atom "cat-multi-unit-residential" data/multi-unit-atom)
            (ef/at "#nav-hint-inner" (ef/content "Multi-unit residential"))
            (ef/at "body" (ef/set-attr :background "grey"))))

(defroute "/commercial" {:as params}
          (do
            (allprojects/all-projects-init data/individual-projects-atom "cat-commercial" data/commercial-atom)
            (ef/at "#nav-hint-inner" (ef/content "Commercial"))
            (ef/at "body" (ef/set-attr :background "grey"))))

(defroute admin "/admin" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "Admin"))
            (admin/admin-init data/home-page-atom)))

(defroute "/" []
          (do
            (ef/at "body" (ef/set-attr :background "home"))
            (ef/at "#nav-hint-inner" (ef/content "Welcome"))
            (home/home-init data/home-page-atom)))


(defroute "/wadestown" []
          (do
            (project/project-init (atom (get-in @data/projects-atom [:projects 0 :projects 0])))
            (ef/at "#nav-hint-inner" (ef/content "Residential - Wadestown"))
            (ef/at "body" (ef/set-attr :background "grey"))))

(defroute "/lyall" []
          (do
            (project/project-init (atom (get-in @data/projects-atom [:projects 0 :projects 1])))
            (ef/at "#nav-hint-inner" (ef/content "Residential - Lyall"))
            (ef/at "body" (ef/set-attr :background "grey"))))

(defroute "/catline" []
          (do
            (project/project-init (atom (get-in @data/projects-atom [:projects 0 :projects 2])))
            (ef/at "#nav-hint-inner" (ef/content "Residential - Catline"))
            (ef/at "body" (ef/set-attr :background "grey"))))


(defroute "/our-process" {:as params}
          (do
            (process/process-init data/process-atom)
            (ef/at "body" (ef/set-attr :background "from-us"))
            (ef/at "#nav-hint-inner" (ef/content "Our Process"))))


(defroute "/faqs" []
          (do
            (faqs/faqs-init data/faqs-atom)
            (ef/at "body" (ef/set-attr :background "from-us"))
            (ef/at "#nav-hint-inner" (ef/content "faqs"))))


(defn your-career-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:style #js {:color "white" :maxWidth "800px" :marginLeft "auto" :marginRight "auto"}}
               (dom/p #js {:style #js {:font-size "1.6em" :line-height "1.2em"}}
                      (dom/b nil (:bold (:main data)) ) (:paragraph (:main data)))
               (dom/p nil (:paragraph-one data))
               (dom/p nil (:paragraph-two data))))))

(defroute "/your-career" []
          (do
            (om/root your-career-page data/your-career-atom
                   {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "your-career"))
            (ef/at "#nav-hint-inner" (ef/content "Your Career"))))

(defn meet-the-team-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the meet your team page"))))


(defroute "/meet-the-team" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "Your Team"))
            (yourteam/your-team-init data/the-team-atom ))
          #_(do
            (om/root meet-the-team-page {}
                   {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "for-architects"))
            (ef/at "#nav-hint-inner" (ef/content "Meet the team"))))

(defn jobs-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil "This is the jobs"))))


(defroute "/jobs" []
          (do
            (om/root jobs-page {}
                     {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "for-architects"))
            (ef/at "#nav-hint-inner" (ef/content "Jobs"))))

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

;(do (sec/dispatch! (str "" (.-token %))) (println "token" (.-token %))  )
;; Quick and dirty history configuration.

(let [h (History.)]
    (goog.events/listen h EventType/NAVIGATE #(sec/dispatch! (.-token %)))
    (doto h (.setEnabled true)))




