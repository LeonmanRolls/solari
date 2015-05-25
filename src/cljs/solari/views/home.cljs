(ns solari.views.home
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn home-page [data owner]
  (reify

    om/IRender
    (render [this]
      (dom/div #js {:className "main-title"}
               (dom/p #js {:className "title"} (:main-title data))
               (dom/strong nil (:sub-title data))
               (dom/p nil (:paragraph-one data))
               (dom/p nil (:paragraph-two data)))
      )))

(defn home-init [atom]
  (do
    (om/root home-page atom
             {:target (. js/document (getElementById "main-content-container"))})
    (ef/at "body" (ef/set-attr :background "home"))
    (ef/at "#nav-hint-inner" (ef/content "Welcome"))))

