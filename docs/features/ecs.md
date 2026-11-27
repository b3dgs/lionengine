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

## State (2026-11-27)

Batch 1 (2026-11-27), IDs, world, creation and removal, liveness and the explicit flush, with the example `ObjectLifecycleExample` and its API-index row. The signatures and details this page leaves to the batch follow [ADR-0074](../decisions/0074-ecs-object-ids-world-creation-removal-and-flush.md) (owner delegation):
- `World` (`new World()` with `WorldConfig.DEFAULT`, or `new World(config)`) and `WorldConfig(objectCapacity)`, 1,024 by default, at most 2<sup>30</sup>; `World.NO_OBJECT` is `0`.
- `create()` returns a positive packed `long` at once; the object is pending until `flush()`: `isAlive` is `false`, the accessors of the next batches fail fast with `IllegalStateException`. `remove(id)` records the removal of a pending or alive object, applied at the flush; a stale or never-valid ID is refused with `IllegalArgumentException`; a removal recorded twice is applied once. `isAlive` never throws.
- Generations from 1 to 2<sup>31</sup> − 1; a removed index takes the next generation and joins the back of a free queue, the oldest free index reused first, never before the flush that frees it; an index at the last generation is retired, so no ID repeats.
- Capacity: creating beyond it issues pending indices without allocating; the flush doubles the table until they fit; `reserve(n)` grows it ahead; never shrinks. The command log starts at the object capacity and doubles when a step records more commands. `getObjectCount()` and `getObjectCapacity()`.
- No allocation on the steady create, remove and flush paths: `WorldAllocationTest` measures less than 1 KiB over 1,000 steps of 1,000 removals and 1,000 creations (ADR-0074, decision 9; the benchmark is batch 6).
- Tests: `ObjectTableTest`, `WorldTest`, `WorldFlushOrderTest` (regression set of the flush order, reproducibility), `WorldConfigTest`, `WorldAllocationTest`; the device test `EcsTest`.

Batch 2 (2026-11-27), the trait registry, pooled storage and deferred trait changes, with the example `TraitExample` (game-defined trait `Fuse`) and its API-index row. The signatures and details this page leaves to the batch follow [ADR-0075](../decisions/0075-ecs-trait-registry-pooled-storage-deferred-trait-changes.md) (owner delegation):
- `TraitType<T>`, implemented by the game: `create()` a new instance in its reset state, `reset(trait)` back to it. `World.register(type)` gives the next type ID (0, then consecutive) and returns the typed `TraitStorage<T>` used for every access, without lookup; a type registered twice in a world, or `null`, is an `IllegalArgumentException`; the same type may be registered in several worlds; any number of types (mask words of 64 types).
- `TraitStorage.add(id)` hands out a pooled instance at once, to be filled, and attaches it at the flush, on an alive or pending object; an addition over an existing trait replaces it (the last wins: "replace" of this page); `remove(id)` detaches at the flush; an addition then a removal in one step both apply; commands on a trait or an object already gone at that point of the flush change nothing; removing an object removes its traits by ascending type ID, then the object. Object and trait commands share the batch 1 log, so they keep one recorded order.
- `has(id)` and `get(id)` read the state of the last flush through `checkAlive`: `IllegalStateException` for a pending object, `IllegalArgumentException` for a stale or never-valid ID; `get` without the trait is an `IllegalStateException`. Removed and replaced instances are reset and pooled.
- Capacity: `WorldConfig(objectCapacity, traitCapacity)`, 256 traits per type by default, at most 2<sup>30</sup>; each storage creates that many instances at registration; the flush doubles the dense array for the attached and staged traits; `reserve(n)` grows it ahead; never shrinks. Seams of the next batches: the per-object trait masks (queries) and the per-change hook, called for effective changes only, a replacement as a removal then an addition (events).
- No allocation on the steady add, remove, replace and flush paths: `TraitAllocationTest` measures 0 bytes over 1,000 steps of 1,000 objects removed and created with two traits and a third added and removed (about 7 million commands; ADR-0075, decision 10).
- Tests: `TraitStorageTest`, `TraitFlushOrderTest` (regression set of the mixed flush order, reproducibility), `TraitAllocationTest`, `WorldConfigTest`, `TraitExampleTest`; the device test `EcsTest` (`testTraits`, `testTraitGrowth`).

Next: batch 3, the cached queries with include and exclude sets and the ID copy.

## Lifecycle and concurrency

A world is created and owned by the game or its scene; no singleton. All calls come from the runtime thread. Structural changes happen only at the flush (`REQ-LOOP-003`); rendering only reads state (`REQ-LOOP-006`).

## Units and Android/DEX compatibility

No unit of its own (systems receive the step in seconds). Plain Java arrays only; no `HashMap` iteration where order is observable, no reflection, class scanning, bytecode generation or `java.awt`; checked with `android-compatibility.md` in each batch.

## Dependencies

`core` only. Forbidden: a platform or frontend type, a dependency from `core` or an `*-api` module on `ecs`.

## Tests

- Unit tests: ID packing, pending and stale IDs, recycling over many cycles, create/remove, trait add/remove/replace, include/exclude queries, deferred changes inside iteration, add-then-remove events, event order, system order and enabled flag, capacity growth, random generator reference vectors.
- Contract tests (`ecs` test JAR, ADR-0045): trait and system conventions, run by `traits`, `multiplayer` and games.
- Integration tests: a headless world driven by a controllable clock; two identical runs give the same order and state.
- Performance tests: 1,000 objects × 4 traits and 10,000 objects scaling; zero steady-state allocation asserted in CI; wall-clock in the non-blocking trend job.

## Acceptance criteria

- [ ] Requirements `REQ-ECS-001` to `REQ-ECS-013` verified.
- [ ] Automated tests added and passing; complete Javadoc.
- [ ] Headless example (game-defined trait and system) and `api-index.md` entry.
- [ ] No forbidden coupling; Android/DEX compatibility verified.
