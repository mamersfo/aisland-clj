(ns aisland.match
  (:require [aisland.http :as http]))

;; Actions

(def SLEEP       0)
(def SPREAD      1)
(def SPREADALL   2)
(def SPREADLINE  3)
(def EMPOWER     4) 
(def DISCHARGE   5)
(def POWERLINE   6)
(def OVERCLOCK   7)
(def GUARD       8)
(def STORAGE     9)
(def DRAIN      10)
(def EXPLODE    11) 

(def ^:dynamic *match* 1)

(defn matches
  []
  (http/get-json "/matches"))

(defn match
  [id]
  (http/get-json (str "/matches/" id)))

(defn match-rules
  [id]
  (http/get-json (str "/matches/" id "/rules")))

(defn match-turn
  [id]
  (:value (http/get-json (str "/matches/" id "/turn"))))

(defn match-map
  [id]
  (http/get-json (str "/matches/" id "/map")))

(defn match-players
  [id]
  (http/get-json (str "/matches/" id "/players")))

(defn match-player-moves
  [match-id player-id]
  (http/get-json (str "/matches/" match-id "/players/" player-id "/moves")))

;; Moves

(def CENTRAL     0)
(def NORTH_WEST  1)
(def NORTH_EAST  2)
(def WEST        3)
(def EAST        4)
(def SOUTH_WEST  5)
(def SOUTH_EAST  6)

(defn move
  [{:keys [x y]} p direction]
  (case direction
    CENTRAL    {:x x :y y}
    NORTH_WEST {:x (if (= 0 (mod y 2)) (dec x) x) :y (dec y)}
    NORTH_EAST {:x (if (= 1 (mod y 2)) (inc x) x) :y (dec y)}
    WEST       {:x (dec x) :y y}
    EAST       {:x (inc x) :y y}
    SOUTH_WEST {:x (if (= 0 (mod y 2)) (dec x) x) :y (inc y)}
    SOUTH_EAST {:x (if (= 1 (mod y 2)) (inc x) x) :y (inc y)}
))

