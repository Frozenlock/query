(ns query.basics
  (:require [goog.Uri :as uri]))

(defn set-url!
  "Set the url in the browser bar without reloading the document." [url]
  (js/window.history.replaceState nil js/document.title url))

;; we use replaceState instead of the simpler
;; js/document.location.search because it doesn't cause the browser to
;; reload the document.

(defn uri
  "Make a goog uri object from the given string, or default to the
  current url if no argument is provided." 
  ([] (uri js/document.location))
  ([string] (goog.Uri. string)))


(defn to-map
  ([] (to-map (uri)))
  ([url]
     (let [goog-uri (uri url)]
       {:fragment (.getFragment goog-uri)
        :scheme (.getScheme goog-uri)
        :domain (.getDomain goog-uri)
        :path (.getPath goog-uri)
        :query (.getQuery goog-uri)})))
