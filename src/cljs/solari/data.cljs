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

(def projects-atom (atom {}))
(set-validator! projects-atom #((complement empty?) %))
(add-watch projects-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def individual-projects-atom (atom {}))
(set-validator! individual-projects-atom #((complement empty?) %))
(add-watch individual-projects-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def home-page-atom (atom {}))
(set-validator! home-page-atom #((complement empty?) %))
(add-watch home-page-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def your-career-atom (atom {}))
(set-validator! your-career-atom #((complement empty?) %))
(add-watch your-career-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def residential-atom (atom {}))
(set-validator! residential-atom #((complement empty?) %))
(add-watch residential-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def multi-unit-atom (atom {}))
(set-validator! multi-unit-atom #((complement empty?) %))
(add-watch multi-unit-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def commercial-atom (atom {}))
(set-validator! commercial-atom #((complement empty?) %))
(add-watch commercial-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def for-you-atom (atom {}))
(set-validator! for-you-atom #((complement empty?) %))
(add-watch for-you-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def for-architects-atom (atom {}))
(set-validator! for-architects-atom #((complement empty?) %))
(add-watch for-architects-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def from-us-atom (atom {}))
(set-validator! from-us-atom #((complement empty?) %))
(add-watch from-us-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def faqs-atom (atom {}))
(set-validator! faqs-atom #((complement empty?) %))
(add-watch faqs-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def process-atom (atom {}))
(set-validator! process-atom #((complement empty?) %))
(add-watch process-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

(def the-team-atom (atom {}))
(set-validator! the-team-atom #((complement empty?) %))
(add-watch the-team-atom nil (fn [key atom old-state new-state] (go (>! ajax-chan 1))))

;Wait for our ajax calls
(go
  (loop [ajax-count 0]
    (<! ajax-chan)
    (if (= ajax-count 8)
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
                                                  {:id "nav-righ-item-yourt" :name "contact"
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
                                          :items [{:id "nav-right-item-contact" :name "contact" :selected false
                                                   :route "/contact"}]}}]

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
    (data-link "/career/" your-career-atom "")
    (data-link "/residential/" residential-atom "")
    (data-link "/multi-unit/" multi-unit-atom "")
    (data-link "/commercial/" commercial-atom "")
    (data-link "/process/" process-atom "")
    (data-link "/faqs/" faqs-atom "")
    (data-link "/you/" for-you-atom "")
    (data-link "/architects/" for-architects-atom "")
    (data-link "/us/" from-us-atom "")
    (data-link "/team/" the-team-atom "")))

;(first @individual-projects-atom)
;(data-link "/projects/" individual-projects-atom "projects-only")
;(sort compara)
;(sort [2015 2015 2020 2015 2013 2012 2014])


