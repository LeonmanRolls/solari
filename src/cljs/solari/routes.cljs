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
            [solari.views.fromus :as fromus]
            [solari.views.admin :as admin]
            [solari.views.faqs :as faqs]
            [solari.utils :as utils]
            [solari.views.process :as process]
            [ajax.core :refer [GET POST PUT]]
            [cljs.core.async :refer [put! chan <! >! take! close!]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [enfocus.core :as ef]
            [solari.data :as data]
            [solari.views.common :as common])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]])
  (:import goog.History))

(def route-chan (chan))

(go
  (loop []
    (let [route (<! route-chan)]
      (utils/dispatch-route route))
    (recur)))

;Fallback for browsers without html5 history support
(sec/set-config! :prefix "#")

(defroute the-team "/your-team" []
          (do
            (ef/at "body" (ef/set-attr :background "from-us"))
            (ef/at "#nav-hint-inner" (ef/content "Your Team"))
            (om/root theteam/team-members-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:color "white" :key :the-team-data :interop false}})))

(defroute "/" []
          (do
            (ef/at "body" (ef/set-attr :background "home"))
            (ef/at "#nav-hint-inner" (ef/content "architects"))
            (ef/at "#nav-hint-inner" (ef/add-class  "black"))
            (om/root common/paragraph-partial data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:color "black" :key :home-page-data :admin false}})))

(defroute "/admin" []
          (do
            (ef/at "body" (ef/set-attr :background "home"))
            (ef/at "#nav-hint-inner" (ef/content "architects"))
            (om/root common/paragraph-partial data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:color "black" :key :home-page-data :admin true}})))

(defroute for-you "/for-you" []
          (do
            (ef/at "body" (ef/set-attr :background "for-you"))
            (ef/at "#nav-hint-inner" (ef/content "for you"))
            (ef/at "#nav-hint-inner" (ef/remove-class  "black"))
            (om/root common/paragraph-partial data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:color "white" :key :for-you-data :admin false}})))

(defroute for-you "/for-you/admin" []
          (do
            (ef/at "body" (ef/set-attr :background "for-you"))
            (ef/at "#nav-hint-inner" (ef/content "for you"))
            (om/root common/paragraph-partial data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:color "white" :key :for-you-data :admin true}})))

(defroute for-architects "/for-architects" []
          (do
            (ef/at "body" (ef/set-attr :background "for-architects"))
            (ef/at "#nav-hint-inner" (ef/remove-class  "black"))
            (ef/at "#nav-hint-inner" (ef/content "for architects"))
            (om/root common/paragraph-partial data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:color "white" :key :for-architects-data :admin false}})))

(defroute for-architects "/for-architects/admin" []
          (do
            (ef/at "body" (ef/set-attr :background "for-architects"))
            (ef/at "#nav-hint-inner" (ef/content "for architects"))
            (om/root common/paragraph-partial data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:color "white" :key :for-architects-data :admin true}})))


(defroute from-us "/from-us" []
          (do
            (ef/at "body" (ef/set-attr :background "from-us"))
            (ef/at "#nav-hint-inner" (ef/remove-class  "black"))
            (ef/at "#nav-hint-inner" (ef/content "from us"))
            (om/root fromus/from-uss data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:color "white" :key :from-us-data :admin false}})))

(defroute from-us "/from-us/admin" []
          (do
            (ef/at "body" (ef/set-attr :background "from-us"))
            (ef/at "#nav-hint-inner" (ef/content "from us"))
            (om/root common/paragraph-partial data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:color "white" :key :from-us-data :admin true}})))

(defroute all-projects "/all-projects" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "All Projects"))
            (om/root allprojects/all-projects-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :all-projects
                              :extra :for-you-data
                              :subtitle :year
                              :route "/all-projects"
                              :cat "cat-all"
                              :admin false}})))

(defroute all-projects "/all-projects/admin" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "All Projects"))
            (om/root allprojects/all-projects-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :all-projects
                              :extra :for-you-data
                              :route "/all-projects"
                              :subtitle :year
                              :cat "cat-all"
                              :admin true}})))

(defroute residential "/residential" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "residential"))
            (om/root allprojects/all-projects-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :all-projects
                              :extra :residential-data
                              :route "/residential"
                              :subtitle :year
                              :cat "cat-residential"
                              :admin false}})))

