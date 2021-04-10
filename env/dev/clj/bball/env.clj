(ns bball.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [bball.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[bball started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[bball has shut down successfully]=-"))
   :middleware wrap-dev})
