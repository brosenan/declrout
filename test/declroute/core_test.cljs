(ns declroute.core-test
  (:require [cljs.test :refer-macros [is testing deftest]]
            [declroute.core :as dr]
            [reagent-query.core :as rq])
  (:import goog.History))


;; Defining a mapping between a component function and a page URI is
;; done using the set-page-uri function
(defn foo []
  [:div "foo"])
(dr/set-page-uri foo "foo")

(defn bar [x y z ]
  [:div "bar"])
(dr/set-page-uri bar "bar")

(defn default []
  [:div "Default page"])
(dr/set-page-uri default :default)

;; The parse-hash function takes a uri from the current URL,
;; and converts it into a hiccup-like vector, with a function as the
;; first argument and string arguments. The string arguments are
;; derived from a splitting of the given string on "/", and performing
;; decodeURI() on the components.
;; 
;; The function is converted from the first component in the given
;; string, as defined by set-page-uri.
(deftest parse-hash-1
  ;; A simple string is converted to a function by consulting the map
  (is (= (dr/parse-hash "foo") [foo]))
  ;; If the given string contains slashes, the additional components
  ;; are provided as string arguments.
  (is (= (dr/parse-hash "foo/a/b/c") [foo "a" "b" "c"]))
  ;; The string's components go through URI decoding so that they can
  ;; contain any character, including "/".
  (is (= (dr/parse-hash "foo/%2F/%2B/!") [foo "/" "+" "!"]))
  ;; If the uri does not match any of the defined pages, the :default
  ;; page is consulted.
  (is (= (dr/parse-hash "something-that-does-not-exist") [default]))
  )


;; The build-uri function takes a hiccup-like vector, and returns a
;; hash-URI such that (with the hash removed) parse-hash would yield
;; the same vector
(deftest build-uri-1
  ;; A vector containing only a function maps into the associated URI
  (is (= (dr/build-uri [foo]) "#foo"))
  ;; Additional parameters are added as path components
  (is (= (dr/build-uri [foo "a" "b" "c"]) "#foo/a/b/c"))
  ;; The parameters are URI-encoded to allow special characters to be used.
  (is (= (dr/build-uri [foo "/" "+" "!"]) "#foo/%2F/%2B/!")))

