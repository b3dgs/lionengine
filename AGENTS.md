# Instructions for AI agents

## 0. Language

- All repository content is written in English: documentation, Javadoc, code comments, identifiers, file and branch names, commit messages, pull request titles and descriptions, issue and review comments, test names and data, exception and log messages, decision packets and templates.
- The language of the user's message does not change this. A request in French (or any other language) may be answered in that language, but what is written into the repository is English: translate the request before writing it. Keep non-English text only when it is literal data that must stay as is (for example a quoted string under test), and explain it in English around it.
- Never mix languages in one file, one sentence or one label, and never leave a French label in a template or a workflow prompt.
- Before committing documentation, run `python3 -I scripts/check-docs.py`: it rejects French accented characters in the Markdown files it checks. It does not catch unaccented French, so review the diff too.

All repository content (documentation, Javadoc, identifiers, commit messages) is written in English.

## 1. Source of truth

Before any task:
1. read this file;
2. read [`docs/how-the-engine-works.md`](docs/how-the-engine-works.md) and [`docs/engine-features.md`](docs/engine-features.md), then the documents listed in `README.md` that concern the task;
3. check the decisions in `docs/decisions/` and the items in `docs/open-questions.md`;
4. inspect the existing code before proposing a change.

`VALIDATED` documents are contractual. `PROPOSED` items are working hypotheses, not authorizations to implement them as established facts.

For an implementation batch, start from its page in [`docs/features/`](docs/features/README.md): it holds every rule and default value the batch needs. If documents disagree: ADR, then requirement, then feature page, then `contracts.md` ([`docs/documentation.md`](docs/documentation.md)).

## 2. Prohibitions

- Do not invent a responsibility, a public API, a dependency or an unspecified behavior.
- Do not couple engine logic to a concrete frontend or library (AWT, OpenGL, Android, a concrete audio library). No core module may depend on one, and no frontend type may appear in a core API.
- Do not add a cross-cutting feature to a module without checking its boundaries.
- Do not change a public contract without updating the corresponding tests and documentation.
- Do not hide an ambiguity behind an implicit decision. If a point is `TO BE DEFINED`, ask; when no human is available, record it in `docs/open-questions.md`, state the assumption you made in your report, and keep the work reversible.
- Do not create backward compatibility without an explicit requirement.
- Do not treat a successful compilation as a functional validation.
- Do not assume that a Java 25 feature, a Java API or a dependency works on Android/DEX. Verify it first with [`docs/android-compatibility.md`](docs/android-compatibility.md), including when the *core library desugaring* is required (its support and configuration must be verified and documented).
- Do not add a dependency (library, plugin, codec) without recording its purpose, its license and its Android compatibility in [`docs/dependencies.md`](docs/dependencies.md) (ADR-0032).
- Do not modify the body of an `ACCEPTED` ADR. Only editorial fixes (typos, broken links) are allowed; anything else requires a new ADR that supersedes it.

## 3. Procedure before implementation

For each task:
1. identify the relevant requirements (`REQ-*`);
2. identify the relevant modules and contracts;
3. check the applicable decisions and open questions;
4. list ambiguities and dependencies;
5. propose a limited, testable plan;
6. wait for human validation if the task involves a `TO BE DEFINED` decision, an architecture change, or a public API that no `VALIDATED` contract describes (or a change to an existing public API). New Java signatures that implement a `VALIDATED` contract of a feature file are proposed in the batch and reviewed in its pull request.

For an already scoped task, implement only the requested scope. Use the default values stated in the ADRs and feature files; never choose a value that a document leaves unstated: report it as a gap.

## 4. Java code requirements

- Use Java 25 or higher for development, subject to the Android/DEX compatibility constraints defined in ADR-0001.
- Provide complete Javadoc for public APIs: description, `@param`, `@return`, `@throws`, `@since`, and `@deprecated` where relevant (see `docs/documentation.md`).
- Document invariants, units, coordinate conventions, resource ownership and lifecycle rules.
- Avoid public classes without a clear responsibility.
- Prefer explicit contracts over implicit behavior.
- Follow [`docs/conventions.md`](docs/conventions.md) and the conventions of the existing code; do not massively reorganize code in a functional task.
- For shared code, favor the APIs available on the targeted Android versions; isolate Android-specific adaptations.

## 5. Mandatory tests

Every feature must include the appropriate tests:
- unit tests for deterministic logic;
- contract tests for implementations of the same abstraction;
- integration tests at module boundaries;
- regression tests for defect fixes.

Tests must not depend on a real display, a physical audio device or a native window when a headless test is possible. Tests requiring hardware or a frontend must be identified separately. Details: [`docs/testing.md`](docs/testing.md).

## 6. Commits and batches

- One batch (see `docs/templates/FEATURE-TEMPLATE.md`) corresponds to one reviewable change set.
- Reference the relevant `REQ-*` and `ADR-NNNN` identifiers in commit messages and pull requests.
- Do not mix a functional change with a mass reformatting or reorganization.

## 7. End of task

At the end, provide:
- a summary of the changes;
- the requirements covered;
- the files and APIs added/modified;
- the tests run and their results;
- known limitations;
- the documentation updated, including [`docs/engine-features.md`](docs/engine-features.md) whenever the change adds, removes or changes what a game can do, or moves an area to another state (*Specified*, *In progress*, *Available*);
- the decisions or questions remaining to be settled.

When documentation was changed, run `python3 -I scripts/check-docs.py` and report its result. A change coming from a decision packet ([`docs/templates/DECISION-PACKET-TEMPLATE.md`](docs/templates/DECISION-PACKET-TEMPLATE.md)) is applied only as far as the packet states decisions as taken.

Never claim a test was run if it was not. Build and test commands are written by the build-skeleton batch (ADR-0042); until it is merged none exists: never invent them.

## 8. Building a game with the engine

These rules apply when writing code that uses the engine (examples, demonstrators, games). A game repository copies them from [`docs/templates/GAME-AGENTS-TEMPLATE.md`](docs/templates/GAME-AGENTS-TEMPLATE.md).

- Use **only** classes and methods found in this repository's code, Javadoc or [`docs/api-index.md`](docs/api-index.md). Never rely on knowledge of other engines or of earlier LionEngine versions: their APIs differ.
- Start from [`docs/game-development-guide.md`](docs/game-development-guide.md) and the closest example in `examples`; copy its structure.
- Compile and run the tests after each change. A method that does not compile does not exist: search for the right one instead of inventing it.
- A public API change is not complete without its compiled example and its API-index entry (REQ-DOC-001, REQ-DOC-002).
- If the engine lacks a needed capability, raise an engine change request instead of working around it inside the game.
- Work split between local and cloud agents: [`docs/agent-workflow.md`](docs/agent-workflow.md).

## 9. Changing the architecture

Any change to a module boundary, a public contract, a lifecycle strategy or a dependency between layers must:
1. be described in a proposal;
2. be assessed for its impacts;
3. be approved;
4. be recorded in `docs/decisions/`;
5. trigger the update of the affected diagrams, requirements and tests.
