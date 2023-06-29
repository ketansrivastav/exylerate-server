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

;                  "))
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

