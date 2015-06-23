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

(go (.log js/console (<! (jsonp query-url))))

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
     om/IDidMount
    (did-mount [this]
      (.dcSocialStream
        (js/$ "#social-wall-root")
        #js {:feeds #js {:pinterest #js {:id "jsolari"}} :wall true}))

    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (om/build common/paragraph-partial data {:state state})
               (dom/div #js {:id "social-wall-root"})))))
