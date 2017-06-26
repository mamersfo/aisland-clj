(ns aisland.http
  (:require [aisland.constants :refer [SERVER]]
            [clj-http.client :as http]
            [cheshire.core :as json]))

(defn get-json
  [uri]
  (let [resp (http/get (str SERVER uri) {:accept :json})]
    (json/parse-string (:body resp) true)))

(defn post-json
  [uri body]
  (let [resp (http/post (str SERVER uri)
                        {:body (json/generate-string body)
                         :content-type :json
                         :accept :json})]
    (json/parse-string (:body resp) true)))

(defn put-json
  ([uri]
   (put-json uri nil))
  ([uri body]
   (let [req {:body (json/generate-string (or body {}))
              :content-type :json
              :accept :json}
         resp (http/put (str SERVER uri) req)]
     (json/parse-string (:body resp) true))))
