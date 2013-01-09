(ns ojo.extension.track-appends-test
  (:require [midje.sweet :refer :all]
            [ojo.extension.track-appends :as base]
            [clojure.string :as str]
            [swiss-arrows.core :refer [-<>]])
  (:import [java.io File]))

(defn plat-ind-path [s]
  (-<> s (str/split <> #"/") (str/join File/separator <>)))

(let [file1 (plat-ind-path "resources/ojo_test/test_file.txt")
      file2 (plat-ind-path "resources/ojo_test/test_file_appended.txt")]

  (fact
   "event-info response that runs in :before-response"
   (base/event-info
    {:events [{:file file1 :kind :create}
              {:file file2 :kind :modify}]
     :state {file1 {}
             file2 {:bit-position 27 :checksum 2245143621}}
     :settings nil})
   =>
   {:events [{:file file1 :kind :create}
             {:file file2 :kind :modify
              :appended-only? true :bit-position 27}]
    :state {file1 {}
            file2 {:bit-position 27 :checksum 2245143621}}
    :settings nil})

  (fact
   "update-file-info response that runs in :init and :after-response"
   (base/update-file-info
    {:events [{:file file1 :kind :create}
              {:file file2 :kind :modify
               :appended-only? true :bit-position 0}]
     :state {file1 {:bit-position 0  :checksum 0}
             file2 {:bit-position 27 :checksum 2245143621}}
     :settings nil})
   =>
   {:events [{:file file1 :kind :create}
             {:file file2 :kind :modify
              :appended-only? true :bit-position 0}]
    :state {file1 {:bit-position 27 :bit-difference 27 :checksum 2245143621}
            file2 {:bit-position 35 :bit-difference 8 :checksum 706879945}}
    :settings nil}))
