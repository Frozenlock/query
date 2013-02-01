(ns query.core
  (:require [cljs.reader :as reader]))


(defn new-url [url]
  (js/window.history.replaceState nil js/document.title url))

(defn location
  "Get location info. Host, pathname, port, search... return a map."[]
  (let [location js/document.location]
    {:host (.-host location)
     :hostname (.-hostname location)
     :href (.-href location)
     :origin (.-origin location)
     :pathname (.-pathname location)
     :port (.-port location)
     :protocol (.-protocol location)
     :search (.-search location)}))

(defn to-query
  "Convert a smap to a query string."[smap]
  (let [prepared-smap (into {}
                            (for [[k v] smap]
                              (if (coll? v)
                                [(symbol (str (name k) "[]")) (if (map? v)
                                                                (pr-str v)
                                                                (map pr-str v))]
                                [k (pr-str v)])))]
    (str "?" (.toString (goog.Uri.QueryData.createFromMap.
                                (clj->js prepared-smap))))))


(defn from-query
  "Return a clojure smap from a query string" [string]
  (let [query (goog.Uri.QueryData. string)
        ks (.getKeys query)
        key-value-pairs (for [k (distinct ks)]
                          [(keyword (clojure.string/replace k #"\[|\?|\]" ""))
                           ;;remove "[", "]" and "?" from keyword
                           (let [v (js->clj (.getValues query k))]
                             (into [] (map reader/read-string v)))])
        ;; unfortunately the "?" is included in the key name, which means we can end up with
        ;; more than one value for a key. Let's merge them...
        raw-map (apply merge-with into (for [[k v] key-value-pairs] {k v}))]
    ;;we now need to find singleton (for example [12]) and remove them from their collection
    (into {} (for [[k v] raw-map] [k (cond
                                      (nth v 1 nil) v;;singleton?
                                      (map? (first v)) (first v);; a map is a collection, but still a singleton
                                      (coll? (first v)) v
                                      :else (first v))]))))
      

(defn qquery
  "Return the query parameters as a map" []
  (from-query (:search (location))))


(defn replace-in-query-string
  "Return a string of the current query, updated with the given smap."
  [smap]
  (-> (merge (qquery) smap)
      (to-query)))

(defn make-url-query
  "Convert the smap into a URL query and put it in the browser's URL
  bar."[smap]
  (let [p (:pathname (location))]
    (new-url (str p (to-query smap)))))
n  
(defn update-url-query
  "Update the query in the browser's URL bar." [smap]
  (let [p (:pathname (location))]
    (new-url (str p (replace-in-query-string smap)))))

(defn clear-url-query
  "Remove every query parameters." []
  (new-url (:pathname (location))))

(defn goto-href
  "Redirect the browser to the given URL and the optional query parameters."
  [url & [queries-smap]]
  (let [query (to-query queries-smap)
        complete-url (str url (when queries-smap query))]
    (set! js/location.href complete-url)))
