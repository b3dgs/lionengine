# Feature: standard traits and systems

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 3, see `roadmap.md`
- Flow: B of [`change-process.md`](../change-process.md) (new modules `traits` and `physics`); ADRs written and accepted
- Affected modules: `traits`, `physics`, `ecs`, `core`, `test-support`, `examples`
- Requirements: `REQ-STD-001` to `REQ-STD-011`, `REQ-PHYS-001`, `REQ-TRAIT-001`, `REQ-TRAIT-002`
- Applicable ADRs: [`ADR-0011`](../decisions/0011-ecs-dynamic-objects-traits-and-standard-traits.md), [`ADR-0013`](../decisions/0013-world-coordinates-and-angles.md), [`ADR-0014`](../decisions/0014-meters-to-pixels-numeric-type-and-rotation.md), [`ADR-0021`](../decisions/0021-scene-world-viewport-and-object-model.md), [`ADR-0028`](../decisions/0028-tile-map-collision-json-definitions-and-storage.md), [`ADR-0030`](../decisions/0030-ecs-details-ids-flush-scheduling-collision-and-traits.md), [`ADR-0034`](../decisions/0034-camera-and-ecs-defaults-noise-clamp-slots-capacities-events-shapes.md), [`ADR-0038`](../decisions/0038-network-model-server-authority-snapshots-prediction.md)
- Open questions involved: none

## Objective

A standard, documented set of traits and systems expected from a 2D game engine, built only on `ecs` and `physics`, that also serves as the model for game-defined traits.

## Out of scope

Shapes other than axis-aligned boxes, one-way tiles, slopes, swept detection and object-versus-object response (later, ADR-0034); rendering and animation links; the candidates of `REQ-STD-009`.

## Expected behavior

Systems run in this order each step, by slot: `Jump` (1000), `Gravity` (2000), `Movement` (3000), `Collision` (4000), `Lifetime` (5000); the flush then applies structural changes. Game systems take slots between them (ADR-0034). Each standard system accepts, when created, additional trait types that its query excludes (default none), so that objects carrying them are skipped, for example the `NetProxy` trait of network proxies (`REQ-STD-011`, ADR-0038).

| Trait | Data (units) | System behavior |
|---|---|---|
| `Transformable` | position of the object center, size (full width and height), previous position (meters, `double`) | previous position captured at the step start, for interpolation with `alpha` |
| `Movable` | velocity (m/s), maximum speed (m/s) | `Movement`: integrates velocity into position with the step in seconds; clamps the speed |
| `Gravity` | acceleration (m/s², negative Y pulls down), terminal velocity (m/s, a magnitude) | `Gravity`: accelerates `Movable`, never beyond the terminal velocity |
| `Jumpable` | impulse (m/s, positive), grounded, coyote time (s, default 0.1), jump buffer (s, default 0.1), variable-height cut factor (default 0.5, off by default) | `Jump`: applies the impulse on a request when grounded, within the coyote time, or when a buffered request meets the ground; on early release while ascending, multiplies the upward velocity by the cut factor when enabled |
| `Collidable` | axis-aligned box: width, height and center offset from the position (meters, offset default `(0, 0)`), layer, mask, trigger flag | `Collision`: uniform-grid broad phase (cell size per world, default 2 m); a pair collides only if each mask includes the other's layer; reports enter and exit events after the system; triggers never block; tiles block through an axis-separated push-out that sets `grounded` from a contact below |
| `Lifetime` | duration (s) | `Lifetime`: requests removal on expiry (applied at the flush) |

Registry ids for object definition files: `lionengine:transformable`, `lionengine:movable`, `lionengine:gravity`, `lionengine:jumpable`, `lionengine:collidable`, `lionengine:lifetime`; parameter names are the field names above in lower camel case (`maxSpeed`, `terminalVelocity`, `coyoteTime`, ...) (ADR-0032).

Collision rule without swept detection: per-step displacement must stay under half the smallest collider, helped by the `Movable` speed clamp.

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Objects and traits); canonical type names in [ADR-0037](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md); signatures written by the batch. Each trait is data only; each system implements the `ecs` system contract and declares its slot; `physics` provides the box shape, overlap test, shape-query contract (implemented by `tilemap`) and broad phase.

## Batches (order)

1. `Transformable`, `Movable`, `Movement` (`REQ-STD-001`, `REQ-STD-002`).
2. `Gravity` (`REQ-STD-003`) and `Lifetime` (`REQ-STD-006`).
3. `physics` box, overlap and broad phase; `Collidable` and `Collision` for objects (`REQ-PHYS-001`, `REQ-STD-005`).
4. Tile push-out and `grounded` through the `physics` shape query (`REQ-STD-010`, `REQ-MAP-008`).
5. `Jumpable` and `Jump` (`REQ-STD-004`).
6. Pipeline documentation, example and extension guide (`REQ-STD-007`, `REQ-STD-008`).
7. Extra excluded trait types at system creation (`REQ-STD-011`); may be done with any earlier batch.

## Lifecycle and concurrency

Same as `ecs`: runtime thread only, structural changes at the flush, no singleton.

## Units and Android/DEX compatibility

Meters, m/s, m/s², seconds, constant step (REQ-TECH-003 to REQ-TECH-005); `double`. Pure math, no reflection, no `java.awt`.

## Dependencies

`traits` on `ecs`, `physics`, `core`; `physics` on `core`. Forbidden: rendering, input, platform modules, `tilemap` (tile collision goes through the `physics` shape-query contract).

## Tests

- Unit tests: each system at several step durations (movement and clamp, terminal velocity, jump windows and cut, expiry); overlap, layer/mask pairs, enter and exit events, push-out and grounded on a tile floor.
- Contract tests: trait and system conventions shared with `ecs`; shape-query contract suite run by `tilemap`.
- Integration tests: headless pipeline with a controllable clock (falling, landing, jumping and colliding object).
- Performance tests: broad phase against a brute-force reference; part of the 1,000 objects × 4 traits budget (`REQ-ECS-009`). The `ecs` benchmark runs on stand-ins of `Transformable`, `Movable`, `Gravity` and `Lifetime` until these exist ([ADR-0079](../decisions/0079-ecs-benchmark-harness-stand-in-scene-and-trend-job.md)): the batch that delivers the last of the four replaces the stand-ins with them, measures again with the `ecs` harness (`StepHarness`, tag `benchmark`) and records the values in `testing.md`; each standard trait records its bytes per instance in its Javadoc, measured the same way (ADR-0034).

## Acceptance criteria

- [ ] Requirements `REQ-STD-001` to `REQ-STD-010` verified.
- [ ] Automated tests added and passing; complete Javadoc for every trait and system, with units, slot and bytes per instance.
- [ ] Example and `api-index.md` entry; no forbidden coupling; Android/DEX compatibility verified.
