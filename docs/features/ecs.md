# Feature: ECS dynamic objects, traits, queries and systems

**Status: PROPOSED**

- Owner: to be filled in
- Roadmap phase: Phase 3, see `roadmap.md` (needs the Phase 0 build skeleton)
- Flow: B of [`change-process.md`](../change-process.md) (new module `ecs`); ADR required and written
- Affected modules: `ecs`, `core`, `test-support`, `examples`
- Requirements: `REQ-ECS-001` to `REQ-ECS-010`
- Applicable ADRs: [`ADR-0011`](../decisions/0011-ecs-dynamic-objects-traits-and-standard-traits.md) (ACCEPTED), [`ADR-0001`](../decisions/0001-technical-baseline-and-conventions.md)
- Open questions involved: `OQ-020`, `OQ-030`, `OQ-122`, `OQ-164` (`OQ-137` to `OQ-141`, `OQ-145` resolved by ADR-0030)

## Objective

Create and remove objects at runtime by ID, add and remove traits dynamically, process the objects having given traits efficiently (at least 1,000 objects), and let games extend the model with their own traits and systems.

## Out of scope

Standard traits and systems (`features/standard-traits.md`); scenes (lifecycle contract in `runtime`, ADR-0021); networking and replication; multithreaded systems (`OQ-030`); serialization of objects; a camera adapter (ADR-0029).

## Expected behavior

- Create returns an opaque ID; remove by ID removes the object and all its traits at the flush.
- A stale or never-valid ID fails fast in accessors; the liveness check never throws; indices are recycled with a new generation.
- Trait types are registered once with an `int` ID; adding a trait, removing it, replacing it and `has` follow the flush rules.
- A query with include and exclude sets keeps a cached dense list updated incrementally; iteration allocates nothing.
- Structural changes requested during an update, including from inside a query iteration, are deferred and applied at one flush per step; events are delivered during the flush.
- Systems run in a fixed order by ordering slot; registration order breaks ties.

## Envisaged APIs / contracts

Responsibilities only; signatures are not fixed (ADR-0011, `architecture/contracts.md`): object ID and world, trait type registry, trait storage, query, command buffer and flush, event listeners, system contract and ordering slot.

## Batches (proposed order)

1. IDs, world, create/remove, liveness (`REQ-ECS-001`, `REQ-ECS-002`, `REQ-ECS-010`).
2. Trait registry, sparse-set storage, deferred add/remove and flush (`REQ-ECS-003`, `REQ-ECS-004`, `REQ-ECS-006`).
3. Cached queries with include/exclude (`REQ-ECS-005`).
4. Events (`REQ-ECS-007`) and system ordering (`REQ-ECS-008`).
5. Benchmark and allocation check at 1,000 objects × 4 traits (`REQ-ECS-009`); layout change if needed (ADR-0030).

## Lifecycle and concurrency

A world is created and owned by the game; no singleton. All calls come from the update thread (`OQ-030`). Structural changes happen only at the flush (`REQ-LOOP-003`); rendering only reads state (`REQ-LOOP-006`). Trait instances are owned by the world and reused after removal; a reference to a removed trait must not be kept.

## Units and Android/DEX compatibility

No unit of its own (time is passed in seconds by the systems). Plain Java arrays and collections only; no reflection, class scanning, bytecode generation or `java.awt`; to be confirmed with `android-compatibility.md` once the types are chosen.

## Dependencies

`core` only. Forbidden: a platform or frontend type, a dependency from `core` or an `*-api` module on `ecs`.

## Tests

- Unit tests: ID recycling and stale access, create/remove, trait add/remove/replace, include/exclude queries, deferred changes inside iteration, event order, system order.
- Contract tests: trait and system conventions in `test-support`.
- Integration tests: a headless world driven by a controllable clock.
- Platform/performance tests: benchmark 1,000 objects × 4 traits and 10,000 objects scaling; allocation check; no wall-clock assertion that could make CI fragile (ADR-0030).

## Acceptance criteria

- [ ] Requirements `REQ-ECS-001` to `REQ-ECS-010` verified.
- [ ] ADR-0011 accepted by the owner and the open questions of this file settled or explicitly assumed.
- [ ] Automated tests added and passing; complete Javadoc.
- [ ] Headless example (game-defined trait and system) and `api-index.md` entry.
- [ ] No forbidden coupling; Android/DEX compatibility verified.
