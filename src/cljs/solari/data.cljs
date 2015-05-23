(ns solari.data)

(def nav-map (atom {:root     [{:id      "nav-left-01" :label "for you" :selected false
                                :submenu {:id    "nav-left-01-sub"
                                          :items [{:id       "nav-right-item-residential" :name "residential"
                                                   :selected false :route "/residential"}
                                                  {:id       "nav-right-item-muti" :name "multi-residential"
                                                   :selected false :route "/multi-residential"}
                                                  {:id       "nav-right-item-commerical" :name "commerical"
                                                   :selected false :route "/commercial"}
                                                  {:id       "nav-right-item-our" :name "our process"
                                                   :selected false :route "/our-process"}
                                                  {:id       "nav-right-item-faqs" :name "faqs"
                                                   :selected false :route "/faqs"}
                                                  {:id       "nav-righ-item-yourt" :name "your team"
                                                   :selected false :route "/your-team"}]}}

                               {:id      "nav-left-02" :label "for architects" :selected false
                                :submenu {:id    "nav-left-02-sub"
                                          :items [{:id "nav-right-item-yourc" :name "your career"
                                                   :selected false :route "/your-career"}
                                                  {:id "nav-right-item-meet" :name "meet the team"
                                                   :selected false :route "/your-career"}
                                                  {:id "nav-right-item-jobs" :name "jobs"
                                                   :selected false :route "/jobs"}]}}

                               {:id      "nav-left-03" :label "from us" :selected false
                                :submenu {:id    "nav-left-03-sub"
                                          :items [{:id "nav-right-item-contact" :name "contact" :selected false}]}}]

                    :selected false}))


(def res-atom (atom {:text "Homes are personal projects and we love that. When we take on a residential project we take on the thoughts, feelings, personality and unique circumstances of the client. We work closely with you to ensure that your home is exactly that – yours. You’re with us every step of the way, this not only makes absolute sense but undoubtedly delivers the best results. We share the challenges and successes with you and make you the expert of your own project by going at a pace that promotes attention to detail and clarity of thought from start to finish."
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
                                 :thumbnail "/img/lyall.jpg"}]}))

(def wadestown-res-atom (atom {:images []
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

