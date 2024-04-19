(ns server.http-server.core
  "sets up and handles HTTP apis"
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [muuntaja.core :as m]
            [reitit.coercion.spec]
            [reitit.dev.pretty :as pretty]
            [reitit.openapi :as openapi]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            ;; Uncomment to use
            ; [reitit.ring.middleware.dev :as dev]
            ; [reitit.ring.spec :as spec]
            ; [spec-tools.spell :as spell]
            [spec-tools.core :as st]))

(s/def ::file multipart/temp-file-part)
(s/def ::file-params (s/keys :req-un [::file]))

(s/def ::name string?)
(s/def ::size int?)
(s/def ::file-response (s/keys :req-un [::name ::size]))

;; Use data-specs to provide extra JSON-Schema properties:
;; https://github.com/metosin/spec-tools/blob/master/docs/04_json_schema.md#annotated-specs
(s/def ::x (st/spec {:spec int?
                     :name "X parameter"
                     :description "Description for X parameter"
                     :json-schema/default 42}))
(s/def ::y int?)
(s/def ::total int?)
(s/def ::math-request (s/keys :req-un [::x ::y]))
(s/def ::math-response (s/keys :req-un [::total]))

(def app
  (ring/ring-handler
    (ring/router
      [["/docs/swagger.json"
        {:get {:no-doc true
               :swagger {:info {:title "my-api"}}
               :handler (swagger/create-swagger-handler)}}]
       ["/docs/openapi.json"
        {:get {:no-doc true
               :openapi {:info {:title "my-api"
                                :description "openapi3-docs with reitit-http"
                                :version "0.0.1"}}
               :handler (openapi/create-openapi-handler)}}]

       ["/files"
        {:tags ["files"]}

        ["/upload"
         {:post {:summary "upload a file"
                 :parameters {:multipart ::file-params}
                 :responses {200 {:body ::file-response}}
                 :handler (fn [{{{:keys [file]} :multipart} :parameters}]
                            {:status 200
                             :body {:name (:filename file)
                                    :size (:size file)}})}}]

        ["/download"
         {:get {:summary "downloads a file"
                :swagger {:produces ["image/png"]}
                :responses {200 {:description "an image"
                                 :content {"image/png" {:schema string?}}}}
                :handler (fn [_]
                           {:status 200
                            :headers {"Content-Type" "image/png"}
                            :body (io/input-stream
                                    (io/resource "reitit.png"))})}}]]

       ["/math"
        {:tags ["math"]}

        ["/plus"
         {:get {:summary "plus with spec query parameters"
                :parameters {:query ::math-request}
                :responses {200 {:body ::math-response}}
                :handler (fn [{{{:keys [x y]} :query} :parameters}]
                           {:status 200
                            :body {:total (+ x y)}})}
          :post {:summary "plus with spec body parameters"
                 :parameters {:body ::math-request}
                 :responses {200 {:body ::math-response}}
                 :handler (fn [{{{:keys [x y]} :body} :parameters}]
                            {:status 200
                             :body {:total (+ x y)}})}}]]

       ["/us"
        {:get {:summary "plus with spec query parameters"
               :parameters {:query ::math-request}
               :responses {200 {:body ::math-response}}
               :handler (fn [{{{:keys [x y]} :query} :parameters}]
                          {:status 200
                           :body {:total (+ x y)}})}
         :post {:summary "plus with spec body parameters"
                :parameters {:body ::math-request}
                :responses {200 {:body ::math-response}}
                :handler (fn [{{{:keys [x y]} :body} :parameters}]
                           {:status 200
                            :body {:total (+ x y)}})}}]]

      {;;:reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
       ;;:validate spec/validate ;; enable spec validation for route data
       ;;:reitit.spec/wrap spell/closed ;; strict top-level validation
       :exception pretty/exception
       :data {:coercion reitit.coercion.spec/coercion
              :muuntaja m/instance
              :middleware [;; swagger feature
                           swagger/swagger-feature
                           ;; query-params & form-params
                           parameters/parameters-middleware
                           ;; content-negotiation
                           muuntaja/format-negotiate-middleware
                           ;; encoding response body
                           muuntaja/format-response-middleware
                           ;; exception handling
                           exception/exception-middleware
                           ;; decoding request body
                           muuntaja/format-request-middleware
                           ;; coercing response bodys
                           coercion/coerce-response-middleware
                           ;; coercing request parameters
                           coercion/coerce-request-middleware
                           ;; multipart
                           multipart/multipart-middleware]}})
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path "/docs"
         :config {:validatorUrl nil
                  :urls [{:name "swagger" :url "swagger.json"}
                         {:name "openapi" :url "openapi.json"}]
                  :urls.primaryName "openapi"
                  :operationsSorter "alpha"}})
      (ring/create-default-handler))))

