<div align="center">

# <img src="logo.png" height="240" alt="LionEngine"/>

**A Java 2D game engine, born from the [Lionheart Remake](https://lionheart.b3dgs.com) project.**

[![Dev](https://img.shields.io/badge/dev-v11.0.0%20SNAPSHOT-yellow.svg)](https://github.com/b3dgs/lionengine/milestone/35)
[![License](https://img.shields.io/github/license/b3dgs/lionengine)](/LICENSE)

</div>

---

## Architecture

> Status: **VALIDATED specification** — every requirement and document is validated and backed by the decisions in `docs/decisions/` (ADR-0001 to ADR-0049). The build skeleton and the contract skeleton are in place; the next steps are the Android/DEX job and the Phase 1 batches ([`docs/agent-workflow.md`](docs/agent-workflow.md), Handoff). Open points: [`docs/open-questions.md`](docs/open-questions.md).

**New here? Start with [`docs/how-the-engine-works.md`](docs/how-the-engine-works.md)**: a short, diagram-based explanation readable by anybody.

### Objective

Describe the architecture of a 2D Java game engine that is independent of its runtime interfaces, in order to allow:
- incremental addition of features;
- implementation in independent batches;
- contracts that can be verified by tests;
- documentation consistent with the code;
- AI agents to work without implicit architectural decisions.

This branch contains the build skeleton (24 Maven modules, structural checks, CI), the contract skeleton (the cross-module contracts of Phases 1 and 2, compiled and documented) and the complete architecture documentation. No engine behavior exists yet; the Android/DEX job and the Phase 1 batches come next (`docs/roadmap.md`).

### Getting started

Requirements: JDK 25 or later and Maven 3.9 (the build refuses an older JDK), Python 3 for the checks.

- Build, checkstyle, package rule, tests and Javadoc: `mvn -B verify`.
- One module and what it needs: `mvn -B -pl lionengine-<module> -am verify`.
- Structural checks: `python3 -I scripts/check-modules.py`, `python3 -I scripts/check-api-index.py` (after the build), `python3 -I scripts/check-provenance.py`, `python3 -I scripts/check-docs.py`.

Before writing engine code, read [`docs/implementation-guide.md`](docs/implementation-guide.md): the code-quality gates (b3dgs checkstyle, compiler and Javadoc settings, Sonar) and the Android and performance constraints.

### Reading order

1. [`AGENTS.md`](AGENTS.md) — mandatory rules for agents.
2. [`docs/how-the-engine-works.md`](docs/how-the-engine-works.md) — the engine in a few diagrams.
3. [`docs/engine-features.md`](docs/engine-features.md) — what a game can do with the engine, area by area, and the state of each area.
4. [`docs/vision.md`](docs/vision.md) — scope and goals.
5. [`docs/requirements.md`](docs/requirements.md) — functional and non-functional requirements.
6. [`docs/architecture/overview.md`](docs/architecture/overview.md) — principles and boundaries.
7. [`docs/architecture/modules.md`](docs/architecture/modules.md) — modules and their dependencies.
8. [`docs/architecture/interfaces.md`](docs/architecture/interfaces.md) — public types per module and the contracts that cross modules.
9. [`docs/architecture/contracts.md`](docs/architecture/contracts.md) — cross-cutting contracts.
10. [`docs/conventions.md`](docs/conventions.md) — code conventions.
11. [`docs/implementation-guide.md`](docs/implementation-guide.md) — how to write code that passes the quality gates.
12. [`docs/testing.md`](docs/testing.md) — test strategy.
13. [`docs/android-compatibility.md`](docs/android-compatibility.md) — Android/DEX verification checklist.
14. [`docs/documentation.md`](docs/documentation.md) — documentation rules.
15. [`docs/roadmap.md`](docs/roadmap.md) — delivery order.
16. [`docs/decisions/`](docs/decisions/README.md) — architecture decisions (ADRs).
17. [`docs/open-questions.md`](docs/open-questions.md) — every `TO BE DEFINED` item, in one place.
18. [`docs/change-process.md`](docs/change-process.md) — how to modify a feature or add a module consistently.

Building a game with the engine (humans and agents):
- [`docs/features/`](docs/features/README.md) — one page per implementation batch, in order: the entry point for implementers.
- [`docs/game-development-guide.md`](docs/game-development-guide.md) — path from nothing to a small game.
- [`docs/api-index.md`](docs/api-index.md) — find the right types by task.
- [`docs/agent-workflow.md`](docs/agent-workflow.md) — division of work between local and cloud agents.
- [`docs/templates/GAME-AGENTS-TEMPLATE.md`](docs/templates/GAME-AGENTS-TEMPLATE.md) — `AGENTS.md` template for a game repository.

Reference material:
- [`docs/glossary.md`](docs/glossary.md) — terminology.
- [`docs/traceability.md`](docs/traceability.md) — requirement → decision → test matrix.
- [`docs/dependencies.md`](docs/dependencies.md) — every dependency with its purpose, license and Android status.
- [`docs/templates/DECISION-PACKET-TEMPLATE.md`](docs/templates/DECISION-PACKET-TEMPLATE.md) — note to fill in during a discussion, then hand to an agent to propagate the change consistently.
- [`docs/templates/FEATURE-TEMPLATE.md`](docs/templates/FEATURE-TEMPLATE.md) — template for a feature batch.
- [`scripts/check-docs.py`](scripts/check-docs.py) — documentation consistency check (`python3 -I scripts/check-docs.py`).
- [`docs/decisions/ADR-TEMPLATE.md`](docs/decisions/ADR-TEMPLATE.md) — template for an architecture decision.

### Statuses used

- **VALIDATED**: approved decision, binding.
- **PROPOSED**: suggested direction, open for discussion.
- **TO BE DEFINED**: missing information; do not guess it. Tracked in [`docs/open-questions.md`](docs/open-questions.md).
- **OUT OF SCOPE**: not planned for the version concerned.

ADRs use their own lifecycle (`PROPOSED`, `ACCEPTED`, `REJECTED`, `SUPERSEDED`). `ACCEPTED` means the same thing as `VALIDATED`.

### Essential rule

In case of contradiction, a `VALIDATED` decision prevails over a proposal. If two validated documents contradict each other, stop the work concerned and ask for clarification. Never silently turn a proposal into a decision.

### Initial state

All decisions ADR-0001 to ADR-0049 are `ACCEPTED`: technical baseline and units (ADR-0001, ADR-0012 to ADR-0016), game loop, threads, loading and no reflection (ADR-0017 to ADR-0020), object model and modules (ADR-0021, ADR-0022), game workflow and budgets (ADR-0023, ADR-0024), and every feature contract (ADR-0002 to ADR-0011, ADR-0025 to ADR-0030). ADR-0031 to ADR-0035 fixed the remaining defaults (runtime, data files and dependencies, media, camera and ECS, input), ADR-0036 the scene flow, ADR-0037 the package tree, callback types and canonical names, and ADR-0038 and ADR-0039 the network model and protocol, under the owner's delegation of 2026-11-23; ADR-0040 moved the thread hand-off contracts (inbox, asynchronous handles, worker pool, loop control) to `core` after the architecture review, and ADR-0041 to ADR-0044 closed the last questions (Android bytecode level, Maven Central, version 11 display scope, Java 21 shared level with the tested API 21 constraints). ADR-0045 to ADR-0049 come from the skeleton-readiness review requested by the owner the same day: test fixtures with their contracts, display state owned by the platform, `render-api` boundary types, `composition` dependencies, and the interface catalog with the contract-skeleton batch. Every requirement and document is **VALIDATED**; templates keep `PROPOSED` as the initial status of the items created from them.
