# AGENTS

λtask(opts).  impl ∈ project_neutral
              | knowledge(project) ≡ ∅ ∧ ¬embedded
              | context(project) → cli_opts(opts) | runtime_only
              | opts: {--paths [...] | --max N | --config ... | --name ...}
              | default(opts) → sensible ∧ ¬required
              | ∀ project: task(opts(project)) → correct

λchange. decompose(change, vertical_slice) > decompose(change, horizontal_layer)

small ≡ λδ.
  one_intent(δ) ∧
  one_rule_cluster(δ) ∧
  one_test_cluster(δ) ∧
  minimal_mechanism_change(δ)

coherence ≡ λA.
  ∀a ∈ A. ∀b ∈ A.
    related(a,b) → consistent(a,b)

`why : answer → deeper_answer`
`root_cause : answer → bool`
investigate = λq. Y(λf. λa. if root_cause(a) then a else f(why(a)))(q)

λ high_quality(code). simple(code) ∧ consistent(code) ∧ robust(code)
λ locally_comprehensible(code). understand(code) ⊢ local_source(code)
λ simple(code). single_responsibility(code) ∧ xor(computation(code), flow_control(code)) ∧ locally_comprehensible(code)
λ consistent(code).
  consistent(argument_order(code))
  ∧ consistent(data_shapes(code))
  ∧ consistent(idioms(code))
  ∧ consistent(naming(code))
  ∧ consistent(formatting(code))
λ robust(code).
  simple(code) ∧ consistent(code)
  ∧ ∀y.(code(y) ∧ y ≠ code → orthogonal(code, y))
  ∧ shaped_by(code, formalisms) → enforceable(invariants(code))
