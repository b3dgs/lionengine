# Feature: camera

**Status: PROPOSED**

- Owner: to be filled in
- Roadmap phase: Phase 3, see `roadmap.md`
- Affected modules: `camera` (new, PROPOSED), `ecs` (follow system, location `OQ-132`), `tilemap` and `graphics` (consumers of a plain view rectangle), `test-support`, `examples`
- Requirements: `REQ-CAM-001` to `REQ-CAM-008`
- Applicable ADRs: [`ADR-0010`](../decisions/0010-camera-system.md) (PROPOSED), [`ADR-0001`](../decisions/0001-technical-baseline-and-conventions.md), [`ADR-0002`](../decisions/0002-display-modes-resolution-and-output-filtering.md), [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [`ADR-0009`](../decisions/0009-tile-map-contracts-in-tilemap.md) (PROPOSED)
- Open questions involved: `OQ-010`, `OQ-012`, `OQ-013`, `OQ-021`, `OQ-040`, `OQ-041`, `OQ-042`, `OQ-108`, `OQ-130`, `OQ-131`, `OQ-132`, `OQ-133`, `OQ-134`, `OQ-135`, `OQ-136`

## Objective

A camera represents the view area on the world: it can move, zoom, follow an entity by its identifier with a configurable delay, and shake, and it converts between world and screen coordinates.

## Out of scope

Multi-camera and split-screen (`REQ-MP-001`, must not be precluded); clamping to map bounds; rotation; parallax; dead zone and look-ahead (all `OQ-136`); the meters-to-pixels factor value (`OQ-012`); the ECS model itself (`OQ-041`).

## Expected behavior

- The camera exposes its view rectangle in world units (meters, ADR-0001): position, size = logical frame size converted to world units / zoom, shake offset included.
- Zoom: set or animate a factor (duration in seconds), clamped to `[min, max]`, pivot = camera center; a change requested in the update phase applies on the next presented frame.
- Translation: `moveTo` (absolute) and `moveBy` (relative), in world units.
- Follow: the camera tracks a target given by entity identifier through a lookup contract defined by `camera`; when the lookup reports no entity, following stops and the last position is kept; no exception.
- Delay: smoothing with a time constant in seconds, `1 - exp(-dt / tau)` of the remaining distance per update; `0` means rigid; same result whatever the update step size for the same simulated time.
- Shake: `shake(amplitude, duration, frequency)`, linear decay to zero, deterministic per-camera seeded noise; the offset is separate from the logical position, which never changes.
- Conversion: world to screen and screen to world, including zoom and shake (picking policy: `OQ-134`).
- Rendering consumers receive a plain rectangle (`OQ-130`); they never receive a `Camera`.

## Envisaged APIs / contracts

Responsibilities only; signatures are not fixed. See `architecture/contracts.md` (Camera).

## Lifecycle and concurrency

An instance holds its own state (no singleton). Follow, delay, zoom animation and shake advance in the update phase only, from the simulation step in seconds (`REQ-LOOP-003`, `REQ-LOOP-004`); rendering only reads (`REQ-LOOP-006`). Threads: `OQ-030`.

## Units and Android/DEX compatibility

World units are meters (ADR-0001), zoom is dimensionless, time is in seconds, the logical frame is in pixels (ADR-0002). Numeric type: `OQ-013`. Plain Java math: no reflection, no `java.awt`, no new dependency (`docs/android-compatibility.md`).

## Dependencies

Allowed: `core`. Forbidden: any frontend, `render-api`, `ecs` or `world` type; any dependency of `core` or an `*-api` module on `camera`.

## Tests

- Unit tests: zoom clamping, translation, smoothing for several step sizes, shake decay and seed determinism, conversion round trip, missing target.
- Contract tests (`test-support`): the entity lookup contract, if the owner keeps it as a contract.
- Integration tests: headless, fake lookup, controllable clock; render-side consumer receives the expected rectangle.
- Platform/performance tests: none planned.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API validated.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
