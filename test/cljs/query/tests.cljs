(ns query.tests
  (:require [query.query :as q]
            [query.fragment :as f]))

(enable-console-print!)


(def test-map
  {:a "test-string"
   :b 12
   :c [12]
   :d [1 2 3 "a"]
   :e [#{1 2}]
   :f {:z :y :x [1 2 3] :w {:mm "abc"}}})

(defn queries []
  (println "Query tests...")
  (q/set-query! test-map)
  (assert (= (q/get-query) test-map) "Test map failed")
  (q/merge-in-query! {:a "new-string"})
  (assert (= (q/get-query) (merge test-map {:a "new-string"})) "Merging failed")
  (q/clear-query!)
  (assert (= (q/get-query) {}) "Clearing the query failed"))


(defn framents []
  (println "Fragments tests...")
  (f/set-fragment! "test-fragment")
  (assert (= (f/get-fragment) "test-fragment") "Setting the fragment failed")
  (f/clear-fragment!)
  (assert (= (f/get-fragment) "") "Clearing fragment failed"))

(defn run-tests []
  (queries)
  (framents))

(run-tests)

(println "Tests completed without error")
