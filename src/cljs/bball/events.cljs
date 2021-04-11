(ns bball.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]))

;;dispatchers

(rf/reg-event-db
  :common/navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :common/route new-match))))

(rf/reg-fx
  :common/navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :common/navigate!
  (fn [_ [_ url-key params query]]
    {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-db
 :set-scoreboard
 (fn [db [_ sb]]
   (assoc db :scoreboard (clojure.walk/keywordize-keys sb))))

(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success       [:set-docs]}}))

(rf/reg-event-fx
 :fetch-nba-metadata
 (fn [_ _]
   {:http-xhrio {:method          :get
                 :uri             "http://data.nba.net/10s/prod/v1/today.json"
                 :response-format (ajax/json-response-format)
                 :on-success       [:fetch-scoreboard]}}))

(rf/reg-event-fx
 :fetch-scoreboard
 (fn [_ [_ nba-metadata]]
   {:http-xhrio {:method          :get
                 :uri             (str 
                                   "http://data.nba.net"
                                   ((nba-metadata "links") "todayScoreboard"))
                 :response-format (ajax/json-response-format)
                 :on-success       [:set-scoreboard]}}))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-fx
  :page/init-home
  (fn [_ _]
    {:dispatch-n [[:fetch-docs] [:fetch-nba-metadata]]}))

;;subscriptions

(rf/reg-sub
  :common/route
  (fn [db _]
    (-> db :common/route)))

(rf/reg-sub
  :common/page-id
  :<- [:common/route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :common/page
  :<- [:common/route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
 :scoreboard
 (fn [db _]
   (:scoreboard db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))
