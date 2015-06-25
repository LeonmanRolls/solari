(ns solari.views.sidebar
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [solari.routes :as routes]
            [cljs.core.async :refer [put! chan <! >! take! close!]]
            [enfocus.effects :as eff]
            [solari.data :as data]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]
                   [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(defn nav-menu-item-right [data owner]
  (reify
    om/IDidMount
    (did-mount [this]
      (ef/at (str "#" (:id data))
             (ev/listen :click (fn [x] (do (go (>! (:right-clicked (om/get-state owner)) {:route (:route data)
                                                                                          :id (:id data)
                                                                                          :data data})))))))
    om/IRender
    (render [this]
      (dom/a #js {:href (str "/#" (:route data))}
             (dom/li #js {:id (:id data) :className (if (:selected data) "right-nav-selected" "")} (:name data))))))

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
      (dom/a #js {:href (str "#" (:route data))}
             (dom/li #js {:id (:id data)
                          :className (if (:selected data) "nav-left-selected")}
                     (:label data))))))


(defn main-nav-view [menu-atom owner]
  (reify

    om/IInitState
    (init-state [_]
      {:clicked (chan)
       :right-clicked (chan)})

    om/IWillMount
    (will-mount [_]
      (let [clicked (om/get-state owner :clicked)
            right-clicked (om/get-state owner :right-clicked)]

        (go (loop []
              (let [selected (<! clicked)]
                (loop [idx 0]
                  (if (= (get-in menu-atom [:root idx :id])
                         selected)
                    (om/transact! menu-atom [:root idx :selected] (fn [_]  true))
                    (om/transact! menu-atom [:root idx :selected] (fn [_] false)))
                  (if (< (+ 1 idx) (count (:root menu-atom)))
                    (recur (inc idx))))
                (recur))))

        (go (loop []
              (let [selected (<! right-clicked)]
                (loop [idx 0]
                  (when (< idx (count (:root menu-atom)))
                    (let [sub-menu-count (count (get-in menu-atom [:root idx :submenu :items])) ]
                      (loop [idxx 0]
                        (when (< idxx sub-menu-count)
                          (if (= (:id selected) (get-in menu-atom [:root idx :submenu :items idxx :id]))

                            (do (om/transact! menu-atom [:root idx :submenu :items idxx :selected] (fn [_]  true))
                                )

                            (om/transact! menu-atom [:root idx :submenu :items idxx :selected] (fn [_]  false)))
                          (recur (inc idxx)))))
                    (recur (inc idx))))
                (recur))))))

    om/IDidMount
    (did-mount [this]
      (ef/at ".logo"
             (ev/listen :click (fn [x] (do
                                         ;(routes/dispatch-route "/")
                                         (loop [idx 0]
                                           (when (< idx 3)
                                             (om/transact! menu-atom [:root idx :selected] (fn [_] false))
                                             (recur (inc idx)))))))))

    om/IRenderState
    (render-state [this {:keys [clicked right-clicked]}]
      (dom/div nil

               (dom/div #js {:className "main-nav-left"}
                        (dom/a #js {:href "/#/"}
                               (dom/h1 #js {:className "logo"} "Solari"))

                        (apply dom/ul #js {:className "nav-ul-left"}
                               (om/build-all nav-menu-item-left (:root menu-atom)
                                             {:init-state {:clicked clicked}}))

                        (dom/div #js {:id "social-container" :style #js {:textAlign "center"}}
                                 (dom/div nil
                                          (dom/a #js {:href "http://pinterest.com/solariarchitect/" :target "_blank"}
                                                 (dom/i #js {:className "fa fa-pinterest fa-2x"}))
                                          (dom/a #js {:href "https://twitter.com/solariarch" :target "_blank"}
                                                 (dom/i #js {:className "fa fa-twitter fa-2x"}))
                                          (dom/a #js {:href "" :target "_blank"}
                                                 (dom/i #js {:className "fa fa-instagram fa-2x"}))
                                          (dom/a #js {:href "" :target "_blank"}
                                                 (dom/i #js {:className "fa fa-google-plus fa-2x"}))
                                          (dom/a #js {:href "" :target "_blank"}
                                                 (dom/i #js {:className "fa fa-facebook fa-2x"})))

                                 )


                        (dom/a #js {:href "http://nang.rocks" :target "_blank"}
                               (dom/footer #js {:id "main-footer" :className "footer cf" :style #js {:textTransform "uppercase"
                                                                                                     :position "absolute"
                                                                                                     :bottom "0"
                                                                                                     :left "0"
                                                                                                     :right "0"
                                                                                                     :textAlign "center"
                                                                                                     :color "grey"}}
                                           "Website by Nang")))

               (dom/div #js {:className "main-nav-right"}
                        (dom/div #js {:id "nav-hint-outer"}
                                 (dom/div #js {:id "nav-hint-inner"} "architects"))

                        (apply dom/ul #js {:className (str "nav-ul-right sub1 "
                                                           (if (get-in menu-atom [:root 0 :selected]) "" "hidden"))}
                               (om/build-all nav-menu-item-right
                                             (get-in menu-atom [:root 0 :submenu :items])
                                             {:init-state {:right-clicked right-clicked}}))

                        (apply dom/ul #js {:className (str "nav-ul-right sub2 "
                                                           (if (get-in menu-atom [:root 1 :selected]) "" "hidden"))}
                               (om/build-all nav-menu-item-right
                                             (get-in menu-atom [:root 1 :submenu :items])
                                             {:init-state {:right-clicked right-clicked}}))

                        (apply dom/ul #js {:className (str "nav-ul-right sub3 "
                                                           (if (get-in menu-atom [:root 2 :selected]) "" "hidden"))}
                               (om/build-all nav-menu-item-right
                                             (get-in menu-atom [:root 2 :submenu :items])
                                             {:init-state {:right-clicked right-clicked}}))

                        (dom/footer #js {:id "main-footer" :className "gooter cf"}))


               ))))

(defn nav-init [menu-atom]
  (om/root main-nav-view menu-atom
           {:target (. js/document (getElementById "main-nav-container"))}))

