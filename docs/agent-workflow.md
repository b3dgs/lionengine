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
- A local agent that hits a `TO BE DEFINED` point (see [`open-questions.md`](open-questions.md); a gap it finds) records it in `open-questions.md` and stops that part of the work; it does not decide.
- CI results (build, tests, dependency rules, Android/DEX, compiled examples) decide whether a batch is accepted, not the agent's own report.
- Every pull request is reviewed by a person or by an independent agent that did not write it.

## Handoff (2026-11-24)

Done by a cloud agent, merged with this section: the Maven part of the build skeleton ([`features/build-skeleton.md`](features/build-skeleton.md), section "State") and the contract skeleton ([`features/contract-skeleton.md`](features/contract-skeleton.md)). Every engine module exists; the cross-module contracts of Phases 1 and 2 are compiled and documented. `mvn -B verify` and the four check scripts pass.

Next batches, in this order. Each one starts from its feature page and [`implementation-guide.md`](implementation-guide.md), stays one pull request, and ends with the definition of done of the guide (section 9):

1. **Android/DEX job** (end of the build skeleton, item 4; needs the Android SDK and an API 21 emulator, so a local agent or a CI runner):
   - an `android/` Gradle build consuming the shared modules installed with `mvn -B install`, with core library desugaring and a verification module that runs D8 on them;
   - lint `NewApi` as an error, and the shared modules' tests on an API 21 emulator;
   - a `build.yml` job running it;
   - probes for the items the guide treats as not verified (default and static interface methods, `List.copyOf`), and the versions and results recorded in [`android-compatibility.md`](android-compatibility.md).
2. **Core foundation**, batches 1 to 4 ([`features/core-foundation.md`](features/core-foundation.md)): the `Log` facade and its default `LogSink` first, because the inbox reports its drops through it.
3. **Game loop**, batches 1 to 3 ([`features/game-loop.md`](features/game-loop.md)):
   - `GameLoop` implementing `LoopDriver` and its `Inbox`;
   - the controllable clock in the `runtime` test JAR;
   - the fake `WorkerPool`, inbox spy and manual `LoopDriver` in the `core` test JAR (ADR-0045).
4. **Scenes**, batch 1 ([`features/scenes.md`](features/scenes.md)).
5. **Resources**, batches 1 and 2 ([`features/resources.md`](features/resources.md)): `Resources`, `resources-memory`, `resources-file`, and the provider suite in the `resources` test JAR.
6. **Platform and rendering**, batches 1 and 2 ([`features/platform-and-rendering.md`](features/platform-and-rendering.md)):
   - the headless platform and recording backend in `test-support`;
   - the `platform-awt` window and loop thread;
   - `render-awt` presenting the logical frame.
7. **Examples and API index**: the first headless example and the first API-index rows, once a contract is usable (REQ-DOC-001, REQ-DOC-002).

Batch 3 needs batch 2 (the inbox logs its drops); batches 4 and 5 depend only on what is merged and may run in parallel with 2 and 3 on separate branches, as long as each pull request keeps `mvn -B verify` green on the merge result.

Open questions to settle with the owner meanwhile: `OQ-166` (JUnit 6 from the parent) and `OQ-167` (parent version).
