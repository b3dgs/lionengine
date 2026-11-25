# ADR-0053 — Local agents: escalation threshold, branch hand-off and merge gate

- Status: ACCEPTED
- Date: 2026-11-25
- Decision makers: project owner, who delegated the choice to the agent ("review and choose the best for the open questions in packet #910", 2026-11-25, answering `OQ-171`, `OQ-172` and `OQ-173`)
- Related requirements: `REQ-AGENT-001`, `REQ-AGENT-002`, `REQ-AGENT-004`
- Open questions resolved: `OQ-171`, `OQ-172`, `OQ-173`; `OQ-170` is narrowed (benchmark and pass criterion fixed here, results still to be measured on the owner's machines)
- Supersedes / superseded by: none; completes decisions 3B and 4A of issue #910 ([`../local-agents.md`](../local-agents.md)) and the auto-merge rules of [`../change-process.md`](../change-process.md)

## Context

Issue #910 set up the delegation of well-specified work to local models (decisions 1A, 2C, 3B, 4A, 5A) and left four questions open before the first local run:

- `OQ-170`: which model runs on which machine, to be confirmed by a benchmark task;
- `OQ-171`: how many failed gate runs before a local task returns to a cloud agent;
- `OQ-172`: whether the worker opens pull requests itself, and the signing key, since the ruleset of `rewrite/v11` requires signed commits;
- `OQ-173`: how a local-agent pull request is marked, who counts as a cloud review, and who merges after the approval.

Facts checked: the auto-merge workflow only squash-merges, so the commit that reaches `rewrite/v11` is the squash commit, signed by GitHub, whatever the commits of the branch; a label added by a person or by an app starts a `pull_request` `labeled` event, a label added with the workflow token does not; the starting profile of PC3 named "Qwen3-Coder 8B", which is not a published model (the Qwen3-Coder open models are 30B-A3B and larger).

## Decision

1. **Escalation (`OQ-171`).** A local task runs at most **3 gate runs**: the worker runs, then the gate (the acceptance commands of the task file and the four structural checks); a failed gate gives the worker its log and one more run. After the third failed gate run, or when the worker changes a file outside the scope of the task file, the task is escalated: it returns to a cloud agent with the branch and the report, and is not retried locally. Each worker run is also limited to 60 turns. Both limits are constants of `scripts/local-task.sh`, not task-file fields.
2. **Branch hand-off (`OQ-172`).** The worker never pushes and never opens a pull request; it holds no GitHub credential and no signing key. It hands the branch `local/<slug>` and the report of `scripts/local-task.sh` to Claude Code. Claude Code (on the owner's machine, with a cloud model) reviews it, pushes the same branch name and opens the pull request into `rewrite/v11`. The commits of the branch need no signature: only the squash commit reaches `rewrite/v11`, and GitHub signs it.
3. **Merge gate (`OQ-173`).**
   - **Marking**: the branch prefix `local/` alone, kept from the worker to the pull request. Local work is never re-pushed under `claude/` or `packet/`; the auto-merge workflow does not match a `local/` branch on its own.
   - **Cloud review**: a review by Claude Code running a cloud model, or by the owner, by an agent that did not write the change. It reads the diff, the task file, the batch page and the report, and checks the scope, the stop conditions and the rules of [`../local-agent-entry.md`](../local-agent-entry.md). It records its approval by a review comment and by adding the label `cloud-reviewed` to the pull request, from an allow-listed account (`claude[bot]`, `DjThunder`, `b3dgs`). A review that finds a problem does not add the label and asks for a fix or escalates the task.
   - **Merge**: the auto-merge workflow merges a `local/` pull request on the `labeled` event of `cloud-reviewed`, with the same checks as the other agent pull requests (documentation check, build and tests on the merge result, wait for the `sonar` and `android` checks of the head commit, squash merge). Any push to a `local/` pull request removes the label, so an approval always covers the head commit and a new review is needed.
4. **Benchmark (`OQ-170`, narrowed).** The benchmark is the task [`../local-tasks/benchmark-random-example.md`](../local-tasks/benchmark-random-example.md): it removes `RandomExample` and its test in the worktree and asks the worker to recreate them from the API index and the Javadoc. Each machine runs it 3 times; its model is confirmed when at least 2 runs end with the verdict `PASSED` (within 3 gate runs, no file out of scope) in under 30 minutes each; otherwise its fallback model is benchmarked the same way. PC3 runs the single-shot variant and is confirmed when 2 of 3 answers, applied unchanged by Claude Code, pass the same gate. The starting model of PC3 is Qwen3.5-9B (4-bit) instead of the unpublished "Qwen3-Coder 8B". `OQ-170` stays open until the results are recorded in [`../local-agents.md`](../local-agents.md).

## Options considered

- Escalation after 3 gate runs (chosen): one first try and two corrections cover a compile error and a failing test; beyond that, small models tend to loop on the same error and a cloud agent is cheaper than more local hours.
- Escalation after 1 or 2 runs (rejected): wastes the cheap local retries on trivial errors such as a missing import.
- A threshold per task file (rejected): every task would need a value nobody can choose well before the benchmark.
- Worker pushes and opens the pull request (rejected): every local machine would need a GitHub token and a signing key, and an unreviewed branch would appear on GitHub.
- Label `local-agent` set when the pull request is opened under `claude/` (rejected): one forgotten label and local work is merged without review; a prefix cannot be forgotten.
- Approving GitHub review as the cloud review (rejected): the pull request is opened with the owner's account, which cannot approve its own pull request, and the workflow does not run on a review event today.
- Owner merges by hand (rejected): contradicts "the owner is never a bottleneck" ([`../change-process.md`](../change-process.md)).

## Consequences

- Positive consequences: no credential on the local machines; local work can reach `rewrite/v11` only through a recorded cloud review and the usual checks; a stuck local task costs at most 3 runs.
- Costs / constraints: the label is trust-based: an allow-listed account that adds it without reviewing bypasses the gate, as for any approval. The label is created on first use.
- Impact on modules and APIs: none (no engine code).
- Impact on tests and documentation (including Android/DEX compatibility): `.github/workflows/auto-merge-agent-prs.yml` (a `local/` branch is verified and merged on the label, the label is removed on push); `scripts/local-task.sh` (gate loop, escalation, scope check, single-shot mode); [`../local-agents.md`](../local-agents.md), [`../local-agent-entry.md`](../local-agent-entry.md), [`../templates/LOCAL-TASK-TEMPLATE.md`](../templates/LOCAL-TASK-TEMPLATE.md), [`../change-process.md`](../change-process.md), [`../open-questions.md`](../open-questions.md). Android/DEX: unchanged, the `android` job stays the arbiter of local batches.

## Validation

- `scripts/local-task.sh --dry-run` on the benchmark task, and a run against a stub gateway that answers without tools (the gate and the escalation path are exercised, the model is not).
- The first `local/` pull request is not merged before the label, and loses the label on a new push.
- `OQ-170` is closed by the benchmark results on PC1, PC2 and PC3.
