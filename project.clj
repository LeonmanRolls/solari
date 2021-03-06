(defproject solari "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}

            :source-paths ["src/clj"]
            :repl-options {:timeout 200000} ;; Defaults to 30000 (30 seconds)

            :test-paths ["spec/clj"]

            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojurescript "0.0-2511" :scope "provided"]
                           [ring "1.3.2"]
                           [ring/ring-defaults "0.1.3"]
                           [com.cemerick/url "0.1.1"]
                           [org.twitter4j/twitter4j-core "2.1.8"]
                           [secretary "1.2.3"]
                           [compojure "1.3.1"]
                           [org.clojure/java.jdbc "0.3.6"]
                           [postgresql "9.1-901-1.jdbc4"]
                           [liberator "0.10.0"]
                           [cljs-ajax "0.3.11"]
                           [enfocus "2.1.1"]
                           [optimus "0.18.0"]
                           [enlive "1.1.5"]
                           [om "0.8.0-rc1"]
                           [twitter-api "0.7.8"]
                           [bk/ring-gzip "0.1.1"]
                           [environ "1.0.0"]
                           [com.andrewmcveigh/cljs-time "0.3.11"]]

            :plugins [[lein-cljsbuild "1.0.3"]
                      [lein-environ "1.0.0"]]

            :min-lein-version "2.5.0"

            :uberjar-name "solari.jar"

            :cljsbuild {:builds {:app {:source-paths ["src/cljs"]
                                       :compiler {:output-to     "resources/public/js/app.js"
                                                  :output-dir    "resources/public/js/out"
                                                  :source-map    "resources/public/js/out.js.map"
                                                  :preamble      ["react/react.min.js"]
                                                  :optimizations :none
                                                  :pretty-print  true}}}}

            :profiles {:dev {:source-paths ["env/dev/clj"]
                             :test-paths ["test/clj"]

                             :dependencies [[figwheel "0.2.1-SNAPSHOT"]
                                            [figwheel-sidecar "0.2.1-SNAPSHOT"]
                                            [com.cemerick/piggieback "0.1.3"]
                                            [weasel "0.4.2"]]

                             :repl-options {:init-ns solari.server
                                            :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                             :plugins [[lein-figwheel "0.2.1-SNAPSHOT"]]

                             :figwheel {:http-server-root "public"
                                        :server-port 3449
                                        :css-dirs ["resources/public/css"]}

                             :env {:is-dev true}

                             :cljsbuild {:test-commands { "test" ["phantomjs" "env/test/js/unit-test.js" "env/test/unit-test.html"] }
                                         :builds {:app
                                                        {:source-paths ["env/dev/cljs"]
                                                         :compiler
                                                                       {:optimizations :advanced
                                                                        :externs ^:replace ["env/externs/externs.js"]
                                                                        :pretty-print false}

                                                         }
                                                  :test {:source-paths ["src/cljs" "test/cljs"]
                                                         :compiler {:output-to     "resources/public/js/app_test.js"
                                                                    :output-dir    "resources/public/js/test"
                                                                    :source-map    "resources/public/js/test.js.map"
                                                                    :preamble      ["react/react.min.js"]
                                                                    :optimizations :advanced
                                                                    :externs ^:replace ["env/externs/externs.js"]
                                                                    :pretty-print  false}}}}}

                       :uberjar {:source-paths ["env/prod/clj"]
                                 :hooks [leiningen.cljsbuild]
                                 :env {:production true}
                                 :omit-source true
                                 :aot :all
                                 :cljsbuild {:builds {:app
                                                      {:source-paths ["env/prod/cljs"]
                                                       :compiler
                                                                     {:optimizations :advanced
                                                                      :externs ^:replace ["env/externs/externs.js"]
                                                                      :pretty-print false}}}}}})

