(ns query.query
  (:require [cljs.reader :as reader]
            [query.basics :as b]
            [clojure.string :as s]))


;;; I don't really know how to handle sets. Getting sets back when
;;; reading the query would be very nice, but the query first level
;;; sets should also be converted to an array automatically.

;;; As it is, I decided to treat vectors and sets in the same way.
;;; This means that you can give a sets as a query value, but when
;;; reading it back, you'll get a vector.


(defn from-query
  "Return a clojure smap from a query string" [string]
  (let [query (goog.Uri.QueryData. string)
        ks (.getKeys query)
        key-value-pairs (for [k (distinct ks)]
                          [(keyword (s/replace k #"\[|\?|\]" ""))
                           ;;remove "[", "]" and "?" from keyword
                           (let [v (js->clj (.getValues query k))]
                             (into [] (map reader/read-string v)))])
        ;; unfortunately the "?" might be included in the key name, which means we can end up with
        ;; more than one value for a key. Let's merge them...
        raw-map (apply merge-with into (for [[k v] key-value-pairs] {k v}))]
    ;;we now need to find singleton (for example [12]) and remove them from their collection
    (into {} (for [[k v] raw-map] [k (cond
                                      (nth v 1 nil) v;singleton?
                                      (map? (first v)) (first v); a map is a collection, but still a singleton
                                      (coll? (first v)) v
                                      :else (first v))]))))



(defn- to-query*
  "Convert a smap to a goog query. Supports nested structures, but
  only the first level is converted into a normal query (everything
  else is simply converted to a string.)" [smap]
  (let [prepared-smap
        (into {}
              (for [[k v] smap]
                (cond
                 (not (coll? v))  [k (pr-str v)]
                 (map? v)         [k (pr-str v)]
                 :else            [k (map pr-str v)])))] ;; vectors and sets
     (goog.Uri.QueryData.createFromMap.
      (clj->js prepared-smap))))




(defn to-query-string
  "Convert a smap to a query string. Supports nested structures, but
  only the first level is converted into a normal query (everything
  else is simply converted to a string.)" [smap]
  (when-not (empty? smap)
    (str "?" (to-query* smap))))




;;; API ... ?

(defn get-query
  "Return the query parameters as a map from the current url, or the
  provided one." 
  ([] (get-query (b/uri)))
  ([url] (from-query (.getQuery (b/uri url)))))


(defn set-query!
  "Convert the smap into a URL query and put it in the browser's URL
  bar."[smap]
  (b/set-url! (.setQueryData (b/uri) (to-query* smap))))


(defn merge-in-query! 
  "Merge the smap into the currenlt URL query." [smap]
  (-> (get-query)
      (merge smap)
      (set-query!)))

(defn clear-query!
  "Remove every query parameters." []
  (set-query! nil))


(defn goto-href!
  "Redirect the browser to the given URL and the optional query parameters."
  [url & [queries-smap]]
  (let [query (to-query-string queries-smap)
        complete-url (str url (when queries-smap query))]
    (set! js/location.href complete-url)))