(defroute residential "/residential/admin" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "residential"))
            (om/root allprojects/all-projects-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :all-projects
                              :extra :residential-data
                              :route "/residential"
                              :subtitle :year
                              :cat "cat-residential"
                              :admin true}})))

(defroute multi-unit "/multi-residential" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "multi unit"))
            (om/root allprojects/all-projects-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :all-projects
                              :extra :multi-unit-data
                              :route "/multi-residential"
                              :subtitle :year
                              :cat "cat-multi-unit-residential"
                              :admin false}})))

(defroute multi-unit "/multi-residential/admin" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "multi unit"))
            (om/root allprojects/all-projects-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :all-projects
                              :extra :multi-unit-data
                              :route "/multi-residential"
                              :subtitle :year
                              :cat "cat-multi-unit-residential"
                              :admin true}})))

(defroute commerical "/commercial" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "multi unit"))
            (om/root allprojects/all-projects-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :all-projects
                              :extra :commercial-data
                              :route "/commercial"
                              :subtitle :year
                              :cat "cat-commercial"
                              :amdin false}})))

(defroute commerical "/commercial/admin" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "multi unit"))
            (om/root allprojects/all-projects-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :all-projects
                              :extra :commercial-data
                              :route "/commercial"
                              :subtitle :year
                              :cat "cat-commercial"
                              :admin true}})))

(defmulti individual (fn [uid admin dispatch] dispatch))

(defmethod individual "projects"
  [uid admin]  (om/root project/project-page data/all-data-atom
                        {:target (. js/document (getElementById "main-content-container"))
                         :state {:key :all-projects :filter uid :link :link :filterkey :projectid :admin admin
                                 :filter-vector [:all-projects] :subtitle :Role :url "projects"}}))

(defmethod individual "members"
  [uid admin]  (om/root project/project-page data/all-data-atom
                        {:target (. js/document (getElementById "main-content-container"))
                         :state {:key :the-team-data :filter uid :filterkey :memberid :admin admin
                                 :filter-vector [:the-team-data :team-members] :subtitle :year :url "members"}}) )

(defroute "/projects/individual/:id" {:as params}
          (do
            (ef/at "#nav-hint-inner" (ef/content (:id params)))
            (ef/at "body" (ef/set-attr :background "grey"))
            (individual (:id params) false "projects")))

(defroute "/projects/individual/:id/admin" {:as params}
          (do
            (ef/at "#nav-hint-inner" (ef/content (:id params)))
            (ef/at "body" (ef/set-attr :background "grey"))
            (individual (:id params) true "projects")))

(defroute "/members/individual/:id" {:as params}
          (do
            (ef/at "#nav-hint-inner" (ef/content (:id params)))
            (ef/at "body" (ef/set-attr :background "grey"))
            (individual (:id params) false "members")))

(defroute "/members/individual/:id/admin" {:as params}
          (do
            (ef/at "#nav-hint-inner" (ef/content (:id params)))
            (ef/at "body" (ef/set-attr :background "grey"))
            (individual (:id params) true "members")))

(defroute "/our-process" {:as params}
          (do
            (om/root process/process-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :process-data :admin false}})
            (ef/at "body" (ef/set-attr :background "from-us"))
            (ef/at "#nav-hint-inner" (ef/content "Our Process"))))

(defroute "/our-process/admin" {:as params}
          (do
            (om/root process/process-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :process-data :admin true}})
            (ef/at "body" (ef/set-attr :background "from-us"))
            (ef/at "#nav-hint-inner" (ef/content "Our Process"))))

(defroute "/faqs" []
          (do
            (om/root faqs/faqs-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :faqs-data :admin false}})
            (ef/at "body" (ef/set-attr :background "from-us"))
            (ef/at "#nav-hint-inner" (ef/content "faqs"))))

(defroute "/faqs/admin" []
          (do
            (om/root faqs/faqs-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :faqs-data :admin true}})
            (ef/at "body" (ef/set-attr :background "from-us"))
            (ef/at "#nav-hint-inner" (ef/content "faqs"))))

