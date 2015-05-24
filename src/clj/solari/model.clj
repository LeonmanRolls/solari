(ns solari.model
  (:require [liberator.core :refer [defresource resource request-method-in]]
            [environ.core :refer [env]]
            [clojure.core.async :refer [>! <! >!! <!! go chan buffer close! thread alts! alts!! timeout]]
            [clojure.string :as string]
            [clojure.java.jdbc :as sql]
            [clojure.java.jdbc.deprecated :as sql-old]))



(def db
  (env
    :heroku-postgresql-rose-url
    "postgresql://root:1fishy4me@localhost:5432/solari"))

(def projects-atom
  (atom {:projects [{:text "Homes are personal projects and we love that. When we take on a residential project we take on the thoughts, feelings, personality and unique circumstances of the client. We work closely with you to ensure that your home is exactly that – yours. You’re with us every step of the way, this not only makes absolute sense but undoubtedly delivers the best results. We share the challenges and successes with you and make you the expert of your own project by going at a pace that promotes attention to detail and clarity of thought from start to finish."
                     :category "Residential"
                     :projects [{:id "project-01"
                                 :projectid "wadestown"
                                 :title "Wadestown Renovation"
                                 :thumbnail "/img/wadestown.jpg"
                                 :gallery-images ["wadestown-00.jpg" "wadestown-01.jpg" "wadestown-02.jpg"
                                                  "wadestown-03.jpg" "wadestown-04.jpg" "wadestown-05.pg"]
                                 :accordion [{:title "Specifics"
                                              :content "Renovated early 1900s character home. 5 berooms. 3 bathrooms."}
                                             {:title "Client goals and objectives"
                                              :content "Convert a tired, early 1900s character home into a welcoming,
                                              modern, family home, whilst preserving and respecting the existing
                                              character of the house."}
                                             {:title "Solari's solution"
                                              :content "We enjoyed this renovation project as we worked closely with the
                                              client to refine this character home for their family. Like the client,
                                              we felt it was important we still represented the original house and
                                              neighbourhod in the final design but reflected the personality, values
                                              and lifestyle of the family. We kept existing timeless character features
                                              and balanced them with open modern living spaces and fresh, welcoming tones."}
                                             {:title "Challenges"
                                              :content "Workign with an old charcter home."}]
                                 }

                                {:id "project-02"
                                 :projectid "lyall"
                                 :title "Lyall bay renovation"
                                 :thumbnail "/img/lyall.jpg"
                                 :gallery-images ["wadestown-00.jpg" "wadestown-01.jpg" "wadestown-02.jpg"
                                                  "wadestown-03.jpg" "wadestown-04.jpg" "wadestown-05.pg"]
                                 :accordion [{:title "Specifics"
                                              :content "Renovated theatre. 5 bedrooms."}
                                             {:title "Client goals and objectives"
                                              :content "Re-think/re-design an old theatre containing a retail store,
                                              cafe and 3 bedroom flat, into a welcoming contemporary home for a growing,
                                              energetic family."}
                                             {:title "Solari's solution"
                                              :content "When working on this project we were inspired not only by the
                                              original building but its location and the energy of the family. Not wanting
                                              to disconnect the building ffrom tis historic roots in Lyall Bay we created
                                              a space taht continued to rech out to its surroundings. We kept a sense of
                                              freesom throughout the home with a very generous double height centrl living
                                              space that introduced a formal lounge area before rolling into a causal
                                              family space effortlessly blended into the back yard."}
                                             {:title "Challenges"
                                              :content "This home was prone to extreme weather and sea conditions. The
                                              sand, salt and wind presented a myriad of constructional challenges.
                                              The facade had to be durable but not compromise or dull the bold style
                                              the client desired."}]

                                 }

                                {:id "project-03"
                                 :projectid "catline"
                                 :title "Catline Lane Subdivision"
                                 :thumbnail "/img/catline_thumb.jpg"}

                                ]}

                    {:text "Homes are personal projects and we love that. When we take on a residential project we take on the thoughts, feelings, personality and unique circumstances of the client. We work closely with you to ensure that your home is exactly that – yours. You’re with us every step of the way, this not only makes absolute sense but undoubtedly delivers the best results. We share the challenges and successes with you and make you the expert of your own project by going at a pace that promotes attention to detail and clarity of thought from start to finish."
                     :category "Multi-Residential"
                     :projects [{:id "project-01"
                                 :projectid "wadestown"
                                 :title "Wadestown Renovation"
                                 :thumbnail "/img/wadestown.jpg"}

                                {:id "project-02"
                                 :projectid "lyall"
                                 :title "Lyall bay renovation"
                                 :thumbnail "/img/lyall.jpg"}

                                {:id "project-03"
                                 :projectid "catline"
                                 :title "Catline Lane Subdivision"
                                 :thumbnail "/img/lyall.jpg"}]}

                    {:text "Homes are personal projects and we love that. When we take on a residential project we take on the thoughts, feelings, personality and unique circumstances of the client. We work closely with you to ensure that your home is exactly that – yours. You’re with us every step of the way, this not only makes absolute sense but undoubtedly delivers the best results. We share the challenges and successes with you and make you the expert of your own project by going at a pace that promotes attention to detail and clarity of thought from start to finish."
                     :category "Commerical"
                     :projects [{:id "project-01"
                                 :projectid "wadestown"
                                 :title "Wadestown Renovation"
                                 :thumbnail "/img/wadestown.jpg"}

                                {:id "project-02"
                                 :projectid "lyall"
                                 :title "Lyall bay renovation"
                                 :thumbnail "/img/lyall.jpg"}

                                {:id "project-03"
                                 :projectid "catline"
                                 :title "Catline Lane Subdivision"
                                 :thumbnail "/img/lyall.jpg"}]}]}))


(def wadestown-res-atom (atom {:images 6
                               :sections [{:title "Specifics"
                                           :content "Renovated early 1900s character home. 5 Bedrooms. 3 Bathrooms."}
                                          {:title "Client goals and objectives"
                                           :content "Convert a tired, early 1900s character home into..."}
                                          {:title "Solari's solution"
                                           :content "We enjoyed this renovation project.."}
                                          {:title "Challenges"
                                           :content "Working with an old character home."}
                                          {:title "Successes"
                                           :content "Maintaining the original character of the house."}]}))

(def multi-atom (atom {:text "Multi res"
                       :projects [{:id "project-01"
                                   :title "Wadestown Renovation"
                                   :thumbnail "/img/wadestown.jpg"}

                                  {:id "project-02"
                                   :title "Lyall bay renovation"
                                   :thumbnail "/img/lyall.jpg"}

                                  {:id "project-03"
                                   :title "Catline Lane Subdivision"
                                   :thumbnail "/img/lyall.jpg"}]}))


(defresource projects
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                         @projects-atom)
             :put! (fn [ctx]
                     (reset! projects-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])

(defresource applications
             :service-available? true
             :allowed-methods [:get]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (sql/query db ["select appid from applications"])
             :available-media-types ["application/json"])

(defresource dataTest
             :service-available? true
             :allowed-methods [:get :post]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok  (fn [context] (println context))
             :available-media-types ["text/plain"])

