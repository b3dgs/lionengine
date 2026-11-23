# ADR-0034 — Camera and ECS defaults: noise, transform, clamp, dead zone, slots, capacities, events, later shapes

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, under the project owner's explicit delegation of 2026-11-23 ("choose the best choice for all requirements and all sub pages related to them; mark them as VALIDATED with your choice"); reversible by a superseding ADR
- Related requirements: `REQ-CAM-002`, `REQ-CAM-004` to `REQ-CAM-010`, `REQ-ECS-005` to `REQ-ECS-013`, `REQ-STD-004`, `REQ-STD-005`, `REQ-STD-008`, `REQ-STD-009`, `REQ-STD-010`, `REQ-PHYS-001`
- Open questions resolved: `OQ-163`, `OQ-164`, and the remainder of `OQ-142` (further shapes and response)
- Supersedes / superseded by: none; completes [ADR-0029](0029-camera-details-view-transform-follow-shake-zoom.md) and [ADR-0030](0030-ecs-details-ids-flush-scheduling-collision-and-traits.md) (both unchanged)

## Context

ADR-0029 and ADR-0030 left constants, formulas, slot values, capacities, the listener form and the order of later collision features to the batches.

## Decision

### 1. Camera (`OQ-163`)

- **Shake noise:** one-dimensional value noise per axis. Lattice values come from the 32-bit integer hash `fmix32(seed + axis * 0x632BE5AB + index * 0x9E3779B9)` (the MurmurHash3 finalizer) mapped to `[-1, 1]` as `(hash >> 8) / 2^23`, using the signed 24 high bits. The lattice coordinate is the shake time multiplied by the frequency in 16.16 fixed point, computed from the elapsed step count; the weight between two lattice values is smoothstep, also in fixed point. Conversion to `double` happens only for the final offset (amplitude × noise × decay). This keeps the result bit-exact on every platform.
- Default seed: `0x4C494F4E` (the ASCII of "LION"). `reseed(int)` takes effect at the next shake.
- **View transform:** three `double` fields, `scale` (pixels per meter, zoom included), `offsetX` and `offsetY` (pixels of the logical frame). World to screen: `screenX = worldX * scale + offsetX`, `screenY = offsetY - worldY * scale` (the minus sign is the Y flip). Values are converted to `float` only when written into render commands (ADR-0014).
- **Zoom curves:** `LINEAR`, `SMOOTHSTEP` (default), `EASE_IN` and `EASE_OUT` (quadratic). A zoom request without a duration applies at the next frame.
- **Zoom anchor with follow:** while the camera follows a target, a world anchor is ignored and the zoom pivots on the camera center.
- **Bounds clamp:** an optional world rectangle in meters. The view rectangle is kept inside it after follow and smoothing, before shake (a shake may show a little outside the bounds). On an axis where the view is larger than the bounds, the view is centered on the bounds.
- **Dead zone:** a rectangle centered on the view, sized as a fraction of the view in `[0, 1]` per axis, default `0` (none); the camera moves only when the target leaves it.
- **Follow offset:** a vector in meters added to the target position, default `(0, 0)`.
- The follow key encoding is the ECS object ID of ADR-0030.

### 2. ECS (`OQ-164`)

- **Reference point:** the `Transformable` position is the **center** of the object and its size is the full width and height in meters, matching the default sprite pivot (the frame center, ADR-0026). A `Collidable` box is a width, a height and an offset of its center from the position, default `(0, 0)`, all in meters.
- **Initial capacities:** 1,024 objects per world and 256 instances per trait type, configurable at world creation and by an explicit `reserve` call; growth doubles at the flush (ADR-0030).
- **Standard system slots:** `Jump` 1000, `Gravity` 2000, `Movement` 3000, `Collision` 4000, `Lifetime` 5000. Recommended game ranges: input and intent below 1000; decisions that set velocities (artificial intelligence) between 1000 and 2000; reactions to collisions between 4000 and 5000; late bookkeeping after 5000.
- **Listeners:** plain interfaces with primitive arguments, registered on the world, called synchronously on the runtime thread: object created and destroyed `(long objectId)`, trait added and removed `(long objectId, int traitTypeId)` during the flush, collision enter and exit `(long objectA, long objectB)` after the `Collision` system, with `objectA < objectB`. Listener rules follow ADR-0018.
- **Variable jump:** when enabled, releasing the jump while ascending multiplies the upward velocity by a cut factor, default `0.5`.
- **ID sort:** a query can copy its object IDs in ascending order into a caller-provided `long[]`; this is the explicit sort of ADR-0030 and allocates nothing.
- **Benchmark reference:** the desktop reference for the 1 ms budget of `REQ-ECS-009` is the CI trend-job runner (a GitHub Actions standard Linux runner); the Android reference device comes with `OQ-147`.
- **Per-trait memory:** each standard trait documents its bytes per instance in its Javadoc, measured by the benchmark batch.
- Java signatures are written by each batch under `conventions.md` and reviewed in its pull request; they are not an open question.

### 3. Later collision features (rest of `OQ-142`)

- Order of arrival, each its own batch and requirement when a game needs it: circle shapes for objects, then one-way tiles (passable from below, `collision` value `"one-way"`), then slopes (45° first).
- Object-versus-object response stays game code reacting to collision events. Swept detection is added only for a game with fast projectiles; until then the half-collider rule of ADR-0030 holds.

## Options considered

- Noise hash: MurmurHash3 finalizer (chosen; well-known, cheap, good avalanche) vs a custom linear congruential mix (visible patterns).
- System slots with gaps of 1000 (chosen; room for game systems on both sides of every standard system) vs consecutive slots (insertion needs renumbering).
- Listener form: primitive-argument interfaces (chosen; no event objects, no allocation) vs event objects (allocation or pooling).
- Dead zone as a fraction of the view (chosen; stays meaningful when zooming) vs a size in meters.

## Consequences

- Positive consequences: deterministic, reproducible camera effects with documented formulas; game systems can be placed relative to the standard ones; allocation-free ECS events.
- Costs / constraints: fixed-point noise code to test against reference values; one-way tiles and slopes wait for a game need.
- Impact on modules and APIs: `camera`, `ecs`, `traits`, `physics`.
- Impact on tests and documentation: `REQ-CAM-010` and `REQ-ECS-013` added; `features/camera.md`, `features/ecs.md`, `features/standard-traits.md`, `contracts.md` updated.

## Validation

Decided by the agent on 2026-11-23 under the owner's explicit delegation; reversible by a superseding ADR.
