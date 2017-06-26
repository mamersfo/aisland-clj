(ns aisland.match
  (:require [aisland.http :as http]
            [aisland.constants :refer [POWER_THRESHOLD]]))

(defn queue
  []
  (http/get-json "/queue"))

(defn join
  [player-id session-token]
  (Integer/parseInt
   (:value (http/put-json (str "/queue/" player-id "/" session-token)))))

(defn all
  []
  (http/get-json "/matches"))

(defn one
  [id]
  (http/get-json (str "/matches/" id)))

(defn board
  [id]
  (http/get-json (str "/matches/" id "/map")))

(defn players
  [id]
  (http/get-json (str "/matches/" id "/players")))

(defn move
  [{:keys [x y]} direction]
  (case direction
    1 {:x (if (even? y) (dec x) x) :y (dec y)}  ;; NORTH_WEST
    2 {:x (if (odd? y)  (inc x) x) :y (dec y)}  ;; NORTH_EAST
    3 {:x (dec x)                  :y y      }  ;; WEST
    4 {:x (inc x)                  :y y      }  ;; EAST
    5 {:x (if (even? y) (dec x) x) :y (inc y)}  ;; SOUTH_WEST
    6 {:x (if (odd? y)  (inc x) x) :y (inc y)}  ;; SOUTH_EAST
    {:x x :y y}                                 ;; CENTRAL
))

(defn- hexagon
  [m {:keys [x y]}]
  (when (and (< -1 x (:width m)) (< -1 y (:height m)))
    (get (:nodes m) (+ x (* y (:width m))))))

(defn post-moves
  [player-id match-id moves]
  (if-not (empty? moves)
    (let [uri (str "/matches/" match-id "/players/" player-id "/moves")]
      (http/post-json uri moves))))

(defn make-moves
  [player-id match-id]
  (let [b (board match-id)]
    (filter identity
            (for [x (range 0 (:width b))
                  y (range 0 (:height b))
                  :let [p1 {:x x :y y}
                        n1 (hexagon b p1)]]
              (when (and (= (:ownerId n1) player-id)
                         (> (:power n1) POWER_THRESHOLD))
                (let [d (rand-int 7)
                      p2 (move p1 d)
                      n2 (hexagon b p2)]
                  (when (and n2 (not= (:ownerId n2) player-id))
                    (assoc p2 :action SPREAD :direction d))))))))
