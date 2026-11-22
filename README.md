<div align="center">

# <img src="logo.png" height="240" alt="LionEngine"/>

**A Java 2D game engine, born from the [Lionheart Remake](https://lionheart.b3dgs.com) project.**

[![Dev](https://img.shields.io/badge/dev-v11.0.0%20SNAPSHOT-yellow.svg)](https://github.com/b3dgs/lionengine/milestone/35)
[![License](https://img.shields.io/github/license/b3dgs/lionengine)](/LICENSE)

</div>

---

## Architecture (preliminary)

> Status: **scoping draft** — no proposal in this repository is considered validated until it has been approved and recorded in `docs/decisions/`.

### Objective

Describe the architecture of a 2D Java game engine that is independent of its runtime interfaces, in order to allow:
- incremental addition of features;
- implementation in independent batches;
- contracts that can be verified by tests;
- documentation consistent with the code;
- AI agents to work without implicit architectural decisions.

This branch currently contains a Maven baseline (parent `pom.xml`, no modules yet) and the architecture documentation: there is no engine source code and no build/test CI yet (see `docs/roadmap.md`, Phase 0).

### Reading order

1. [`AGENTS.md`](AGENTS.md) — mandatory rules for agents.
2. [`docs/vision.md`](docs/vision.md) — scope and goals.
3. [`docs/requirements.md`](docs/requirements.md) — functional and non-functional requirements.
4. [`docs/architecture/overview.md`](docs/architecture/overview.md) — principles and boundaries.
5. [`docs/architecture/modules.md`](docs/architecture/modules.md) — proposed conceptual breakdown.
6. [`docs/architecture/contracts.md`](docs/architecture/contracts.md) — cross-cutting contracts.
7. [`docs/conventions.md`](docs/conventions.md) — code conventions.
8. [`docs/testing.md`](docs/testing.md) — test strategy.
9. [`docs/android-compatibility.md`](docs/android-compatibility.md) — Android/DEX verification checklist.
10. [`docs/documentation.md`](docs/documentation.md) — documentation rules.
11. [`docs/roadmap.md`](docs/roadmap.md) — proposed delivery order.
12. [`docs/decisions/`](docs/decisions/README.md) — architecture decisions (ADRs).
13. [`docs/open-questions.md`](docs/open-questions.md) — every `TO BE DEFINED` item, in one place.
14. [`docs/change-process.md`](docs/change-process.md) — how to modify a feature or add a module consistently.

Building a game with the engine (humans and agents):
- [`docs/game-development-guide.md`](docs/game-development-guide.md) — path from nothing to a small game.
- [`docs/api-index.md`](docs/api-index.md) — find the right types by task.
- [`docs/agent-workflow.md`](docs/agent-workflow.md) — division of work between local and cloud agents.
- [`docs/templates/GAME-AGENTS-TEMPLATE.md`](docs/templates/GAME-AGENTS-TEMPLATE.md) — `AGENTS.md` template for a game repository.

Reference material:
- [`docs/glossary.md`](docs/glossary.md) — terminology.
- [`docs/traceability.md`](docs/traceability.md) — requirement → decision → test matrix.
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

The choices explicitly confirmed by the project owner are recorded as **VALIDATED** in [`ADR-0001`](docs/decisions/0001-technical-baseline-and-conventions.md): Java 25+, Maven 3+, Android/DEX compatibility, X/Y coordinates in meters, time in seconds, and the principles of the default game loop (details still to be specified). ADR-0002 to ADR-0005 (display modes and output filtering, sprites and animations, audio, common resource convention) are also `ACCEPTED`. All other architectural elements remain **PROPOSALS**.
