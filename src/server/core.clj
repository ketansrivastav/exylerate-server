(ns server.core
  (:require [server.js-runtime.core :as js-runtime])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (js-runtime/eval-js "console.log('hello from JS)")
  (println "Hello, World!"))

(comment

  (js-runtime/eval-module (str
                            "import {get}  from './foo.mjs';"
                            "console.log (get);") "__import__all.mjs")
  nil)
