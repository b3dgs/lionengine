# Feature: standard traits and systems

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 3, see `roadmap.md`
- Flow: B of [`change-process.md`](../change-process.md) (new modules `traits` and `physics`); ADRs written and accepted
- Affected modules: `traits`, `physics`, `ecs`, `core`, `test-support`, `examples`
- Requirements: `REQ-STD-001` to `REQ-STD-011`, `REQ-PHYS-001`, `REQ-TRAIT-001`, `REQ-TRAIT-002`
- Applicable ADRs: [`ADR-0080`](../decisions/0080-standard-traits-transformable-movable-capture-and-movement.md) (batch 1), [`ADR-0081`](../decisions/0081-standard-traits-gravity-lifetime-and-benchmark-on-standard-traits.md) (batch 2), [`ADR-0082`](../decisions/0082-standard-traits-collision-box-broad-phase-collidable-and-events.md) (batch 3), [`ADR-0083`](../decisions/0083-standard-traits-tile-push-out-grounded-and-shape-query-suite.md) (batch 4), [`ADR-0011`](../decisions/0011-ecs-dynamic-objects-traits-and-standard-traits.md), [`ADR-0013`](../decisions/0013-world-coordinates-and-angles.md), [`ADR-0014`](../decisions/0014-meters-to-pixels-numeric-type-and-rotation.md), [`ADR-0021`](../decisions/0021-scene-world-viewport-and-object-model.md), [`ADR-0028`](../decisions/0028-tile-map-collision-json-definitions-and-storage.md), [`ADR-0030`](../decisions/0030-ecs-details-ids-flush-scheduling-collision-and-traits.md), [`ADR-0034`](../decisions/0034-camera-and-ecs-defaults-noise-clamp-slots-capacities-events-shapes.md), [`ADR-0038`](../decisions/0038-network-model-server-authority-snapshots-prediction.md)
- Open questions involved: `OQ-178` (the batch of the object definition files)

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

1. `Transformable`, `Movable`, `Movement` (`REQ-STD-001`, `REQ-STD-002`); done on 2026-11-28 with the previous-position capture and `REQ-STD-011` for its two systems (ADR-0080).
2. `Gravity` (`REQ-STD-003`) and `Lifetime` (`REQ-STD-006`); done on 2026-11-28 with `REQ-STD-011` for their two systems and the benchmark measured again on the standard traits (ADR-0081).
3. `physics` box, overlap and broad phase; `Collidable` and `Collision` for objects (`REQ-PHYS-001`, `REQ-STD-005`); done on 2026-11-28 with the shape-query contract, the enter and exit events and `REQ-STD-011` for its system (ADR-0082).
4. Tile push-out and `grounded` through the `physics` shape query (`REQ-STD-010`, `REQ-MAP-008`); done on 2026-11-28 against `ShapeQuery` only, with `Jumpable` brought forward as data, the shape-query contract suite and `REQ-STD-011` for the push-out (ADR-0083).
5. `Jumpable` and `Jump` (`REQ-STD-004`).
6. Pipeline documentation, example and extension guide (`REQ-STD-007`, `REQ-STD-008`).
7. Extra excluded trait types at system creation (`REQ-STD-011`); may be done with any earlier batch (done for `TransformCaptureSystem` and `MovementSystem` by batch 1, for `GravitySystem` and `LifetimeSystem` by batch 2, for `CollisionSystem` by batch 3 and for its tile push-out by batch 4).

## State (2026-11-28)

Batch 1 (2026-11-28), `Transformable`, `Movable`, the previous-position capture and the `Movement` system, with the example `MovementExample` and its API-index row. The signatures and details this page leaves to the batch follow [ADR-0080](../decisions/0080-standard-traits-transformable-movable-capture-and-movement.md) (owner answers and delegation of the batch):

