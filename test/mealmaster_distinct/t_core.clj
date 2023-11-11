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

(facts "Extract section title"
       (extract-section "abc") => nil
       (extract-section "MMMMM----- main -----") => "main"
       (extract-section "MMMMM----- Recipe via Meal-Master (tm) v8.01") => nil
       (extract-section "MMMMM") => nil
       (extract-section "---------- main -----") => "main")

(def header "MMMMM----- Recipe via Meal-Master (tm) v8.01")
(def content "Test content")
(def footer "MMMMM")
(def recipe-lines [header content footer])
(def recipe (str header "\r\n" content "\r\n" footer))
(def normalized (str "MMMMM-----Meal-Master-----" "\r\n" content "\r\n" footer))

(facts "Join lines of recipes"
       (group-recipes []) => []
       (group-recipes [header]) => []
       (group-recipes [header content footer]) => [recipe-lines]
       (group-recipes [content header content footer content]) => [recipe-lines])

(fact "Concatenate recipe lines"
       (concat-lines recipe-lines) => recipe)

(fact "Normalize line of recipe for improved deduplication"
      (canonical "Test text") => "Test text"
      (canonical "  Test text  ") => "Test text"
      (canonical "MMMMM----- Recipe via Meal-Master (tm) v8.01") => "MMMMM-----Meal-Master-----"
      (canonical "-----") => "MMMMM"
      (canonical " Title: recipe name") => "Title: recipe name"
      (canonical "The Title: recipe name") => "The Title: recipe name"
      (canonical "  Categories: Beverages, Coffee") => "Categories: Beverages, Coffee"
      (canonical "Has  Categories: Beverages, Coffee") => "Has  Categories: Beverages, Coffee"
      (canonical "  Yield: 2 servings") => "Yield: 2 servings"
      (canonical "The Yield: 2 servings") => "The Yield: 2 servings"
      (canonical " Servings: 2 servings") => "Yield: 2 servings"
      (canonical "-------- main ---") => "MMMMM-----main-----")

(fact "Read in a set of recipes"
      (read-recipes "test/mealmaster_distinct/fixtures/read-recipes.txt") => {normalized recipe})
