(ns server.core
  (:require [server.compile.core :as compile]
            [server.js-runtime.core :as js-runtime]
            [taoensso.timbre :as timbre])
  (:gen-class))
(defn- init []
  (timbre/merge-config! {:level :info
                         :output-fn #(println %2)
                         :appenders {:console {:min-level :debug
                                               :auto-flush? true}}}))
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [args-map (apply array-map args)]
    (init)
    (js-runtime/eval-js "console.log('hello from JS')")
    (println args-map)
    (compile/load-edn-config (get args-map "--config"))))

