(ns bball.core
  (:require
   [day8.re-frame.http-fx]
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [markdown.core :refer [md->html]]
   [bball.ajax :as ajax]
   [bball.events]
   [reitit.core :as reitit]
   [reitit.frontend.easy :as rfe]
   [clojure.string :as string]
   [bball.websockets :as ws]
   [mount.core :as mount])
  (:import goog.History))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:common/page-id])) :is-active)}
   title])

(defn navbar [] 
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.is-info.games-list>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "🏀bball"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click #(swap! expanded? not)
                  :class (when @expanded? :is-active)}
                 [:span][:span][:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-start
                 [nav-link "#/" "Home" :home]
                 [nav-link "#/about" "About" :about]]]]))

(defn game [{{vts :score vtc :triCode} :vTeam {hts :score htc :triCode} :hTeam :as data}]
   [:div.box 
    [:article.media
      [:figure.media-left.team-figure
        {:class vtc}
        [:p.image.is-32x32
          [:img {:src (str "/img/" vtc ".png")}]]]
      [:div.media-content
        [:div.content
          [:p (str vtc " - " vts)]]]]
    [:article.media
      [:figure.media-left.team-figure
        {:class htc}
        [:p.image.is-32x32
          [:img {:src (str "/img/" htc ".png")}]]]
      [:div.media-content
        [:div.content
          [:p (str htc " - " hts)]]]]
   ])

(defn games-list []
  [:section.section>div.container>div.content
    (when-let [gdat @(rf/subscribe [:scoreboard])]
    [:div.columns.is-centered
     [:div.column.is-two-thirds
      (for [g (map #(assoc % :key (:gameId %)) (:games gdat))]
        [game g])]])])

(defn about-page []
  [:section.section>div.container>div.content
   [:h1 "bball app runthrough"]
   [:p "This app is written in Clojurescript & Clojure. The " 
       [:a {:href "https://github.com/emgullufsen/bball"} "source"] 
       " is on github."]
   [:p "The frontend uses AJAX and a Websocket to receive live score updates from the official NBA scoreboard (JSON).
        If you open the browser console you'll see the app logging information about websocket events."]
   [:p "In the diagram below, first the client makes the initial request and gets a response for the page at nba-scores.rickysquid.org "
       [:strong "(1 and 2)"]
       ". Then for the initial scoreboard data the client makes an AJAX request to data.nba.net "
       [:strong "(3)"]
       ". The client then immediately establishes a Websocket connection
       with the server at ws://nba-scores.rickysquid.org/ws "
       [:strong "(4)"]
       ". My server will then dutifully run a loop every five seconds - hitting the nba.data.net
       endpoint for the latest scoreboard data and sending this through the websocket connection established "
       [:strong "(5)"]
       ". I like this model because the client isn't
       having to do all the work AJAX-ing - it can sit back and passively receive data from my server."]
   [:figure
    [:img.image.is-636x594 {:src "/img/bball-diagram.png"}]]])

(defn home-page []
  [:section.section>div.container>div.content
   (when-let [docs @(rf/subscribe [:docs])]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])])

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/" {:name        :home
           :view        #'games-list
           :controllers [{:start (fn [_] (rf/dispatch [:page/init-home]))}]}]
     ["/about" {:name :about
                :view #'about-page}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (mount-components)
  (mount/start))
