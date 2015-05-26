(ns solari.views.theteam
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn gallery-partial [data owner]
  (reify

    om/IInitState
    (init-state [this]
      (println "hi data" data))

    om/IRender
    (render [this]

      (dom/div #js {:className (str "mega-entry " (:category data))  :id (:id data) :data-src (str "/img/teampics/" (:thumbnail data))
                    :data-bgposition "50% 50%" :data-width "320" :data-height "240"}
               (dom/div #js {:className "mega-hover"}
                        (dom/div #js {:className "mega-hovertitle"} (:title data)
                                 (dom/div #js {:className "mega-hoversubtitle"} "subtitle"))

                        (dom/a #js {:href (str "/#/" (:memberid data))}
                               (dom/div #js {:className "mega-hoverlink"})))))))


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

    om/IInitState
    (init-state [this]
      #_{:fiter (chan)}
      #_(println "the-team: " data))

    om/IDidMount
    (did-mount [this]
      (do
        (js/megafolioInit)
        (.megafilter js/api "cat-everyday")))

    om/IRender
    (render [this]

      (dom/div #js {:className "container"}

               (dom/span nil
                         (dom/button #js {:onClick #(.megafilter js/api "cat-hipster")} "Architect")
                         (dom/button #js {:onClick #(.megafilter js/api "cat-everyday")} "Everyday"))

               (apply dom/div #js {:className "megafolio-container"}
                      (om/build-all gallery-partial (megafolio-preprocessor (:team-members data))))))))


(defn the-team-init [the-team-atom filter]
  (do (om/root team-members-page the-team-atom
               {:target (. js/document (getElementById "main-content-container"))})
      (ef/at ".context" (ef/content (:title @the-team-atom)))))

