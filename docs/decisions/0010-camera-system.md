# ADR-0010 — Camera system (view, zoom, translation, follow, delay, shake)

- Status: PROPOSED
- Date: 2026-11-22
- Decision makers: project owner (not yet decided: the packet "Camera system" states "Decision taken: pending"); the ADR stays PROPOSED until the owner accepts it. Every statement under "Proposed decision" is a recommendation, not an established fact.
- Related requirements: `REQ-CAM-001` to `REQ-CAM-008`, `REQ-MAP-005`, `REQ-LOOP-003`, `REQ-LOOP-004`, `REQ-LOOP-006`, `REQ-MP-001`, `REQ-CORE-005`
- Open questions resolved: none. Touched: `OQ-010`, `OQ-012`, `OQ-013`, `OQ-021`, `OQ-040`, `OQ-041`, `OQ-042`, `OQ-108`. Created: `OQ-130` to `OQ-136`.
- Supersedes / superseded by: none. Builds on [ADR-0001](0001-technical-baseline-and-conventions.md) (units), [ADR-0002](0002-display-modes-resolution-and-output-filtering.md) (logical frame), [ADR-0003](0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md) (conversion from meters belongs to the camera/viewport) and [ADR-0009](0009-tile-map-contracts-in-tilemap.md) (PROPOSED; viewport as a plain rectangle). None of them is modified.

## Context

Several accepted or proposed documents defer the same concept to "the camera/viewport": the conversion from world meters to pixels (ADR-0001, ADR-0003, `OQ-012`), the viewport rectangle consumed by tile map rendering (ADR-0009, decision 1A), and the definition of a scene, world and viewport (`OQ-040`). No contract exists for it. Games need a view on the world with zoom, translation, following an object by its ECS identifier, a gameplay-tunable movement delay and a shake effect.

Constraints that shape the decision:
- ADR-0001 (ACCEPTED) fixes **world positions in meters** and time in seconds, and says that conversion to pixels belongs to the presentation layer. The originating packet states "world = pixels"; that contradicts ADR-0001, and a validated decision prevails. This ADR therefore expresses the view in world units (meters) and leaves the meters-to-pixels factor to `OQ-012`.
- ECS is not settled as the core model (`OQ-041`, `REQ-OBJ-002` is `Could`). A camera that depends on an ECS module would make ECS mandatory.
- The default loop is independent of machine speed (`REQ-LOOP-004`); update and render are distinct, and render never feeds state back (`REQ-LOOP-003`, `REQ-LOOP-006`).
- Android/DEX: pure math, no reflection, no `java.awt` (`android-compatibility.md`).
- Split-screen (several cameras) is out of scope but must not be precluded (`REQ-MP-001`).

## Proposed decision

1. **Placement (recommended: a new single module `camera`).** Responsibility: the view on the world (position, zoom, size), movement smoothing, shake, and world/screen conversion; pure math. It does **not**: render, read input, own entities, own the ECS, or know any frontend. Allowed dependency: `core` only. No dependency from `core` or an `*-api` module toward it. `tilemap`, `graphics` and games consume the view as a plain rectangle (decision 6).
2. **ECS coupling through a narrow contract.** `camera` defines a target-position lookup (entity identifier to position, or absent). The camera never imports ECS types. The ECS system that calls the camera each update lives outside `camera`, on the `ecs` side or in a thin adapter; where exactly depends on `OQ-041` and is `OQ-132`. A missing target stops following and keeps the last position (`REQ-CAM-004`).
3. **Units.** The view rectangle, translation and follow positions are in world units (meters, ADR-0001). Zoom is dimensionless. Time is in seconds. The view size is the logical frame size (ADR-0002, in pixels) divided by zoom and converted through the meters-to-pixels factor of `OQ-012`; the camera takes the logical frame size and that factor as plain values.
4. **Zoom.** A factor clamped to `[min, max]`; the pivot is the camera center by default; an animated zoom reaches a target factor over a duration in seconds. A zoom change is requested and applied in the update phase, so it takes effect on the next presented frame. Easing and other pivots: `OQ-135`.
5. **Movement delay.** Smoothing toward the target with a time constant in seconds: each update moves the camera by the fraction `1 - exp(-dt / tau)` of the remaining distance, where `dt` is the simulation step; `tau = 0` means a rigid follow. The result is independent of the update frequency (`REQ-LOOP-004`). Alternatives, default value and render-time extrapolation: `OQ-133`.
6. **Shake.** `shake(amplitude, duration, frequency)`: amplitude decays linearly to zero over the duration; the offset is produced by a deterministic noise source seeded per camera (no global random source), advanced only from the update step. The offset is held apart from the logical position; the logical position never changes. Units, direction and picking: `OQ-134`; randomness choice: `OQ-131`.
7. **Output to rendering.** The camera exposes its current view rectangle (including the shake offset) and conversion functions. `render-api`, `graphics` and `tilemap` receive a plain rectangle, never a `Camera` type; this is consistent with ADR-0009 decision 1A and answers the packet's `OQ-NEW-1` as a proposal (`OQ-130`).
8. **State discipline.** Follow, delay, zoom animation and shake advance in the update phase only. Rendering reads the state and never mutates it.
9. **No singleton.** A camera is an instance with its own state, so that several cameras remain possible later (`REQ-MP-001`).

