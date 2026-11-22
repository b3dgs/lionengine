# Feature: standard traits and systems

**Status: PROPOSED**

- Owner: to be filled in
- Roadmap phase: Phase 3, see `roadmap.md`
- Flow: B of [`change-process.md`](../change-process.md) (new module `traits`); ADR required and written
- Affected modules: `traits`, `ecs`, `core`, `test-support`, `examples`
- Requirements: `REQ-STD-001` to `REQ-STD-009`
- Applicable ADRs: [`ADR-0011`](../decisions/0011-ecs-dynamic-objects-traits-and-standard-traits.md) (PROPOSED), [`ADR-0001`](../decisions/0001-technical-baseline-and-conventions.md)
- Open questions involved: `OQ-010`, `OQ-013`, `OQ-022`, `OQ-043`, `OQ-125`, `OQ-142` to `OQ-144`

## Objective

A standard, documented set of traits and systems expected from a 2D game engine, built only on `ecs`, that also serves as the model for game-defined traits.

## Out of scope

Tile collision (`OQ-125`, separate packet); shapes other than axis-aligned boxes and physical response beyond what is specified (`OQ-142`); rendering and animation links; the candidates of `REQ-STD-009`.

## Expected behavior

Systems run in this proposed relative order each step: `Jump` (reads requests), `Gravity`, `Movement`, `Collision`, `Lifetime`; structural removals are then applied at the flush.

| Trait | Data | System behavior |
|---|---|---|
| `Transformable` | position, size, previous position (meters) | previous position captured at the step start |
| `Movable` | velocity (m/s), maximum speed (m/s) | integrates velocity into position with the step in seconds; clamps speed |
| `Gravity` | acceleration (m/s²), terminal velocity (m/s) | accelerates `Movable`, capped at the terminal velocity |
| `Jumpable` | impulse, grounded, coyote time (s), jump buffer (s) | applies the impulse on a request when grounded, within the coyote time, or when a buffered request meets the ground |
| `Collidable` | axis-aligned box, layer, mask, trigger flag | uniform-grid broad phase; reports overlapping pairs; triggers do not block |
| `Lifetime` | duration (s) | requests removal on expiry |

The order, signs (`OQ-010`) and defaults (`OQ-143`) are proposals until accepted.

## Envisaged APIs / contracts

Responsibilities only; signatures are not fixed. Each trait is data only; each system implements the `ecs` system contract and declares its ordering slot.

## Batches (proposed order)

1. `Transformable`, `Movable`, `Movement` (`REQ-STD-001`, `REQ-STD-002`).
2. `Gravity` (`REQ-STD-003`) and `Lifetime` (`REQ-STD-006`).
3. `Collidable` and `Collision` with the uniform grid (`REQ-STD-005`).
4. `Jumpable` and `Jump` (`REQ-STD-004`).
5. Pipeline documentation, example and extension guide (`REQ-STD-007`, `REQ-STD-008`).

## Lifecycle and concurrency

Same as `ecs`: update thread only, structural changes at the flush, no singleton.

## Units and Android/DEX compatibility

Meters, m/s, m/s², seconds, delta-based (REQ-TECH-003 to REQ-TECH-005). Pure math, no reflection, no `java.awt`; numeric type `OQ-013`.

## Dependencies

`ecs` and `core` only. Forbidden: `world`, rendering, input, platform modules.

## Tests

- Unit tests: each system at several step durations (movement, terminal velocity, jump windows, expiry); collision overlap, layer/mask and trigger.
- Contract tests: trait and system conventions shared with `ecs`.
- Integration tests: headless pipeline with a controllable clock (falling, jumping and colliding object).
- Platform/performance tests: broad phase against a brute-force reference; part of the 1,000 objects × 4 traits budget (`REQ-ECS-009`).

## Acceptance criteria

- [ ] Requirements `REQ-STD-001` to `REQ-STD-009` verified.
- [ ] ADR-0011 accepted; `OQ-010`, `OQ-013`, `OQ-142`, `OQ-143` settled or explicitly assumed.
- [ ] Automated tests added and passing; complete Javadoc for every trait and system.
- [ ] Example and `api-index.md` entry; no forbidden coupling; Android/DEX compatibility verified.
