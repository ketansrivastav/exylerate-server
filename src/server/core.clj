(ns server.core
  (:require [integrant.core :as ig]
            [integrant.repl :refer [clear go halt prep init reset reset-all]]
            [ring.adapter.jetty :as jetty]
            [server.errors :as errors]
            [server.http-server.core :as http-server]
            [server.js-runtime.core :as js-runtime]
            [server.loader.core :as loader]
            [taoensso.timbre :as timbre])
  (:gen-class))

(def config
  {::server {:port 5000 :join? false}})

(defmethod ig/init-key ::server [_ options]
  (jetty/run-jetty http-server/app options))

(defmethod ig/halt-key! ::server
  [_ server]
  (.stop server))

(defn- init-server []
  (ig/init config)
  (timbre/merge-config! {:level :info
                         :output-fn (fn [args]
                                      (apply println (:vargs args)))
                         :appenders {:console {:min-level :debug
                                               :auto-flush? true}}}))

(defn runtime [& args]

  (let [args-map (apply array-map args)
        edn-config (first (errors/try->or-error (get args-map "--config")
                                                loader/load-edn-config))
        get-new-context (->> edn-config
                             (select-keys [:js-path])
                             (loader/load-user-js)
                             (js-runtime/get-js-source)
                             (js-runtime/init-context-factory))
        http-handler-fn (fn [js-fun]
                          (let [current-context (get-new-context)]
                            (js-runtime/eval-js current-context (str js-fun "();")) ;; --> return the value
                            ))]
    ;;bootstrap http routes
    ;; create vector of APIs from directory structure

    edn-config))
(defn -main
  [& args]
  (init-server)
  (runtime args))

(comment
  (runtime "--config" "/home/ketan/code/exylerate/server/test-js-dir/exyl.edn")

  (integrant.repl/set-prep! (constantly config))
  (go)
  (reset)

  nil)
