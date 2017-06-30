(ns aisland.client
  (:require [aisland.match :as match]
            [aisland.player :as player]
            [aisland.constants :refer :all]
            [com.stuartsierra.component :as component]))

(defn make-moves
  [player-id match-id token]
  (let [b (match/board match-id)]
    (filter identity
            (for [x (range 0 (:width b))
                  y (range 0 (:height b))
                  :let [p1 {:x x :y y}
                        n1 (match/hexagon b p1)]]
              (when (and (= (:ownerId n1) player-id)
                         (> (:power n1) POWER_THRESHOLD))
                (let [d (rand-int 7)
                      p2 (match/move p1 d)
                      n2 (match/hexagon b p2)]
                  (when (and n2 (not= (:ownerId n2) player-id))
                    (assoc p2 :action SPREAD :direction d))))))))
(defn do-turn
  [player-id match-id token]
  (while (not (Thread/interrupted))
    (let [moves (make-moves player-id match-id token)]
      (match/post-moves player-id match-id token moves))
    (Thread/sleep TURN_DURATION_MILLIS)))

(defrecord Client [player-id match-id token future]
  component/Lifecycle
  (start [component]
    (println ";; Starting client for player:" player-id "match:" match-id)
    (let [f (future-call (partial do-turn player-id match-id token))]
      (assoc component :future f)))
  (stop [component]
    (println ";; Stopping client for player:" player-id "match:" match-id)
    (future-cancel future)
    (assoc component :future nil)))

(defn make-system
  []
  (component/system-map
   :client1 (map->Client {:player-id 7 :match-id 1 :token "lpt1g0i51zh7u8hzk0o0lvz6"})))

(def system (make-system))

(defn start
  []
  (alter-var-root #'system component/start))

(defn stop
  []
  (alter-var-root #'system component/stop))
