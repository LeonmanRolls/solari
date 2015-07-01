(ns solari.views.fromus
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [ajax.core :refer [GET POST PUT]]
            [cljs.core.async :refer [put! chan <! >! take! close!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [solari.views.common :as common]
            [solari.utils :as u])
  (:require-macros [enfocus.macros :as em] [cljs.core.async.macros :refer [go]])
  (:import [goog.net Jsonp]
           [goog Uri]))

(enable-console-print!)

(def query-url "https://api.instagram.com/v1/users/1926926651/media/recent/?access_token=1399685155.be8a912.5059822a0f5f49d5bf922d070c44971d")

(defn jsonp [uri]
  (let [out (chan)
        req (Jsonp. (Uri. uri))]
    (.send req nil (fn [res] (put! out res)))
    out))


#_(defn get-all-games [result-chan]
  (GET "/games/"
     {:params {:info "all-game-info"
               :fields "gamename,gameid"
               :gameid "we"}
      :handler #(go (>! result-chan %))
      :error-handler u/ajax-error-handler}))

#_(GET "https://api.instagram.com/v1/users/1926926651/media/recent/?access_token=1399685155.be8a912.5059822a0f5f49d5bf922d070c44971d"
     {:handler #(println "fromus: " (js->clj %)) #_(go (>! result-chan %))
      :format :json
      :error-handler u/ajax-error-handler})

(defn from-uss [data owner]

  (reify
   om/IWillMount
    (will-mount [_]
      (go (om/update! data :instagram-data (js->clj (<! (jsonp query-url)) :keywordize-keys true))))

     om/IDidMount
    (did-mount [this]
      (js/megafolioInit))

    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/b #js {:style #js {:color "white"}}
                      "A gathering of ideas, images, thoughts, brainstorms, news and the miscellaneous interesting-ness.")

(apply dom/div #js {:className "megafolio-container" :stye #js {:marginTop "20px"}}
               (om/build-all common/instagram-gallery-partial (:data (:instagram-data data)))

               )

      (apply dom/div #js {:className "megafolio-container"}
               (om/build-all common/twitter-gallery-partial (:body (:twitter-data data)))

               )

               )

      ))
  )


