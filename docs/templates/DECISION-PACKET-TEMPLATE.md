# Decision packet: title

- Status: PROPOSED
- Date: YYYY-MM-DD
- Kind: new feature / change to an existing feature / architecture change / clarification of an open question

> Purpose: fill this in during a discussion (chat), with only the files that directly concern the topic. Once the content is clear, hand the packet to Claude Code to propagate the change across the whole repository and keep it consistent. The packet is a working note: it is not stored as a decision; the ADR, requirements and feature file derived from it are.

## 1. Summary

One or two sentences: what is added or changed, and why.

## 2. Files consulted during the discussion

List the documents that were provided (for example `contracts.md`, `requirements.md`, `open-questions.md`, `ADR-0001`). Anything not listed was not seen: Claude Code must check it.

## 3. Options and recommendation

| Option | Advantages | Drawbacks | Impact on modules / contracts |
|---|---|---|---|
| A | | | |
| B | | | |

Recommendation and reason:

Decision taken (to be filled by the owner): `A` / `B` / other — date:

## 4. Requirement changes

| Action | ID | Text | Priority | Status |
|---|---|---|---|---|
| add / modify / retire | `REQ-...` | | Must / Should / Could | PROPOSED |

## 5. Open questions

- Resolved by this packet: `OQ-...`
- Created by this packet (with the phase that needs them): `OQ-...`

## 6. Architecture decision needed?

- [ ] Yes — proposed ADR title: `ADR-NNNN — ...` (module boundary, public contract, lifecycle strategy or inter-layer dependency changes require one; see `AGENTS.md`).
- [ ] No — reason:

## 7. Checks specific to this engine

- Units involved (meters, seconds, m/s, m/s²):
- Android/DEX impact (Java APIs, dependencies, desugaring; see `docs/android-compatibility.md`):
- Frontend or platform types leaking into the core? (must be no):
- Effect on the game loop invariant (extrapolated state never reused as simulation input):
- Tests expected (unit / contract / integration / platform):
- Example and API-index entry needed (REQ-DOC-001, REQ-DOC-002):

## 8. Files expected to change

Check those that apply; Claude Code must confirm the list before writing.

- [ ] `docs/requirements.md`
- [ ] `docs/traceability.md`
- [ ] `docs/open-questions.md`
- [ ] `docs/architecture/modules.md`
- [ ] `docs/architecture/contracts.md`
- [ ] `docs/architecture/overview.md`
- [ ] `docs/decisions/` (new ADR and index)
- [ ] `docs/glossary.md`
- [ ] `docs/roadmap.md`
- [ ] `docs/testing.md`
- [ ] `docs/api-index.md` and `docs/game-development-guide.md`
- [ ] feature file from `docs/templates/FEATURE-TEMPLATE.md`

## 9. Instruction for Claude Code

Copy the following, with this packet attached or pasted:

> Read `AGENTS.md` and this decision packet. In plan mode, list every file that must change to apply it consistently (requirements, traceability, open questions, modules, contracts, ADR and its index, glossary, roadmap, tests, API index). Wait for my approval. Then apply it, without turning any proposal into a decision that the packet does not state as taken, and run `python3 -I scripts/check-docs.py`. Report the files changed, the checks run and any point left `TO BE DEFINED`.
