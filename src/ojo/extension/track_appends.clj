(ns ojo.extension.track-appends
  "An ojo extension for tracking append versus non-append file modifications."
  (:require [ojo.extension.track-appends.impl :refer :all]
            [ojo.respond :refer :all]
            [fs.core :as fs]
            [mostly-useful.core :refer [assoc-keep]]))

(defresponse event-info
  "add :appended-only? to :modify events when the file has only been appended"
  {:events (map
            (fn [{:keys [kind file] :as event}]
              (if-not (:bit-position (*state* file))
                event
                (let [{:keys [bit-position] :as file-state} (*state* file)]
                  (assoc-keep event
                    :bit-position bit-position
                    :appended-only? (when bit-position
                                      (appended-only? event file-state))))))
            *events*)})

(defresponse update-file-info
  "add bit position and checksum info to file state for each of the files"
  {:state (reduce
           (fn [r {file :file}]
             (update-in r [file] #(assoc %
                                    :bit-position (fs/size file)
                                    :bit-difference (- (fs/size file)
                                                       (or
                                                        (:bit-position
                                                         (*state* file)) 0))
                                    :checksum (crc-32 (file->bytes file)))))
           *state*
           *events*)})

(def track-appends
  {:before-response event-info
   :after-response update-file-info})
