# Feature: ECS dynamic objects, traits, queries and systems

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 3, see `roadmap.md` (needs the Phase 0 build skeleton)
- Flow: B of [`change-process.md`](../change-process.md) (new module `ecs`); ADRs written and accepted
- Affected modules: `ecs`, `core`, `test-support`, `examples`
- Requirements: `REQ-ECS-001` to `REQ-ECS-013`, `REQ-WORLD-001`, `REQ-OBJ-001`, `REQ-OBJ-002`, `REQ-DET-001`, `REQ-DET-003`
- Applicable ADRs: [`ADR-0011`](../decisions/0011-ecs-dynamic-objects-traits-and-standard-traits.md), [`ADR-0018`](../decisions/0018-runtime-thread-events-and-cache-thread-safety.md), [`ADR-0021`](../decisions/0021-scene-world-viewport-and-object-model.md), [`ADR-0030`](../decisions/0030-ecs-details-ids-flush-scheduling-collision-and-traits.md), [`ADR-0031`](../decisions/0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md), [`ADR-0034`](../decisions/0034-camera-and-ecs-defaults-noise-clamp-slots-capacities-events-shapes.md)
- Open questions involved: none (Android benchmark once the Android job exists, ADR-0044)

## Objective

Create and remove objects at runtime by ID, add and remove traits dynamically, process the objects having given traits efficiently (at least 1,000 objects), and let games extend the model with their own traits and systems.

## Out of scope

Standard traits and systems (`features/standard-traits.md`); scenes (lifecycle contract in `runtime`); networking and replication (module `multiplayer`, [`multiplayer.md`](multiplayer.md)); multithreaded systems; serialization of objects; the camera follow adapter (`composition`).

## Expected behavior

- A `World` is created by the game with a configuration (capacities: 1,024 objects and 256 instances per trait type by default; grid cell size for collision; random seed). Several worlds coexist.
- **IDs:** `create` returns a packed `long` (generation high 32 bits, index low 32 bits; generation starts at 1, so `0` is never valid). The object is pending until the flush: `isAlive` returns false and accessors fail fast, but commands may already be queued with the ID. `remove` removes the object and all its traits at the flush; the index is recycled with a new generation; a stale ID fails fast in accessors, while `isAlive` never throws.
- **Traits:** data only; each type registered once with an `int` type ID (duplicate registration fails); instances pooled in dense arrays and reset on removal; add, remove, replace and `has` follow the flush rules; a reference to a removed trait must not be kept.
- **Queries:** include and exclude sets; a cached dense list updated incrementally; iteration allocates nothing; the order is deterministic for the same operation sequence and is not creation order; `copySortedIds(long[])` copies ascending IDs into a caller buffer.
- **Command buffer and flush:** structural changes requested during an update (including from inside a query iteration, and from inbox events) are recorded and applied in recorded order at one flush per step, at the end, after all systems and before rendering; an add followed by a remove both apply and both fire events; explicit `flush` is allowed outside the loop (setup, tests) and between two world updates (snapshot application by `multiplayer`, ADR-0038); capacity doubles at the flush when exceeded and never shrinks.
- **Events:** listeners with primitive arguments, synchronous on the runtime thread: created and destroyed `(long id)`, trait added and removed `(long id, int traitTypeId)` during the flush.
- **Systems:** registered with an `int` slot (lower first, ties by registration order) and an enabled flag applied at the next step; `world.update(stepSeconds)` runs them with the constant step, then flushes. An exception propagates; the world is undefined afterwards and the loop fails (`REQ-LOOP-010`).
- **Random source:** the world owns a SplitMix64-seeded xoshiro256\*\* generator (REQ-DET-003).
- **Object definitions:** a `lionengine.object` JSON file maps trait registry ids to parameter objects; creating an object from it queues the create and one add per trait for the next flush (ADR-0032). Each trait type is registered with its id and a factory reading a typed configuration; unknown ids, duplicate registrations and invalid or unread parameters fail with the definition exception.

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Objects and traits); canonical type names in [ADR-0037](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md); signatures written by the batch: `World`, world configuration, trait type registry, trait storage, query, command buffer, listeners, system contract.

## Batches (order)

1. IDs, world, create/remove, liveness, explicit flush (`REQ-ECS-001`, `REQ-ECS-002`, `REQ-ECS-010`, `REQ-ECS-011`).
2. Trait registry, pooled storage, deferred add/remove and flush (`REQ-ECS-003`, `REQ-ECS-004`, `REQ-ECS-006`, `REQ-ECS-012`).
3. Cached queries with include/exclude and ID copy (`REQ-ECS-005`).
4. Events and system ordering (`REQ-ECS-007`, `REQ-ECS-008`, `REQ-ECS-013`).
5. Random source (`REQ-DET-003`).
6. Benchmark and allocation check at 1,000 objects × 4 traits; primitive arrays for `Transformable` and `Movable` only if the budget is missed (`REQ-ECS-009`).

## Lifecycle and concurrency

A world is created and owned by the game or its scene; no singleton. All calls come from the runtime thread. Structural changes happen only at the flush (`REQ-LOOP-003`); rendering only reads state (`REQ-LOOP-006`).

## Units and Android/DEX compatibility

No unit of its own (systems receive the step in seconds). Plain Java arrays only; no `HashMap` iteration where order is observable, no reflection, class scanning, bytecode generation or `java.awt`; checked with `android-compatibility.md` in each batch.

## Dependencies

`core` only. Forbidden: a platform or frontend type, a dependency from `core` or an `*-api` module on `ecs`.

## Tests

- Unit tests: ID packing, pending and stale IDs, recycling over many cycles, create/remove, trait add/remove/replace, include/exclude queries, deferred changes inside iteration, add-then-remove events, event order, system order and enabled flag, capacity growth, random generator reference vectors.
- Contract tests (`test-support`): trait and system conventions.
- Integration tests: a headless world driven by a controllable clock; two identical runs give the same order and state.
- Performance tests: 1,000 objects × 4 traits and 10,000 objects scaling; zero steady-state allocation asserted in CI; wall-clock in the non-blocking trend job.

## Acceptance criteria

- [ ] Requirements `REQ-ECS-001` to `REQ-ECS-013` verified.
- [ ] Automated tests added and passing; complete Javadoc.
- [ ] Headless example (game-defined trait and system) and `api-index.md` entry.
- [ ] No forbidden coupling; Android/DEX compatibility verified.
