(defproject scs "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [rm-hull/jasentaa "0.2.5"]
                 [org.babashka/sci "0.2.9-SNAPSHOT"]
                 [instaparse/instaparse "1.4.10"]]
  :aot :all
  :source-paths ["src/main/clojure"]
  :java-source-paths ["src/main/java"]
  :resource-paths ["src/main/resources"]
  :javac-options ["-target" "1.8" "-source" "1.8" "-Xlint:-options"]
  :target ["target"])
