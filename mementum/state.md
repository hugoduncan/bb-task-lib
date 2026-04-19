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
`:gordian`, `:clj-kondo`, `:cljfmt` tasks added and registered.
`bbum` itself updated: `z-splice-tasks` now inserts tasks in alphabetical order (or grouped by prefix).
The bbum commit is in `/Users/duncan/projects/hugoduncan/bbum/bbum-master` on branch `bbum-master`.

## Completed Tasks
- `:gordian` — `src/hugoduncan/bb_task_lib/gordian.clj`
  - **Pure passthrough** — all args forwarded directly to gordian CLI
  - Auto-installs gordian via `bbin install io.github.hugoduncan/gordian` if not on PATH
  - Post-install fallback: resolves exe via `bbin bin` dir (PATH is fixed at process start)
  - Uses `proc/exec` (replaces process) — gordian's exit code propagates directly; critical for `gate` CI command
  - `bbin` assumed installed; task only ensures _gordian_ is present


- `:clj-kondo` — `src/hugoduncan/bb_task_lib/clj_kondo.clj`
  - Default: lints `src/` + `test/` with `--repro`
  - `--src` / `--test` flags restrict scope; `--src-paths` / `--test-paths` override dirs
  - Positional args → lint exactly those paths
  - `--parallel`, `--fail-level` forwarded to clj-kondo
  - `--copy-configs [--classpath CP]` → init mode (`--skip-lint`); derives CP via `clojure -Spath` if not given
  - Exit code propagated from clj-kondo

- `:cljfmt` — `src/hugoduncan/bb_task_lib/cljfmt.clj`
  - Default subcommand: `check` (non-destructive); `fix` requires explicit intent
  - Default paths: `src/` + `test/`, filtered with `fs/exists?`
  - `--src` / `--test` restrict to one default set; positional args override entirely
  - No `--src-paths`/`--test-paths` — positional args cover that case (simpler API)
  - `--parallel`, `--config FILE` forwarded to cljfmt
  - First non-flag arg consumed as subcommand if it is `check` or `fix`; otherwise treated as a path
  - Exit code propagated from cljfmt
