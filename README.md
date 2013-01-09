# ojo.extension

Extensions for ojo file-watching library.

* throttle: An ojo extension for limiting immediate consecutive events for any one file. The motivation for adding this is that often the filesystem will issue multiple consecutive events for one user-relevant event. Throttling allows you to respond to a meaningful event, ignoring the extraneous events.

  Throttling is a generally a useful behavior to have, however it is implemented as an extension so that it can be excluded in cases where it is not.

* track-appends: An ojo extension for tracking append versus non-append file modifications. Provides information to the body of the response on whether or not a file modification is append-only, as well as a crc checksum of the file, the current bit position, and the difference in position from the last event.

## Dependency Coordinates

https://clojars.org/ojo.extension

## Usage

```clojure
    (defwatch dat-file-watcher
      [dat-dir [["*fooBar*.csv" #"^\S+$"]]] [:create :modify]
      {:parallel true
       :worker-poll-ms (config/value :watcher :worker-poll-ms)
       :worker-count (config/value| :watcher :worker-count)
       :extensions [throttle track-appends redis]
       :settings {:throttle-period (config/value :watcher :throttle-period)}}
      ... )
```

## License

Author: Robert Levy / @rplevy-draker

Copyright © 2013 Draker

Distributed under the Eclipse Public License, the same as Clojure.
