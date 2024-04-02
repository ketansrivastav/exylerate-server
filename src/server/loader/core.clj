(ns server.loader.core
  "author: ketan srivastav
  loads handelrs/modules from disk"
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [server.errors :refer [->RuntimeError]]
            [taoensso.timbre :as timbre]))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]

  (timbre/info (str "reading config from edn:" source))
  (try
    (with-open [r (io/reader source)]
      [(edn/read (java.io.PushbackReader. r)) nil])

    (catch java.io.IOException e
      [nil (->RuntimeError :fatal (str "Couldn't open '%s': %s\n" source (.getMessage e)))])
    (catch RuntimeException e
      [nil (->RuntimeError :fatal (str "Error parsing edn file '%s': %s\n" source (.getMessage e)))])))

(defn load-edn-config
  ([config]
   (try
     (let [config (if (nil? config) "./exyl.edn" config)
           edn (load-edn config)]
       edn)
     (catch Exception e
       [nil (->RuntimeError :fatal (.getMessage e))]))))

(defn load-user-js [path])

(comment
  (timbre/error "hello")
  (-> (load-edn-config "/home/ketan/code/exylerate/server/test-js-dir/exyl.edn")
      (get-in [:handler-path :http]))
  (edn/read "{:hello :whello }")
  nil)

