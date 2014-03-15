(defproject go-radiator "0.1.1"
  :description "Build radiator for ThoughtWorks Go"
  :url "http://github.com/dlokesh/go-radiator"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [cheshire "5.3.0"]
                 [org.webbitserver/webbit "0.4.15"]
                 [org.clojure/clojurescript "0.0-2069"]
                 [com.cemerick/url "0.1.1"]]
  :main ^:skip-aot go-radiator.core
  :target-path "target/%s"
  :plugins [[lein-midje "3.0.0"]
            [lein-cljsbuild "1.0.0"]]
  :source-paths ["src" "src/cljs"]
  :cljsbuild {:builds
              [{
                :source-paths ["src/cljs"]
                :compiler {
                           :output-to "resources/public/js/main.js"
                           :optimizations :whitespace
                           :pretty-print true}}]}

  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[midje "1.5.1"]]}})
