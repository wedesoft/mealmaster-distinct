(ns mealmaster-distinct.core)

(defn file-names
  "Get file names of input directory"
  [directory]
  (let [directory (or directory "input")]
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

(defn join-recipes
  "Combine lines into recipes"
  ([lines] (:recipes (reduce join-recipes {:recipes #{}} lines)))
  ([state line]
   (if (:in-progress state)
     (if (footer? line)
       {:recipes (conj (:recipes state) (str (:in-progress state) line "\r\n"))}
       (update state :in-progress #(str % line "\r\n")))
     (if (header? line)
       (assoc state :in-progress (str line "\r\n"))
       state))))
