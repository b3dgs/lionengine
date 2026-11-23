# Architecture decisions (ADR)

**Status: PROPOSED — process description; the decisions listed below carry their own status.**

An approved decision is binding for future implementations until it is explicitly superseded.

## Lifecycle

- `PROPOSED`: proposal under discussion.
- `ACCEPTED`: approved decision (equivalent to `VALIDATED` in other documents).
- `REJECTED`: rejected proposal, kept for the record.
- `SUPERSEDED`: replaced by another decision.

## Convention

Create a file `NNNN-short-title.md` from [`ADR-TEMPLATE.md`](ADR-TEMPLATE.md), using the next free number. Never rewrite the history of an accepted decision: create a new ADR that supersedes it. Only editorial fixes (typos, broken links, translation) may touch an accepted ADR, and they must be noted in the file.

Decisions must be atomic (one main decision each), motivated, and describe both positive and negative consequences. When a decision answers an open question, mark that question `RESOLVED` in [`../open-questions.md`](../open-questions.md) and update [`../traceability.md`](../traceability.md).

## Accepted decisions

- [`ADR-0001`](0001-technical-baseline-and-conventions.md) — Java 25+, Maven 3+, Android/DEX compatibility, world units and principles of the default game loop.
- [`ADR-0002`](0002-display-modes-resolution-and-output-filtering.md) — Display modes, resolution selection and output filtering contracts (accepted 2026-11-22).
- [`ADR-0003`](0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md) — Sprite, animation and sprite sheet contracts in `graphics` (accepted 2026-11-22).
- [`ADR-0004`](0004-audio-contracts-short-and-long-sounds.md) — Audio contracts: short and long sounds, variant sets and playlists (accepted 2026-11-22).
- [`ADR-0005`](0005-common-convention-for-modules-handling-resources.md) — Common convention for modules handling resources: `resources` media-agnostic, decoded caches in consuming modules, one key and release convention (accepted 2026-11-22).
- [`ADR-0006`](0006-display-scaling-effective-result-and-fallback-orders.md) — Display scaling policy, effective-result report and fallback orders; extends ADR-0002 (accepted 2026-11-22).
- [`ADR-0007`](0007-resource-convention-keys-release-and-errors.md) — Resource convention: keys, release and errors; extends ADR-0005 (accepted 2026-11-22).
- [`ADR-0008`](0008-awt-desktop-implementer-of-platform-api.md) — AWT desktop implementer of `platform-api` (`platform-awt`) (accepted 2026-11-22).
- [`ADR-0010`](0010-camera-system-in-camera.md) — Camera system in `camera`: world-unit view rectangle, zoom, follow through a lookup contract, smoothing, seeded shake (accepted 2026-11-22, issue #868).

- [`ADR-0009`](0009-tile-map-contracts-in-tilemap.md) — Tile map contracts in `tilemap` (accepted 2026-11-22, issue #865).

- [`ADR-0011`](0011-ecs-dynamic-objects-traits-and-standard-traits.md) — ECS: dynamic objects and traits, cached queries, deferred structural changes, system order and standard traits in `ecs` and `traits` (accepted 2026-11-22).
- [`ADR-0012`](0012-platform-and-build-baseline.md) — Platform and build baseline: Android API 21, DEX through a separate Gradle module, Maven layout, JUnit 5, GPL-3.0, semantic versioning, GitHub Actions (accepted 2026-11-22, issue #875).
- [`ADR-0013`](0013-world-coordinates-and-angles.md) — World coordinate system and angles: origin (0, 0), Y up, degrees (accepted 2026-11-22, issue #875).
- [`ADR-0014`](0014-meters-to-pixels-numeric-type-and-rotation.md) — Meters-to-pixels conversion in the camera, `double` world quantities, counter-clockwise rotation (accepted 2026-11-22, issue #875).
- [`ADR-0015`](0015-single-version-and-package-dependency-rule.md) — One version for all modules; package dependency rule of `radial-encapsulation-maven-plugin` (accepted 2026-11-22, issue #875).
- [`ADR-0016`](0016-angle-range-and-normalization.md) — Angle range: absolute angles in `[0, 360)`, signed differences in `(-180, 180]` (accepted 2026-11-22, issue #875).
- [`ADR-0017`](0017-fixed-step-game-loop.md) — Fixed-step game loop with accumulator, interpolation by default, lag cap, pause and time scale, fail-fast (accepted 2026-11-22, issue #876).
- [`ADR-0018`](0018-runtime-thread-events-and-cache-thread-safety.md) — One runtime thread, platform event inbox, cache thread confinement (accepted 2026-11-22, issue #877).
- [`ADR-0021`](0021-scene-world-viewport-and-object-model.md) — Scene, world, camera and viewport vocabulary; single ECS object model; world hosted by `ecs`; physics contracts module (accepted 2026-11-23, issue #878).
- [`ADR-0022`](0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md) — Optional multiplayer, audio backend split, desktop AWT reference frontend, `render-awt` on `platform-awt`, unknown display values as 0 (accepted 2026-11-23, issue #879).
- [`ADR-0023`](0023-game-repository-change-requests-assets-and-api-index-check.md) — Game repository on pinned Maven artifacts, engine change request issues, asset provenance, API index CI check (accepted 2026-11-23, issue #880).
- [`ADR-0024`](0024-determinism-performance-budgets-and-desktop-support.md) — Determinism groundwork, performance budgets per tier, supported desktop platforms (accepted 2026-11-23, issue #881).
- [`ADR-0025`](0025-filter-implementation-shaders-and-display-settings.md) — Own filter implementations, common GLSL/GLSL ES shaders through `resources`, display settings persistence left to the game, HiDPI constraint (accepted 2026-11-23, issue #882).
- [`ADR-0026`](0026-graphics-pixels-cache-transforms-animation-and-commands.md) — Graphics contracts: pixel buffer and PNG decoder, image cache, color transform capability, pivot and composition, animation, sheet ids, loading, render commands (accepted 2026-11-23, issue #883).
- [`ADR-0027`](0027-audio-policies-formats-latency-and-first-scope.md) — Audio policies: short-sound cache, voices, variants, streaming, playlists, formats, latency targets, first scope (accepted 2026-11-23, issue #884).
- [`ADR-0028`](0028-tile-map-collision-json-definitions-and-storage.md) — Tile maps: collision through `physics`, JSON definition files, Tiled JSON, auto-tiling rules, tileset ownership, storage (accepted 2026-11-23, issue #885).
- [`ADR-0029`](0029-camera-details-view-transform-follow-shake-zoom.md) — Camera details: view transform, `long` follow key, rigid default delay, integer-noise shake, zoom, candidate features (accepted 2026-11-23, issue #886).
- [`ADR-0019`](0019-loading-resource-policy-and-memory.md) — Synchronous-first loading with opt-in asynchronous contract, explicit reference-counted resources, hot-path memory policy (accepted 2026-11-22, issue #877).
- [`ADR-0020`](0020-no-reflection-factory-registry-and-capability-lookup.md) — No reflection: factory registry for data-driven objects, typed capability lookup (accepted 2026-11-22, issue #877).

## Proposed decisions

None at the moment.

Other likely candidates are listed in [`../open-questions.md`](../open-questions.md) (see items needed by Phase 0).
