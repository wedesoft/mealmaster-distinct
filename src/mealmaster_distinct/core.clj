(ns mealmaster-distinct.core
    (:require [clojure.set :refer (union)]
              [clojure.string :refer (join) :as string])
    (:gen-class))

(defn filenames
  "Get file names of input directory"
  ([] (filenames "input"))
  ([directory]
   (map #(str directory \/ %) (.list (clojure.java.io/file directory)))))

(defn lines
  "Read lines from a text file"
  [file]
  (line-seq file))

(defn filter-printable
  "Remove non-printable characters"
  [line]
  (apply str (re-seq #"[\r\n\t -\xFF]" line)))

(defn footer?
  "Check whether line is a Mealmaster footer"
  [line]
  (#{"MMMMM" "-----"} line))

(defn header?
  "Check whether line is a Mealmaster header"
  [line]
  (re-matches #"(-----|MMMMM)[^\r\n]*[Mm][Ee][Aa][Ll]-[Mm][Aa][Ss][Tt][Ee][Rr][^\r\n]*" line))

(defn section?
  "Check whether line is a section header"
  [line]
  (and
    (re-matches #"(MMMMM|-----)[^\r\n]+" line)
    (not (header? line))))

(defn extract-section
  "Extract section title"
  [line]
  (if (section? line)
    (-> line
        (string/replace #"^(MMMMM|-----)[- ]*" "")
        (string/replace #"[- ]*$" ""))
    nil))

(defn group-recipes
  "Group lines of recipes"
  ([lines] (:recipes (reduce group-recipes {:recipes []} lines)))
  ([state line]
   (if (:in-progress state)
     (if (footer? line)
       {:recipes (conj (:recipes state) (conj (:in-progress state) line))}
       (update state :in-progress #(conj % line)))
     (if (header? line)
       (assoc state :in-progress [line])
       state))))

(defn concat-lines
  "Concatenate lines into a recipe"
  [lines]
  (join "\r\n" lines))

(defn normalize-header
  "Replace headers with a unique one"
  [line]
  (if (header? line)
    "MMMMM-----Meal-Master-----"
    line))

(defn normalize-footer
  "Replace footers with a unique one"
  [line]
  (if (footer? line)
    "MMMMM"
    line))

(defn normalize-title
  "Align title tag"
  [line]
  (string/replace line #"^ *Title:" "      Title:"))

(defn normalize-categories
  "Align category tag"
  [line]
  (string/replace line #"^\ *Categories:" " Categories:"))

(defn normalize-yield
  "Align yield/servings"
  [line]
  (string/replace line #"^\ *(Yield|Servings):" "      Yield:"))

(defn build-section
  "Create a canonical section header"
  [section]
  (str "MMMMM-----" section "-----"))

(defn normalize-section
  "Normalize section header"
  [line]
  (or
    (some-> line
            extract-section
            build-section)
    line))

(defn canonical
  "Determine canonical form of line"
  [line]
  (-> line
      normalize-header
      normalize-footer
      normalize-title
      normalize-categories
      normalize-yield
      normalize-section))

(defn categorize
  "Create hash map from canonical recipe to recipe"
  [recipes]
  (reduce (fn [h recipe] (assoc h (concat-lines (map canonical recipe)) (concat-lines recipe))) {} recipes))

(defn read-recipes
  "Read a set of Mealmaster recipes from a text file"
  [filename]
  (.println *err* filename)
  (with-open [reader (clojure.java.io/reader filename :encoding "ISO-8859-15")]
    (categorize (group-recipes (map filter-printable (lines reader))))))

(defn -main
  "Main method to deduplicate recipe collection"
  [& _args]
  (with-open [writer (clojure.java.io/writer "output/recipes.mm" :encoding "ISO-8859-15")]
    (doseq [recipe (vals (apply merge (map read-recipes (filenames))))]
           (.write writer recipe)
           (.write writer "\r\n\r\n"))))
