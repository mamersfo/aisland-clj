(ns aisland.constants)

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

;; Moves

(def CENTRAL     0)
(def NORTH_WEST  1)
(def NORTH_EAST  2)
(def WEST        3)
(def EAST        4)
(def SOUTH_WEST  5)
(def SOUTH_EAST  6)

;; Comms

;; (def SERVER "http://localhost:8080")
(def SERVER "http://172.16.32.50:8080")

(def TURN_DURATION_MILLIS 500)
(def POWER_THRESHOLD 30)
