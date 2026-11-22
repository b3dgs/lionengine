# ADR-0010 — Camera system in `camera`

- Status: PROPOSED
- Date: 2026-11-22
- Decision makers: project owner (decisions 1 to 6 of issue #868, comment `@claude apply 1A 2A 3A 4A 5A 6A`). The issue also asks for ACCEPTED, but the apply instructions say to keep the ADR PROPOSED; it stays PROPOSED until the owner confirms the status change (see the pull request).
- Related requirements: `REQ-CAM-001` to `REQ-CAM-008`, `REQ-LOOP-003`, `REQ-LOOP-006`
- Open questions resolved: none. Touched: `OQ-010`, `OQ-012`, `OQ-013`, `OQ-021`, `OQ-040`, `OQ-041`, `OQ-042`, `OQ-108`. Created: `OQ-130` to `OQ-136`.
- Supersedes / superseded by: none. Builds on [ADR-0001](0001-technical-baseline-and-conventions.md), [ADR-0003](0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md) and [ADR-0009](0009-tile-map-contracts-in-tilemap.md) (not modified).

## Context

The engine has no camera: ADR-0003 and ADR-0009 pass a plain pixel viewport and leave the world-to-screen conversion to `OQ-012` and `OQ-040`. A camera is needed to show a view area of the world, zoom, translate, follow an object, smooth its movement for gameplay feel and shake. The ECS strategy is still open (`OQ-041`), so the camera must not depend on it.

## Decision

Decisions taken by the owner (issue #868):

1. **Placement (1A):** a single `camera` module depending on `core` only; no `camera-api` split.
2. **ECS coupling (2A):** `camera` defines a lookup contract (entity ID to position); the ECS system or adapter that implements it lives outside `camera`. `camera` does not depend on `ecs`. The entity ID type is `OQ-132`.
3. **World units (3A):** the camera works in world units, meters per ADR-0001; the packet's "pixels" is not followed. The meters-to-pixels factor stays `OQ-012`.
4. **Movement delay (4A):** exponential smoothing with a time constant in seconds; 0 means rigid (no delay); frame-rate independent. Follow delay details: `OQ-133`.
5. **Shake randomness (5A):** per-camera seeded noise, deterministic for a given seed; no shared engine RNG. Details: `OQ-130`; units and picking: `OQ-134`.
6. **Output to rendering (6A):** the camera exposes a plain view rectangle in world units, consistent with ADR-0009 decision 1A; no `Camera` type is passed to rendering. The consumer contract is `OQ-131` and `OQ-108`.

Invariants: world units are meters, time is seconds, zoom is dimensionless and clamped to a configured range. Zoom changes, translation, follow, delay and shake advance in the update phase only; rendering reads the state and never mutates it (`REQ-LOOP-003`, `REQ-LOOP-006`). Following a missing entity is safe (no exception, the camera keeps its position). The camera supports world-to-screen and screen-to-world conversion. No platform type appears in `camera`, no singleton, no reflection (Android/DEX unaffected); no dependency of `core` or an `*-api` module on `camera`.

Not decided: Java types and signatures, zoom details (`OQ-135`), the origin and Y direction (`OQ-010`), numeric type (`OQ-013`), multi-camera, bounds clamp, rotation, parallax, dead zone and follow offset (`OQ-136`).

## Options considered

### Placement
- A (retained): single `camera` module.
- B: `camera-api` plus an implementation. No second implementation is foreseen.
- C: inside `render-api`. Couples simulation state to rendering.
- D: inside `world`. Forces a `world` dependency.

### ECS coupling
- A (retained): lookup contract defined by `camera`, adapter outside.
- B: `camera` depends on `ecs`. Pre-empts `OQ-041`.
- C: the game drives the position; no follow. Pushes the work to every game.

### World units
- A (retained): meters (ADR-0001).
- B: pixels. Contradicts ADR-0001.

### Movement delay
- A (retained): exponential smoothing, time constant in seconds.
- B: fixed lag. Less natural.
- C: dead zone and look-ahead. Larger scope (`OQ-136`).

### Shake randomness
- A (retained): per-camera seeded noise.
- B: shared engine RNG. Couples cameras and makes tests order-dependent.

### Output to rendering
- A (retained): plain view rectangle.
- B: a `Camera` type seen by rendering. Couples rendering modules to `camera`.

## Consequences

- Positive consequences: headless, deterministic and testable camera; no dependency on ECS or a frontend; modules such as `tilemap` stay decoupled.
- Costs / constraints: an adapter is needed to follow ECS entities; the view-rectangle consumer contract and the entity ID type remain open (`OQ-131`, `OQ-132`).
- Impact on modules and APIs: new module `camera` (dependency: `core`); `modules.md`, `contracts.md`, `roadmap.md`, `testing.md`, `api-index.md` updated.
- Impact on tests and documentation: unit tests with a controllable clock and fixed seed; feature file `features/camera.md`; headless example and `api-index.md` entry; no new dependency.

## Validation

Not yet approved as ACCEPTED in this change. Status stays PROPOSED until the owner confirms it.
