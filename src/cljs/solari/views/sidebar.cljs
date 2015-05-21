(ns solari.views.sidebar
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [cljs.core.async :refer [put! chan <! >! take! close!]]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

#_(def menu-map (atom {:top-level ["for you" "for architects" "from us"]
                     :sub-level1 ["Residential" "Multi-Residential" "Commercial" "Our Process" "FAQs" "Your Team"]
                     :sub-level2 ["Your Career" "Meet the team" "Jobs"]
                     :sub-level3 ["contact"]}))

(def nav-map (atom {:root [{:id "nav-left-01" :label "for you" :selected false
                            :submenu {:id "nav-left-01-sub"
                                      :items [{:id "nav-right-item-residential" :name "residential" :selected false}
                                              {:id "nav-right-item-muti" :name "multi-residential" :selected false}
                                              {:id "nav-right-item-commerical" :name "commerical" :selected false}
                                              {:id "nav-right-item-our" :name "our process" :selected false}
                                              {:id "nav-right-item-faqs" :name "faqs" :selected false}
                                              {:id "nav-righ-item-yourt" :name "your team" :selected false}]}}

                           {:id "nav-left-02" :label "for architects" :selected false
                            :submenu {:id "nav-left-02-sub"
                                      :items [{:id "nav-right-item-yourc" :name "your career" :selected false}
                                              {:id "nav-right-item-meet" :name "meet the team" :selected false}
                                              {:id "nav-right-item-jobs" :name "jobs" :selected false}]}}

                           {:id "nav-left-03" :label "from us" :selected false
                            :submenu {:id "nav-left-03-sub"
                                      :items [{:id "nav-right-item-contact" :name "contact" :selected false}]} }]

                    :selected false}))


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
                         #(let [text (.-innerHTML (.-currentTarget %))]
                           (.dir js/console %)
                           (toggle-main-menu (.-currentTarget %))
                           (cond
                             (not= -1 (.indexOf text "you")) (toggle-submenu "sub1")
                             (not= -1 (.indexOf text "architects")) (toggle-submenu "sub2")
                             (not= -1 (.indexOf text "us")) (toggle-submenu "sub3")
                             :else "list item not there")))))

(defn right-nav-listener []
 (ef/at ".nav-ul-right"
        (ev/listen-live :click "li" #(let [text (.-innerHTML (.-currentTarget %))]
                                      (println "hi there guys:" (.toLowerCase (.-innerHTML (.-currentTarget %))) )
                                      (println (.indexOf (.toLowerCase text) "your career"))
                           (cond
                             (= 0 (.indexOf (.toLowerCase text) "residential")) (sec/dispatch! "/residential")
                             (= 0 (.indexOf (.toLowerCase text)  "multi-residential")) (sec/dispatch! "/multi-residential")
                             (= 0 (.indexOf (.toLowerCase text) "commercial"))  (sec/dispatch! "/commercial")
                             (= 0 (.indexOf (.toLowerCase text) "our process")) (sec/dispatch! "/our-process")
                             (= 0 (.indexOf (.toLowerCase text) "faqs")) (sec/dispatch! "/faqs")
                             (= 0 (.indexOf (.toLowerCase text) "your team")) (sec/dispatch! "/your-team")
                             (= 0 (.indexOf (.toLowerCase text) "your career"))  (sec/dispatch! "/your-career")
                             (= 0 (.indexOf (.toLowerCase text) "meet the team")) (sec/dispatch! "/meet-the-team")
                             (= 0 (.indexOf (.toLowerCase text) "jobs")) (sec/dispatch! "/jobs")
                             (= 0 (.indexOf (.toLowerCase text) "contacts")) (sec/dispatch! "/contacts")
                             :else "list item not there"))))
  )
(right-nav-listener)
(sec/dispatch! "/faqs")

;Listeners
(defn listener-init []
  (left-nav-listener)
  (right-nav-listener))

(defn nav-menu-item-right [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/li #js {:id (:id data)} (:name data)))))

(defn nav-menu-item-left [data owner]
  (reify
    om/IDidMount
    (did-mount [this]
      (ef/at (str "#" (:id data))
           (ev/listen :click
                      #(put! (:clicked (om/get-state owner))
                             (.-id (.-currentTarget %))))))
    om/IRender
    (render [this]
      (dom/li #js {:id (:id data)
                   :className (if (:selected data) "nav-left-selected")}
              (:label data)))))


@nav-map
(get-in @nav-map [:root 0 :submenu :items] )
(defn main-nav-view [menu-atom owner]
  (reify

    om/IInitState
    (init-state [_]
      {:clicked (chan)})

    om/IWillMount
    (will-mount [_]
      (let [clicked (om/get-state owner :clicked)]
        (go (loop []
              (let [selected (<! clicked)]
                (loop [idx 0]
                      (if (= (get-in menu-atom [:root idx :id])
                            selected)
                        (om/transact! menu-atom [:root idx :selected] (fn [_]  true))
                        (om/transact! menu-atom [:root idx :selected] (fn [_] false)))
                      (if (< (+ 1 idx) (count (:root menu-atom)))
                        (recur (inc idx))))
                (recur))))))

    om/IRenderState
    (render-state [this {:keys [clicked]}]
      (dom/div nil

               (dom/div #js {:className "main-nav-left"}
                        (dom/h1 #js {:className "logo"} "Solari")
                        (apply dom/ul #js {:className "nav-ul-left"}
                               (om/build-all nav-menu-item-left (:root menu-atom)
                                             {:init-state {:clicked clicked}}))
                        (dom/footer #js {:id "main-footer" :className "gooter cf"}))

               (dom/div #js {:className "main-nav-right"}
                        (dom/h1 #js {:className "context"} "Context")
                        (apply dom/ul #js {:className "nav-ul-right sub1 "}
                               (om/build-all nav-menu-item-right
                                             (get-in menu-atom [:root 0 :submenu :items])))
                        (apply dom/ul #js {:className "nav-ul-right sub2 "}
                               (om/build-all nav-menu-item-right
                                             (get-in menu-atom [:root 1 :submenu :items])))
                        (apply dom/ul #js {:className "nav-ul-right sub3 "}
                               (om/build-all nav-menu-item-right
                                             (get-in menu-atom [:root 2 :submenu :items])))
                        (dom/footer #js {:id "main-footer" :className "gooter cf"}))))))

(defn nav-init [menu-atom]
  (do
    (om/root main-nav-view menu-atom
             {:target (. js/document (getElementById "main-nav-container"))})
    #_(listener-init)))

(nav-init nav-map)

