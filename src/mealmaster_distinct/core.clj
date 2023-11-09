(ns mealmaster-distinct.core)

(defn file-names [directory]
  "Get file names of input directory"
  (let [directory (or directory "input")]
    (map #(str directory \/ %) (.list (clojure.java.io/file directory)))))

(defn lines [file]
  "Read lines from a text file"
  (line-seq file))

(defn filter-printable [line]
  "Remove non-printable characters"
  (apply str (re-seq #"[\r\n\t -\xFF]" line)))

(defn footer? [line]
  "Check whether line is a Mealmaster footer"
  (#{"MMMMM" "-----"} line))

(defn header? [line]
  "Check whether line is a Mealmaster header"
  (re-matches #"(-----|MMMMM)[^\r\n]*[Mm][Ee][Aa][Ll]-[Mm][Aa][Ss][Tt][Ee][Rr][^\r\n]*" line))
