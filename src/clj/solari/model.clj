(ns solari.model
  (:require [liberator.core :refer [defresource resource request-method-in]]
            [environ.core :refer [env]]
            [clojure.core.async :refer [>! <! >!! <!! go chan buffer close! thread alts! alts!! timeout]]
            [clojure.java.jdbc :as sql]
            [clojure.java.jdbc.deprecated :as sql-old]))

(def db
  (env
    :heroku-postgresql-rose-url
    "postgresql://root:1fishy4me@localhost:5432/u1st_games"))


