(ns solari.data
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <! >! take! close!]]
            [solari.utils :as u]
            [cemerick.url :refer (url url-encode)]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [ajax.core :refer [GET POST PUT]]
            [secretary.core :as sec])
  (:import goog.History))

(def ajax-chan (chan))

(def all-data-atom (atom {}))
(set-validator! all-data-atom #((complement empty?) %))
(add-watch all-data-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(defn all-projectids [] (map (fn [x] (first (:projectid x))) (:all-projects @all-data-atom)))

(defn all-memberids []  (map (fn [x] (first (:memberid x))) (:team-members (:the-team-data @all-data-atom))))

;Wait for our ajax calls
(go
  (loop [ajax-count 0]
    (<! ajax-chan)
    (if (= ajax-count 0)
      (aset
        js/document
        "onreadystatechange"
        (sec/dispatch! (:anchor (url (-> js/window .-location .-href))))))
    (recur (inc ajax-count))))

(def nav-map (atom {:root     [{:id      "nav-left-01" :label "for you" :selected false :route "/for-you"
                                :submenu {:id    "nav-left-01-sub"
                                          :filter "cat-all"
                                          :items [{:id "nav-right-item-all-projects" :name "all-projects "
                                                   :selected false :route "/all-projects"
                                                   :category "cat-all"}
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
                                                   :selected false :route "/your-team"}
                                                  {:id "nav-righ-item-contact" :name "contact"
                                                   :selected false :route "/contact"}]}}

                               {:id      "nav-left-02" :label "for architects" :selected false :route "/for-architects"
                                :submenu {:id    "nav-left-02-sub"
                                          :items [{:id "nav-right-item-yourc" :name "your career"
                                                   :selected false :route "/your-career"}
                                                  {:id "nav-right-item-meet" :name "meet the team"
                                                   :selected false :route "/meet-the-team"}
                                                  {:id "nav-right-item-jobs" :name "jobs"
                                                   :selected false :route "/jobs"}]}}

                               {:id      "nav-left-03" :label "from us" :selected false :route "/from-us"
                                :submenu {:id    "nav-left-03-sub"
                                          :items [{:id "nav-right-item-contact" :name "#SolariSocial" :selected false
                                                   :route "/from-us"}]}}

                               {:id      "nav-left-04" :label "contact" :selected false :route "/contact"
                                :submenu {:id    "nav-left-04-sub"
                                          :items [{:id "nav-right-item-contact" :name "#SolariSocial" :selected false
                                                   :route "/from-us"}]}}
                               ]

                    :selected false}))

(defn watcher [atom link]
  (add-watch atom nil
             (fn [key atom old-state new-state]
               (PUT link
                    {:params {:all-data (prn-str @atom)}
                     :format :raw
                     :error-handler u/ajax-error-handler}))))

(defn data-link [link atom]
  (GET link
       {:format :edn
        :handler #(do (reset! atom %) (watcher atom link))
        :error-handler u/ajax-error-handler}))

(defn data-init []
  (do
    (data-link "/alldata/" all-data-atom)))


