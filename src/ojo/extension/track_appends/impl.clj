(ns ^:impl ojo.extension.track-appends.impl
  (:require [fs.core :as fs]
            [clojure.java.io :as io])
  (:import [java.io InputStreamReader RandomAccessFile]
           java.util.zip.CRC32
           org.apache.commons.io.IOUtils))

(defn crc-32 [bytes]
  (.getValue (doto (CRC32.) (.update bytes))))

(defn file->bytes [path]
  (with-open [instr (io/input-stream path)] (IOUtils/toByteArray instr)))

(defn appended-only?
  "check the previously recorded checksum against the crc-32 checksum of the
   changed file truncated to previous bit-position."
  [{:keys [kind file] :as event}
   {:keys [bit-position checksum] :as earlier-state}]
  (and
   (<= bit-position (fs/size file))
   (let [bytes (byte-array bit-position)]
     (doto (java.io.RandomAccessFile. (fs/file file) "r")
       (.readFully bytes 0 bit-position)
       (.close))
     (= (crc-32 bytes) checksum))))
