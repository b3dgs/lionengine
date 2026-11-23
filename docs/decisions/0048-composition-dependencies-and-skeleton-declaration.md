# ADR-0048 — `composition` dependencies and its declaration at the build skeleton

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, at the project owner's request of 2026-11-23 to review the architecture with the dedicated skills and apply the changes the review suggests, so that the base skeleton of each module and its interfaces can be defined; reversible by a superseding ADR
- Related requirements: `REQ-CORE-005`, `REQ-CAM-004`, `REQ-IN-001`, `REQ-MAP-008`, `REQ-STD-010`
- Open questions resolved: none (defect found by the skeleton-readiness review of 2026-11-23)
- Supersedes / superseded by: supersedes the dependency list of `composition` in [ADR-0031](0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md) (section 3); every other point of ADR-0031 is unchanged; completes [ADR-0037](0037-package-tree-per-module-callbacks-and-canonical-names.md) (section 2) and [ADR-0040](0040-thread-hand-off-contracts-in-core-and-loop-wiring.md) (section 3), both unchanged

## Context

ADR-0031 makes `composition` the optional ready-made scene composition: world, views, tile map, background, music, and the camera follow adapter over the world. It lists the module's dependencies as `runtime`, `ecs`, `camera`, `graphics`, `tilemap`, `audio-api` and `render-api`. The responsibilities given to it since then need more:

- **Follow adapter** (ADR-0029, [`features/camera.md`](../features/camera.md)): it implements the camera follow lookup (`long` ID → position) over a `World`. An object's position is its `Transformable` trait, a `traits` type ([`features/standard-traits.md`](../features/standard-traits.md)).
- **Loop wiring** (ADR-0040, section 3): "the application (or `composition`)" hands the inbox to the platform, the input adapters, the audio backend and the loaders, and the platform's worker pool to the loaders. Its update callback advances every `InputState` before the scenes; its render callback renders the scenes and then presents through the render backend. These are `platform-api` and `input-api` types.
- **World and tile map together**: tile collision reaches the standard `Collision` system through the `physics` shape-query contract, which `TileMap` implements (ADR-0028, `REQ-MAP-008`, `REQ-STD-010`). A composition of a world and a tile map hands the map to the `Collision` system as a `physics` type.

Separately, ADR-0037 (section 2) says the build skeleton declares the modules of Phases 1 to 3, but the module list of [`features/build-skeleton.md`](../features/build-skeleton.md) omits `composition` (Phase 3).

## Decision

1. The `composition` row becomes: `runtime`, `ecs`, `traits`, `physics`, `camera`, `graphics`, `tilemap`, `audio-api`, `input-api`, `platform-api`, `render-api`. The module stays optional: no module depends on it, and the foundation never needs it.
2. The build skeleton declares `composition` with the other modules of Phases 1 to 3, empty. Its feature page is written when Phase 3 schedules it ([`features/README.md`](../features/README.md)).

## Options considered

- Widen the row to what the accepted responsibilities use (chosen).
- Keep the row and leave the wiring, the follow adapter and the tile collision hand-over to the application (rejected): contradicts ADR-0031 and ADR-0040, which give them to `composition`.
- Split `composition` into a wiring module and a content module (rejected): two optional modules for one convenience.

## Consequences

- Positive consequences: every responsibility of `composition` can be written against its declared dependencies; the skeleton matches ADR-0037.
- Costs / constraints: `composition` is the widest module of the graph, which is acceptable because nothing depends on it.
- Impact on modules and APIs: `composition` row and diagram; no type changes.
- Impact on tests and documentation (including Android/DEX compatibility): `modules.md`, `overview.md`, `interfaces.md`, `features/build-skeleton.md`, `roadmap.md`. Shared module, Java 21 (ADR-0044); no new JDK API.

## Validation

Decided by the agent on 2026-11-23 at the owner's request to apply the review's changes; reversible by a superseding ADR.
