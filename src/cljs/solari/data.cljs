(ns solari.data
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <! >! take! close!]]
            [solari.utils :as u]
            [ajax.core :refer [GET POST PUT]]))

(def projects-atom (atom {}))
(def individual-projects-atom (atom {}))
(def home-page-atom (atom {}))
(def faqs-atom (atom {}))
(def process-atom (atom {}))
(def the-team-atom (atom {}))

(def nav-map (atom {:root     [{:id      "nav-left-01" :label "for you" :selected false
                                :submenu {:id    "nav-left-01-sub"
                                          :items [{:id "nav-right-item-all-projects" :name "all-projects "
                                                   :selected false :route "/all-projects"}
                                                  {:id "nav-right-item-residential" :name "residential"
                                                   :selected false :route "/residential"
                                                   :category "cat-residential"}
                                                  {:id "nav-right-item-muti" :name "multi-unit"
                                                   :selected false :route "/multi-residential"
                                                   :category "cat-multi-unit-residential"}
                                                  {:id "nav-right-item-commerical" :name "commerical"
                                                   :selected false :route "/commercial"
                                                   :category "cat-commercial"}
                                                  {:id "nav-right-item-our" :name "our process"
                                                   :selected false :route "/our-process"}
                                                  {:id "nav-right-item-faqs" :name "faqs"
                                                   :selected false :route "/faqs"}
                                                  {:id "nav-righ-item-yourt" :name "your team"
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

(defn watcher [atom link]
  (add-watch atom nil
               (fn [key atom old-state new-state]
                 (PUT link
                      {:params {:projects (prn-str @atom) }
                       :format :raw
                       :error-handler u/ajax-error-handler}))))

(defn data-link [link atom query]
  (GET link
         {:params {:query query}
          :format :edn
          :handler #(do (reset! atom %) (watcher atom link))
          :error-handler u/ajax-error-handler}))

(defn data-init []
  (do
    (data-link "/projects/" projects-atom "full-info")
    (data-link "/projects/" individual-projects-atom "projects-only")
    (data-link "/home/" home-page-atom "")
    (data-link "/process/" process-atom "")
    (data-link "/faqs/" faqs-atom "")
    (data-link "/team/" the-team-atom "")
    )
  )

@the-team-atom
(data-init)


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
                            {:thumbnail (:hipster (:profilepics x))}
                            {:category "cat-all cat-everyday"}
                            {:id (:memberid x)}
                            {:title (:name x)}
                            {:memberid (:memberid x)}))
                  team-members)))))

(vector? (into [] (megafolio-preprocessor (:team-members @the-team-atom))))

(count (megafolio-preprocessor (:team-members @the-team-atom)))

