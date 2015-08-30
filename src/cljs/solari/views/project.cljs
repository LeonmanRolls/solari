(ns solari.views.project
  (:require [secretary.core :as sec :refer-macros [defroute]]
            [enfocus.core :as ef]
            [enfocus.events :as ev]
            [enfocus.effects :as eff]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [solari.views.common :as common]
            [solari.data :as data])
  (:require-macros [enfocus.macros :as em]))

(enable-console-print!)

(defn img-page [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/img #js {:className "rsImg" :src (str "/img/" data ) :data-rsTmb (str "/img/" data )}))))

(defn img-individual-member [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:className "row"}
               (dom/img #js {:style #js {:marginTop "10px"}
                             :className "col-xs-12 col-sm-6" :src (str (first (:everyday (:profilepics data))))})
               (dom/img #js {:style #js {:marginTop "10px"}
                             :className "col-xs-12 col-sm-6" :src (str (first (:hipster (:profilepics data))))})))))

(defmulti image-display (fn [dispatch _] dispatch))

(defmethod image-display :the-team-data
  [dispatch project] (om/build img-individual-member (first project)))

(defmethod image-display :all-projects
  [dispatch project] (apply dom/div #js {:className "royalSlider rsDefault"}
                       (doall (om/build-all img-page (:gallery-images (first project))))))

(defn process-member-data [project]
  (into [] (map (fn [x] {:bold [(name (first x))] :paragraph [(last x)] })
                (common/map->vector (select-keys (first project)  [:name :Role :advice :contact])))))

(defmulti accordion-display (fn [dispatch _] dispatch))

(defmethod accordion-display :the-team-data
  [dispatch project state]  (dom/div #js {:className ""}
                        (apply dom/div #js {:style #js {:border "2px solid #c0392b" :padding "20px" :marginTop "20px" :color "white"}}
                               (om/build-all common/uppercase-paragraph-partial (process-member-data project) {:state state}))))

(defmethod accordion-display :all-projects
  [dispatch project state] (dom/div #js {:className "accordion"}
                        (apply dom/dl nil
                               (om/build-all common/accordion-partial (:accordion (first project)) {:state state}))))

(defn project-page [data owner]
  (reify

    om/IDidMount
    (did-mount [this]
      (let [local (first (get-in data (:filter-vector (om/get-state owner))))]
        (do
         (.animate (js/$ "html,body") #js {:scrollTop 0} 0)

          (.royalSlider (js/$ ".royalSlider") #js {:keyboardNavEnabled true :controlNavigation "none"
                                                   :autoScaleSlider true :autoScaleSliderWidth 900 :autoScaleSliderHeight 400
                                                   :fullscreen #js {:enabled true :nativeFS true}
                                                   :visibleNearby #js {:enabled false :centerArea 0.5 :center true
                                                                       :breakpoint 650 :breakpointCenterArea 0.64
                                                                       :navigateByCenterClick true}})

          (js/accordion)

          (js/Share. "#share-button"
                     #js {:url (clojure.string/replace (.-href (.-location js/window)) #"#" "%23")
                          :networks #js {:facebook #js{:title "Test title"}
                                         :email #js{:description (str "Hey take a look at this: "
                                                                      (.-href (.-location js/window)))}}}))))

    om/IRenderState
    (render-state [this state]
     (let [local (get-in data (:filter-vector state))
           project (filter (fn [x] (= (:filter state) (first ((:filterkey state) x)))) local)]

       (dom/div #js {:id "project-container" :style #js {:color "white"}}

                (image-display (:key state) project)

                (accordion-display (:key state) project state)

                (dom/div #js {:style #js {:padding "20px" }}
                         (if (not= (first (:filter-vector state)) :all-projects) (dom/b nil "How"))

                (apply dom/div nil (om/build-all common/p-p-partial (:how (first project) )))

                         (dom/div #js {:id "share-button"})))))))





