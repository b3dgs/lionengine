# ADR-0024 — Determinism, performance budgets and supported desktop platforms

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner ("Confirmed as proposed, write the ADR")
- Related requirements: `REQ-DET-001`, `REQ-DET-002`, `REQ-PERF-001`, `REQ-PERF-002`, `REQ-SUPPORT-001`, `REQ-MP-002`, `REQ-ECS-009`, `REQ-RT-006`, `REQ-LOOP-004`
- Open questions resolved: `OQ-060` (network mode itself deferred, see `OQ-158`), `OQ-070`, `OQ-071`
- Supersedes / superseded by: none

## Context

The network model, the performance budgets and the supported desktop platforms were open. Several accepted decisions depend on them: the fixed step (ADR-0017), the ECS budget (`REQ-ECS-009`), the allocation rules (ADR-0019) and the CI matrix (ADR-0012).

## Decision

1. **Network (`OQ-060`).** No network protocol, authority, prediction or rollback is decided now (Phase 5, with a real game need, `OQ-158`). The engine keeps a later network mode possible with three rules:
   - the simulation is deterministic for a given sequence of inputs and steps; randomness comes from a seeded generator owned by the world, never from a global source;
   - game state can be read and replaced as a whole (snapshots are not built now);
   - inputs go through the `input-api` actions so they can be recorded or sent.
   Bit-exact determinism between the JVM and Android is **not** promised: `double` arithmetic is strict on the JVM since Java 17 but is not verified through DEX. Replay, rollback and prediction are not requirements.
2. **Budgets (`OQ-070`).** Default target: 60 updates per second (ADR-0017), rendering at the display rate. Budgets are set per platform tier and measured, not guessed. Desktop reference: 1,000 objects within `REQ-ECS-009` (at most 1 ms per step). Android reference: a mid-range API 21-era device is the floor; a frame completes in 16.6 ms; steady-state allocation in hot paths is zero (`REQ-RT-006`). Memory budget values per tier are filled in once the first benchmark exists (`OQ-139`).
3. **Desktop platforms (`OQ-071`).** Windows, macOS and Linux on the JDK 25 baseline (ADR-0001). A platform is *supported* only if CI runs the headless tests on it. First CI matrix: Linux (as `master`); Windows and macOS are added and claimed when verified. The AWT frontend needs the JDK `java.desktop` module. Minimum OS versions are those supported by JDK 25.

## Options considered

- Network: three cheap rules now (chosen) vs full rollback support now (expensive, no game to validate) vs ignoring the question (risk of a rewrite).
- Budgets: per-tier measured budgets (chosen) vs fixed numbers now (untestable before benchmarks exist).
- Desktop support: "supported means tested" (chosen) vs claiming all three immediately (unverified claims).

## Consequences

- Positive consequences: a later network mode stays possible at little cost; no unverified numbers or platform claims.
- Costs / constraints: no hard memory numbers yet; Windows and macOS claimed later than Linux; a seeded random source must be part of the world contract.
- Impact on modules and APIs: none yet; the seeded generator is an `ecs` or `core` follow-up (`OQ-158`).
- Impact on tests and documentation: new requirements added; `requirements.md`, `traceability.md`, `roadmap.md`, `testing.md` updated.

## Validation

Decisions confirmed by the project owner on 2026-11-23. Follow-ups: `OQ-158`.
