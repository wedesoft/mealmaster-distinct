(ns mealmaster-distinct.t-core
    (:require [midje.sweet :refer :all]
              [mealmaster-distinct.core :refer :all]))

(fact "Get file names from input directory"
      (filenames "test/mealmaster_distinct/fixtures/file-names")
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
      (filter-printable "text\r\nmore\ttext \007\377 finish") => "text\r\nmore\ttext \377 finish"
      (filter-printable "äöü") => "äöü")

(facts "Check for footer"
       (footer? "abc") => falsey
       (footer? "MMMMM") => truthy
       (footer? "-----") => truthy)

(facts "Check for header"
       (header? "abc") => falsey
       (header? "MMMMM----- Recipe via Meal-Master (tm) v8.01") => truthy
       (header? "abc ---------- Recipe via Meal-Master (tm) v8.01") => falsey
       (header? "---------- Recipe via Meal-Master (tm) v8.01") => truthy
       (header? "---------- MEAL-MASTER ----------") => truthy
       (header? "---------- Recipe Software") => falsey)

(def header "MMMMM----- Recipe via Meal-Master (tm) v8.01")
(def content "Test content")
(def footer "MMMMM")
(def recipe (str header "\r\n" content "\r\n" footer "\r\n\r\n"))

(facts "Join lines of recipes"
       (join-recipes []) => #{}
       (join-recipes [header]) => #{}
       (join-recipes [header content footer]) => #{recipe}
       (join-recipes [content header content footer content]) => #{recipe})

(fact "Read in a set of recipes"
      (read-recipes "test/mealmaster_distinct/fixtures/read-recipes.txt") => #{recipe})
