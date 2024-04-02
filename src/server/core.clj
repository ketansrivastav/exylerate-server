(ns server.core
  (:require [server.errors :as errors]
            [server.js-runtime.core :as js-runtime]
            [server.loader.core :as loader]
            [taoensso.timbre :as timbre])
  (:gen-class))

(defn- init []
  (timbre/merge-config! {:level :info
                         :output-fn #(println %2)
                         :appenders {:console {:min-level :debug
                                               :auto-flush? true}}}))

(defn -main
  [& args]
  (init)
  (let [args-map (apply array-map #_args [])
        edn-config (errors/try->or-error "/home/ketan/code/exylerate/server/test-js-dir/exyl.edn" #_(get args-map "--config")
                                         loader/load-edn-config)
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
    ))

