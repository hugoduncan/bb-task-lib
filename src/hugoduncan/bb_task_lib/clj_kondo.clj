(ns hugoduncan.bb-task-lib.clj-kondo
  "Lint Clojure source files using the clj-kondo command-line tool.

  Usage: bb clj-kondo [options] [paths...]

  By default lints src/ and test/.  Use --src or --test to restrict to one
  set.  Positional path arguments override --src/--test and lint exactly those
  paths.

  Always passes --repro so home-directory configuration is ignored.
  Requires clj-kondo to be installed and on the PATH.

  Lint options:
    --src              Lint source paths only
    --test             Lint test paths only
    --parallel         Lint sources in parallel
    --fail-level LEVEL Minimum severity for non-zero exit: warning|error

  Init mode (copies exported configs from deps, does not lint project sources):
    --copy-configs     Copy configs from classpath dependencies
    --classpath CP     Classpath for --copy-configs (default: clojure -Spath)"
  (:require [babashka.fs      :as fs]
            [babashka.process :as proc]
            [clojure.string   :as str]))

(defn- parse-opts [args]
  (loop [args          args
         src?          false
         test?         false
         parallel?     false
         fail-level    nil
         copy-configs? false
         classpath     nil
         paths         []]
    (if-let [arg (first args)]
      (cond
        (= arg "--src")
        (recur (next args) true test? parallel? fail-level copy-configs? classpath paths)

        (= arg "--test")
        (recur (next args) src? true parallel? fail-level copy-configs? classpath paths)

        (= arg "--parallel")
        (recur (next args) src? test? true fail-level copy-configs? classpath paths)

        (= arg "--fail-level")
        (if-let [v (second args)]
          (recur (nnext args) src? test? parallel? v copy-configs? classpath paths)
          (throw (ex-info "--fail-level requires an argument" {:args args})))

        (= arg "--copy-configs")
        (recur (next args) src? test? parallel? fail-level true classpath paths)

        (= arg "--classpath")
        (if-let [v (second args)]
          (recur (nnext args) src? test? parallel? fail-level copy-configs? v paths)
          (throw (ex-info "--classpath requires an argument" {:args args})))

        :else
        (recur (next args) src? test? parallel? fail-level copy-configs? classpath (conj paths arg)))
      {:src?          src?
       :test?         test?
       :parallel?     parallel?
       :fail-level    fail-level
       :copy-configs? copy-configs?
       :classpath     classpath
       :paths         paths})))

(defn- derive-classpath []
  (let [{:keys [exit out err]} (proc/sh "clojure" "-Spath")]
    (when-not (zero? exit)
      (throw (ex-info "Failed to derive classpath via clojure -Spath" {:err err})))
    (str/trim out)))

(defn- lint-paths [{:keys [src? test? paths]}]
  (cond
    (seq paths)      paths
    (and src? test?) ["src" "test"]
    src?             ["src"]
    test?            ["test"]
    :else            ["src" "test"]))

(defn run []
  (let [{:keys [parallel? fail-level copy-configs? classpath] :as opts}
        (parse-opts *command-line-args*)]
    (if copy-configs?
      (let [cp   (or classpath (derive-classpath))
            argv ["clj-kondo" "--repro"
                  "--copy-configs" "--dependencies" "--skip-lint"
                  "--lint" cp]]
        (System/exit (:exit (apply proc/shell {:continue true} argv))))
      (let [paths (filterv #(fs/exists? %) (lint-paths opts))]
        (when (empty? paths)
          (println "No paths to lint.")
          (System/exit 0))
        (let [argv (cond-> (into ["clj-kondo" "--repro" "--lint"] paths)
                     parallel?  (conj "--parallel")
                     fail-level (conj "--fail-level" fail-level))]
          (System/exit (:exit (apply proc/shell {:continue true} argv))))))))