Not decided: Java types and signatures, geometry value types (where they live: `core` or `camera`), entity identifier type, the ECS adapter location, default values, easing, bounds clamping, rotation, parallax, multi-camera behavior.

## Options considered

### Placement
- A (recommended): one `camera` module depending on `core` only. Matches the splitting principles of `modules.md`: there is a single plausible implementation of pure math, so an API/implementation split adds complexity without isolation benefit.
- B (packet proposal): `camera-api` plus an implementation module. Gives a replaceable implementation, but no second implementation is foreseen.
- C: put the camera in `render-api`. Mixes simulation-side state (follow, delay, shake) with the rendering contracts and would make an `*-api` module carry update logic.
- D: put the camera in `world`. Ties the camera to the unsettled scene model (`OQ-040`) and to the object model (`OQ-041`).

### ECS coupling
- A (recommended): lookup contract defined by `camera`; the ECS system and adapter live outside it. No ECS dependency in `camera`.
- B: `camera` depends on `ecs`. Simple, but makes ECS mandatory while `OQ-041` is open.
- C: the game drives the camera position every frame with no follow feature. Pushes the work to every game and fails the stated capability.

### Movement delay
- A (recommended): exponential smoothing with a time constant in seconds. Frame-rate independent, one parameter.
- B: fixed lag (the camera targets where the object was `n` seconds ago). Needs a history buffer.
- C: dead zone and look-ahead. Richer gameplay behavior, larger contract; candidate for later (`OQ-136`).

### Shake randomness
- A (recommended): per-camera seeded noise. Deterministic and testable with a fixed seed; no dependency on an engine random source that does not exist yet.
- B: shared engine random source. Couples to a contract not yet defined; replay determinism then depends on how it is consumed.

## Consequences

- Positive consequences: the view concept is defined once and reused by tilemap, graphics and input; `OQ-012` gets an owner; the module can start without ECS, events or physics contracts; every behavior is deterministic and headless-testable.
- Costs / constraints: a new module (`modules.md`, build skeleton and dependency check once they exist); the packet's pixel-based world is not followed (ADR-0001 prevails), so the meters-to-pixels factor must be settled (`OQ-012`) before an implementation; the ECS identifier type and position access remain open (`OQ-041`, `OQ-042`, `OQ-132`).
- Impact on modules and APIs: new module `camera` (`core` only); consumers receive a plain rectangle; no change to `core`, `render-api`, `graphics` or `tilemap` contracts.
- Impact on tests and documentation: unit tests of zoom clamping, translation, smoothing (several step sizes reaching the same state for the same simulated time), shake (seeded, decay, no logical change), conversions (round trip) and missing-target follow; headless integration test with a fake ECS lookup and a controllable clock (`REQ-TEST-001`); feature file [`features/camera.md`](../features/camera.md); headless example and `api-index.md` entry. Android/DEX unaffected: plain Java math, no new dependency, no `java.awt`.

## Validation

Not yet approved. Status stays PROPOSED until the owner accepts it. Until then, no implementation may treat any statement above as established.
