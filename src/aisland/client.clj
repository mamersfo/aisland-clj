(ns aisland.client
  (:require [aisland.match :as match]
            [aisland.constants :refer [TURN_DURATION_MILLIS]]
            [com.stuartsierra.component :as component]))

(defn do-turn
  [player-id match-id]
  (while (not (Thread/interrupted))
    (let [b (match/board match-id)]
      (let [moves (make-moves b)]
        (post-moves player-id match-id moves)))
    (Thread/sleep TURN_DURATION_MILLIS)))

(defrecord Client [player-id match-id future]
  component/Lifecycle
  (start [component]
    (println ";; Starting client for player:" player-id "match:" match-id)
    (let [f (future-call (partial do-turn player-id match-id))]
      (assoc component :future f)))
  (stop [component]
    (println ";; Stopping client for player:" player-id "match:" match-id)
    (future-cancel future)
    (assoc component :future nil)))

(defn make-system
  []
  (component/system-map
   :client1 (map->Client {:player-id 1 :match-id 1})))

(def system (make-system))

(defn start
  []
  (alter-var-root #'system component/start))

(defn stop
  []
  (alter-var-root #'system component/stop))
