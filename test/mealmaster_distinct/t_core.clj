(ns mealmaster-distinct.t-core
    (:require [midje.sweet :refer :all]
              [mealmaster-distinct.core :refer :all]))

(fact "Get file names from input directory"
      (file-names "test/mealmaster_distinct/fixtures/file-names")
      => ["test/mealmaster_distinct/fixtures/file-names/testfile.txt"])

(fact "Read lines from a Unix file"
      (lines (clojure.java.io/reader (char-array "first line\nsecond line")))
      => ["first line" "second line"])

(fact "Read lines from a DOS file"
      (lines (clojure.java.io/reader (char-array "first line\r\nsecond line")))
      => ["first line" "second line"])
