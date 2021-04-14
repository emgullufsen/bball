(ns bball.routes.websockets
  (:require
   [bball.middleware :as middleware]
   [clojure.tools.logging :as log]
   [taoensso.sente :as sente]
   [mount.core :refer [defstate]]
   [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]))

(defstate sockboi
   :start (sente/make-channel-socket!
           (get-sch-adapter)
           {:user-id-fn (fn [ring-req]
                          (get-in ring-req [:params :client-id]))}))

(defn send! [uid message]
  ((:send-fn sockboi) uid message))

(defn receive-message! [{:keys [id ?reply-fn ring-req]
                         :as   message}]
  (case id
    :chsk/bad-package   (log/debug "Bad Package:\n" message)
    :chsk/bad-event     (log/debug "Bad Event: \n" message)
    :chsk/uidport-open  (log/trace (:event message))
    :chsk/uidport-close (log/trace (:event message))
    :chsk/ws-ping       nil
    ;; ELSE
    nil))

(defstate channel-router
  :start (sente/start-chsk-router!
          (:ch-recv sockboi)
          #'receive-message!)
  :stop (when-let [stop-fn channel-router]
          (stop-fn)))


(defn websockboi-routes []
  ["/ws"
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]
    :get (:ajax-get-or-ws-handshake-fn sockboi)
    :post (:ajax-post-fn sockboi)}])
