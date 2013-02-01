# query

![Query](./QuestionA.jpg)


Helper functions to manipulate the URL query as a clojure map.

## Usage
   Add =[org.clojars.frozenlock/query "0.1.0"]= to your
   project dependencies.

```clojure

(ns my-ns
  (:require [query.core :as q]))

(q/to-query {:a 1 :b [3 4] :c {:d "hello"}})
 ;=> "?a=1&b%5B%5D=3&b%5B%5D=4&c%5B%5D=%7B%3Ad%20%22hello%22%7D"

(q/from-query "?a=1&b%5B%5D=3&b%5B%5D=4&c%5B%5D=%7B%3Ad%20%22hello%22%7D")
 ;=> {:a 1, :b [3 4], :c {:d "hello"}}

;; We can set the browser's current query (in the URL bar) with the
;; following:

(q/make-url-query {:some-key "hey, I'm some string"})
 ;=> nil
;; (but the browser now has a query in the URL bar: ?some-key="hey%2C%20I'm%20some%20string")

;; We can retrieve the current query:
(q/qquery)
 ;=> {:some-key "hey, I'm some string"}

;; To update the URL query, simply give it a map with the new info in it
(q/update-url-query {:new-info [1 2 3]})

;;now if we get the query again
(q/qquery)
 ;=> {:new-info [1 2 3], :some-key "hey, I'm some string"}

;; reset the query
(q/clear-url-query)


;; And finally to go to a new URL with a new query
(q/goto-href "/some/path" {:a "this is success" :b [1 2 3 4]})
;; of course if you try this in your REPL, the browser will go to the
;; new page and you'll loose your REPL...
```

## License

Copyright © 2013 Frozenlock

Distributed under the Eclipse Public License, the same as Clojure.
