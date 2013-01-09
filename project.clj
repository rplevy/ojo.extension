(defproject ojo.extension "1.0.0"
  :description "Extensions for ojo file-watching library"
  :url "https://clojars.org/ojo.extension"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ojo "1.0.0"]
                 [swiss-arrows "0.5.1"]
                 [mostly-useful "0.5.0"]
                 [commons-io/commons-io "2.3"]
                 [fs "1.2.0"]]
  :profiles {:dev {:dependencies [[midje "1.5-alpha6"]]}}
  :plugins [[lein-cucumber "1.0.1"]
            [lein-midje "3.0-alpha2"]])
