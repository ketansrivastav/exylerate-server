(ns server.loader.core
  "author: ketan srivastav
  loads handelrs/modules from disk"
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [server.errors :refer [->RuntimeError try->or-error]]
            [taoensso.timbre :as timbre]))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]

  (timbre/info (str "reading config from edn:" source))
  (try
    (with-open [r (io/reader source)]
      [(edn/read (java.io.PushbackReader. r)) nil])

    (catch java.io.IOException e
      [nil (->RuntimeError :fatal (str "Couldn't open " source (.getMessage e)))])
    (catch RuntimeException e
      [nil (->RuntimeError :fatal (str "Error parsing edn file" source (.getMessage e)))])))

(defn load-edn-config
  [config-file]
  (let [config (if (nil? config-file) "./exyl.edn" config-file)
        return-vector (load-edn config)]
    (update-in return-vector [0] #(merge % {
                                            :project-path (.getPath (.getParentFile (.getCanonicalFile (io/file config))))}))))

(defn get-all-sub-directories [src-directory]
  (->> src-directory
       (io/file)
       (file-seq)
       (filter #(true? (.isDirectory %)))))

(defn load-user-js [path]

  (defn get-all-paths-and-js [path]

    {"/v1/home/one" "function get(){return \"hello world \"}"}))
(comment
  (timbre/error "hello")
  (try->or-error
    "/home/ketan/code/exylerate/server/test-js-dir/exyl.edn"
    load-edn-config
    #_(get-in [:handler-path :http]))
  (edn/read "{:hello :whello }")
  (concat [2 2 2 34 4] [3])
  nil)

