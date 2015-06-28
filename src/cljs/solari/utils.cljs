(ns solari.utils
  (:require [goog.net.cookies :as cookies]
            [cljs.reader :as reader]
            [clojure.string :as string]
            [clojure.walk :as walk]
            [secretary.core :as sec]))

(def default-games
  (atom #{{:gamename "Farmville 2" :gameid 321574327904696}
          {:gamename "Criminal Case" :gameid 148494581941991}}))

;Cookie helpers
(defn c-get
  "Returns the cookie after parsing it with cljs.reader/read-string."
  [k]
  (reader/read-string (or (.get goog.net.cookies (name k)) "nil")))

(defn c-set!
  "Stores the cookie value using pr-str."
  [k v]
  (.set goog.net.cookies (name k) (pr-str v)))

(defn remove! "" [k] (.remove goog.net.cookies (name k)))

(defn timeAgo [seconds]
      (cond
        (< seconds 60) (str seconds " seconds ago")
        (< seconds 3600) (str (quot seconds 60) " minutes ago")
        (< seconds 86400) (str (quot seconds 3600) " hours ago")
        (> seconds 86400) (str (quot (quot seconds 3600) 24) " Days ago")
        :else "A long time ago"))

(defn dispatch-route [route] (sec/dispatch! route))

(defn ajax-error-handler [{:keys [status status-text response original-text is-parse-error parse-error]}]
  (.log js/console
        (str "something bad happened: " status " " status-text " " response " " original-text " "
             is-parse-error " " parse-error)))

;string helpers
(defn idify [gamename]
  (string/replace gamename " " "-"))

(defn match-string-ci [gamename subject]
  ((not empty?) (re-find (js/RegExp gamename "i") subject)))

(defn gamename->gameinfo [gamename all-games]
  (walk/keywordize-keys
    (filter
      #(= (re-find (js/RegExp gamename "i") (% "gamename"))
          (% "gamename"))
      all-games)))

(defn on-doc-ready [doc-ready-handler]
  (aset  js/document "onreadystatechange" doc-ready-handler ))


