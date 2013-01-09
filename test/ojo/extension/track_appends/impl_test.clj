(ns ojo.extension.track-appends.impl-test
  (:require [midje.sweet :refer :all]
            [clojure.java.io :as io]
            [fs.core :as fs]
            [clojure.string :as str]
            [ojo.extension.track-appends.impl :as base]
            [swiss-arrows.core :refer [-<>]])
  (:import [java.io File]))

(defn plat-ind-path [s]
  (-<> s (str/split <> #"/") (str/join File/separator <>)))

(let [file1 (plat-ind-path "resources/ojo_test/test_file.txt")
      file2 (plat-ind-path "resources/ojo_test/test_file_appended.txt")
      file3 (plat-ind-path "resources/ojo_test/test_file_modified.txt")
      file4 (plat-ind-path "resources/ojo_test/test_file_reduced.txt")
      size (fs/size file1)
      bytes (base/file->bytes file1)
      checksum (base/crc-32 bytes)]

  (facts
   (type (first bytes)) => java.lang.Byte
   (count bytes) => 27
   (count bytes) => size
   checksum => 2245143621)

  (fact
   "basic appended only"
   (base/appended-only?
    {:file file1 :kind :modify}
    {:bit-position size :checksum checksum})
   => true)

  (fact
   "file with a new line added"
   (base/appended-only?
    {:file file2 :kind :modify}
    {:bit-position size :checksum checksum})
   => true)

  (fact
   "file with a change made before the bit-position"
   (base/appended-only?
    {:file file3 :kind :modify}
    {:bit-position size :checksum checksum})
   => false)

  (fact
   "file with a change made before the bit-position (decrease number of bytes)"
   (base/appended-only?
    {:file file4 :kind :modify}
    {:bit-position size :checksum checksum})
   => false))
