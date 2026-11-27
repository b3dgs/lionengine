# Documentation policy

**Status: VALIDATED**

## Expected documentation

- `README.md`: getting started, prerequisites and links.
- `AGENTS.md`: agent working rules (`CLAUDE.md` simply imports it).
- `docs/how-the-engine-works.md`: plain-language summary with diagrams, readable by anybody.
- `docs/engine-features.md`: what a game can do, area by area, with the state of each area; updated by every batch that changes a capability.
- `docs/vision.md`: purpose and scope.
- `docs/requirements.md`: identified requirements.
- `docs/architecture/`: views, modules and contracts.
- `docs/decisions/`: approved architecture decisions.
- `docs/open-questions.md`: unresolved points.
- `docs/traceability.md`: requirement → decision → test matrix.
- `docs/dependencies.md`: register of every dependency and its license (ADR-0032).
- `docs/game-development-guide.md` and `docs/api-index.md`: how to use the engine, by task.
- Javadoc: detailed reference of the public APIs.
- examples: minimal, real usage demonstrations, compiled and run by the build (REQ-DOC-001). They are the primary reference for newcomers and AI agents; code snippets in prose documents are included from them, never written by hand.

## Java Javadoc

Every public class, interface, enum, record, constructor and method must have useful Javadoc. The build runs doclint `all` on public and protected members and fails on any warning (REQ-CORE-003, ADR-0032). Add the relevant tags:
- `@param` for every parameter;
- `@return` for every returned value;
- `@throws` for contractual exceptions;
- `@since` for APIs introduced in a version;
- `@deprecated` with alternative and removal timeline where relevant;
- `@see` for related contracts.

Each package must have a `package-info.java` describing its responsibility and its place in the module breakdown. Public APIs of a module that has no Javadoc, or whose tags are incomplete, fail the documentation check (REQ-CORE-003).

Javadoc must state units, coordinates, allowed null values, side effects, resource ownership/closing, thread constraints and default values where they apply.

Do not copy the method name as documentation. Describe the observable behavior.

## Which document wins

Each fact has one owner; other documents summarize it and link to it.

| Kind of fact | Owner | Summaries |
|---|---|---|
| Why a choice was made, and the choice itself at architecture level | the ADR | requirements, contracts, feature page |
| What must be verifiable | `requirements.md` | traceability, feature page |
| Everything a batch needs to implement (rules, defaults, states, order of operations, names) | the feature page (`docs/features/`) | `architecture/contracts.md`, `architecture/interfaces.md` |
| Overview for newcomers | `how-the-engine-works.md`, `engine-features.md` | — |

In a conflict: ADR, then requirement, then feature page, then `contracts.md` and `interfaces.md`, then `how-the-engine-works.md`. A feature page is `VALIDATED` and may fix batch-level details (state names, method lists, order of steps) inside the limits of its ADRs without a new ADR; a change of an architecture choice needs an ADR. Whoever finds a conflict fixes the lower document in the same pull request, or records it in `open-questions.md` if the higher one is unclear.

## Traceability

Requirements use stable `REQ-*` identifiers. Decisions use `ADR-NNNN`. Open questions use `OQ-NNN`. A feature documents the requirements it covers and the decisions it applies. [`traceability.md`](traceability.md) must be kept in sync.

## Automated check

`python3 -I scripts/check-docs.py` (also run by the `docs-check` workflow) verifies: relative links, orphan files, undefined or duplicate `REQ-*`/`OQ-*`/`ADR-*`, consistency between `requirements.md` and `traceability.md`, the `Status:` line of each document, and that every feature page is linked from `engine-features.md`. It must pass before any documentation change is merged.

## Changing the documentation consistently

Discuss a change with only the directly relevant files, summarize it in a [decision packet](templates/DECISION-PACKET-TEMPLATE.md), then have an agent with access to the whole repository propagate it and run the check. The full step-by-step flows (existing feature, new module) are in [`change-process.md`](change-process.md).

## Mandatory updates

Any change to a public contract must update:
1. the Javadoc;
2. the contract tests;
3. the relevant examples;
4. the architecture documentation if responsibility or dependencies change;
5. the traceability matrix.

## Language and status headers

Documentation is written in English, whatever the language of the request that triggered the change ([`AGENTS.md`](../AGENTS.md), section 0). Every document under `docs/` starts with a `Status:` line using the vocabulary of the root `README.md`.
