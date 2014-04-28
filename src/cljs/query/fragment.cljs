(ns query.fragment
  (:require [query.basics :as b]))

(defn get-fragment
  "Return the fragment from the current url, or the provided one."
  ([] (get-fragment (b/uri)))
  ([url] (.getFragment (b/uri url))))

(defn set-fragment!
  "Set the fragment string of the current url."
  [fragment]
  (b/set-url! (.setFragment (b/uri) fragment)))

(defn clear-fragment! []
  (set-fragment! ""))
