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

(fact "Read past zeroes"
      (lines (clojure.java.io/reader (char-array "text\000more text")))
      => ["text\000more text"])

(fact "Filter for printable characters"
      (filter-printable "text\r\nmore\ttext \007\377 finish") => "text\r\nmore\ttext \377 finish")

(facts "Check for footer"
       (footer? "abc") => falsey
       (footer? "MMMMM") => truthy
       (footer? "-----") => truthy)

(facts "Check for header"
       (header? "abc") => falsey
       (header? "MMMMM----- Meal-Master-Tools fuer Windows V0.1") => truthy
       (header? "abc MMMMM----- Meal-Master-Tools fuer Windows V0.1") => falsey
       (header? "---------- Meal-Master-Tools fuer Windows V0.1") => truthy
       (header? "---------- Recipe Software") => falsey)