(defn your-career-page [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (let [local (:your-career-data data)]
        (dom/div #js {:style #js {:color "white"}}
                 (dom/b #js {:style #js {:fontSize "1.5em"}} "Here’s some good advice: Never choose to work for someone based on their brand or portfolio. Work for people who are going to teach you and make you better.")
                 (om/build common/p-p-partial (:paragraph-one local) {:state state})
                 (om/build common/p-p-partial (:paragraph-two local) {:state state}))))))

(defroute "/your-career" []
          (do
            (om/root your-career-page data/all-data-atom
                   {:target (. js/document (getElementById "main-content-container"))
                    :state {:admin false}})
            (ef/at "body" (ef/set-attr :background "your-career"))
            (ef/at "#nav-hint-inner" (ef/content "Your Career"))))

(defroute "/your-career/admin" []
          (do
            (om/root your-career-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:admin true}})
            (ef/at "body" (ef/set-attr :background "your-career"))
            (ef/at "#nav-hint-inner" (ef/content "Your Career"))))

(defroute "/meet-the-team" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "Your Team"))
            (om/root theteam/team-members-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :the-team-data :admin false :color "white" :interop true}})))

(defroute "/meet-the-team/admin" []
          (do
            (ef/at "body" (ef/set-attr :background "grey"))
            (ef/at "#nav-hint-inner" (ef/content "Your Team"))
            (om/root theteam/team-members-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:key :the-team-data :admin true :color "white"}})))

(defn jobs-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:style #js {:color "white"}}
               (dom/b #js {:style #js {:fontSize "2em"}} "We're a small tight-knit team that's big on attitude.")
               (dom/p nil "Talent without the right attitude is wasted. Talent with a great attitude is invaluable. If you'd like to joing the solari team - send us what makes you proud and be yourself.")

               (dom/b #js {:style #js {:fontSize "1.5em"}} "We've got the perfect home for a...")

               (dom/div #js {:style #js {:border "2px solid #c0392b" :padding "20px" :marginTop "20px"}}
                        (dom/b #js {:style #js {:textTransform "uppercase"}} "Gifted massage therapist")
                        (dom/p nil "Wanting to work for nothing.")
                       (dom/p nil "Apart from that we’ve no other listings. But stay tuned or follow us on LinkedIn for job updates and other happenings."))

               ))))


(defroute "/jobs" []
          (do
            (om/root jobs-page {}
                     {:target (. js/document (getElementById "main-content-container"))})
            (ef/at "body" (ef/set-attr :background "for-architects"))
            (ef/at "#nav-hint-inner" (ef/content "Jobs"))))


(defn contact-page [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (let [local (:contact-data data)]
        (dom/div #js {:style #js {:color "white"}}
                 (om/build common/paragraph-partial local {:state {:admin (:admin state) :key :text :color "white"}})

                 (dom/div  #js {:style #js {:border "2px solid #c0392b" :padding "20px" :marginTop "20px" :color "white"}}

                           (dom/b #js {:style #js {:textTransform "uppercase"}} "Visit, drink, chat, bounce ideas here")
                           (dom/p nil "Level 1")
                           (dom/p nil "13-15 Adelaide Road")
                           (dom/p nil "Mt Cook")
                           (dom/p nil "Wellington")

                           (dom/b #js {:style #js {:textTransform "uppercase"}} "Call, talk, joke, debate, ask here: ")
                           (dom/a #js {:href "tel:+6449744562" :style #js {:color "white"}}
                                  (dom/p nil "+64 4 9744562"))

                           (dom/b #js {:style #js {:textTransform "uppercase"}} "Email jokes, work or gifs here: ")
                           (dom/a #js {:style #js {:color "white"}
                                       :href "mailto:hello@solariarchitects.com"}
                                  (dom/p nil "hello@solariarchitects.com"))))))))


(defroute "/contact" {:as params}
          (do
            (om/root contact-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:admin false}})
            (ef/at "body" (ef/set-attr :background "from-us"))))

(defroute "/contact/admin" {:as params}
          (do
            (om/root contact-page data/all-data-atom
                     {:target (. js/document (getElementById "main-content-container"))
                      :state {:admin true}})
            (ef/at "body" (ef/set-attr :background "from-us"))))

;(do (sec/dispatch! (str "" (.-token %))) (println "token" (.-token %))  )
;; Quick and dirty history configuration.

(let [h (History.)]
  (goog.events/listen h EventType/NAVIGATE #(sec/dispatch! (.-token %)))
  (doto h (.setEnabled true)))

