(ns solari.views.faqs
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [solari.views.common :as common])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn text-partial [data owner]
  (reify

    om/IRender
    (render [this]
      (dom/p #js {:className "text-box"} data))))


(defn faqs-page [data owner]
  (reify

    om/IDidMount
    (did-mount [this]
      (js/accordion))

    om/IRender
    (render [this]
      (dom/div nil

              (om/build text-partial (:text data))

               (dom/div #js {:className "accordion"}
                        (apply dom/dl nil
                               (om/build-all common/accordion-partial (:questions data))))))))


(defn faqs-init [atom]
  (do
    (om/root faqs-page atom
             {:target (. js/document (getElementById "main-content-container"))})
    (ef/at "body" (ef/set-attr :background "home"))
    (ef/at "#nav-hint-inner" (ef/content "Welcome"))))

