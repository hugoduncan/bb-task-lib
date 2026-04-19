🎯 **bbum `z-splice-tasks` insertion order (config.clj)**

`z-splice-tasks` now inserts new bb.edn tasks in order rather than always appending.  Key rules:
- **Globally sorted tasks** → insert at correct alphabetical position (`{:after last-smaller}` or `{:before first-larger}`)
- **Unsorted + colon in name** (`bar:fix`) → group with tasks sharing the same prefix (`bar:*`), in local alpha order
- **Otherwise** → append at end

**Critical gotcha**: `z/insert-left` and `z/insert-right` auto-inject padding whitespace around every
inserted node.  Do NOT use them for structural insertions.  The working pattern is:
1. `(z-map-entries-ordered (z/of-string map-str))` → extract `[[key-sexpr value-str] …]`
2. Splice new entry at computed `pos`
3. Rebuild map string via `(str "{" (join "" (map #(str "\n" indent k "\n" indent v) entries)) "}")`
4. `(z/replace map-zloc (z/node (z/of-string new-map-str)))` to put it back

Helper chain: `task-insertion-point` → `names-sorted?` / `prefix-insertion-point` → `z-insert-map-entry`.
Commit: `c73a534` in `/Users/duncan/projects/hugoduncan/bbum/bbum-master`.
