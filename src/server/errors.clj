(ns server.errors)

(defrecord RuntimeError [error-type message])

(defn handle-runtime-error [[val err]]
  (if (instance? RuntimeError err)
    (condp = (:error-type err)
      :fatal (println "exiting")
      "unhandled error -- this should never happen")
    val))
(defn !print-and-exit [error])

(defn apply-or-error [f [val err]]
  (if (instance? RuntimeError err)
    [nil err]
    (f val)))

(defmacro try->or-error [val & fns]
  (let [fns-with-apply-or-error (for [f fns] `(apply-or-error ~f))]
    `(->> [~val nil]
          ~@fns-with-apply-or-error
          (handle-runtime-error))))

(comment
  (defn foo1 [data]
    [data nil])

  (defn foo2 [data]
    [data (->RuntimeError :fatal "foo2 error")])

  (defn foo5 [data]
    [data nil])

  (try->or-error {:data :a}
                 foo1
                 foo2
                 foo5))
