# Documentation policy

**Status: PROPOSED**

## Expected documentation

- `README.md`: getting started, prerequisites and links.
- `AGENTS.md`: agent working rules (`CLAUDE.md` simply imports it).
- `docs/vision.md`: purpose and scope.
- `docs/requirements.md`: identified requirements.
- `docs/architecture/`: views, modules and contracts.
- `docs/decisions/`: approved architecture decisions.
- `docs/open-questions.md`: unresolved points.
- `docs/traceability.md`: requirement → decision → test matrix.
- `docs/game-development-guide.md` and `docs/api-index.md`: how to use the engine, by task.
- Javadoc: detailed reference of the public APIs.
- examples: minimal, real usage demonstrations, compiled and run by the build (REQ-DOC-001). They are the primary reference for newcomers and AI agents; code snippets in prose documents are included from them, never written by hand.

## Java Javadoc

Every public class, interface, enum, record, constructor and method must have useful Javadoc. Add the relevant tags:
- `@param` for every parameter;
- `@return` for every returned value;
- `@throws` for contractual exceptions;
- `@since` for APIs introduced in a version;
- `@deprecated` with alternative and removal timeline where relevant;
- `@see` for related contracts.

Each package must have a `package-info.java` describing its responsibility and its place in the module breakdown. Public APIs of a module that has no Javadoc, or whose tags are incomplete, fail the documentation check (REQ-CORE-003).

Javadoc must state units, coordinates, allowed null values, side effects, resource ownership/closing and thread constraints where they apply.

Do not copy the method name as documentation. Describe the observable behavior.

## Traceability

Requirements use stable `REQ-*` identifiers. Decisions use `ADR-NNNN`. Open questions use `OQ-NNN`. A feature documents the requirements it covers and the decisions it applies. [`traceability.md`](traceability.md) must be kept in sync.

## Automated check

`python3 -I scripts/check-docs.py` (also run by the `docs-check` workflow) verifies: relative links, orphan files, undefined or duplicate `REQ-*`/`OQ-*`/`ADR-*`, consistency between `requirements.md` and `traceability.md`, and the `Status:` line of each document. It must pass before any documentation change is merged.

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

Documentation is written in English. Every document under `docs/` starts with a `Status:` line using the vocabulary of the root `README.md`.
