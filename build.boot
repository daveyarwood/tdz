(set-env!
  :source-paths #{"src"}
  :dependencies '[; dev
                  [adzerk/bootlaces         "0.1.13" :scope "test"]
                  [io.djy/boot-kotlin       "0.3.0"  :scope "test"]
                  [alandipert/boot-trinkets "2.0.0"  :scope "test"]

                  ; silence slf4j logging dammit
                  [org.slf4j/slf4j-nop              "1.8.0-beta1"]

                  ; app
                  [com.google.api-client/google-api-client           "1.23.0"]
                  [com.google.oauth-client/google-oauth-client-jetty "1.23.0"]
                  [com.google.apis/google-api-services-calendar      "v3-rev308-1.23.0"]
                  [com.beust/jcommander             "1.72"]
                  [com.jcabi/jcabi-manifests        "1.1"]])

(require '[adzerk.bootlaces         :refer :all]
         '[io.djy.boot-kotlin       :refer (kotlinc kotlin-repl)]
         '[alandipert.boot-trinkets :refer (run)])

(def ^:const +version+ "0.0.1")

(bootlaces! +version+)

(task-options!
 javac   {:options ["-source" "1.8"
                    "-target" "1.8"]}

  pom     {:project 'io.djy/tdz
           :version +version+
           :description "An Inbox Zero-oriented task management tool"
           :url "https://github.com/daveyarwood/tdz"
           :scm {:url "https://github.com/daveyarwood/tdz"}
           :license {"name" "Eclipse Public License"
                     "url" "https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt"}}

  jar     {:file     "tdz.jar"
           :manifest {"tdz-version" +version+}
           :main     'tdz.MainKt}

  target  {:dir #{"target"}})

(ns-unmap *ns* 'test)

(deftask main
  [a args ARGUMENTS [str] "string arguments to pass to the main method"]
  (comp (kotlinc) (run :main 'tdz.MainKt :args args)))

(deftask deploy
  "Builds jar file, installs it to local Maven repo, and deploys it to Clojars."
  []
  (comp (kotlinc) (build-jar) (push-release)))
