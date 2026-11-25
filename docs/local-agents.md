# Local agents: delegation, models, task files and merge gate

**Status: PROPOSED — decisions 1A, 2C, 3B, 4A and 5A of the packet of issue #910 (owner comment of 2026-11-25). Requirements `REQ-AGENT-001` to `REQ-AGENT-004` are PROPOSED; the open points are `OQ-170` to `OQ-173`.**

This page extends [`agent-workflow.md`](agent-workflow.md): Claude Code (cloud) delegates token-heavy, well-specified work to local models on the owner's machines, reviews the result, and keeps high-reasoning work in the cloud. Requirements: `REQ-AGENT-001` to `REQ-AGENT-004` in [`requirements.md`](requirements.md).

## 1. Machines and model profiles

Decision 5A. The models are the owner's delegated choice; they are confirmed (or replaced) by a benchmark task, `OQ-170`. Until then they are a starting profile, not a guarantee.

| Machine | GPU (VRAM) | RAM | Model | Mode |
|---|---|---|---|---|
| PC1 | RTX 5070 Ti (16 GB) | 64 GB | Qwen3.6-35B-A3B, 4-bit, the part that does not fit in VRAM held in RAM, 64K context; fallback gpt-oss-20b | Agent loop |
| PC2 | RTX 4070 (12 GB) | 32 GB | the same model with the MoE experts offloaded to RAM, or Qwen3-Coder-30B-A3B | Agent loop |
| PC3 | RTX 2060 (8 GB) | 16 GB | Qwen3-Coder 8B, small quantization | Single-shot jobs only (Javadoc, documentation); no agent loop |

Hardware confirmed by the owner on 2026-11-25: the RTX 4070 has 12 GB of VRAM (32 GB of RAM) and the RTX 2060 has 8 GB of VRAM (16 GB of RAM). The PC3 profile stays single-shot: a task given to PC3 is one prompt and one answer applied by Claude Code, never a multi-step tool loop.

## 2. What is delegated (decision 2C)

1. **First**: examples, API-index rows and Javadoc for an API that already landed.
2. **Then**, once the first stage has passed the benchmark of `OQ-170`: bounded batches, starting with Resources batches 3 and 4 and Game loop batch 4 ([`features/resources.md`](features/resources.md), [`features/game-loop.md`](features/game-loop.md)).

Delegation criteria (all must hold):

- the work is described by a batch page or by an existing contract, with every default value stated (an agent never chooses a value a document leaves unstated);
- the scope is one module, a few files, and fits the context of the profile;
- the acceptance is a list of commands that a machine can run (section 4);
- no `TO BE DEFINED` point, no architecture decision, no public API that no `VALIDATED` contract describes.

Never delegated: ADRs, answers to open questions, cross-module consistency reviews, anything touching the game-loop invariant without a batch page, `.github/workflows/`, and anything on `rewrite/v11` directly.

## 3. Entry file for small contexts

[`local-agent-entry.md`](local-agent-entry.md) is the compact entry file of a local worker (`REQ-AGENT-003`). It lists the rules in a few lines and links to the sections of [`AGENTS.md`](../AGENTS.md) instead of copying them, so a rule has a single source.

## 4. Task file

A task is a Markdown file written by Claude Code from [`templates/LOCAL-TASK-TEMPLATE.md`](templates/LOCAL-TASK-TEMPLATE.md) (`REQ-AGENT-002`): the batch page, the files in scope, the acceptance commands and the stop conditions. The task file is the hand-off: the worker relies on no conversation history. Task files are working material for the delegation and are not committed unless the owner asks.

## 5. Running a worker

Decision 4A. `scripts/local-task.sh <task-file>` creates a git worktree of its own on a branch `local/<slug>` cut from `rewrite/v11`, then runs a headless `claude -p` there through a local gateway (an Anthropic-compatible endpoint in front of the local model, configured by the environment of the owner's machine, never by the script). After the worker, the script runs the four structural checks in the worktree and reports their results. `scripts/local-task.sh --dry-run <task-file>` prints what it would do and changes nothing.

Rules of the script:

- it contains no secret and no permission-skip flag; the worker has an explicit list of allowed tools;
- the worker works only in its worktree and never on `rewrite/v11`; the script refuses to reuse an existing worktree or branch;
- the script does not push and does not open a pull request: the worker hands a branch to Claude Code (default taken for `OQ-172`).

## 6. Escalation

A task returns to a cloud agent when:

- a stop condition of the task file is met (for example a `TO BE DEFINED` point, a needed public API without a validated contract, an Android/DEX doubt);
- the acceptance commands still fail after a number of gate runs that is not decided yet (`OQ-171`).

The worker records a gap in its report (or in [`open-questions.md`](open-questions.md) through Claude Code) and stops that part of the work; it does not decide (`agent-workflow.md`, "Rules that never depend on the agent").

## 7. Merge gate (decision 3B)

A pull request that comes from a local agent is **not auto-merged** until a cloud review has approved it (`REQ-AGENT-004`). Local-agent branches are identifiable by the prefix `local/`. The CI results still decide whether a batch is accepted, the `android` job remaining the arbiter for shared code.

State of the implementation: the workflow `.github/workflows/auto-merge-agent-prs.yml` is **not changed** by this change. Today it merges only branches `packet/*` and `claude/*` of the allow-listed authors, so a `local/*` branch is not matched and is not auto-merged. The gate is complete only when three gaps are settled, recorded as `OQ-173`: how a pull request is marked when Claude Code opens it under `claude/` from a `local/` branch (label or prefix), who counts as a cloud review, and who merges after approval. The workflow is edited after `OQ-173` is answered, and after the plan is reviewed.
