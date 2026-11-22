# ADR-0011 — ECS: dynamic objects and traits, queries, systems and standard traits

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner (acceptance requested in the session that followed the proposal: "accept ADR-0011 ... accept your proposal"; the proposal was built from the owner packet "ECS dynamic objects, traits & standard trait package")
- Related requirements: `REQ-ECS-001` to `REQ-ECS-010`, `REQ-STD-001` to `REQ-STD-009`, `REQ-OBJ-001`, `REQ-OBJ-002`, `REQ-TRAIT-001`, `REQ-TRAIT-002`, `REQ-CAM-004`, `REQ-LOOP-003`, `REQ-LOOP-006`
- Open questions resolved: none. Touched: `OQ-010`, `OQ-013`, `OQ-020`, `OQ-022`, `OQ-030`, `OQ-033`, `OQ-041`, `OQ-042`, `OQ-043`, `OQ-070`, `OQ-122`, `OQ-125`, `OQ-132`. Created: `OQ-137` to `OQ-146`.
- Supersedes / superseded by: none. Builds on [ADR-0001](0001-technical-baseline-and-conventions.md) and [ADR-0010](0010-camera-system-in-camera.md) (not modified).

## Context

The engine has no object model. `OQ-041` (is ECS the core model or an optional module) and `OQ-042` (are traits attached to classic objects or to ECS entities) are open, and `modules.md` lists `traits` depending on `world` and `ecs` as an optional module. The owner needs: add and remove objects by ID at runtime, add and remove traits dynamically, an efficient way to process the objects having given traits, an extensible standard trait package (gravity, jumping, movement, collision, ...), and enough performance for at least 1,000 objects (also on Android).

Inventory of existing ECS-related identifiers (packet item `OQ-NEW-1`): there is no ECS code and no ECS requirement. Existing references are the glossary entries (Component, ECS, Entity, System, Trait), `REQ-OBJ-001`, `REQ-OBJ-002` (strategies "including simple and ECS", `Could`), `REQ-TRAIT-001`, `REQ-TRAIT-002`, `OQ-041` to `OQ-044`, and the camera follow lookup contract (`REQ-CAM-004`, `OQ-132`, ADR-0010) whose entity ID type is still open. The packet item `OQ-NEW-2` (tile collision with a tile map) is the existing `OQ-125`, deliberately left to a separate packet.

## Decision

Decisions 1 to 10 below were proposed by an agent and accepted by the owner on 2026-11-22. The wording "Object" of the packet is the glossary's "Entity"; both name the opaque ID.

1. **Placement.** Two new modules: `ecs` (object IDs, trait registry and storage, queries, command buffer, events, system scheduling; depends on `core` only) and `traits` (the standard traits and systems; depends on `ecs` only, plus `core`). `traits` no longer depends on `world` (it did in `modules.md`); the relation between `world` and `ecs` is `OQ-146`.
2. **Object.** An opaque ID made of an index and a generation. Indices are recycled, the generation is incremented on removal, so a stale ID is detected. Using a stale or never-valid ID in an accessor fails fast with a defined error; a separate liveness check is safe and never throws. This is how a consumer such as the camera follow adapter (`REQ-CAM-004`, "a missing entity is safe") coexists with fail-fast accessors. Java representation: `OQ-137`.
3. **Trait.** Data only, no logic. Logic lives in systems. Each trait type is registered once at initialization and receives a small `int` type ID; registration is explicit (no reflection, no class scanning, no bytecode generation).
4. **Storage.** One sparse set per trait type: O(1) add, remove and has, and dense iteration. Removal swaps with the last element. A per-object bit mask of trait types supports the queries (more than 64 trait types are supported).
5. **Query (view).** A view selects the objects having all of a set of trait types and none of an exclusion set. A view caches its dense list of matching objects and is updated incrementally when traits are added or removed. Iterating a view allocates nothing.
6. **Structural changes are deferred.** Creating or removing an object and adding or removing a trait are recorded in a command buffer and applied at one point per simulation step (the flush), never during iteration. Data inside traits may be mutated at any time during an update. A flush may also be requested explicitly outside the loop, for setup (level loading) and tests. Semantics of IDs created before the flush and of conflicting commands: `OQ-138`.
7. **Events.** `onAdded` and `onRemoved` per trait type, and object created or destroyed, delivered synchronously during the flush in the update phase. Aligned later with the `core` event contract (`OQ-122`). This is the hook for future modules (camera, audio, map).
8. **Systems.** A system is logic over a view, executed in a fixed, deterministic order during the update phase; the order is given by an explicit ordering slot at registration, ties broken by registration order (details `OQ-141`). Rendering reads state only (`REQ-LOOP-006`); structural changes happen only at the flush (`REQ-LOOP-003`).
9. **Standard traits and systems** (package `traits`): `Transformable` (position, size, previous position for interpolation), `Movable` (velocity, maximum speed), `Gravity` (acceleration, terminal velocity), `Jumpable` (impulse, grounded, coyote time, jump buffer), `Collidable` (axis-aligned box, layer and mask, trigger flag), `Lifetime` (automatic destruction after a duration), with the matching `Movement`, `Gravity`, `Jump`, `Collision` (broad phase: uniform grid) and `Lifetime` systems. Candidate additions, not in the first batches: friction/drag, health, parent link (hierarchy), controller intent (input to movement), render link (sprite binding), tag (`OQ-144`).
10. **Units.** World units are meters, velocities m/s, accelerations m/s², time in seconds, all delta-based (ADR-0001). Y direction and gravity sign: `OQ-010`. Numeric type: `OQ-013`.

