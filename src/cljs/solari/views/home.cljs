(ns solari.views.home
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [solari.views.common :as common]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [solari.utils :as u])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)


(defn home-init [atom]
  (do
    (om/root common/paragraph-partial atom
             {:target (. js/document (getElementById "main-content-container"))
              :init-state {:color "black"}})
    (ef/at "body" (ef/set-attr :background "home"))
    (ef/at "#nav-hint-inner" (ef/content "architects"))))

(defn for-you-init [atom]
  (do
    (om/root common/paragraph-partial atom
             {:target (. js/document (getElementById "main-content-container"))
              :init-state {:color "white"}})
    (ef/at "body" (ef/set-attr :background "for-you"))
    (ef/at "#nav-hint-inner" (ef/content "for you"))))

(defn for-architects-init [atom]
  (do
    (om/root common/paragraph-partial atom
             {:target (. js/document (getElementById "main-content-container"))
              :init-state {:color "white"}})
    (ef/at "body" (ef/set-attr :background "for-architects"))
    (ef/at "#nav-hint-inner" (ef/content "for architects"))))


(defn from-us [data owner]
  (reify

    om/IDidMount
    (did-mount [this]
      (.dcSocialStream
          (js/$ "#social-wall-root")
          #js {:feeds
               #js {:pinterest #js {:id "jsolari"}}
               :wall true}))

    om/IRender
    (render [this]
      (dom/div #js {:id "social-wall-root"}))))


(defn from-us-init [atom]
  (do
    (om/root from-us atom
             {:target (. js/document (getElementById "main-content-container"))})
    (ef/at "body" (ef/set-attr :background "from-us"))
    (ef/at "#nav-hint-inner" (ef/content "from us"))))

