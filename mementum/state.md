# State

## Current Focus
Branch `add-tasks` — adding new babashka tasks to `hugoduncan/bb-task-lib`.

## Project Snapshot
- **Lib:** `hugoduncan/bb-task-lib` — babashka task library installed via `bbum`
- **Manifest:** `bbum.edn` — defines `:lib` and `:tasks` map; each task has `:doc`, `:files`, `:task`
- **Src:** `src/hugoduncan/bb_task_lib/`
- **Existing tasks:** `:ref-report` (clojure-lsp public fn visibility), `:file-lengths` (line-count CI gate)

## Task Shape
```edn
:task-name
{:doc   "One-liner description"
 :files ["src/hugoduncan/bb_task_lib/task_name.clj"]
 :task  {:doc      "..."
         :requires ([hugoduncan.bb-task-lib.task-name :as task-name])
         :task     (task-name/run)}}
```

## Status
`:clj-kondo` task added and registered.

## Completed Tasks
- `:clj-kondo` — `src/hugoduncan/bb_task_lib/clj_kondo.clj`
  - Default: lints `src/` + `test/` with `--repro`
  - `--src` / `--test` flags restrict scope; `--src-paths` / `--test-paths` override dirs
  - Positional args → lint exactly those paths
  - `--parallel`, `--fail-level` forwarded to clj-kondo
  - `--copy-configs [--classpath CP]` → init mode (`--skip-lint`); derives CP via `clojure -Spath` if not given
  - Exit code propagated from clj-kondo
