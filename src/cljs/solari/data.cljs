(ns solari.data
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <! >! take! close!]]
            [solari.utils :as u]
            [ajax.core :refer [GET POST PUT]]))

(def projects-atom (atom {}))
(def home-page-atom (atom {}))

(def nav-map (atom {:root     [{:id      "nav-left-01" :label "for you" :selected false
                                :submenu {:id    "nav-left-01-sub"
                                          :items [{:id "nav-right-item-all-projects" :name "all-projects "
                                                   :selected false :route "/all-projects"}
                                                  {:id "nav-right-item-residential" :name "residential"
                                                   :selected false :route "/residential"}
                                                  {:id "nav-right-item-muti" :name "multi-residential"
                                                   :selected false :route "/multi-residential"}
                                                  {:id "nav-right-item-commerical" :name "commerical"
                                                   :selected false :route "/commercial"}
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

(defn data-link [link atom]
  (do
    (GET link
         {:format :edn
          :handler #(reset! atom %)
          :error-handler u/ajax-error-handler})

    (add-watch atom nil
               (fn [key atom old-state new-state]
                 (PUT link
                      {:params {:projects (prn-str @atom) }
                       :format :raw
                       :error-handler u/ajax-error-handler})))))

(defn data-init []
  (do
    (data-link "/projects/" projects-atom)
    (data-link "/home/" home-page-atom)))