- `Transformable` (center position, full size, previous position, m) and `Movable` (velocity, maximum speed, m/s), data only, private `double` fields with accessors; each class has a public `TraitType` constant `TYPE`, registered by the game in each world (`world.register(Transformable.TYPE)`). Reset state: every field `0`, and no speed limit (`Double.POSITIVE_INFINITY`); a negative or `NaN` maximum speed is an `IllegalArgumentException`. A game placing or teleporting an object sets its previous position too.
- `TransformCaptureSystem` at slot `Integer.MIN_VALUE`, before every other system: copies the position into the previous position of every `Transformable`, with or without `Movable`, so the previous position is the one of the step start; registered first among the systems of its slot.
- `MovementSystem` at slot 3000: clamps the velocity on its magnitude, direction kept, then adds `velocity * stepSeconds` to the position (explicit Euler, constant step).
- Both systems are created with the world and the storages of `register`, and optionally an array of excluded trait types (`REQ-STD-011`, done for these two); each has one query, exposed by `getQuery()`, and allocates nothing per step (`MovementAllocationTest`, 1 KiB tolerance over 1,000 steps of 1,000 objects).
- Bytes per instance on the desktop JVM, in the Javadoc: 64 per `Transformable`, 40 per `Movable` (`TraitMemoryTest`, the allocation-counter method of `StepBenchmarkTest`). The `ecs` benchmark is measured again with batch 2, once `Gravity` and `Lifetime` exist (ADR-0079).
- No factory for `lionengine:transformable` and `lionengine:movable` yet: the object definition files get a separate batch (`OQ-178`).
- Tests: `TransformableTest`, `MovableTest`, `TransformCaptureSystemTest`, `MovementSystemTest` (steps of 1/30, 1/60 and 1/120 s), `MovementAllocationTest`, `TraitMemoryTest`, `MovementExampleTest`; the device test `TraitsTest`.

Batch 2 (2026-11-28), `Gravity` and `Lifetime` with their systems, the example `FallExample` and its API-index row, and the `REQ-ECS-009` benchmark on the standard traits. The details this page leaves to the batch follow [ADR-0081](../decisions/0081-standard-traits-gravity-lifetime-and-benchmark-on-standard-traits.md) (owner answers and delegation of the batch):

- `Gravity` (acceleration, m/s², and terminal velocity, m/s) and `Lifetime` (duration, s), data only, private `double` fields with accessors (`getAcceleration`, `setAcceleration`, `getTerminalVelocity`, `setTerminalVelocity`, `getDuration`, `setDuration`) and a public `TYPE` constant each. The acceleration is vertical only, negative pulls down. Reset state: no acceleration and no terminal velocity (`Double.POSITIVE_INFINITY`); a lifetime that never expires (`Double.POSITIVE_INFINITY`). A negative or `NaN` terminal velocity or duration is an `IllegalArgumentException`.
- `GravitySystem` at slot 2000, on the objects having `Movable` and `Gravity` (no `Transformable` needed; an object without `Movable` is skipped): adds `acceleration * stepSeconds` to the vertical velocity, never past the terminal velocity in the direction of the acceleration (`-terminalVelocity` for a pull down); a vertical velocity already beyond it is kept, neither cut nor increased; the horizontal velocity is unchanged; `MovementSystem` then clamps the whole speed to `maxSpeed`.
- `LifetimeSystem` at slot 5000, on the objects having `Lifetime`: the duration is the remaining one, counted down in place by the step; at or under `0` the system requests `World.remove(id)`, applied by the flush ending the step (systems after 5000 still see the object in that step), so the removal is requested once. An object of duration `d` expires after about `ceil(d / dt)` steps, one more when the `double` subtraction leaves a remainder.
- Both systems take the world, the storages and optionally an array of excluded trait types (`REQ-STD-011`), have one query exposed by `getQuery()`, and allocate nothing per step (`GravityLifetimeAllocationTest`, 1 KiB tolerance over 1,000 steps of 1,000 objects with one expiry and one creation per step).
- Bytes per instance on the desktop JVM, in the Javadoc: 32 per `Gravity`, 24 per `Lifetime` (`TraitMemoryTest`).
- Benchmark (ADR-0079): the stand-ins are replaced in place by the standard traits. The scene (`BenchmarkWorld`) and `StepBenchmarkTest`, `BenchmarkAllocationTest` and `BenchmarkScalingTest` are in the `traits` test sources, with five systems (`TransformCaptureSystem`, `GravitySystem`, `MovementSystem`, `LifetimeSystem` and the spawner at 6000); `StepHarness` and `CountingType` are public in the `ecs` test JAR. Command `mvn -B -pl lionengine-traits -am verify -Pbenchmark`, report `lionengine-traits/target/benchmark-reports/step.md`. Values in `testing.md`, "Measured budgets".
- No factory for `lionengine:gravity` and `lionengine:lifetime` yet (`OQ-178`).
- Tests: `GravityTest`, `LifetimeTest`, `GravitySystemTest` and `LifetimeSystemTest` (steps of 1/30, 1/60 and 1/120 s), `GravityLifetimeAllocationTest`, `TraitMemoryTest`, `BenchmarkAllocationTest`, `BenchmarkScalingTest`, `StepBenchmarkTest` (tag `benchmark`), `FallExampleTest`; the device test `TraitsTest`.

