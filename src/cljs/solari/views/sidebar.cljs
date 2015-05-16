(ns solari.views.sidebar
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(def menu-map (atom {:top-level ["for you" "for architects" "from us"]
                     :sub-level1 ["Residential" "Multi-Residential" "Commercial" "Our Process" "FAQs" "Your Team"]
                     :sub-level2 ["Your Career" "Meet the team" "Jobs"]
                     :sub-level3 ["contact"]}))

;Actions
(defn toggle-sub-menu-selection [target]
  (do
    (ef/at ".nav-ul-right li" (ef/remove-class "nav-right-selected"))
    (ef/at target (ef/add-class "nav-right-selected"))))

(defn toggle-submenu [submenu]
  (do (ef/at ".nav-ul-right" (ef/add-class "hidden"))
      (ef/at (str "." submenu) (ef/remove-class "hidden"))))

(defn toggle-main-menu [target]
  (do
    (ef/at ".nav-ul-left li" (ef/remove-class "nav-left-selected"))
    (ef/at target (ef/add-class "nav-left-selected"))))

(defn left-nav-listener []
  (ef/at ".logo"
         (ev/listen :click  #((do
                                (sec/dispatch! "/")
                                (ef/at ".nav-ul-left li" (ef/remove-class "nav-left-selected"))
                                (ef/at ".nav-ul-right" (ef/add-class "hidden"))))))

  (ef/at ".nav-ul-left"
         (ev/listen-live :click "li"
                         #(let [text (.-innerText (.-currentTarget %))]
                           (toggle-main-menu (.-currentTarget %))
                           (cond
                             (not= -1 (.indexOf text "you")) (toggle-submenu "sub1")
                             (not= -1 (.indexOf text "architects")) (toggle-submenu "sub2")
                             (not= -1 (.indexOf text "us")) (toggle-submenu "sub3")
                             :else "list item not there")))))

(defn right-nav-listener []
 (ef/at ".nav-ul-right"
        (ev/listen-live :click "li" #(let [text (.-innerText (.-currentTarget %))]
                           (toggle-main-menu (.-currentTarget %))
                           (cond
                             (not= -1 (.indexOf text "you")) (toggle-submenu "sub1")
                             (not= -1 (.indexOf text "architects")) (toggle-submenu "sub2")
                             (not= -1 (.indexOf text "us")) (toggle-submenu "sub3")
                             :else "list item not there"))))

  )

;Listeners
(defn listener-init []
  (left-nav-listener)
  (right-nav-listener))

(defn nav-menu-item-right [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/li nil data))))

(defn nav-menu-item-left [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/li nil data))))

(defn main-nav-view [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil

               (dom/div #js {:className "main-nav-left"}
                        (dom/h1 #js {:className "logo"} "Solari")
                        (apply dom/ul #js {:className "nav-ul-left"}
                               (om/build-all nav-menu-item-left (@menu-map :top-level)))
                        (dom/footer #js {:id "main-footer" :className "gooter cf"}))

               (dom/div #js {:className "main-nav-right"}
                        (apply dom/ul #js {:className "nav-ul-right sub1 hidden"}
                               (om/build-all nav-menu-item-right (@menu-map :sub-level1)))
                        (apply dom/ul #js {:className "nav-ul-right sub2 hidden"}
                               (om/build-all nav-menu-item-right (@menu-map :sub-level2)))
                        (apply dom/ul #js {:className "nav-ul-right sub3 hidden"}
                               (om/build-all nav-menu-item-right (@menu-map :sub-level3)))
                        (dom/footer #js {:id "main-footer" :className "gooter cf"}))))))

(defn nav-init []
  (do
    (om/root main-nav-view {}
             {:target (. js/document (getElementById "main-nav-container"))})
    (listener-init)))
(nav-init)

