(ns query.query
  (:require [cljs.reader :as reader]
            [query.basics :as b]
            [clojure.string :as s]))


;;; I don't really know how to handle sets. Getting sets back when
;;; reading the query would be very nice, but the query's first level
;;; sets should also be converted to an array automatically.

;;; As it is, I decided to treat vectors and sets in the same way.
;;; This means that you can give a sets as a query value, but when
;;; reading it back, you'll get a vector.

;;; This limitation is only for the first level, as everything nested
;;; deeper is just converted to a string.


(defn from-query
  "return a clojure smap from a query string" [string]
  (let [query (goog.Uri.QueryData. string)
        ks (.getKeys query)
        pairs (for [k (distinct ks)]
                [k (mapv reader/read-string (.getValues query k))])]
    ;; at this point the pairs are like this:  (["a[]" (1)] ["b[]" (1 2)]...)
    (into {}
          (for [[k v] pairs]
            (if (re-find #"\[\]" k) ;; if it's a vector keyword
              [(keyword (s/replace k #"\[\]" "")) v]
              [(keyword k) (first v)])))))
        

(defn encode-plus
  [string]
  (some-> string str (js/encodeURIComponent) (.replace "+" "%20")))
  
(defn pr-str-plus
  "Same as pr-str, but encode the plus sign."
  [string]
  (encode-plus (pr-str string)))

(defn- to-query*
  "Convert a smap to a goog query. Supports nested structures, but
  only the first level is converted into a normal query (everything
  else is simply converted to a string.)" [smap]
  (let [prepared-smap
        (into {}
              (for [[k v] smap]
                (cond
                 (not (coll? v))  [k (pr-str-plus v)]
                 (map? v)         [k (pr-str-plus v)]
                 :else            [(str (encode-plus (name k)) "[]") (map pr-str-plus v)])))] ;; vectors and sets
     (goog.Uri.QueryData.createFromMap.
      (clj->js prepared-smap))))




(defn to-query-string
  "Convert a smap to a query string. Supports nested structures, but
  only the first level is converted into a normal query (everything
  else is simply converted to a string.)" [smap]
  (when-not (empty? smap)
    (str "?" (to-query* smap))))




;;; API

(defn get-query
  "Return the query parameters as a map from the current url, or the
  provided one." 
  ([] (get-query (b/uri)))
  ([url] (from-query (.getDecodedQuery (b/uri url)))))


(defn set-query!
  "Convert the smap into a URL query and put it in the browser's URL
  bar."[smap]
  (b/set-url! (str (.setQueryData (b/uri) (to-query* smap)))))


(defn- clean-map
  "Remove any keys with 'nil' as a value"
  [m]
  (into {} (remove (comp nil? second) m)))
  
(defn merge-in-query! 
  "Merge the smap into the currenlt URL query. Nil values are
  discarded."
  [smap]
  (-> (get-query)
      (merge smap)
      (clean-map) ;; remove keys with 'nil' as a value
      (set-query!)))

(defn clear-query!
  "Remove every query parameters." []
  (set-query! nil))


;; Very specific use case... should be removed?

(defn goto-href!
  "Redirect the browser to the given URL and the optional query parameters."
  [url & [queries-smap]]
  (let [query (to-query-string queries-smap)
        complete-url (str url (when queries-smap query))]
    (set! js/location.href complete-url)))
