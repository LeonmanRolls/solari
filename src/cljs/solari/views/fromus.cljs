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
            [solari.utils :as u]
            [cljs-time.core :as cjt]
            [cljs-time.coerce :as cjtc])
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

(defn filter-twitter-media [twitter-data]
  (filter (fn [x] (:media (:entities x))) twitter-data))

(defn flatten-instagram-data [instagram-data]
  (map
    (fn [instagram-data]
      {:href (:link instagram-data)
       :image (:url (:standard_resolution (:images instagram-data)))
       :iort "i"
       :timestamp (int (:created_time (:caption instagram-data)))
       :subtitle (:text (:caption instagram-data))})
    instagram-data))

(defn parse-twitter-time-stamp [twitter-timestamp]
  (.format
    (js/moment twitter-timestamp "dd MMM DD HH:mm:ss ZZ YYYY" "en")
    "X"))

(defn flatten-twitter-data [twitter-data]
  (map
    (fn [twitter-data]
      {:href  (:url (first (:media (:entities twitter-data))))
       :image (:media_url (first (:media (:entities twitter-data))))
       :iort "t"
       :timestamp (int (parse-twitter-time-stamp (:created_at twitter-data)))
       :subtitle (:text twitter-data)})
    (filter-twitter-media twitter-data)))

(defn time-stamp-sort [all-social-data]
  (sort-by :timestamp < all-social-data))

(defn from-uss [data owner]

  (reify
    om/IWillMount
    (will-mount [_])

    om/IDidMount
    (did-mount [this]
      (go
        (let [twitter-chan (chan)]
          (om/update! data :instagram-data (js->clj (<! (jsonp query-url)) :keywordize-keys true))
          (GET "/twitter/"
               {:format :edn
                :handler #(go (>! twitter-chan %))
                :error-handler u/ajax-error-handler})
          (om/update! data :twitter-data (<! twitter-chan))
          (js/megafolioInit)

          (ef/at "#social-loading" (ef/add-class "hidden"))
          (ef/at "#mfone" (ef/remove-class "hidden"))
          (ef/at "#mftwo" (ef/remove-class "hidden")))))

    om/IWillUnmount
    (will-unmount [this]
      (ef/at "#social-loading" (ef/remove-class "hidden")))

    om/IRenderState
    (render-state [this state]
      (let [instagram-data (:data (:instagram-data data))
            twitter-data  (:body (:twitter-data (:twitter-data data)))
            processed-data (time-stamp-sort
                             (into
                               (flatten-instagram-data instagram-data)
                               (flatten-twitter-data twitter-data)))]

        (dom/div nil

               (dom/b #js {:style #js {:color "white"}}
                      "A gathering of ideas, images, thoughts, brainstorms, news and the miscellaneous interesting-ness.")

               (dom/div #js {:id "social-loading" :className "loader" :style #js {:color "white" :left "50%"}})

               (apply dom/div #js {:id "mfone" :className "megafolio-container hidden" :style #js {:marginTop "20px"}}
                      (om/build-all common/social-gallery-partial processed-data)))))))


