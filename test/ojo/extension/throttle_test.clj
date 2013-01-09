(ns ojo.extension.throttle-test
  (:require [midje.sweet :refer :all]
            [ojo.extension.throttle :as base]
            [clojure.string :as str]
            [swiss-arrows.core :refer [-<>]])
  (:import [java.io File]))

(defn plat-ind-path [s]
  (-<> s (str/split <> #"/") (str/join File/separator <>)))

(let [file1 (plat-ind-path "resources/ojo_test/test_file.txt")
      file2 (plat-ind-path "resources/ojo_test/test_file_appended.txt")
      t1 (#'base/now)
      [t2 t3] [(+ 250 t1)
               (+ 1050 t1)]
      throttle-period 1000]
  (fact
   "last-event-state response that runs in :before-event"
   (base/last-event-state
    {:events [{:file file1 :kind :create}
              {:file file2 :kind :modify}]
     :state {file1 {:bit-position 0 :checksum 0}
             file2 {:bit-position 28 :checksum 3574348632}}
     :settings nil})
   =>
   {:events [{:file file1 :kind :create}
             {:file file2 :kind :modify}]
    :state {file1 {:bit-position 0 :checksum 0 :last-event t1}
            file2 {:bit-position 28 :checksum 3574348632 :last-event t1}}
    :settings nil}
   (provided (#'base/now) => t1))

  (fact
   "throttle-event response that runs in before-event: throttle out all"
   (base/throttle-event
    {:events [{:file file1 :kind :create}
             {:file file2 :kind :modify}]
     :state {file1 {:bit-position 0 :checksum 0 :last-event t1}
             file2 {:bit-position 28 :checksum 3574348632 :last-event t1}}
     :settings {:throttle-period throttle-period}})
   => nil
   (provided (#'base/now) => t2))


  (fact
   "throttle-event response that runs in before-event: all pass throttle"
   (base/throttle-event
    {:events [{:file file1 :kind :create}
              {:file file2 :kind :modify}]
     :state {file1 {:bit-position 0 :checksum 0 :last-event t1}
             file2 {:bit-position 28 :checksum 3574348632 :last-event t1}}
     :settings {:throttle-period throttle-period}})
   =>
   {:events [{:file file1 :kind :create}
             {:file file2 :kind :modify}]
    :state {file1 {:bit-position 0 :checksum 0 :last-event t1}
            file2 {:bit-position 28 :checksum 3574348632 :last-event t1}}
    :settings {:throttle-period throttle-period}}
   (provided (#'base/now) => t3)))

