(ns server.js-runtime.core
  (:import [java.util Date]
           (org.graalvm.polyglot Engine Context Value Source)
           (org.graalvm.polyglot.proxy ProxyArray ProxyExecutable ProxyObject)))

(defn get-js-source
  "returns a Source object of user handlers and lib"
  [files]
  (Source/create "js" " var tmp = 2+1;"))

(defn eval-js [code context]
  (.eval ^Context context "js" code))

(defn init-context-factory [js-source]
  (let [!engine (Engine/create)]
    (fn []
      (let [
            !context (-> (Context/newBuilder (into-array ["js"]))
                         (.allowIO true)
                         (.allowHostAccess true)
                         (.engine !engine)
                         (.build))
            compiled-source js-source]

        ; inject source into the context --> context from same engines should have cached the source
        (.eval ^Context !context compiled-source)
        !context))))

(comment
  (let [new-req #(let [get-new-context (init-context-factory)]
                   (->> (get-new-context)
                        (eval-js (str " function settime (){
                          var startTime = Date.now();
                          var endTime = startTime + " % "; // Run the loop for 5 seconds

                          // Loop until the current time exceeds the end time
                          while (Date.now() < endTime) {
                          // Perform some computation
                          // This could be any operation that consumes CPU time
                          // For example, performing mathematical calculations or string manipulations
                          }; console.log(\"from js" % "now \") }; settime() ")))
                   (str "from clj" %))

        t1 (future (new-req 5000))
        t2 (future (new-req 2000))]
    (println @t2)
    (println @t1))

  nil)

; (defn eval-module [src module-name]
;   (.eval ^Context context (.build (Source/newBuilder "js" src module-name))))

; (def -code (str "const FileWriter = Java.type('java.io.FileWriter');
; const BufferedWriter = Java.type('java.io.BufferedWriter');
; const File = Java.type('java.io.File');

; // Specify the file path
; const filePath = './file.txt';

; // Create a FileWriter and BufferedWriter
; const file = new File(filePath);
; const fileWriter = new FileWriter(file);
; const bufferedWriter = new BufferedWriter(fileWriter);

; try {
;   // Write data to the file
;   bufferedWriter.write('Hello, World!');
; } finally {
;   // Close the BufferedWriter to flush and release resources
;   bufferedWriter.close();
; }"))

; (def -code1 (str "function writeFileAsync(filePath, data) {
;   return new Promise((resolve, reject) => {
;     const file = new File(filePath);
;     const fileWriter = new FileWriter(file);
;     const bufferedWriter = new BufferedWriter(fileWriter);

;     bufferedWriter.write(data, (err) => {
;       if (err) {
;         reject(err);
;       } else {
;         bufferedWriter.close();
;         resolve();
;       }
;     });
;   });
; };

; writeFileAsync('./file.txt', 'text').then (d=>console.log('done')).catch(e=>console.log(e))             

; (defn foo [] (println "hello"))
; (future)
; (comment
;   (eval-js -code1)
;   (-> context
;       (.getBindings "js")
;       (.putMember "foo" foo))
;   (eval-js "foo()")
;   (eval-js "console.log(_foo(1,1))")
;   (eval-js "(await setTimeout(;)")
;   (eval-module (str
;                  "import * as api1  from '/home/ketan/code/exylerate/server/test-js-dir/foo.mjs';"
;                  "console.log (api1.get);") "__import__all.mjs")
;   nil)

