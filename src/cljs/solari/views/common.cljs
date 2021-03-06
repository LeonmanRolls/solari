(ns solari.views.common
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [clojure.set :as set]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(def admin-mode (atom false))

(def box-style #js {:style #js {:color "white" :marginTop "10px" :padding "20px" :border "2px solid #c0392b"
                                :textTransform "uppercase"}})

(defn update-value
  ([data owner target]
   (let [new-contact (-> (om/get-node owner target)
                         .-value)]
     (om/transact! data (fn [x] [new-contact]))))

  ([data owner target key]
   (let [new-contact (-> (om/get-node owner target)
                         .-value)]
     (om/transact! data key (fn [x] new-contact)))))

(def colors {:transparent-grey "rgba(29,29,27,0.4)"})

(def categories ["cat-residential" "cat-multi-residential" "cat-commercial"])

(def project-schema {:id "non-user" :year "text-input" :projectid "text-input" :link "non-user" :category "user-limited"
                     :title "text-input" :thumbnail "user-upload" :gallery-images "editable-list-upload"
                     :accordion "non-user" :bold "text-input" :paragraph "text-area" :placeholder "text-input"
                     :content "text-area" :step1 "text-input" :step2 "text-input" :step3 "text-input"
                     :leaderboard "user-upload"})

;Takes a vector of key and value
(defmulti input-partial (fn [data] (if (< 100 (count (first data))) "text-area" "text-input")))

(defn map->vector [data]
  (map (fn [x] (into [] x)) data))

(defmulti admin-li (fn [data] (type data)))

(defmethod admin-li (type "") [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/li #js {:style #js {:color "white"} :onClick (:callback state)} data)
               (dom/button nil (:button-label state))))))

(defmethod admin-li (type {}) [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div #js {:className "cbp-mc-form"}
               (dom/div #js {:classname "cbp-mc-column"}
               (dom/li #js {:style #js {:color "white"} :onClick (:callback state)} (:title data))
               (dom/li #js {:style #js {:color "white"} :onClick (:callback state)} (:content data))
               (dom/button nil (:button-label state)))
               ))))

(defn short-simple-input-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/label #js {:for (val data)} (name (key data)))
               (dom/input #js {:placeholder (val data) :type "text" :ref (val data)})))))

(defn short-input-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil (println "short-input: " (count (first data)) ))
      (let [raw-val (first data)]
        (dom/div #js {:className "cbp-mc-form"}
                 (dom/div #js {:className "cbp-mc-column"}
                 (dom/input #js {:placeholder raw-val :type "text" :ref raw-val})
                 (dom/button #js {:onClick #(update-value data owner raw-val)
                                  :className "cbp-mc-submit"} "Update Site")))))))

(defn long-input-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil (println "short-input: " (count (first data)) ))
      (let [raw-val (first data)]
        (dom/div #js {:className "cbp-mc-form"}
                 (dom/div #js {:className "cbp-mc-column"}
                          (dom/textarea #js {:placeholder raw-val :type "text" :ref raw-val})
                          (dom/button #js {:onClick #(update-value data owner raw-val)
                                           :className "cbp-mc-submit"} "Update Site")))))))

(defn radio-input-quark [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/input #js {:type "radio" :name data :value data} data))))

(defn radio-input-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (apply dom/div nil
             (om/build-all radio-input-quark categories)))))

(defn user-upload-partial [data owner]
  (let [almost-unique (str  (rand-int 1000000))]
    (reify
      om/IDidMount
      (did-mount [this]
        (.dropzone
          (js/$ (str "#" almost-unique) ) #js {:url "/imgupload/"
                                               :maxFilesize 2
                                               :init (fn [] (this-as this
                                                                     (.on this "complete"
                                                                          #(-> js/window (.-location) (.reload))
                                                                          #_(om/refresh! (:owner (om/get-state owner))))))}))

      om/IRenderState
      (render-state [this state]
        (dom/div #js {:id almost-unique :style #js {:margin-top "20px" :height "200" :color "white"
                                                    :background "rgba(29,29,27,0.4)"}}
                 "Drop files here or click to upload")))))

(defn b-b-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (println "b-b-partial: " (first data))
               (dom/b nil (first data))
               (if (:admin state) (om/build input-partial data))))))

