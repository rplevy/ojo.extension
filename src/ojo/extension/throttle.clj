(ns ojo.extension.throttle
  "An ojo extension for limiting immediate consecutive events for any one file.
   The motivation for adding this behavior is that often the filesystem will
   issue multiple consecutive events for one user-relevant event. Throttling
   enables responding to a meaningful event, ignoring the extraneous events."
  (:require [ojo.respond :refer :all]))

(defn ^:private now [] (System/currentTimeMillis))

(defresponse last-event-state
  {:state (reduce
           (fn [new-state {file :file}]
             (update-in new-state [file]
                        #(assoc % :last-event (now))))
           *state*
           *events*)})

(defresponse throttle-event
  {:events (reduce
            (fn [result event]
              (or
               (when-let [last-event (:last-event (*state* (:file event)))]
                 (when (> last-event (- (now) (*settings* :throttle-period)))
                   result))
               (conj result event)))
            []
            *events*)})

(def throttle
  {:before-event (comp last-event-state throttle-event)})