Batch 3 (2026-11-28), the first types of `physics` and the collision of objects, with the example `CollisionExample` and its API-index row. The signatures and details this page leaves to the batch follow [ADR-0082](../decisions/0082-standard-traits-collision-box-broad-phase-collidable-and-events.md) (owner answers and delegation of the batch):

- `physics`, plain data on `core` only (`REQ-PHYS-001`): `Box`, a mutable value (center `x`, `y`, full `width` and `height`, m; finite values, size positive or zero) with its bounds and a **strict** overlap test, `minA < maxB && minB < maxA` on both axes, so boxes touching by an edge or a corner do not overlap; `ShapeQuery`, `int query(Box area, Box[] results)`, which writes the solid shapes overlapping the area into the caller's boxes and returns their count even beyond the buffer, allocates nothing and keeps nothing, implemented by `tilemap` with batch 4 (reference `BoxListQuery` in the `physics` test JAR); `BroadPhase(cellSize, capacity)`, an unbounded hashed uniform grid (`clear`, `add(box)` returning the index, `findPairs`, `getFirst(pair) < getSecond(pair)`), each pair once (kept by the cell of the lower-left corner of its overlap), capacities doubling when needed and never shrinking, no allocation in steady state, at most `BroadPhase.MAX_ENTRIES` (`2^30`) cell entries.
- `Collidable` (box size and center offset from the position, m, offset default `(0, 0)`; `int` layer and mask bit sets, 32 layers; trigger flag), data only, private fields with accessors (`getWidth`, `getHeight`, `setSize`, `getOffsetX`, `getOffsetY`, `setOffset`, `getLayer`, `setLayer`, `getMask`, `setMask`, `isTrigger`, `setTrigger`) and a public `TYPE`. Reset state: a box of size `0`, offset `(0, 0)`, layer `Collidable.DEFAULT_LAYER` (`1`), mask `Collidable.ALL_LAYERS` (`-1`), not a trigger. A negative or non-finite size and a non-finite offset are an `IllegalArgumentException`.
- `CollisionSystem` at slot 4000, on the objects having `Transformable` and `Collidable`: builds each box at `(x + offsetX, y + offsetY)`, finds the overlapping pairs with its `BroadPhase`, keeps those whose masks include each other's layer, sorts them by `(objectA, objectB)` with `objectA < objectB` (readable by `getPairCount`, `getObjectA`, `getObjectB`), then reports to its `CollisionListener`s (`onEnter`, `onExit`, default no-op, registered on the system with `addListener` and `removeListener`) every exit, then every enter, each in ascending pair order. Every accepted pair reports, triggers included: object versus object is detection only, nothing is moved. An object leaving the query while overlapping (removed, its `Collidable` or `Transformable` removed, an excluded trait added) reports an exit at the next step of the system, its ID stale if removed. Listeners run inside the step: they may change traits and record structural changes (applied at the flush); a registration during a delivery takes effect from the next event.
- The cell size is per world, one `CollisionSystem` per world: `CollisionSystem(world, transformables, collidables, cellSize, excluded)`, the other constructors using `BroadPhase.DEFAULT_CELL_SIZE` (2 m). Excluded trait types as for the other systems (`REQ-STD-011`); one query exposed by `getQuery()`; the broad phase and pair lists start at the trait capacity of the world and double when a step needs more, a warm-up cost; no allocation per step after it (`BroadPhaseAllocationTest`, `CollisionAllocationTest`, 1 KiB tolerance over 1,000 steps of 1,000 boxes or moving objects).
- Bytes per instance on the desktop JVM, in the Javadoc: 56 per `Collidable` (`TraitMemoryTest`).
- Benchmark: the `REQ-ECS-009` scene keeps 1,000 objects × 4 traits; the collision is measured by a separate scene (`CollisionBenchmarkWorld`, 1,000 and 10,000 moving squares of 0.5 m, 4 m² per object, with the capture, `Movement`, a bounce and `Collision`) in `StepBenchmarkTest`, reported without a budget; values in `testing.md`, "Measured budgets".
- No factory for `lionengine:collidable` yet (`OQ-178`). The trigger flag changes no detection: the tile push-out of batch 4 never blocks a trigger.
- Tests: `BoxTest`, `BroadPhaseTest` (against a brute-force reference on seeded random boxes at five cell sizes), `BroadPhaseAllocationTest`, `ShapeQueryTest`, `CollidableTest`, `PairListTest`, `CollisionSystemTest` (layers and masks both ways, event order, removals, triggers, listeners during a delivery, moving objects at steps of 1/30, 1/60 and 1/120 s, a random moving scene against a brute-force reference), `CollisionAllocationTest`, `TraitMemoryTest`, `StepBenchmarkTest` (tag `benchmark`), `CollisionExampleTest`; the device test `PhysicsTest`.

