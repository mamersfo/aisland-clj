(ns aisland.core
  (:require [aisland.http :as http]))

(def users {1 {:name "John" :email "John@test.uk" :password "password"}
            2 {:name "Jake" :email "Jake@test.uk" :password "password"}})

(def USER (atom (get users 1)))

(defn register
  [user]
  (http/post-json "/players/register" user)
  (swap! USER merge user)
  @USER)

(defn players
  []
  (http/get-json "/players"))

(defn player-id
  [name]
  (:id (first (filter #(= name (:name %)) (players)))))

(defn login
  []
  (let [resp (http/post-json "/players/login" @USER)]
    (swap! USER assoc :session-token (:value resp))
    (swap! USER assoc :player-id (player-id (:name@USER)))
    @USER))

(defn queue
  []
  (http/get-json "/queue"))

(defn get-value
  [m]
  (Integer/parseInt (:value m)))

(defn queue-position
  [x]
  (try
    (get-value (http/get-json (str "/queue/" x)))
    (catch Exception e
      -1)))

(defn join-queue
  []
  (let [player-id (:player-id @USER)
        session-id (:session-token @USER)]
    (get-value
     (http/put-json (str "/queue/" player-id "/" session-id)))))

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
  [{:keys [x y]} direction]
  (case direction
    1 {:x (if (even? y) (dec x) x) :y (dec y)}  ;; NORTH_WEST
    2 {:x (if (odd? y) (inc x) x) :y (dec y)}   ;; NORTH_EAST
    3 {:x (dec x) :y y}                         ;; WEST
    4 {:x (inc x) :y y}                         ;; EAST
    5 {:x (if (even? y) (dec x) x) :y (inc y)}  ;; SOUTH_WEST
    6 {:x (if (odd? y) (inc x) x) :y (inc y)}   ;; SOUTH_EAST
    {:x x :y y}                                 ;; CENTRAL
    ))

(def ^:dynamic *match-id* 1)
(def ^:dynamic *player-id* 1)
(def ^:dynamic *power-threshold* 30)

(defn find-node
  [m {:keys [x y]}]
  (when (and (< -1 x (:width m)) (< -1 y (:height m)))
    (get (:nodes m) (+ x (* y (:width m))))))

(defn make-moves
  [m]
  (filter identity
          (for [x (range 0 (:width m))
                y (range 0 (:height m))
                :let [p1 {:x x :y y}
                      n1 (find-node m p1)]]
            (when (and (= (:ownerId n1) *player-id*)
                       (> (:power n1) *power-threshold*))
              (let [d (rand-int 7)
                    p2 (move p1 d)
                    n2 (find-node m p2)]
                (when (and n2 (not= (:ownerId n2) *player-id*))
                  (assoc p2 :action SPREAD :direction d)))))))

(defn post-moves
  [c]
  (if-not (empty? c)
    (let [uri (str "/matches/" *match-id* "/players/" *player-id* "/moves")]
      (println (http/post-json uri c)))))

(defn do-work
  []
  (while (not (Thread/interrupted))
    (let [m (match-map 1)]
      (let [moves (make-moves m)]
        (println "moves:" moves)
        (post-moves moves)))
    (Thread/sleep 1000)))

;; start:
;; (def f (future-call do-work))
;; stop:
;; (future-cancel f)

