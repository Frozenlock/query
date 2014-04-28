(ns query.dev.repl
  (:require [clojure.browser.repl :as repl]))

(defn connect []
  (repl/connect "http://localhost:9000/repl"))

(set! (.-onload js/window) connect)
