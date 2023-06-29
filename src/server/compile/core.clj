(ns server.compile.core
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [taoensso.timbre :as timbre]))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]

  (timbre/info (str "reading config from edn:" source))
  (try
    (with-open [r (io/reader source)]
      (edn/read (java.io.PushbackReader. r)))

    (catch java.io.IOException e
      (printf "Couldn't open '%s': %s\n" source (.getMessage e)))
    (catch RuntimeException e
      (printf "Error parsing edn file '%s': %s\n" source (.getMessage e)))))

(defn load-edn-config
  ([config]
   (try
     (let [config (if (nil? config) "./exyl.edn" config)
           edn (load-edn config)]
       (print edn))
     (catch Exception e
       (timbre/error (println "An exception occurred:" (.getMessage e)))))))

(comment
  (timbre/info "hello")
  (load-edn-config "/home/ketan/code/exylerate/server/test-js-dir/exyl.edn")
  (edn/read "{:hello :whello }")
  nil)

