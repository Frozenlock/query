# query

![Query](./QuestionA.jpg)

# Changelog
	* Version 0.2.0
	  - Majority of functions renamed and/or rewritten

Helper functions to manipulate the URL query as a clojure map.

*Supports nested structures!*

That's right, you can use a map, inside a map, inside a map.... (Only
the first level is converted to a normal query, everything else is
converted to a string)

## Usage
   Add `[org.clojars.frozenlock/query "0.2.1"]` to your
   project dependencies.

```clj

(ns my-ns
  (:require [query.core :as q]))

(q/to-query {:a 1 :b [3 4] :c {:d "hello"}})
 ;=> "?a=1&b%5B%5D=3&b%5B%5D=4&c%5B%5D=%7B%3Ad%20%22hello%22%7D"

(q/from-query "?a=1&b%5B%5D=3&b%5B%5D=4&c%5B%5D=%7B%3Ad%20%22hello%22%7D")
 ;=> {:a 1, :b [3 4], :c {:d "hello"}}

;; We can set the browser's current query (in the URL bar) with the
;; following:

(q/set-query! {:some-key "hey, I'm some string"})
 ;=> nil
;; (but the browser now has a query in the URL bar: ?some-key="hey%2C%20I'm%20some%20string")

;; We can retrieve the current query:
(q/get-query)
 ;=> {:some-key "hey, I'm some string"}

;; To update the URL query, simply give it a map with the new info in it
(q/set-query! {:new-info [1 2 3]})

;;now if we get the query again
(q/get-query)
 ;=> {:new-info [1 2 3], :some-key "hey, I'm some string"}

;; reset the query
(q/clear-query!)


	;; And finally to go to a new URL with a new query
(q/goto-href! "/some/path" {:a "this is success" :b [1 2 3 4]})
;; of course if you try this in your REPL, the browser will go to the
;; new page and you'll loose your REPL...

```

## Testing
Run `lein browser-test` and check the console in the opened page.

## License

Copyright © 2014 Frozenlock

Distributed under the Eclipse Public License, the same as Clojure.
