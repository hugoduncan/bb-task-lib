(ns hugoduncan.bb-task-lib.cljfmt
  "Check or fix Clojure formatting using the cljfmt command-line tool.

  Usage: bb cljfmt [check|fix] [options] [paths...]

  Subcommands (default: check):
    check   Report formatting errors; exits non-zero if any file differs (CI-safe)
    fix     Rewrite files in place to correct formatting errors

  By default runs on src/ and test/.  Use --src or --test to restrict to one
  set.  Positional path arguments override --src/--test and format exactly those
  paths.

  Requires cljfmt to be installed and on PATH.
  Configuration is auto-discovered from .cljfmt.edn; use --config to override.

  Options:
    --src          Use source paths only
    --test         Use test paths only
    --parallel     Process files in parallel
    --config FILE  Path to cljfmt config file (default: auto-discovered .cljfmt.edn)"
  (:require [babashka.fs      :as fs]
            [babashka.process :as proc]))

(def ^:private subcommands #{"check" "fix"})

(defn- parse-opts [args]
  (loop [args      args
         cmd       nil
         src?      false
         test?     false
         parallel? false
         config    nil
         paths     []]
    (if-let [arg (first args)]
      (cond
        (and (nil? cmd) (subcommands arg))
        (recur (next args) arg src? test? parallel? config paths)

        (= arg "--src")
        (recur (next args) cmd true test? parallel? config paths)

        (= arg "--test")
        (recur (next args) cmd src? true parallel? config paths)

        (= arg "--parallel")
        (recur (next args) cmd src? test? true config paths)

        (= arg "--config")
        (if-let [v (second args)]
          (recur (nnext args) cmd src? test? parallel? v paths)
          (throw (ex-info "--config requires an argument" {:args args})))

        :else
        (recur (next args) cmd src? test? parallel? config (conj paths arg)))
      {:cmd       (or cmd "check")
       :src?      src?
       :test?     test?
       :parallel? parallel?
       :config    config
       :paths     paths})))

(defn- format-paths [{:keys [src? test? paths]}]
  (cond
    (seq paths)      paths
    (and src? test?) ["src" "test"]
    src?             ["src"]
    test?            ["test"]
    :else            ["src" "test"]))

(defn run []
  (let [{:keys [cmd parallel? config] :as opts} (parse-opts *command-line-args*)
        paths (filterv #(fs/exists? %) (format-paths opts))]
    (when (empty? paths)
      (println "No paths to format.")
      (System/exit 0))
    (let [argv (cond-> ["cljfmt" cmd]
                 parallel? (conj "--parallel")
                 config    (conj "--config" config)
                 true      (into paths))]
      (System/exit (:exit (apply proc/shell {:continue true} argv))))))
