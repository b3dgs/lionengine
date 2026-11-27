# Feature: camera

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 3, see `roadmap.md`
- Affected modules: `camera`, `core`, `test-support`, `examples`; the follow adapter over the world lives in `composition` (ADR-0031)
- Requirements: `REQ-CAM-001` to `REQ-CAM-010`
- Applicable ADRs: [`ADR-0010`](../decisions/0010-camera-system-in-camera.md), [`ADR-0013`](../decisions/0013-world-coordinates-and-angles.md), [`ADR-0014`](../decisions/0014-meters-to-pixels-numeric-type-and-rotation.md), [`ADR-0017`](../decisions/0017-fixed-step-game-loop.md), [`ADR-0029`](../decisions/0029-camera-details-view-transform-follow-shake-zoom.md), [`ADR-0034`](../decisions/0034-camera-and-ecs-defaults-noise-clamp-slots-capacities-events-shapes.md)
- Open questions involved: none

## Objective

A headless camera representing the visible area of the world: view rectangle and view transform, zoom, translation, follow by object ID, smoothing, bounds and shake.

## Out of scope

Rotation (deferred); parallax (a background-layer feature reading the camera position); split screen and multi-camera need no camera code (several instances, views in `composition`).

## Expected behavior

- **Scale:** the game sets the view width in meters; `pixelsPerMeter = logical frame width in pixels / view width in meters × zoom` (ADR-0014). For pixel art, 1 tile = 1 m and `pixelsPerMeter` equals the tile size.
- **View rectangle** in meters: the logical frame size converted with `pixelsPerMeter` (zoom included).
- **View transform** per frame, immutable, from the state interpolated with `alpha`: `scale`, `offsetX`, `offsetY`; `screenX = x * scale + offsetX`, `screenY = offsetY - y * scale`. Rendering receives only this value or the derived pixel rectangle (for `tilemap`).
- **Zoom:** multiplier, default 1.0, clamped to `[0.25, 4.0]` by default (configurable); animated over a duration with `LINEAR`, `SMOOTHSTEP` (default), `EASE_IN` or `EASE_OUT`; immediate requests apply at the next frame; pivot on the center or on a world anchor, ignored while following.
- **Translation:** absolute and relative, in meters.
- **Follow:** a lookup contract `long key → position` (or "absent"); the key is the ECS object ID; an absent target stops the follow and the camera keeps its last position, never an exception. Smoothing: exponential, time constant in seconds, default 0 (rigid), frame-rate independent; target switch moves smoothly; explicit `snap`; optional snap distance, off by default. The target is sampled at the end of the update step, after the world's systems and flush.
- **Bounds, dead zone, offset:** optional bounds rectangle in meters applied after follow and smoothing, before shake, centering the view on an axis where it is larger than the bounds; dead zone as a fraction of the view (default 0); follow offset in meters (default 0).
- **Shake:** amplitude in meters, frequency in Hz, duration in seconds, linear decay; fixed-point value noise from the MurmurHash3 finalizer (ADR-0034), bit-exact; per-camera seed (default `0x4C494F4E`) and `reseed`; the strongest of overlapping shakes wins; the logical position never changes.
- **Conversions:** world to screen includes shake; screen to world (picking) ignores it unless `includeShake`.

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Camera); canonical type names in [ADR-0037](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md); signatures written by the batch: camera, view transform value, view rectangle value, follow lookup contract, zoom curve enum.

## Batches (order)

1. View rectangle, view transform, translation, zoom clamp (`REQ-CAM-001`, `REQ-CAM-003`, `REQ-CAM-007`, `REQ-CAM-009`).
2. Follow and smoothing (`REQ-CAM-004`, `REQ-CAM-005`).
3. Zoom animation (`REQ-CAM-002`).
4. Shake (`REQ-CAM-006`).
5. Bounds clamp, then dead zone and follow offset (`REQ-CAM-010`).

## Lifecycle and concurrency

State changes in the update phase only (`REQ-LOOP-003`); rendering reads (`REQ-LOOP-006`). No singleton. Runtime thread only.

## Units and Android/DEX compatibility

Meters, seconds, Hz, dimensionless zoom; `double` everywhere, `float` only when writing commands. Pure math, no reflection, no `java.awt`, no `java.util.Random`.

## Dependencies

`core` only. The adapter implementing the lookup over a `World` lives in `composition` and in examples.

## Tests

Unit tests with a controllable clock and a fixed seed: view rectangle and transform at several zoom and `alpha` values, zoom clamp and curves, translation, follow including a missing object, smoothing at several frame rates, bounds and dead zone, shake reference values, round-trip conversions with and without shake. Headless example required (`REQ-DOC-001`).

## Acceptance criteria

- [ ] Requirements `REQ-CAM-001` to `REQ-CAM-010` verified.
- [ ] Automated tests added and passing; complete Javadoc.
- [ ] Example and `api-index.md` entry; no forbidden coupling; Android/DEX compatibility verified.
