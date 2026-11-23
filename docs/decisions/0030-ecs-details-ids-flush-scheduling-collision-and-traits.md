# ADR-0030 — ECS details: object ID, flush, benchmark, storage, scheduling, collision, jump, further traits, iteration order

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner ("Confirmed as proposed, write the ADR")
- Related requirements: `REQ-ECS-001` to `REQ-ECS-012`, `REQ-STD-001` to `REQ-STD-010`, `REQ-DET-001`, `REQ-PHYS-001`, `REQ-MAP-008`
- Open questions resolved: `OQ-137`, `OQ-138`, `OQ-139`, `OQ-140`, `OQ-141`, `OQ-142` (first batches), `OQ-143`, `OQ-144`, `OQ-145`
- Supersedes / superseded by: none; completes [ADR-0011](0011-ecs-dynamic-objects-traits-and-standard-traits.md) (unchanged)

## Context

ADR-0011 left nine ECS details open. Since then ADR-0013 (Y up), ADR-0014 (`double`), ADR-0017 (fixed step, fail-fast), ADR-0018, ADR-0019, ADR-0021 (`World` in `ecs`, `physics`), ADR-0024 (budgets, determinism), ADR-0028 (tile collision) and ADR-0029 (`long` follow key) constrain them.

## Decision

1. **Object ID (`OQ-137`).** A packed primitive `long`: generation in the high 32 bits, index in the low 32 bits; no value object, no allocation. The generation starts at 1, so `0` is never valid and means "no object". The same `long` is the camera follow key (ADR-0029).
2. **Deferred changes (`OQ-138`).** Creating an object returns its ID at once so further commands can be queued with it; until the flush the object is *pending*: liveness checks return false and accessors fail fast. Commands apply in recorded order, the last wins; an add followed by a remove both apply and both fire their events. An explicit flush is allowed outside the loop (setup, tests). In the loop there is one flush per step, at the end, after all systems and before rendering; changes are visible from the next step. Commands from inbox events (ADR-0018) go into the same buffer.
3. **Benchmark (`OQ-139`).** No benchmark library: a small own headless harness with warm-up and a median over many steps. CI asserts only what is not flaky: zero steady-state allocation (JVM-only test using the per-thread allocation counter) and linear scaling of operation counts. Wall-clock timing is reported, not asserted; a time budget is checked in a separate non-blocking trend job. The Android reference device and its measurement come with the DEX job (`OQ-147`).
4. **Storage (`OQ-140`).** Pooled trait instances in dense arrays first; only the hot standard traits (`Transformable`, `Movable`) move to primitive arrays if the benchmark misses the budget. Each trait has a `reset` on removal. Capacity is pre-sized from configuration (initial value to calibrate) and doubles at flush when exceeded, a documented warm-up cost; storage never shrinks. Per-trait memory is documented for Android.
5. **Scheduling (`OQ-141`).** The ordering slot is an `int` (lower first), ties by registration order; no dependency graph. Standard systems have documented slot ranges. A per-system enabled flag takes effect at the next step. The scene calls `world.update(step)` from the runtime update; systems receive the constant step in seconds; variable delta is not supported (ADR-0017). An exception in a system propagates, the loop fails (`REQ-LOOP-010`), and the world is documented as undefined afterwards.
6. **Collision (`OQ-142`, first batches).** Grid cell size is configured per world, default 2 m (to calibrate by benchmark); automatic derivation later. Shapes: axis-aligned boxes first, circles later. Object versus object is detection only: overlapping pairs and trigger events. Blocking resolution applies to tiles only, as a basic axis-separated push-out in the first tile-collision batch, because `grounded` needs it. Layer and mask: two objects collide if each one's mask includes the other's layer. Trigger events are enter and exit, delivered synchronously after the collision system. No continuous (swept) detection yet: per-step displacement must stay under half the smallest collider, helped by the `Movable` speed clamp. Further shapes and response stay in `OQ-142`.
7. **Jump and ground (`OQ-143`).** `grounded` is set by the collision system from a contact below; game code may also set it. Default coyote time and jump buffer: 0.1 s each. Variable jump height: an optional early-release cut factor, off by default. With Y up (ADR-0013) gravity is a negative Y acceleration and the jump impulse is positive; terminal velocity is stored as a magnitude.
8. **Further traits and `Transformable` (`OQ-144`).** Order of arrival: controller intent, render link and tag first; friction/drag and health next; parent link (hierarchy) last, because it adds ordering and transform propagation. `Transformable` uses `double` (ADR-0014) and captures the previous position at step start for interpolation with `alpha`; extrapolation is opt-in and never feeds the next step (ADR-0017).
9. **Iteration order (`OQ-145`).** The order is deterministic for the same sequence of operations: no hashing on identity, no `HashMap` iteration (`REQ-DET-001`). It is not creation order and not stable across removals; game logic must not depend on it. Where order matters (drawing), use an explicit sort key or layer; a sort by ID is an explicit operation, not the default.

## Options considered

- ID: packed `long` (chosen) vs packed `int` (generation wraps too soon) vs object (allocation, no stale detection).
- Flush: end of step (chosen; rendering sees consistent state, changes visible next step) vs start of step (changes visible to systems of the same iteration but after the inbox, harder to reason about) vs immediate (iteration hazards, ADR-0011).
- Benchmark: own harness, no wall-clock assertions (chosen) vs JMH (a dependency, license and Android record) vs wall-clock assertions in CI (fragile).
- Collision response: detection only for objects (chosen) vs a general physics response now (premature) vs none for tiles (platformers cannot be built).
- Scheduling: integer slots (chosen) vs a dependency graph (more complex, equally deterministic only with tie rules).

## Consequences

- Positive consequences: allocation-free IDs; predictable change visibility; deterministic order; a benchmark plan that cannot flake; the first platformer building blocks are defined.
- Costs / constraints: pending objects are not usable until the flush; a documented warm-up growth cost; no tunneling protection; object collision response is left to game systems.
- Impact on modules and APIs: `ecs`, `traits`, `physics`; Java signatures stay open (`OQ-164`).
- Impact on tests and documentation: `REQ-ECS-011`, `REQ-ECS-012`, `REQ-STD-010` added; `features/ecs.md`, `features/standard-traits.md`, `testing.md`, `contracts.md`, traceability updated.

## Validation

Decisions confirmed by the project owner on 2026-11-23. Follow-ups: `OQ-164`.
