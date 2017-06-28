# aisland-clj

A starter kit for the [AIsland game contest](http://joyofcoding.org/speaker/a-i-sland/) in Clojure.

### Usage

You run a client by opening a REPL from [`client.clj`](https://github.com/mamersfo/aisland-clj/blob/master/src/aisland/client.clj), from which you invoke:

    (start)
    
This will run a partial function based on `do-turn` in a separate thread. If you want to exit, this is the invocation to do so (so you would expect):

    (stop)

The `do-turn` function takes a `player-id` and a `match-id`. This is set up in the `make-system` function, so you have to tweak that.

But first you need to obtain a `player-id` for yourself, and to find out which `match-id` you are assigned to.

To obtain a `player-id`, first you register:

    (player/register {:name "Your name" 
                      :email "you@example.com"
                      :password "secret"})
    
Then you login:

    (player/login {:name "Your name" 
                   :email "you@example.com"
                   :password "secret"}

This will return your credentials, along with your `player-id` and a `session-key`, e.g.:

    {:name "Your name" 
     :email "you@example.com"
     :password "secret"
     :player-id 42
     :session-key "AC5738D6BF937C4399D3747AE081783D"}

To join a match, use the `player-id` and `session-key` from the [`match.clj`](https://github.com/mamersfo/aisland-clj/blob/master/src/aisland/match.clj) namespace:

    (match/join 42 "AC5738D6BF937C4399D3747AE081783D")

This will put you in the queue. After that you need to poll the `matches` function to find out if you have been assigned to a match:

    (match/all)
    
So after entering a match, you start the client after supplying the `player-id` and the `match-id` in the `make-system` function.

This will invoke the `do-turn` function, which continuously performs the following three steps:

1. Query the server to return the situation on the board
2. Make moves using the `make-moves` function and the board situation
3. Post the moves back to the server

The `make-moves` function is an implementation with a totally naive strategy, so it's up to you to make a more sophisticated version of it. Good luck!


### License

Copyright © 2017 Stichting Devnology

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