Batch 4 (2026-11-28), the tile push-out and `grounded`, with the example `LandingExample` and its API-index row. The details this page leaves to the batch follow [ADR-0083](../decisions/0083-standard-traits-tile-push-out-grounded-and-shape-query-suite.md) (owner answers and delegation of the batch):

- Done against `ShapeQuery` only: the tile map implements it with tile-map batch 5 (after its batches 1 to 4); until then a game passes any `ShapeQuery` (the example's `Ground`, the reference `BoxListQuery` and the grid stand-in `TileGrid` of the tests).
- `Jumpable` brought forward as data, the `Jump` system staying batch 5: private fields `impulse` (m/s, positive or zero), `grounded`, `coyoteTime` and `jumpBuffer` (s, positive or zero, default `0.1` each, `Jumpable.DEFAULT_COYOTE_TIME`, `Jumpable.DEFAULT_JUMP_BUFFER`), `cutFactor` (in `[0, 1]`, default `Jumpable.DEFAULT_CUT_FACTOR`, `0.5`) and its switch `variableHeight` (off), with accessors and a public `TYPE`; a negative or non-finite impulse or time and a factor outside `[0, 1]` are an `IllegalArgumentException`; 48 bytes per instance (`TraitMemoryTest`). The game may set `grounded` too.
- The push-out runs inside `CollisionSystem` (slot 4000), first in its step, before the object detection, so the pairs see the pushed positions. It is created with `CollisionSystem(world, transformables, collidables, movables, jumpables, tiles)` or `(world, transformables, collidables, movables, jumpables, cellSize, excluded)`; `setShapeQuery(tiles)` replaces the query (a map change, from the next step; `null` for none), one per world (several maps: one query of the game over them); `getShapeQuery()` and `getTileQuery()` read them. The batch 3 constructors have no push-out, their tile methods an `IllegalStateException`.
- Pushed objects: every non-trigger object having `Transformable`, `Collidable` and `Movable` and none of the excluded types (the second query, `getTileQuery()`); triggers never; an object without `Movable` is not pushed; no tile layer or mask.
- Algorithm, per object, from its displacement since its previous position (captured at the step start): an X pass with the box at `(x, previousY)`, against the X displacement (left of the leftmost overlapping shape when moving right, right of the rightmost one when moving left), then a Y pass at the resolved X and `y`, against the Y displacement (on the highest shape when moving down, under the lowest one when moving up); no pass on an axis without displacement; the box ends exactly touching (the last ulps adjusted); a velocity component pointing into the stopping shape is set to `0`, one pointing away is kept; the `Jumpable`, if any, is grounded if and only if the Y pass pushed the object up. No tolerance: since touching boxes do not overlap, a resting object is grounded at each step only while a gravity moves it into the floor. No tile event. The X pass at the previous Y keeps a walking object from catching on the seams between floor tiles, and a falling object reaching the top corner of a wall lands on it.
- Half-collider rule: the displacement per step stays under half of the smallest box, the object's and the tile's (0.5 m per step for 1 m tiles and boxes of at least 1 m, 0.25 m for a box of 0.5 m: 15 m/s at 1/60 s); a terminal velocity or `maxSpeed` keeps it there.
- The result buffer starts empty and grows to `max(count, 2 * length)` boxes, a warm-up cost; no allocation per step (`TileCollisionAllocationTest`, 1 KiB over 1,000 steps of 1,000 objects).
- `ShapeQueryContract`, the abstract contract suite in the `physics` test JAR (tiles of 1 m at the origin, columns and rows in `[0, 8)`): strict overlap, written boxes, count beyond the buffer, untouched remainder, `null` arguments, order, nothing kept, no allocation; bound by `BoxListQueryContractTest` and `TileGridContractTest`, and by the tile map with its batch.
- Benchmark: the tile scene `TileBenchmarkWorld` (squares of 0.5 m walking and hopping in a tile arena of 4 m² per object) measured by `StepBenchmarkTest` at 1,000 and 10,000 objects in the collision section of the report, without a budget; values in `testing.md`, "Measured budgets".
- No factory for `lionengine:jumpable` yet (`OQ-178`).
- Tests: `JumpableTest`, `TileCollisionTest` (floor, wall from both sides, corner, ceiling, floor seams, `grounded` set and cleared, triggers, objects without `Movable` and excluded ones, query replaced, offset, buffer growth and exact contact, a velocity pointing away kept, pairs after the push-out, invalid arguments; steps of 1/30, 1/60 and 1/120 s with gravity and movement), `BoxListQueryContractTest`, `TileGridContractTest`, `TileCollisionAllocationTest`, `TraitMemoryTest`, `StepBenchmarkTest` (tag `benchmark`), `LandingExampleTest`; the device test `PhysicsTest`.

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
- Performance tests: broad phase against a brute-force reference (`BroadPhaseTest`, `CollisionSystemTest`, batch 3); part of the 1,000 objects × 4 traits budget (`REQ-ECS-009`). The benchmark ran on stand-ins of `Transformable`, `Movable`, `Gravity` and `Lifetime` until these existed ([ADR-0079](../decisions/0079-ecs-benchmark-harness-stand-in-scene-and-trend-job.md)); batch 2 replaced the stand-ins with them, measured again with the `ecs` harness (`StepHarness`, tag `benchmark`) from the `traits` tests and recorded the values in `testing.md` ([ADR-0081](../decisions/0081-standard-traits-gravity-lifetime-and-benchmark-on-standard-traits.md)); each standard trait records its bytes per instance in its Javadoc, measured the same way (ADR-0034).

## Acceptance criteria

- [ ] Requirements `REQ-STD-001` to `REQ-STD-010` verified (`REQ-STD-001` and `REQ-STD-002` by batch 1, `REQ-STD-003` and `REQ-STD-006` by batch 2, `REQ-STD-005` and `REQ-PHYS-001` by batch 3, `REQ-STD-010` by batches 3 and 4, the `Jumpable` data of `REQ-STD-004` by batch 4).
- [ ] Automated tests added and passing; complete Javadoc for every trait and system, with units, slot and bytes per instance (done for `Transformable`, `Movable`, `Gravity`, `Lifetime`, `Collidable`, `TransformCaptureSystem`, `MovementSystem`, `GravitySystem`, `CollisionSystem` and `LifetimeSystem`, and for `Box`, `BroadPhase` and `ShapeQuery` in `physics`; `Jumpable` and the tile push-out of `CollisionSystem` by batch 4).
- [ ] Example and `api-index.md` entry; no forbidden coupling; Android/DEX compatibility verified (batch 1: `MovementExample`, its row, device test `TraitsTest`; batch 2: `FallExample`, its row, `TraitsTest` extended; batch 3: `CollisionExample`, its row, device test `PhysicsTest`; batch 4: `LandingExample`, its row, `PhysicsTest` extended).
