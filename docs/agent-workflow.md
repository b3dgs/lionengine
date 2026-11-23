# Working with local and cloud agents

**Status: VALIDATED — the division of work between agents; the rules in the last section are binding, the split table is a recommendation.**

Both the engine and the game are built with the same two kinds of agents:

- **Cloud agents** (chat, Claude Code in the cloud): strong reasoning, large context, no access to local hardware.
- **Local agents**: run on the owner's machines, possibly smaller models and smaller context, access to local GPUs and files (for example local sprite generation).

## Principle: the repository is the shared memory

No agent relies on a conversation history. Everything an agent needs is in the repository: `AGENTS.md`, requirements, ADRs, `open-questions.md`, batch files, the API index and the examples. When one agent stops and another continues, the hand-off is a commit plus a batch file, not a chat summary.

## Suggested split

| Work | Preferred agent | Why |
|---|---|---|
| Settling open questions, drafting and reviewing ADRs, challenging the architecture | Cloud | Needs broad reasoning and the whole documentation |
| Defining batches (feature files), reviewing pull requests, cross-module consistency checks | Cloud | Needs a global view |
| Implementing a small, well-specified batch (one module, clear contract and tests) | Local or cloud | Bounded scope fits a smaller context |
| Writing examples, recipes, API-index entries after an API lands | Local or cloud | Mechanical once the code exists |
| Asset generation (sprites) and anything needing local GPUs | Local | Hardware is local; provenance and licensing recorded per asset (ADR-0023) |
| Android/DEX verification, long builds and device tests | Wherever the pipeline runs | CI is the arbiter, not the agent |

## Making smaller-context agents effective

- Keep batches small: one module, one contract, its tests (see `templates/FEATURE-TEMPLATE.md`).
- Point the agent to [`how-the-engine-works.md`](how-the-engine-works.md), the batch's feature file, [`api-index.md`](api-index.md) and one example rather than the whole code base.
- Every default value an implementation needs is in an ADR and in the feature file; an agent that cannot find one stops and asks instead of choosing.
- Keep `AGENTS.md` short and link to the details.
- Require the compile/test loop after every change; the compiler catches invented APIs cheaply.

## Rules that never depend on the agent

- Architecture decisions are made by the owner, recorded as ADRs, whichever agent proposed them.
- A local agent that hits a `TO BE DEFINED` point (today only OQ-096, OQ-147, OQ-149 and OQ-165, or a gap it finds) records it in `open-questions.md` and stops that part of the work; it does not decide.
- CI results (build, tests, dependency rules, Android/DEX, compiled examples) decide whether a batch is accepted, not the agent's own report.
- Every pull request is reviewed by a person or by an independent agent that did not write it.
