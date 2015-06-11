(ns solari.views.theteam
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [solari.views.common :as common]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(def hipster-data [{:href "" :text "everyday us"
                    :callback #(do (.megafilter js/api "cat-everyday")
                                   (ef/at "#group_photo" (ef/set-attr :src "/img/group_photo_everyday.jpg")))}

                   {:href "" :text "\"architect\" us"
                    :callback #(do (.megafilter js/api "cat-hipster")
                                   (ef/at "#group_photo" (ef/set-attr :src "/img/group_photo_hipster.jpg")))}])


(defn megafolio-preprocessor [team-members]
  (into []
        (concat
          (into []
                (map
                  (fn [x] (conj
                            {}
                            {:thumbnail (:hipster (:profilepics x))}
                            {:category "cat-all cat-hipster"}
                            {:id (:memberid x)}
                            {:title (:name x)}
                            {:memberid (:memberid x)}))
                  team-members))
          (into []
                (map
                  (fn [x] (conj
                            {}
                            {:thumbnail (:everyday (:profilepics x))}
                            {:category "cat-all cat-everyday"}
                            {:id (:memberid x)}
                            {:title (:name x)}
                            {:memberid (:memberid x)}))
                  team-members)))))


(defn team-members-page [data owner]
  (reify

    om/IDidMount
    (did-mount [this]
      (do
        (js/megafolioInit)
        (.megafilter js/api "cat-everyday")))

    om/IRenderState
    (render-state [this state]
      (let [local (get data (:key state))]
        (dom/div nil

               #_(apply dom/ul #js {:style #js {:top "100px" :width "140px" :right "0px" :position "fixed"
                                              :listStyle "none" :borderBottom "1px solid white" :padding "0px" }}
                      (om/build-all common/simple-li hipster-data))

                (println "loca: " local)

               (om/build common/paragraph-partial local {:state {:key :text :color "white"}})

               (dom/img #js {:id "group_photo" :src (:architect (:leaderboard local))
                             :style #js {:marginBottom "20px" :width "100%"}})

                 (om/build
                   common/input-partial
                   [:leaderboard "/img/leaderboards/group_photo_everyday.jpg"] {:state {:owner owner}})

               (dom/br nil)

               (apply dom/div #js {:className "megafolio-container"}
                      (om/build-all common/gallery-partial (megafolio-preprocessor (:team-members local))
                                    {:state {:link :memberid}})))))))


