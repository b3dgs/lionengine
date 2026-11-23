# Feature: camera

**Status: PROPOSED**

- Owner: to be filled in
- Roadmap phase: Phase 3, see `roadmap.md`
- Affected modules: `camera`, `core`, `test-support`, `examples`
- Requirements: `REQ-CAM-001` to `REQ-CAM-008`
- Applicable ADRs: [`ADR-0010`](../decisions/0010-camera-system-in-camera.md) (ACCEPTED), [`ADR-0001`](../decisions/0001-technical-baseline-and-conventions.md)
- Open questions involved: `OQ-010`, `OQ-012`, `OQ-013`, `OQ-021`, `OQ-108`, `OQ-130` to `OQ-136`

## Objective

A headless camera representing the visible area of the world: zoom, translation, follow by entity ID, smoothed movement and shake.

## Out of scope

Multi-camera and split screen, bounds clamp, rotation, parallax, dead zone, follow offset (`OQ-136`); meters-to-pixels factor (`OQ-012`).

## Expected behavior

- The camera exposes a view rectangle in world units (meters).
- Zoom is clamped to a range and animated; a change applies from the next frame.
- Translation is absolute or relative.
- Follow targets an entity ID through a lookup contract defined by `camera`; a missing entity is safe.
- Delay is exponential smoothing with a time constant in seconds, frame-rate independent; 0 is rigid.
- Shake has amplitude, duration and frequency; per-camera seeded noise, deterministic for a given seed.
- World-to-screen and screen-to-world conversion.

## Envisaged APIs / contracts

Responsibilities only; signatures are not fixed. See `architecture/contracts.md` (Camera).

## Lifecycle and concurrency

State changes in the update phase only (`REQ-LOOP-003`); rendering is read-only (`REQ-LOOP-006`). No singleton. Threads: `OQ-030`.

## Units and Android/DEX compatibility

Meters, seconds, dimensionless zoom. Pure math, no reflection, no `java.awt`.

## Dependencies

`core` only. The ECS adapter lives outside `camera` (`OQ-132`).

## Tests

Unit tests with a controllable clock and a fixed seed: zoom clamp, translation, follow including a missing entity, smoothing at different frame rates, seeded shake determinism, conversions. Headless example required (`REQ-DOC-001`).

## Acceptance criteria

- [ ] Requirements `REQ-CAM-001` to `REQ-CAM-008` verified.
- [ ] Example and `api-index.md` entry.
