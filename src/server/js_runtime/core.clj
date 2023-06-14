(ns server.js-runtime.core
  (:import [java.util Date]
           (org.graalvm.polyglot Context Value Source)
           (org.graalvm.polyglot.proxy ProxyArray ProxyExecutable ProxyObject)))

(def context
  (-> (Context/newBuilder (into-array ["js"]))
      (.allowIO true)
      (.allowHostAccess true)
      (.build)))

(defn eval-js [code]
  (.eval ^Context context "js" code))

(defn eval-module [src module-name]
  (.eval ^Context context (.build (Source/newBuilder "js" src module-name))))
(comment
  (eval-module (str
                 "import * as api1  from '/home/ketan/code/exylerate/server/test-js-dir/foo.mjs';"
                 "console.log (api1.get);") "__import__all.mjs")
  nil)