(defn p-p-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (println "p-p-partial: " (first data))

               (if (< 0 (.indexOf (first data) "@"))
                 (dom/a #js {:style #js {:color "white"}
                             :href (str "mailto:" (first data))} (dom/p nil (first data)))
                 (dom/p nil (first data)))))))

(defn accordion-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (dom/dt nil
                       (dom/a #js {:href "#accordion1" :aria-expanded "false" :aria-controls "accordion1"
                                   :className "accordion-title accordionTitle js-accordionTrigger"}
                              (first (:title data))))
               (dom/dd #js {:className "accordion-content accordionItem is-collapsed" :id "accordion1"
                            :aria-hidden "true"}
                       (if (:admin state) (om/build input-partial (:title data)))
                       (apply dom/div nil
                              (om/build-all p-p-partial (:content data) {:state state})))))))

(defn p-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/p #js {:style #js {:color (:color state)}}

             (dom/b nil (first (:bold data)))
             (first (:paragraph data))

             (if (:admin state) (om/build input-partial (:bold data)))
             (if (:admin state) (om/build input-partial (:paragraph data)))))))

(defn paragraph-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (let [local (get data (:key state))]
        (dom/div nil
                 (om/build p-partial (if (:text local) (:text local)  local)
                           {:state {:admin (:admin state) :color (:color state)}}))))))


(defn for-architects-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div #js {:style #js {:color "white"}}
                            (dom/b nil "There are architects and then there’s Solari Architects.")
                            (dom/p nil "We’re the “un-architecty” architects (the ones that say architecty). We’ve found (well, created) this little sweet spot in the industry. Basically we have a huge passion for architecture but not in a consuming ‘it’s my whole life’ way. It’s balanced and continually inspired and fuelled by all the non-architect things we welcome into our lives. We enjoy exploring in our weekends, spending time with our families and hanging out with friends and are always up to try new experiences and give things a go. We’ll make an effort to be aware of trends but not make them gospel."
                                   (dom/b nil " For example we completely missed the memo that thick rimmed glasses and showing a questionable amount of sockless ankle was the latest uniform…but we’re fine with that.")))
      )))

(defn uppercase-paragraph-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (let [local (get data (:key state))]
        (dom/div nil
                 (dom/b #js {:style #js {:textTransform "uppercase"}} (first (:bold data)))
                 (apply dom/div nil
                        (om/build-all p-p-partial
                                      (:paragraph data) {:state {:admin (:admin state) :color (:color state)}})))))))


