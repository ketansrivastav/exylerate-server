(defproject server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.graalvm.js/js "22.3.2"]
                 [integrant/integrant "0.8.0"]
                 [integrant/repl "0.3.2"]

                 [com.github.clj-easy/graal-build-time "0.1.4"]
                 [com.taoensso/timbre "6.5.0"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [metosin/reitit "0.7.0-alpha7"]
                 [metosin/ring-swagger-ui "5.9.0"]]

  :main ^:skip-aot server.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
