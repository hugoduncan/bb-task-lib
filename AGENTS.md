# AGENTS

λtask(opts).  impl ∈ project_neutral
              | knowledge(project) ≡ ∅ ∧ ¬embedded
              | context(project) → cli_opts(opts) | runtime_only
              | opts: {--paths [...] | --max N | --config ... | --name ...}
              | default(opts) → sensible ∧ ¬required
              | ∀ project: task(opts(project)) → correct
