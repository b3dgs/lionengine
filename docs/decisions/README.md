# Architecture decisions (ADR)

**Status: PROPOSED — process description; the decisions listed below carry their own status.**

An approved decision is binding for future implementations until it is explicitly superseded.

## Lifecycle

- `PROPOSED`: proposal under discussion.
- `ACCEPTED`: approved decision (equivalent to `VALIDATED` in other documents).
- `REJECTED`: rejected proposal, kept for the record.
- `SUPERSEDED`: replaced by another decision.

## Convention

Create a file `NNNN-short-title.md` from [`ADR-TEMPLATE.md`](ADR-TEMPLATE.md), using the next free number. Never rewrite the history of an accepted decision: create a new ADR that supersedes it. Only editorial fixes (typos, broken links, translation) may touch an accepted ADR, and they must be noted in the file.

Decisions must be atomic (one main decision each), motivated, and describe both positive and negative consequences. When a decision answers an open question, mark that question `RESOLVED` in [`../open-questions.md`](../open-questions.md) and update [`../traceability.md`](../traceability.md).

## Accepted decisions

- [`ADR-0001`](0001-technical-baseline-and-conventions.md) — Java 25+, Maven 3+, Android/DEX compatibility, world units and principles of the default game loop.

## Proposed decisions

- [`ADR-0002`](0002-display-modes-resolution-and-output-filtering.md) — Display modes, resolution selection and output filtering contracts (PROPOSED).

Other likely candidates are listed in [`../open-questions.md`](../open-questions.md) (see items needed by Phase 0).
