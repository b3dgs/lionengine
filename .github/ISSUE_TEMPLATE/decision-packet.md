---
name: Decision packet
about: Compact decision packet applied by Claude Code (see docs/change-process.md)
title: "[packet] "
---

- Status: PROPOSED
- Date: YYYY-MM-DD
- Flow: A light / A full / B (ADR required: yes/no)

## Summary
One or two sentences: what and why.

## Seen / not seen
Seen: files provided in the chat. Not seen: everything else; Claude Code must check it.

## Decisions
Fill "Taken" here, or give the decisions in the `@claude apply` comment (`1B 2A 3ok`: number then letter, `ok` = the Reco).
| # | Question | Options | Reco | Taken |
|---|---|---|---|---|
| 1 | | A: ... / B: ... | B | |
| 2 | | A: ... / B: ... | A | |

## Requirements (placeholders, Claude Code assigns IDs)
- add `REQ-<AREA>-NNN` (Must/Should/Could): text
- modify / retire: existing ID, text

## Open questions
- Resolved: `OQ-...`
- Created: `OQ-NEW-n`: question [phase needing it]

## ADR
Title `ADR-NNNN — ...` or "not needed: reason".

## Engine checks (one line each)
- Units:
- Android/DEX:
- No platform types in core/*-api:
- Game-loop invariant:
- Tests (unit/contract/integration/platform):
- Example + api-index entry:

## Files likely to change
Claude Code confirms in plan mode. Typical: requirements, traceability, open-questions, modules, contracts, ADR + index, glossary, roadmap, testing, api-index, engine-features, feature file(s).

## Instruction for Claude Code
Read `AGENTS.md` and this issue. Apply the process in `docs/change-process.md`. Do not turn any proposal into a decision that "Taken" does not state. Run `python3 -I scripts/check-docs.py`. Report files changed, checks run, points left `TO BE DEFINED`.
