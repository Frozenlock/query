(defproject org.clojars.frozenlock/query "0.2.2"
  :description "Helper functions to manipulate the URL query and fragment (anchor)"
  :url "http://github.com/Frozenlock/query"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2173" :scope "provided"]]
  
  :profiles {:dev {:dependencies [[ring "1.2.1"]
                                  
                                  ;; for the cljs REPL
                                  [com.cemerick/piggieback "0.1.3"]]
                   :main anchor.server
                   :ring {:handler query.server/app}
                   :source-paths ["src/clj"]
                   :injections [(ns user)
                                (require '[cljs.repl.browser :as brepl]
                                         '[cemerick.piggieback :as pb])
                                (defn browser-repl []
                                  (pb/cljs-repl :repl-env
                                                (brepl/repl-env :port 9000)))]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}


  :plugins [[lein-cljsbuild "1.0.2"]
            [lein-ring "0.8.10"]]
;  :hooks [leiningen.cljsbuild]
  :source-paths ["src/cljs"] ;; don't include the ring/server code
  :cljsbuild { 
              :builds {
                       :main {
                              :source-paths ["src/cljs"]
                              :compiler {:output-to "resources/public/js/cljs.js"
                                         :optimizations :simple
                                         :pretty-print true}
                              :jar true}
                       :dev {:source-paths ["src/cljs" "src-dev/cljs"]
                             :compiler {:output-to "resources/public/js/cljs.js"
                                        :optimizations :whitespace
                                        :pretty-print true}}
                       :test {:source-paths ["src/cljs" "test/cljs"]
                             :compiler {:output-to "resources/public/js/cljs.js"
                                        :optimizations :whitespace
                                        :pretty-print true}}
                       }}
  :aliases {"browser-test" ["do" ["cljsbuild" "clean"] ["cljsbuild" "once" "test"] ["ring" "server" "3001"]]
            "cljsbuild-dev" ["do" ["cljsbuild" "clean"] ["cljsbuild" "auto" "dev"]]})