;link, category, id, thumbnail, title
(defn gallery-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (if (:text data)
        (dom/div #js {:href (str (:prelink state) (first ((:link state) data)) )
                      :className (str "mega-entry cat-all text-gallery " (first (:category data)))  :id (first (:id data))
                      :data-bgposition "50% 50%" :data-width "320" :data-height "320" :textDecoration "none"}

                 (dom/div nil
                          (dom/p #js {:style #js {:color "white" :fontWeight "700"}} (first (:title data)))
                          (dom/p #js {:style #js {:color "white" :overflow "hidden"}} (first (:subtitle data))))
                 (dom/div #js {:data-toggle "modal" :data-target (str "#" (first ((:link state) data)))
                               :style #js {:position "absolute" :bottom "0px" :left "0px" :width "100%"
                                           :backgroundColor "#1d1d1b" :textAlign "center" :color "white"
                                           :textTransform "uppercase" :borderTop "2px solid #c0392b"}} "...See All"))

        (dom/a #js {:href (str (:prelink state) (first ((:link state) data)) )
                    :className (str "mega-entry cat-all " (first (:category data)))  :id (first (:id data))
                    :data-src (first (:thumbnail data))  :data-bgposition "50% 50%" :data-width "320" :data-height "240"}
               (dom/div #js {:className "mega-hover"}
                        (dom/div #js {:className "mega-hovertitle" :style #js {:left 0 :width "100%" :top "40%"}}
                                 (first (:title data))
                                 (dom/div #js {:className "mega-hoversubtitle"} (first ((:subtitle state) data))))))))))


(defn instagram-gallery-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]

      (dom/a #js {:href (:link data) :target "_blank"
                    :className (str "mega-entry cat-all ")  :id "lsdjflsdjf" #_(first (:id data))
                    :data-src (:url (:standard_resolution (:images data))) :data-bgposition "50% 50%" :data-width "320" :data-height "240"}
               (dom/div #js {:className "mega-hover"}
                        (dom/div #js {:className "mega-hovertitle" :style #js {:left 0 :width "100%" :top "20%"}}
                                 (dom/i #js {:className "fa fa-instagram fa-2x"})
                                 (dom/div #js {:className "mega-hoversubtitle"} (:text (:caption data)) )))))))


(defn twitter-gallery-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]

      (if (:media (:entities data))

       (dom/a #js {:href "/#/from-us"
                    :className (str "mega-entry cat-all ")  :id (str (rand 10000000))
                    :data-src (:media_url (first (:media (:entities data)))) :data-bgposition "50% 50%"
                   :data-width "320" :data-height "240"}
               (dom/div #js {:className "mega-hover"}
                        (dom/div #js {:className "mega-hovertitle" :style #js {:left 0 :width "100%" :top "20%"}}
                                 (dom/i #js {:className "fa fa-twitter fa-2x"})
                                 (dom/div #js {:className "mega-hoversubtitle"} (:text data)))))))))


;href image iort timestamp subtitle
(defn social-gallery-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]

      (dom/a #js {:href (:href data) :target "_blank" :className (str "mega-entry cat-all ")  :id (str (rand 10000000))
                    :data-src (:image data) :data-bgposition "50% 50%" :data-width "320" :data-height "240"}

               (dom/div #js {:className "mega-hover"}
                        (dom/div #js {:className "mega-hovertitle" :style #js {:left 0 :width "100%" :top "20%"}}
                                 (dom/i #js {:className (str "fa "
                                                             (if (= "t" (:iort data)) "fa-twitter" "fa-instagram")
                                                             " fa-2x")})
                                 (dom/div #js {:className "mega-hoversubtitle"} (:subtitle data))))))))



(defn simple-li [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/li #js {:style #js {:cursor "pointer" :height "50px" :width "140px" :letterSpacing "1px" :color "white"
                               :textTransform "uppercase" :fontSize "80%" :position "relative" :textAlign "center"
                                :display "block" :textDecoration "none"
                               :padding "16px" :outline "none" :marginLeft "-2px" :marginRight "-1px"
                               :borderTop "1px solid white" :borderLeft "1px solid white"}
                   :onClick (:callback data)
                   :id (:id data)}
              (:label data)))))

(defn editable-list-upload-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (apply dom/ul nil
                      (om/build-all admin-li (last data)))
               (om/build user-upload-partial data)))))

(defn editable-list-text-partial [data owner]
  (reify
    om/IRenderState
    (render-state [this state]
      (dom/div nil))))


(defmethod input-partial "text-input"
  [data owner] (short-input-partial data owner))

(defmethod input-partial "text-area"
  [data owner] (long-input-partial data owner))

(defmethod input-partial "user-limited"
  [data owner] (radio-input-partial data owner))

(defmethod input-partial "editable-list-upload"
  [data owner] (editable-list-upload-partial data owner))

(defmethod input-partial "editable-list-text"
  [data owner] (editable-list-text-partial data owner))

(defmethod input-partial "user-upload"
  [data owner] (user-upload-partial data owner))

(defmethod input-partial "colour-input"
  [data owner] (user-upload-partial data owner))


