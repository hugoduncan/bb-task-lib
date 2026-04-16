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
No new tasks added yet. Awaiting direction.
