# Change process: modifying a feature, adding a module

**Status: VALIDATED — the workflow for every change; amend it by a pull request like any other document.**

Two flows share the same skeleton: **discuss with minimal context, write a decision packet, then let an agent with the whole repository propagate the change and run the checks.** Consistency comes from stable identifiers (`REQ-*`, `OQ-*`, `ADR-*`), the traceability matrix and the automated check, not from showing every file to the discussion.

## 0. Which flow?

| Situation | Flow | ADR required? |
|---|---|---|
| Add behavior or fix/extend a feature **inside** an existing module, public contracts unchanged | A (light) | No |
| Change a public contract, a lifecycle strategy, a module boundary or a dependency between layers | A (full) | **Yes** |
| Add a new module, or split/merge modules | B | **Yes** |
| Answer an open question (`OQ-*`) | A, with a decision packet | Yes if it fixes an architecture choice |

If unsure, treat the change as requiring an ADR: it is cheaper than discovering later that an implicit decision was made (see `AGENTS.md`).

## Roles and tools

- **Discussion (chat):** explore options and trade-offs with only the directly relevant files (the module's section in `contracts.md`/`modules.md`, `requirements.md`, `open-questions.md`, the applicable ADR). No file is edited here.
- **Propagation (Claude Code, or any agent with the whole repository):** edits all affected files, runs the check, opens the pull request.
- **Decision:** always the project owner. An agent proposes; the owner decides; the ADR records it.
- **Review:** the pull request is reviewed by a person, or by an independent session that did not write it. Use a stronger model only for hard-to-reverse decisions (see `agent-workflow.md`).

## Flow A — complete or modify a feature of an existing module

1. **Frame it (chat).** State the change in one or two sentences. Ask for options, trade-offs, and impacts on the module's contract, units, lifecycle and Android/DEX compatibility.
2. **Write the decision packet.** Fill [`templates/DECISION-PACKET-TEMPLATE.md`](templates/DECISION-PACKET-TEMPLATE.md): options, decision taken, requirement changes (`add` / `modify` / `retire`), open questions resolved or created, whether an ADR is needed, expected files.
3. **Decide.** The owner fills "Decision taken". Anything not stated as taken stays a proposal.
4. **Plan the propagation (Claude Code, plan mode).** The agent lists every file to change and waits for approval. Typical set:
   - `requirements.md` and `traceability.md` (new or changed `REQ-*`; never reuse an identifier);
   - `open-questions.md` (resolved or new items; never delete a row);
   - `architecture/contracts.md` (and `modules.md` if responsibilities or dependencies move);
   - `decisions/` (new ADR and its index) if the change alters a contract, lifecycle or boundary;
   - `glossary.md`, `roadmap.md`, `testing.md` if concepts, phases or test strategy change;
   - `engine-features.md` if what a game can do changes.
5. **Specify the batch.** Create a feature file from [`templates/FEATURE-TEMPLATE.md`](templates/FEATURE-TEMPLATE.md): scope, out of scope, behavior, contract, lifecycle, units, tests, acceptance criteria.
6. **Implement in small batches.** Code, tests (unit, contract, integration as needed), complete Javadoc (units, ownership, thread, nullability), Android/DEX check ([`android-compatibility.md`](android-compatibility.md)).
7. **Make it usable.** Add or update the compiled example and the [`api-index.md`](api-index.md) entry (REQ-DOC-001, REQ-DOC-002); update the game development guide if the usage path changes.
8. **Verify.** Run the tests and `python3 -I scripts/check-docs.py`. Report real results only.
9. **Merge.** Pull request referencing the `REQ-*` and `ADR-*` identifiers; review; merge. Update the traceability test column.

## Flow B — add a new module and insert it into the architecture

A new module is an architecture change: it always goes through a proposal and an ADR.

1. **Justify it (chat).** Check the splitting principles in [`architecture/modules.md`](architecture/modules.md): coherent responsibility, small public API, a real boundary (several implementations or a platform) or a real isolation/test benefit. If it is just a class or a tiny feature, do not create a module.
2. **Define its place.** In the decision packet, state:
   - responsibility (one sentence) and what it explicitly does **not** do;
   - allowed dependencies (direction toward `core` and `*-api` modules only); no cycle, no dependency from `core` or an `*-api` module toward it;
   - whether it is API + implementations or a single module;
   - the contracts it exposes and the capabilities it may lack on some platforms;
   - Android impact: shared code or platform-specific, desugaring, new dependencies and their licenses;
   - which open questions it touches or creates (`open-questions.md`) and which ADRs it completes or supersedes.
3. **Write the ADR.** From [`decisions/ADR-TEMPLATE.md`](decisions/ADR-TEMPLATE.md): context, decision, options (including "no new module"), consequences. It stays `PROPOSED` until the owner approves; then it becomes `ACCEPTED`.
4. **Plan the propagation (agent, plan mode).** Typical files:
   - [`architecture/modules.md`](architecture/modules.md): new table row, dependency-direction diagram, open points;
   - [`architecture/overview.md`](architecture/overview.md) if boundaries or the layer diagram change;
   - [`architecture/contracts.md`](architecture/contracts.md): the new cross-cutting contract section;
   - [`architecture/interfaces.md`](architecture/interfaces.md): the module's root package, public types and seams (ADR-0049);
   - `requirements.md` and `traceability.md`: new `REQ-*` for the module;
   - `open-questions.md`: resolved and new questions;
   - `decisions/` (ADR and index), `glossary.md`, `roadmap.md` (phase and ordering), `testing.md` (new matrix row), `api-index.md`;
   - build: Maven module declaration and the dependency-rule check (REQ-CORE-005, ADR-0012, ADR-0015), once the build skeleton exists;
   - [`dependencies.md`](dependencies.md) if the module adds a dependency (ADR-0032).
5. **Create the skeleton first.** Empty module, `package-info.java` describing its responsibility, dependencies as decided, build and dependency-rule check passing, Android/DEX job passing if the module is shared code. No behavior yet.
6. **Add the contract tests.** Reusable contract suites and fakes in the new module's test JAR before or with the first implementation (REQ-TEST-001, ADR-0045), so every future implementation is checked the same way.
7. **Implement by batches** as in Flow A, steps 5–8, starting with the minimal contract and a headless implementation.
8. **Add the example and index entries**, then verify (tests, dependency-rule check, Android/DEX job, `scripts/check-docs.py`).
9. **Merge** with the ADR set to `ACCEPTED`, referencing it in the pull request.

## Mobile flow (optional automation)

From a phone, the same process can run without a terminal. In a chat of the claude.ai project, describe the change; the chat answers with a compact packet ([`templates/DECISION-PACKET-COMPACT.md`](templates/DECISION-PACKET-COMPACT.md)) and a decision card to answer in one line. The chat instructions are kept in [`templates/PROJECT-INSTRUCTIONS.md`](templates/PROJECT-INSTRUCTIONS.md). Paste the final packet as a GitHub issue titled `[packet] ...`, comment `@claude plan` to get the list of files to change (nothing is modified), then `@claude apply` once the plan is approved, optionally with the decisions (`@claude apply 1B 2A 3ok`: decision number then letter, or number then `ok` for the recommendation; anything not stated stays a proposal): the workflow `.github/workflows/claude-packet.yml` applies the packet, runs `scripts/check-docs.py` and opens a pull request. The owner still decides and reviews; the agent never turns a proposal into a decision.

## Auto-merge during the tuning phase (optional)

While the architecture has no code yet, the owner should not be a bottleneck: `.github/workflows/auto-merge-agent-prs.yml` squash-merges pull requests opened by the agents (branches `packet/*` and `claude/*`, authors in the allow-list) once the checks pass on the merge result: `scripts/check-docs.py` always, and when the pull request changes more than documentation, the Maven build and tests and the module, API index and provenance checks. A merge made by the workflow starts no push workflow on `rewrite/v11` (GitHub rule), which is why the build runs before the merge; it merges only once the `sonar` check of `build.yml` has succeeded on the head commit of the pull request (that job fails with the Sonar quality gate), and a failed, skipped or late check leaves the pull request open until a new push or a re-run; after the merge, the workflow starts `build.yml` on `rewrite/v11` (manual trigger, the one event the workflow token may start), so the branch is built and analyzed by Sonar after every merge. `build.yml` can also be started by hand on any branch.

- **Only the owner validates.** Packets stay `PROPOSED`. An agent opens a pull request that adds `Status: ACCEPTED` or a `| VALIDATED` cell only when the owner explicitly asks for it (in a session or on the issue); since the owner's request is the validation, such a pull request is merged automatically like the others (owner decision of 2026-11-22). The run log flags it, and reverting the squash commit undoes it.
- **Why the workflow runs the check itself.** The branch ruleset requires signed commits (hence squash only: GitHub signs the squash commit) and has no required status check; without one, GitHub's own auto-merge would merge immediately. Adding `docs-check` and the `linux` job of `build.yml` as required checks is an optional second safety net. The pull request's code runs in a read-only job without stored credentials; only the merge job holds the write token.
- **Number collisions.** Merges run one at a time and the check runs on the merge result, so two pull requests taking the same `REQ-*`/`OQ-*`/`ADR-*` number are caught, except in a rare race; the check also runs after each push.
- **Keep the integrated review.** After a few merges, or before a roadmap phase starts, run the read-only review with the strongest model (see the prompt below) and tag the commit beforehand so that reverting is easy.
- **To stop** (when the project matures): delete the workflow file, or narrow the allow-lists in it.

## Prompts to give the agent

Propagation of a packet (both flows):

> Read `AGENTS.md` and this decision packet. In plan mode, list every file that must change to apply it consistently. Wait for my approval. Then apply it without turning any proposal into a decision the packet does not state as taken, and run `python3 -I scripts/check-docs.py`. Report files changed, checks run and points left `TO BE DEFINED`.

Independent review of an ADR (separate session):

> Read `AGENTS.md`, `docs/architecture/` and this ADR. Look for contradictions with validated decisions, forbidden dependencies, hidden decisions, Android/DEX risks and missing consequences. Do not edit files; list findings with the file and identifier concerned.

## Definition of done (either flow)

- [ ] Decision recorded (ADR if required) and open questions updated.
- [ ] `REQ-*` added/changed/retired, `traceability.md` in sync.
- [ ] Architecture documents consistent (`modules.md`, `contracts.md`, `overview.md` when relevant).
- [ ] Code, tests and complete Javadoc; no forbidden coupling.
- [ ] Android/DEX compatibility verified; new dependencies recorded in `dependencies.md`.
- [ ] Compiled example and `api-index.md` entry.
- [ ] [`engine-features.md`](engine-features.md) updated if a capability or an area's state changed.
- [ ] `scripts/check-docs.py` passes; test results reported honestly.
- [ ] Pull request reviewed by someone who did not write it.
