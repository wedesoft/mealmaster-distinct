(ns mealmaster-distinct.t-core
    (:require [midje.sweet :refer :all]
              [mealmaster-distinct.core :refer :all]))

(fact "Get file names from input directory"
      (file-names "test/mealmaster_distinct/fixtures/file-names")
      => ["test/mealmaster_distinct/fixtures/file-names/testfile.txt"])

(fact "Read lines from a unix file"
      (lines "test/mealmaster_distinct/fixtures/lines/unix.txt")
      => ["first line" "second line"])
