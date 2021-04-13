(ns bball.routes.websockets
  (:require
   [org.httpkit.server
    :refer [send! with-channel on-close on-receive]]
   [cognitect.transit :as t]
   [clojure.tools.logging :as log]
   [clojure.data.json :as json]
   [clojure.core.async :refer [thread]]
   [overtone.at-at :as atat]))

(defn feed-connection-updates [channel]
  (let [today-json (json/read-str (slurp "http://data.nba.net/10s/prod/v1/today.json"))
        my-pool (atat/mk-pool)]
    (atat/every
     1000
     #(let [latest-sb (json/read-str (slurp (str "http://data.nba.net" ((today-json "links") "currentScoreboard"))))
            sendresult (send! channel (json/write-str latest-sb))]
        (println (str (type channel)))
        (println (str "SEND RESULT WAS: " sendresult)))
     my-pool)))

(defonce channels (atom #{}))

(defn connect! [channel]
  (log/info "channel open")
  (swap! channels conj channel)
  (log/info "beginning updates feed"))

(defn disconnect! [channel status]
  (log/info "channel closed:" status)
  (swap! channels #(remove #{channel} %)))

(defn notify-clients [msg]
  (doseq [channel @channels]
    (send! channel msg)))

(defn ws-handler [request]
  (with-channel request channel
    (connect! channel)
    (on-close channel (partial disconnect! channel))
    (on-receive channel #(notify-clients %))))

(def websocket-routes
  ["/ws" ws-handler])