(ns solari.views.theteam
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [cljs.core.async :refer [put! chan <! >! take! close!]]
            [enfocus.effects :as eff]
            [solari.views.common :as common]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em] [cljs.core.async.macros :refer [go]] ))

(enable-console-print!)

(defn timeout [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))

(defn everyday [] (do (.megafilter js/api "cat-everyday")
                      (ef/at "#everyday-li" (ef/set-attr :color "red"))
                      (ef/at "#hipster-li" (ef/set-attr :color "none"))
                      (ef/at "#group_photo" (ef/set-attr :src "/img/leaderboards/group_photo_everyday.jpg"))))

(defn hipster [] (do (.megafilter js/api "cat-hipster")
                                   (ef/at "#everyday-li" (ef/set-attr :color "none"))
                                   (ef/at "#hipster-li" (ef/set-attr :color "red"))
                                   (ef/at "#group_photo"
                                          (ef/set-attr :src "/img/leaderboards/group_photo_hipster.jpg"))))

(def hipster-data [{:href "" :label "everyday us" :id "everyday-li"
                    :callback #(do (.megafilter js/api "cat-everyday")
                                   (ef/at "#everyday-li" (ef/set-attr :color "red"))
                                   (ef/at "#hipster-li" (ef/set-attr :color "none"))
                                   (ef/at "#group_photo"
                                          (ef/set-attr :src "/img/leaderboards/group_photo_everyday.jpg")))}

                   {:href "" :label "\"architect\" us" :id "hipster-li"
                    :callback #(do (.megafilter js/api "cat-hipster")
                                   (ef/at "#everyday-li" (ef/set-attr :color "none"))
                                   (ef/at "#hipster-li" (ef/set-attr :color "red"))
                                   (ef/at "#group_photo"
                                          (ef/set-attr :src "/img/leaderboards/group_photo_hipster.jpg")))}])


(defn megafolio-preprocessor [team-members interop]
  (into []
        (concat
          (flatten (into []
                (map
                  (fn [x] [(conj
                            {}
                            {:thumbnail (:hipster (:profilepics x))}
                            {:category ["cat-all cat-hipster"]}
                            {:id (:memberid x)}
                            {:subtitle (:Role x)}
                            {:title (:name x)}
                            {:memberid (:memberid x)}
                            {:text false}
                            )
                           (if interop
                           (conj
                            {}
                            {:thumbnail (:everyday (:profilepics x))}
                            {:category ["cat-all cat-hipster"]}
                            {:id (:memberid x)}
                            {:title (:texttitle x)}
                            {:subtitle (:textpara x)}
                            {:memberid (:memberid x)}
                            {:text true}
                            )
                             )

                           ] )
                  team-members)))
          (flatten (into []
                (map
                  (fn [x] [(conj
                            {}
                            {:thumbnail (:everyday (:profilepics x))}
                            {:category ["cat-all cat-everyday"]}
                            {:id (:memberid x)}
                            {:title (:name x)}
                            {:subtitle (:Role x)}
                            {:memberid (:memberid x)}
                            {:text false})
                           (if interop
                           (conj
                            {}
                            {:thumbnail (:everyday (:profilepics x))}
                            {:category ["cat-all cat-everyday"]}
                            {:id (:memberid x)}
                            {:title (:texttitle x)}
                            {:subtitle (:textpara x)}
                            {:memberid (:memberid x)}
                            {:text true}
                            )
                             )

                           ])
                  team-members))) )))


(defn team-members-page [data owner]
  (reify

    om/IDidMount
    (did-mount [this]
      (do
        (js/megafolioInit)
        (.megafilter js/api "cat-everyday")
        (ef/at "#everyday-li" (ef/set-attr :color "red"))
        (ef/at "#hipster-li" (ef/set-attr :color "none"))
        (if (js/mobilecheck) (hipster))))

    om/IRenderState
    (render-state [this state]
      (let [local (get data (:key state))]
        (dom/div nil

               (apply dom/ul #js {:id "right-right-nav"
                                  :style #js {:top "100px" :width "140px" :right "0px" :position "fixed"
                                              :listStyle "none" :borderBottom "1px solid white" :padding "0px" }}
                      (om/build-all common/simple-li hipster-data))

                 (if (:interop state)
                   (dom/div #js {:style #js {:color "white"}}
                            (dom/b nil "The people you work with should make you want to come to work each day. We support, share, teach and encourage. We all back each other which is something Solari thinks is really important - after all a business is only as good as it’s people.")
                            (dom/p nil "Since starting this company in 2011 James Solari has grown the business from being a sole practitioner to be a growing team of seven talented and driven designers, thinkers and most of all, doers. We know every company bangs on about how wonderful they are and tell you that they all like working with each other but, we really do. "
                                   (dom/b nil "And Here’s why:")))


                 (om/build common/paragraph-partial local {:state {:key :text
                                                                   :admin (:admin state)
                                                                   :color (:color state)}}))


               (dom/img #js {:id "group_photo" :src (first (:architect (:leaderboard local)))
                             :style #js {:marginBottom "20px" :width "100%"}})

                 (if (:admin state) (om/build
                                      common/input-partial
                                      [:leaderboard "/img/leaderboards/group_photo_everyday.jpg"] {:state {:owner owner}}))

               (dom/br nil)

               (apply dom/div #js {:className "megafolio-container"}
                      (om/build-all common/gallery-partial (megafolio-preprocessor (:team-members local ) (:interop state))
                                    {:state {:link :memberid :prelink "/#/members/individual/" :subtitle :subtitle}})))))))