Invariants: no singleton (several worlds can coexist); no platform type in `ecs` or `traits`; no `java.awt`, no reflection or class scanning in any update path, no runtime bytecode generation (Android/DEX); no allocation per step in steady state for queries, system execution and the flush of already-sized buffers (a pooled trait instance is reused after removal). Threading: single-threaded, owned by the update thread (`OQ-030`).

Not decided (kept open): Java types and signatures, ID bit layout (`OQ-137`), flush and pending-ID semantics (`OQ-138`), numeric benchmark budget and its measurement (`OQ-139`), trait storage layout and pooling (`OQ-140`), system scheduling details (`OQ-141`), collision and physics-contract details including tile collision (`OQ-142`, `OQ-043`, `OQ-125`), jump and ground details (`OQ-143`), additional standard traits (`OQ-144`), iteration-order guarantee (`OQ-145`), `world` relation (`OQ-146`).

## Options considered

### Placement
- A (proposed): `ecs` generic module plus `traits` standard package on top, both under `core` rules. Reusable ECS, replaceable standard set, matches the packet constraint "standard traits depend on core ECS only".
- B: a single module. Mixes the mechanism and the game vocabulary; a game that wants only its own traits still depends on the standard ones.
- C: ECS optional and objects first in `world` (the `OQ-041` status quo). Delays the capabilities the owner asks for and duplicates the model.

### Storage
- A (proposed): sparse set per trait type with cached views. O(1) structural operations, dense iteration, simple, no reflection, predictable memory.
- B: archetype tables (objects grouped by exact trait set). Best iteration locality, but every trait change moves the object between tables, which is costly for the frequent add/remove the owner wants and complex to implement.
- C: a per-object array of trait slots, scanning all objects. Simple but iteration is O(all objects) for each system.

### Object ID
- A (proposed): index plus generation. Safe recycling and stale detection with no allocation.
- B: monotonically increasing `long`. Never stale, but needs a hash map from ID to data, slower and allocating.
- C: object references. Cannot detect removal and keeps removed objects alive.

### Structural changes
- A (proposed): command buffer flushed at the tick boundary. Iteration is always safe and deterministic.
- B: immediate changes. Simpler API, but a system removing a trait while iterating a view invalidates the iteration.

### Trait storage layout
- A (proposed, to confirm by measurement: `OQ-140`): trait instances in dense arrays, reused through pooling. Simple, extensible by game code.
- B: primitive arrays per field (structure of arrays) for standard traits. Fastest and allocation-free by construction, but a less friendly API and harder for extension traits.

## Consequences

- Positive consequences: one extensible model for dynamic objects; iteration cost proportional to the matching objects, not to all objects; safe stale-ID handling; deterministic update order; no reflection (Android/DEX unaffected); the standard set doubles as the reference for writing new traits and systems.
- Costs / constraints: trait changes are visible only after the flush, which callers must understand (`OQ-138`); swap-remove reorders dense arrays, so iteration order is deterministic for a given sequence of operations but is not creation order (`OQ-145`); views and masks use memory proportional to objects × trait types; the first measurements may force the layout change `OQ-140`.
- Impact on modules and APIs: new modules `ecs` and `traits`; the `traits` dependency in `modules.md` changes from `world` to `ecs` if accepted; the camera lookup adapter (ADR-0010, `OQ-132`) can be implemented over the liveness check. No physical module exists until the build skeleton (`OQ-003`), so this ADR changes no build file.
- Impact on tests and documentation: unit tests of IDs, storage, views, command buffer and events; contract tests for the trait and system conventions; a headless integration test of the standard systems; a headless benchmark of 1,000 objects × 4 traits; feature files `features/ecs.md` and `features/standard-traits.md`; updated `requirements.md`, `traceability.md`, `open-questions.md`, `modules.md`, `contracts.md`, `roadmap.md`, `testing.md`, `api-index.md`, `glossary.md`. No new dependency (a benchmark tool, if later chosen, needs its own license and Android record: `OQ-139`).

## Validation

Accepted by the project owner on 2026-11-22 (explicit request in the session, accepting decisions 1 to 10 as proposed). Acceptance resolves no open question: `OQ-041` and `OQ-042` stay open and `OQ-137` to `OQ-146` stay open. The related requirements `REQ-ECS-001` to `REQ-ECS-010` and `REQ-STD-001` to `REQ-STD-009` stay PROPOSED until validated.
