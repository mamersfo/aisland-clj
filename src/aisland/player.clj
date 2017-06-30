(ns aisland.player
  (:require [aisland.http :as http]))

(defn register
  [user]
  (http/post-json "/players/register" user))

(defn all
  []
  (http/get-json "/players"))

(defn- player-id
  [name]
  (:id (first (filter #(= name (:name %)) (all)))))

(defn login
  [{:keys [name email password] :as user}]
  (let [resp (http/post-json "/players/login" user)]
    (assoc user
           :token (:value resp)
           :player-id (player-id name))))
