(ns server.js-runtime.java-class
  (:gen-class
    :name my.package.MyClass
    :methods [^{:static true} [myStaticMethod [String] void]
              [myInstanceMethod [int] int]]))

(defn -myStaticMethod [arg]
  (println "Static method called with argument:" arg))

(defn -myInstanceMethod [this arg]
  (+ arg 1))
