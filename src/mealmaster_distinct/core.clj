(ns mealmaster-distinct.core)

(defn file-names [directory]
  "Get file names of input directory"
  (let [directory (or directory "input")]
    (map #(str directory \/ %) (.list (clojure.java.io/file directory)))))

(defn lines [filename]
  "Read lines from a text file"
  (line-seq (clojure.java.io/reader filename)))
