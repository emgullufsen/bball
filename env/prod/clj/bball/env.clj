(ns bball.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[bball started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[bball has shut down successfully]=-"))
   :middleware identity})
