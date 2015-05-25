(ns solari.model
  (:require [liberator.core :refer [defresource resource request-method-in]]
            [environ.core :refer [env]]
            [clojure.core.async :refer [>! <! >!! <!! go chan buffer close! thread alts! alts!!]]
            [clojure.string :as string]
            [solari.projects]
            [clojure.java.jdbc :as sql]
            [clojure.java.jdbc.deprecated :as sql-old]))

(def db
  (env
    :heroku-postgresql-rose-url
    "postgresql://root:1fishy4me@localhost:5432/solari"))

(def home-page-atom
  (atom {:main-title "Come on in..."
         :sub-title "We're Solari architects."
         :paragraph-one "Our studio is based in Wellington and our thoughts, projects and experiences span New Zealand, Australia and beyond. When working with you we focus on speaking a common language  - you’ll find no architectural lingo here. "
         :paragraph-two "We take your vision from pictures, words, half-formed or full-formed ideas and “ya knows” and translate them into architecture representative of your values, goals and personality. Our strengths lie in commercial, residential and multi-residential projects where we work on the small and the large. We’re flexible, agile and updateable but we do keep one thing consistent across the board; every project is led by YOUR vision and crafted by our tools and expertise."}))


(def projects-atom
  (atom {:projects [{:text "Homes are personal projects and we love that. When we take on a residential project we take on the thoughts, feelings, personality and unique circumstances of the client. We work closely with you to ensure that your home is exactly that – yours. You’re with us every step of the way, this not only makes absolute sense but undoubtedly delivers the best results. We share the challenges and successes with you and make you the expert of your own project by going at a pace that promotes attention to detail and clarity of thought from start to finish."
                     :category "Residential"
                     :projects [project-01 project-02 project-03 project-04]}

                    {:text "Solari’s success in the multi-unit residential development sector across New Zealand and Australia comes down to what we like to call ‘The Solari Way’. In a nutshell it’s an approach that balances the values and objectives of the developer, designer and tenants. Everyone involved with the project stands to benefit from such an insightful approach. Blending our understanding of commercial realities, how a target market perceives quality living spaces and how to effectively manage the design process from sketches to site, ensures our developments maintain their purpose and quality."
                     :category "Multi-unit Residential"
                     :projects [project-05 project-06 project-07 project-08 project-09]}

                    {:text "At Solari we don’t define commercial buildings by their sheer scale but by their purpose.  We treat them as strategic assets, marketing tools and enablers of achieving business goals. We take full advantage of the power commercial and workplace design has to impact three key objectives shared by all businesses (including our own): efficiency, effectiveness and expression. "
                     :category "Commerical"
                     :projects [project-10 project-11]}]}))


(defresource projects
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @projects-atom)
             :put! (fn [ctx]
                     (reset! projects-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])

(defresource home
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @home-page-atom)
             :put! (fn [ctx]
                     (reset! home-page-atom (:projects (:params (:request ctx)))))
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

