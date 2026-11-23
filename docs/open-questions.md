# Open questions

**Status: PROPOSED — single registry of every `TO BE DEFINED` item.**

Rules:
- Do not guess the answer to an open question in code or documentation. Ask, or record the assumption you made and keep it reversible.
- When a question is answered, record the answer in an ADR (or a `REQ-*`), then mark the row `RESOLVED` with a link. Never delete a row; identifiers are stable.
- "Needed by" is the roadmap phase before which the question must be settled.

## Resolution log

Summary of the answers recorded so far, by decision. Details are in the rows below and in the ADRs. Follow-up questions created by a decision stay open in their own rows.

| Decision | Resolved | Follow-ups opened | Issue |
|---|---|---|---|
| [ADR-0012](decisions/0012-platform-and-build-baseline.md) platform and build baseline | OQ-001 to OQ-008 | OQ-147, OQ-148, OQ-149 | #875 |
| [ADR-0013](decisions/0013-world-coordinates-and-angles.md) coordinates and angles | OQ-010, OQ-011 | — | #875 |
| [ADR-0014](decisions/0014-meters-to-pixels-numeric-type-and-rotation.md) meters to pixels, numeric type, rotation | OQ-012, OQ-013 | — | #875 |
| [ADR-0015](decisions/0015-single-version-and-package-dependency-rule.md) single version, package rule | narrows OQ-005, OQ-149 | — | #875 |
| [ADR-0016](decisions/0016-angle-range-and-normalization.md) angle range | OQ-150 | — | #875 |
| [ADR-0017](decisions/0017-fixed-step-game-loop.md) fixed-step game loop | OQ-020 to OQ-023 | OQ-151 | #876 |
| [ADR-0018](decisions/0018-runtime-thread-events-and-cache-thread-safety.md) runtime thread, events, cache thread safety | OQ-030, OQ-121, OQ-122 | OQ-153 | #877 |
| [ADR-0019](decisions/0019-loading-resource-policy-and-memory.md) loading, resource policy, memory | OQ-031, OQ-032, OQ-033 | OQ-153 | #877 |
| [ADR-0020](decisions/0020-no-reflection-factory-registry-and-capability-lookup.md) no reflection, factory registry, capabilities | OQ-034, OQ-152 | OQ-154 | #877 |
| [ADR-0021](decisions/0021-scene-world-viewport-and-object-model.md) scene, world, viewport, single ECS object model, physics contracts | OQ-040, OQ-041, OQ-042, OQ-043, OQ-044, OQ-146 | OQ-155 | #878 |
| [ADR-0022](decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md) optional multiplayer, audio backends, reference frontend, surface hand-off, unknown display values | OQ-052, OQ-053, OQ-055, OQ-123, OQ-124 | OQ-156 | #879 |
| [ADR-0023](decisions/0023-game-repository-change-requests-assets-and-api-index-check.md) game repository, engine change requests, asset provenance, API index check | OQ-080, OQ-081, OQ-082, OQ-083 | OQ-157 | #880 |

Sections "Platform and build", "Coordinates and units", "Game loop", "Runtime" and "World and objects" and "Modules and APIs" and "Game development and workflow" have no unresolved original question left. Next section to treat: to be chosen by the owner.

## Platform and build

| ID | Question | Needed by |
|---|---|---|
| OQ-001 | **RESOLVED** by [ADR-0012](decisions/0012-platform-and-build-baseline.md): minimum Android API level 21, raised only if Java 25 requires it (owner answer, issue #875). Which Java 25 features and APIs work at that level: OQ-147. Original question: Minimum Android API level and targeted Android versions. | Phase 1 |
| OQ-002 | **RESOLVED** by [ADR-0012](decisions/0012-platform-and-build-baseline.md): DEX produced by a separate Gradle module consuming the Maven artifacts (Android Gradle Plugin 9.2.1, core library desugaring `desugar_jdk_libs:2.1.5`, as in `master`). DEX verification job: OQ-147. Original question: How is DEX produced from a **Maven** build? Android Gradle Plugin is Gradle-based: options include a Maven-driven D8/R8 invocation or a separate Gradle module consuming the Maven artifacts. Also: D8/R8 versions and *core library desugaring* policy. | Phase 0 |
| OQ-003 | **RESOLVED** by [ADR-0012](decisions/0012-platform-and-build-baseline.md): multiple Maven modules, group `com.b3dgs.lionengine`, artifacts `lionengine-XXX`, base package `com.b3dgs.lionengine`; modules are added at the build skeleton step. Remaining details: OQ-149. Original question: Physical layout: single or multiple Maven modules, group/artifact identifiers, base package name, versioning of artifacts, build/test commands. *Baseline observed (not a decision): `pom.xml` is an aggregator (`packaging pom`), group `com.b3dgs.lionengine`, artifact `lionengine`, version `11.0.0-SNAPSHOT`, parent `com.b3dgs:b3dgs-parent`, base package `com.b3dgs.lionengine`; no modules yet.* | Phase 0 |
| OQ-004 | **RESOLVED** by [ADR-0012](decisions/0012-platform-and-build-baseline.md): JUnit 5; coverage with JaCoCo merged for SonarCloud, as in `master`. Original question: Test framework and configuration (JUnit 5 is a candidate), coverage tooling. | Phase 0 |
| OQ-005 | **RESOLVED** by [ADR-0012](decisions/0012-platform-and-build-baseline.md): `radial-encapsulation-maven-plugin`; it forbids dependencies from a package to its descendant packages ([ADR-0015](decisions/0015-single-version-and-package-dependency-rule.md)); its coverage of REQ-CORE-005 is verified on the build skeleton. Original question: Tool enforcing module dependency rules automatically (REQ-CORE-005). *Baseline observed: the parent POM already configures `radial-encapsulation-maven-plugin` with `maxViolations` 0; evaluate whether it covers REQ-CORE-005.* | Phase 0 |
| OQ-006 | **RESOLVED** by [ADR-0012](decisions/0012-platform-and-build-baseline.md): GPL-3.0 confirmed. The third-party policy details moved to OQ-148. Original question: Project license, and policy for third-party libraries and codecs. *Baseline observed: the repository `LICENSE` is GPL-3.0 (confirm it stays the project license); the third-party policy remains to be defined.* | Phase 0 |
| OQ-007 | **RESOLVED** by [ADR-0012](decisions/0012-platform-and-build-baseline.md): semantic versioning; deprecation as in Java. Details: OQ-149. Original question: API compatibility and versioning policy (pre-1.0 stability, deprecation delay). | Phase 1 |
| OQ-008 | **RESOLVED** by [ADR-0012](decisions/0012-platform-and-build-baseline.md): GitHub Actions, same pipeline as `master` `ci-cd.yml`, adapted for `rewrite/v11` (OQ-149); Android job: OQ-147. Original question: Continuous-integration platform, including the Android/DEX job required by ADR-0001. | Phase 0 |
| OQ-147 | Android toolchain verification for Java 25: highest class file version, language level and Java API set accepted by D8/R8 for shared modules at API 21 with desugaring (the proven path in `master` is Java 21 bytecode); the Gradle verification module that really dexes the artifacts (an AAR build alone does not run D8, REQ-TECH-006); JDK used by that job. Results go to `android-compatibility.md`. *Owner: scope approved as proposed (issue #875); stays open until the verification is done and recorded.* | Phase 0 |
| OQ-148 | Third-party dependency policy under GPL-3.0: allowed licenses (GPL-3.0-compatible), where each dependency is recorded (purpose, license, Android compatibility), codec policy. *Owner: scope approved as proposed (issue #875); stays open until the verification is done and recorded.* | Phase 0 |
| OQ-149 | Build skeleton details: module list, package naming per module under `com.b3dgs.lionengine`, Maven profiles and exact build/test commands, package tree reflecting module dependency direction (ADR-0015), deprecation removal delay (Java practice), adaptation of `ci-cd.yml` (the `editor` job is not carried over unless requested). *Owner: TODO, to be filled at the build skeleton step (issue #875).* | Phase 0 |

## Coordinates and units

| ID | Question | Needed by |
|---|---|---|
| OQ-010 | **RESOLVED** by [ADR-0013](decisions/0013-world-coordinates-and-angles.md): origin `(0, 0)`, Y up in the world; the screen is Y down, converted in the camera. Original question: Origin of the coordinate system and positive direction of the Y axis. | Phase 0 |
| OQ-011 | **RESOLVED** by [ADR-0013](decisions/0013-world-coordinates-and-angles.md): degrees in every public API. Rotation direction by [ADR-0014](decisions/0014-meters-to-pixels-numeric-type-and-rotation.md), range by OQ-150. Original question: Angle conventions (radians/degrees, rotation direction). Also governs sprite rotation (ADR-0003, OQ-104). | Phase 0 |
| OQ-012 | **RESOLVED** by [ADR-0014](decisions/0014-meters-to-pixels-numeric-type-and-rotation.md): conversion in the camera only, `pixelsPerMeter` = logical frame width in pixels / view width in meters x zoom, no global constant; screen space top-left origin, Y down, flipped in the camera. Original question: Meters-to-pixels conversion: pixels per meter, camera, zoom, viewport, screen coordinate system. Sprites are placed in pixels of the logical frame (ADR-0003, ADR-0002); this question owns the conversion from meters. | Phase 2 |
| OQ-013 | **RESOLVED** by [ADR-0014](decisions/0014-meters-to-pixels-numeric-type-and-rotation.md): `double` for world quantities, `float` only at the render boundary. Original question: Numeric type and precision for world quantities (`float` vs `double`), including on Android. | Phase 0 |
| OQ-150 | **RESOLVED** by [ADR-0016](decisions/0016-angle-range-and-normalization.md): absolute angles in `[0, 360)`, signed differences in `(-180, 180]`; direction by ADR-0014. Original question: rotation direction, reference axis and angle range. | Phase 0 |

## Game loop

| ID | Question | Needed by |
|---|---|---|
| OQ-020 | **RESOLVED** by [ADR-0017](decisions/0017-fixed-step-game-loop.md): fixed step (default 60 Hz, configurable), abstract monotonic clock, accumulator with catch-up, lag cap with excess dropped and reported (cap value: OQ-151). Original question: Simulation step duration, monotonic clock, catch-up strategy and maximum tolerated lag. | Phase 0 |
| OQ-021 | **RESOLVED** by [ADR-0017](decisions/0017-fixed-step-game-loop.md): the factor is `accumulator / step`, always in `[0, 1)`; lateness is absorbed by catch-up, a factor above 1 never exists. Original question: Exact mathematical definition of the extrapolation factor, its bounds, and the behavior under extreme lag (factor greater than 1). | Phase 0 |
| OQ-022 | **RESOLVED** by [ADR-0017](decisions/0017-fixed-step-game-loop.md): interpolation by default, extrapolation opt-in; both presentation-only. Original question: Interpolation: whether it is offered, how it differs from extrapolation, and the limits of prediction. | Phase 2 |
| OQ-023 | **RESOLVED** by [ADR-0017](decisions/0017-fixed-step-game-loop.md): pause by clock reset without catch-up, slow motion by a time scale on the accumulator, fail-fast on exceptions, shutdown at a frame boundary (thread: OQ-030). Original question: Pause, slow motion, exceptions during a step, and shutdown policy. | Phase 1 |
| OQ-151 | Game loop follow-ups of ADR-0017: calibrated value of the lag cap (proposed 0.25 s) and of the default step; bounds of the time scale; form of the lag report; snap tolerance of `alpha` when the render rate equals the update rate; separate unscaled time domain for UI and menus; integer accumulator details. | Before the loop batch |

## Runtime

| ID | Question | Needed by |
|---|---|---|
| OQ-030 | **RESOLVED** by [ADR-0018](decisions/0018-runtime-thread-events-and-cache-thread-safety.md): one runtime thread owns all state, the platform runs the loop on its required thread, few documented thread-safe entry points, separate render thread deferred. Original question: Owning thread of rendering and runtime; overall concurrency model and thread rules. | Phase 1 |
| OQ-031 | **RESOLVED** by [ADR-0019](decisions/0019-loading-resource-policy-and-memory.md): synchronous loading first, opt-in asynchronous contract completed through the inbox, workers only decode. Original question: Asynchronous loading policy. | Phase 2 |
| OQ-032 | **RESOLVED** by [ADR-0019](decisions/0019-loading-resource-policy-and-memory.md): explicit reference counting and release, no weak references or automatic eviction, reload only for context loss and development. Original question: Resource ownership, closing, caching and reloading policy. Cache key and release convention of the consuming modules: OQ-118 (ADR-0005). | Phase 1 |
| OQ-033 | **RESOLVED** by [ADR-0019](decisions/0019-loading-resource-policy-and-memory.md): no allocation in hot paths in steady state, explicit release, no finalizers or `Cleaner`. Original question: Memory management policy (allocation in hot paths, release of resources). | Phase 1 |
| OQ-034 | **RESOLVED** by [ADR-0020](decisions/0020-no-reflection-factory-registry-and-capability-lookup.md): typed optional capability lookup on an explicit platform object, wired explicitly, no class-name lookup. Original question: Platform capability mechanism (interface, registry or configuration). | Phase 1 |
| OQ-121 | **RESOLVED** by [ADR-0018](decisions/0018-runtime-thread-events-and-cache-thread-safety.md): cache operations on the runtime thread only, decoded data immutable, workers never touch caches. Original question: Thread-safety rule shared by the caches of the modules handling resources (ADR-0005, ADR-0007): which threads may load, read and release. Split from OQ-118 by issue #859. Asynchronous loading stays OQ-031; reloading stays OQ-032. | Phase 2 (before the first consuming module) |
| OQ-122 | **RESOLVED** by [ADR-0018](decisions/0018-runtime-thread-events-and-cache-thread-safety.md): platform events through a thread-safe FIFO inbox delivered on the runtime thread at the start of an iteration; internal events synchronous in the update phase. Original question: `core` event contract: how engine events (such as the effective result of a display or filter change, ADR-0006) are delivered, on which thread, and in which order relative to the update step. Raised by issue #859. | Phase 1 |
| OQ-152 | **RESOLVED** by [ADR-0020](decisions/0020-no-reflection-factory-registry-and-capability-lookup.md): explicit registry of string ids and factories registered at startup, typed configuration, no reflection; build-time generation is a possible later option (OQ-154). Original question: Data-driven object definition without reflection: how a definition file (XML in the previous engine) names the traits and types to instantiate and their parameters.  Related: OQ-041, OQ-042, OQ-126, OQ-146, OQ-034. | Before the first data-driven loader batch |
| OQ-153 | Runtime follow-ups of ADR-0018 and ADR-0019: inbox capacity and overflow policy, asynchronous worker pool size, exact asynchronous handle contract and progress, memory budgets (OQ-070). | Before the runtime batch |
| OQ-154 | Follow-ups of ADR-0020: id naming rules (namespacing, case), typed configuration API, definition file format (XML or other, with OQ-126), module hosting the registry (with OQ-146), optional build-time generated registration and its dependency review (OQ-148). | Before the first data-driven loader batch |
| OQ-155 | Follow-ups of ADR-0021: scene manager details (transitions, scene stack for pause or overlay menus, scene identity, loading screen with asynchronous loading of ADR-0019), the optional composition module (world + camera + background + music) and its name, package layout of `physics`, how several views are bound at render time (OQ-131, OQ-108). | Before the scene batch |
| OQ-156 | Follow-ups of ADR-0022: final names of the audio backend modules and decoder modules, shared `awt-common` module if a second AWT consumer of the surface appears, exact form of the "is known" check for display values, naming of the AWT surface capability. | Before the first audio backend or render-awt batch |
| OQ-157 | Follow-ups of ADR-0023: format and location of the asset provenance metadata file, CI check of its presence, hosting of published artifacts (OQ-149), implementation of the API index check. | Before the first game asset or the build skeleton |

## World and objects

| ID | Question | Needed by |
|---|---|---|
| OQ-040 | **RESOLVED** by [ADR-0021](decisions/0021-scene-world-viewport-and-object-model.md): scene = lifecycle unit composing a world, cameras, background and music (lifecycle contract in `runtime`); world = container of objects, hosted by `ecs`; camera = world-side view (ADR-0010); viewport = surface-side region; a view pairs one camera with one viewport. Original question: Definition of a scene, a world and a viewport. | Phase 3 |
| OQ-041 | **RESOLVED** by [ADR-0021](decisions/0021-scene-world-viewport-and-object-model.md): the ECS of ADR-0011 is the single object model; no second "simple" strategy; a game using no objects links neither `ecs` nor `traits` (owner delegated the decision). Original question: Relationship between simple object management and ECS. The project goal describes an ECS-based engine, whereas `docs/vision.md` makes ECS optional (REQ-OBJ-002 is `Could`). Decide whether ECS is the core model or an optional module. | Phase 0 |
| OQ-042 | **RESOLVED** by [ADR-0021](decisions/0021-scene-world-viewport-and-object-model.md): traits attach to ECS objects only. Original question: Are traits attached to classic objects, ECS entities, or both through a common contract? | Phase 3 |
| OQ-043 | **RESOLVED** by [ADR-0021](decisions/0021-scene-world-viewport-and-object-model.md): new headless module `physics` (on `core`): shapes, overlap tests, uniform-grid broad phase; used by `traits` and later `tilemap`. Details: OQ-142, OQ-125. Original question: Where do the "physics contracts" required by `traits` live (collision shapes, bodies)? No module owns them today. | Phase 3 |
| OQ-044 | **RESOLVED** by [ADR-0021](decisions/0021-scene-world-viewport-and-object-model.md): `ecs`/world and `tilemap` are independent; they meet only through `physics` contracts and the owning scene. Original question: Is `world` independent of `tilemap`? | Phase 3 |

## Modules and APIs

| ID | Question | Needed by |
|---|---|---|
| OQ-050 | **RESOLVED** by [ADR-0002](decisions/0002-display-modes-resolution-and-output-filtering.md): `platform-api` and `render-api` are separate modules (owner decision 1B of issue #850). Original question: are `platform-api` and `render-api` separate or merged? Desktop implementer of `platform-api`: OQ-119. | Phase 1 |
| OQ-051 | **RESOLVED** by [ADR-0003](decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md): `graphics` depends on `render-api`; sprites emit `render-api` commands (owner decision 3B of issue #852). Original question: does `graphics` depend on `render-api`, or does it only describe image/sprite data? | Phase 2 |
| OQ-052 | **RESOLVED** by [ADR-0022](decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md): `multiplayer` is an optional extension; local split-screen needs no such module. Original question: Is `multiplayer` a base module or an optional extension? | Phase 5 |
| OQ-053 | **RESOLVED** by [ADR-0022](decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md): audio backends split per output library or platform; decoders per codec only when a third-party library is needed. Original question: Are audio backends split by library, codec or platform? Touched by issue #853 (see OQ-115), not resolved. | Phase 2 |
| OQ-054 | **RESOLVED** by [ADR-0003](decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md): abstract commands (owner decision 3B of issue #852); the command contract itself is OQ-108. Original question: rendering API style: abstract commands, drawing context, or specialized interface. | Phase 2 |
| OQ-055 | **RESOLVED** by [ADR-0022](decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md): desktop AWT (`platform-awt` plus `render-awt`) and the headless frontend are the first reference; Android follows, DEX check from the skeleton (OQ-147). Original question: Which frontend is the first reference frontend? (A headless frontend for tests is already required by REQ-TEST-001.) Touched by [ADR-0008](decisions/0008-awt-desktop-implementer-of-platform-api.md) (ACCEPTED): `platform-awt` is the desktop reference implementer of `platform-api`; the render side is OQ-123. | Phase 1 |
| OQ-120 | **RESOLVED** by [ADR-0006](decisions/0006-display-scaling-effective-result-and-fallback-orders.md) (ACCEPTED): effective result as an immutable value (requested vs effective per field, `degraded` flag, reason) delivered at the frame boundary and readable as current state; apply never blocks; fallback orders for screen modes, display modes and filters fixed in the ADR (issue #859). Delivery mechanism: OQ-122. Original question: form of the effective-result report and the documented fallback orders for screen modes (REQ-DISPLAY-002) and filter chains (REQ-FILTER-002). | Phase 2 |
| OQ-119 | **RESOLVED** by [ADR-0008](decisions/0008-awt-desktop-implementer-of-platform-api.md) (issue #861): a separate `platform-awt` module implements `platform-api` on desktop with the JDK AWT only. The render side is OQ-123. Original question: which module implements `platform-api` on desktop (AWT, OpenGL): `render-awt` / `render-opengl`, or separate desktop platform modules? | Phase 1 |
| OQ-123 | **RESOLVED** by [ADR-0022](decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md): `render-awt` depends on `platform-awt`; the AWT surface is reached through the typed capability lookup (ADR-0020), its type lives in `platform-awt`. Original question: Which module provides the desktop render side with `platform-awt` (`render-awt`, `render-opengl`, a later LWJGL/GLFW backend), and how the AWT surface reaches it without crossing an `*-api` (an implementation-to-implementation dependency or another mechanism). Raised by issue #861. | Phase 1 |
| OQ-124 | **RESOLVED** by [ADR-0022](decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md): `int` with `0` meaning unknown or multiple, plus an "is known" check. Original question: How the neutral display-mode value types (ADR-0002) represent an unknown refresh rate or a multi or unknown bit depth (AWT reports `0` and `-1`). Raised by issue #861. | Phase 2 (before display contracts) |

## Game development and workflow

| ID | Question | Needed by |
|---|---|---|
| OQ-080 | **RESOLVED** by [ADR-0023](decisions/0023-game-repository-change-requests-assets-and-api-index-check.md): separate game repository on pinned Maven artifacts (sources and Javadoc JARs); snapshot only during development. Original question: Layout of the game repository and how it consumes the engine: separate repository, Maven dependency and version pinning (snapshot vs release), sources/Javadoc artifacts available to agents (REQ-DOC-004). | Phase 1 |
| OQ-081 | **RESOLVED** by [ADR-0023](decisions/0023-game-repository-change-requests-assets-and-api-index-check.md): GitHub issue with the engine change request template; owner triages into batch or ADR. Original question: Process for engine change requests raised by the game (who triages, how they become batches or ADRs). | Phase 3 |
| OQ-082 | **RESOLVED** by [ADR-0023](decisions/0023-game-repository-change-requests-assets-and-api-index-check.md): provenance metadata file per asset or folder; assets in the game repository; format and CI check: OQ-157. Original question: Asset production pipeline (including locally AI-generated sprites): formats, naming, provenance and licensing. | Phase 2 |
| OQ-083 | **RESOLVED** by [ADR-0023](decisions/0023-game-repository-change-requests-assets-and-api-index-check.md): hand-written index plus a CI check once the build skeleton exists. Original question: How the API index is kept in sync with the code: manual review, generation, or a CI check (REQ-DOC-002). | Phase 1 |

## Display and output filtering

Raised by issue #850 and [ADR-0002](decisions/0002-display-modes-resolution-and-output-filtering.md) (ACCEPTED on 2026-11-22). Acceptance resolved OQ-050, OQ-090 and OQ-092; the parts of OQ-090 and OQ-092 still open moved to OQ-120. OQ-096 is deferred, not resolved. Packet placeholders `OQ-NEW-1` to `OQ-NEW-7` of issue #850 map to OQ-090 to OQ-096, in order.

| ID | Question | Needed by |
|---|---|---|
| OQ-090 | **RESOLVED** by [ADR-0002](decisions/0002-display-modes-resolution-and-output-filtering.md): pure best-effort, the effective result is reported; no "required" flag in the first batch (owner decision 2A of issue #850). The form of the report and the fallback orders moved to OQ-120. Original question: request exactness. | Phase 2 (before display contracts) |
| OQ-091 | **RESOLVED** by [ADR-0006](decisions/0006-display-scaling-effective-result-and-fallback-orders.md) (ACCEPTED): `FIT` (default), `INTEGER` (opt-in, falls back to `FIT` when the surface is smaller than the logical frame), `STRETCH`; policy type in `render-api`, surface size from `platform-api` (issue #859). Original question: scaling policy set, its default, and whether it is owned by `platform-api` or `render-api`. | Phase 2 (before display contracts) |
| OQ-092 | **RESOLVED** by [ADR-0002](decisions/0002-display-modes-resolution-and-output-filtering.md): filtering applies from the logical frame to the output surface only, no per-layer filtering (owner decision 3A of issue #850). Chain fallback details moved to OQ-120. Original question: filter scope. | Phase 2 (before the filter feature) |
| OQ-093 | Source and license of the `HQ2X` and CRT algorithms (own implementation or third-party library; license and Android/DEX compatibility to be recorded, see OQ-006). | Phase 2 (before filter implementation) |
| OQ-094 | Shader portability (GLSL vs GLSL ES) and delivery of shader assets through `resources` (needed by CRT and custom filters); if confirmed, rendering backends follow the common resource convention (ADR-0005). | Phase 2 (before CRT) |
| OQ-095 | Persistence of user display settings: engine responsibility or game responsibility. | Phase 4 |
| OQ-096 | Multi-monitor and HiDPI. Owner decision 4A of issue #850 (ADR-0002, ACCEPTED): out of the first batch. Open until it is scheduled; the contracts must not preclude it. AWT applies HiDPI scaling by default on some systems, so `platform-awt` may report scaled pixel sizes (ADR-0008). | Phase 4 (before the display feature is extended) |

## Sprites, animations and sprite sheets

Raised by issue #852 and [ADR-0003](decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md) (ACCEPTED on 2026-11-22). Acceptance resolved OQ-051 (`graphics` depends on `render-api`) and OQ-054 (abstract commands). OQ-031 and OQ-032 are related to OQ-107 and OQ-101. All rows below are open. Packet placeholders `OQ-NEW-1` to `OQ-NEW-9` of issue #852 map to OQ-100 to OQ-108, in order; the numbers announced in the #852 plan comments are not valid (see [Unassigned identifiers](#unassigned-identifiers)).

| ID | Question | Needed by |
|---|---|---|
| OQ-100 | Backend-neutral image / pixel-buffer abstraction in `graphics` (no `java.awt` type in the API; Android/DEX compatibility of image decoding). Also: module placement of the platform-specific image decoders (no `java.awt` or `javax.imageio` on Android), and where modifiable pixel data lives relative to the immutable cached source (REQ-SPR-001, ADR-0003). | Phase 2 (sprite sheet batch) |
| OQ-101 | Image cache policy: scope, eviction, thread safety (related to OQ-032). The cache is owned by `graphics`, not by `resources`; its key form and release rule are fixed by ADR-0007 (OQ-118); thread safety follows OQ-121. | Phase 2 (sprite sheet batch) |
| OQ-102 | Color-transform capability model and CPU fallback. Owner choice (decision 1C of issue #852): backend-independent description, per-frame effects through `render-api` parameters, static transforms on the CPU with cache; the capability query and the fallback rules remain `TO BE DEFINED`. | Phase 2 (sprite batch) |
| OQ-103 | Template authoring convention: key colors vs mask layer. Owner choice (decision 2): a template palette mapping applied as a static transform, cached per (source image, mapping); the authoring convention is not fixed. | Phase 2 (sprite batch) |
| OQ-104 | Pivot semantics, transform composition order, sprite instance mutability. The rotation unit and direction are not decided here: they follow the engine-wide angle convention (OQ-011). | Phase 2 (sprite batch) |
| OQ-105 | Animation definition format and loader (own format vs reuse of an existing file format); frame timing in seconds. | Phase 2 (animated sprite batch) |
| OQ-106 | Sprite sheet id type (string vs typed id) and group lifecycle (who owns and closes sheets). The id form must be consistent with the key form of ADR-0007 (OQ-118). | Phase 2 (sprite sheet batch) |
| OQ-107 | Loading thread (synchronous vs asynchronous) and where GPU upload occurs (related to OQ-031). | Phase 2 (before platform tests) |
| OQ-108 | Render command contract in `render-api` for sprites. Owner choice (decision 3B): sprites emit commands, never draw directly; batching is a later optimization behind the command abstraction (related to OQ-054). | Phase 2 (before batch work) |

## Audio

Raised by issue #853 and [ADR-0004](decisions/0004-audio-contracts-short-and-long-sounds.md) (ACCEPTED on 2026-11-22). OQ-053 is touched, not resolved. All rows below are open. Packet placeholders `OQ-NEW-1` to `OQ-NEW-7` of issue #853 map to OQ-110 to OQ-116; OQ-117 and OQ-118 were added when the packet was applied, and OQ-118 is now tracked by [ADR-0005](decisions/0005-common-convention-for-modules-handling-resources.md). The #853 plan comment announced OQ-100 to OQ-108, ADR-0003 and ADR-0004 with other meanings; those numbers were reassigned before merge and the plan numbers are not valid.

| ID | Question | Needed by |
|---|---|---|
| OQ-110 | Short-sound cache policy: size, eviction, preload vs lazy. The key form and release rule are fixed by ADR-0007 (OQ-118); thread safety follows OQ-121. | Phase 2 (before short-sound implementation) |
| OQ-111 | Maximum simultaneous short voices and overflow behavior (drop, steal oldest, refuse). | Phase 2 (before short-sound implementation) |
| OQ-112 | Variant selection policy (pure random, no immediate repeat, weighted) and meaning of "composed" (simultaneous layers or offsets). | Phase 2 (before variant batch) |
| OQ-113 | Long-sound strategy: fully decoded preload vs streamed, and Android memory budget (related to OQ-033). | Phase 2 (before long-sound implementation) |
| OQ-114 | Playlist end behavior (stop vs loop), failing track (skip vs abort), and which thread triggers "next" (related to OQ-030; the callback must not mutate simulation state from the audio thread). | Phase 2 (before playlist batch) |
| OQ-115 | Supported formats and decoders per platform (REQ-AUDIO-002), decoder licenses and Android compatibility (OQ-006, `android-compatibility.md`); touches OQ-053. | Phase 2 (before first real backend) |
| OQ-116 | Android latency target and the threshold meant by "low latency". | Phase 2 (before Android backend) |
| OQ-117 | Audio features out of scope of issue #853, listed as candidates only: pause/resume, volume, looping, fades, spatial audio, mixer channels. | Phase 6 (first game), or earlier if one is requested |
| OQ-118 | **RESOLVED** by [ADR-0007](decisions/0007-resource-convention-keys-release-and-errors.md) (ACCEPTED): key = normalized logical path string (`/`, case-sensitive, no leading `/`, no `.` or `..`) for every origin; derived caches key on (resource key, module discriminator); release owned by the consuming module and idempotent; one unchecked exception type for missing or unreadable resources (issue #859). Thread safety moved to OQ-121. Original question: content of the common resource convention of ADR-0005 (key form, release rule, double-release behavior, thread safety). | Phase 2 (before the first consuming module: sprite sheet or short-sound batch) |

## Network and budgets

| ID | Question | Needed by |
|---|---|---|
| OQ-060 | Network protocol, synchronization, authority, prediction, rollback; actual need for replay/rollback determinism. | Phase 5 |
| OQ-070 | CPU/memory budget and target frame rates. | Phase 2 |
| OQ-071 | Supported desktop platforms and minimum versions. | Phase 1 |

## Unassigned identifiers

Numbers announced in issue plan comments with another meaning and never assigned in this register. They are reserved, never to be used, so that an old plan comment cannot be mistaken for a current question.

| ID | Question | Needed by |
|---|---|---|
| OQ-097 | UNASSIGNED, not a question. Announced in the #852 plan for the pixel-buffer abstraction, merged as OQ-100. | — |
| OQ-098 | UNASSIGNED, not a question. Announced in the #852 plan for the image cache policy, merged as OQ-101. | — |
| OQ-099 | UNASSIGNED, not a question. Announced in the #852 plan for the color-transform capability model, merged as OQ-102. | — |

## Tile maps (issue #865, ADR-0009)

| ID | Question | Needed by |
|---|---|---|
| OQ-125 | Tile collision model, static and dynamic: shapes, link with the physics contracts (OQ-043). Out of scope of ADR-0009 (decision 4A); the tile model only keeps a per-tile properties hook. Touches `REQ-MAP-002`. | Phase 3 (before the collision batch) |
| OQ-126 | Map file format and loader (decision 5A): the first batch uses an abstract loader contract tested with an in-memory test format; an own format or an existing one (e.g. Tiled TMX/JSON, license and Android/DEX review needed) stays open. | Before the loader batch |
| OQ-127 | Auto-tiling rule format and neighborhood (4 or 8 neighbors) (decision 2A). | Before the destroy batch |
| OQ-128 | Tileset reference form in a map file and tileset lifecycle: who owns and releases the sprite sheet; consistent with OQ-106 and the ADR-0007 keys. | Before the loader batch |
| OQ-129 | Chunking and render cache strategy beyond viewport culling, and cell storage layout for large maps (Android memory). | Phase 3 (before large-map tests) |

## Camera (issue #868, ADR-0010)

Raised by issue #868 and [ADR-0010](decisions/0010-camera-system-in-camera.md) (ACCEPTED on 2026-11-22). OQ-012 and OQ-040 are touched, not resolved. All rows below are open. The packet placeholders `OQ-NEW-1` (rendering consumer) and `OQ-NEW-2` (shake randomness) map to OQ-131 and OQ-130.

| ID | Question | Needed by |
|---|---|---|
| OQ-130 | Shake randomness detail (decision 5A, per-camera seeded noise): noise algorithm, default seed and reseeding, behavior when several shakes overlap. | Before the shake batch |
| OQ-131 | Rendering consumer contract of the view rectangle (decision 6A): how `render-api` and `tilemap` receive it and apply the world-to-screen transform; related to OQ-108 and ADR-0009 decision 1A. | Phase 3 (before the first camera consumer) |
| OQ-132 | Placement details and ECS adapter: entity ID type of the lookup contract, and the module hosting the adapter that implements it (decisions 1A, 2A); related to OQ-041 and OQ-042. | Before the follow batch |
| OQ-133 | Follow delay details (decision 4A): default time constant, behavior on target switch or teleport, snap threshold, sampling of the target in the update step; related to OQ-021. | Before the follow batch |
| OQ-134 | Shake units and picking: amplitude in meters or fraction of the view, frequency in Hz, decay curve, effect on screen-to-world picking. | Before the shake batch |
| OQ-135 | Zoom details: default range, animation curve and duration, anchor point other than the camera center (the default pivot is the center, REQ-CAM-002), meaning of zoom values. | Before the zoom batch |
| OQ-136 | Features out of scope of issue #868, listed as candidates only: multi-camera and split screen, bounds clamp, rotation, parallax, dead zone, follow offset. | When one is requested |

## ECS: dynamic objects, traits and standard traits (ADR-0011)

Raised by the owner packet "ECS dynamic objects, traits & standard trait package" and [ADR-0011](decisions/0011-ecs-dynamic-objects-traits-and-standard-traits.md) (ACCEPTED). Touched, not resolved: OQ-010, OQ-013, OQ-020, OQ-022, OQ-030, OQ-033, OQ-041, OQ-042, OQ-043, OQ-070, OQ-122, OQ-125, OQ-132. All rows below are open. The packet placeholder `OQ-NEW-1` (links to existing ECS identifiers) is answered by the inventory in the ADR context and by OQ-137; `OQ-NEW-2` (tile collision with a tile map, separate packet) is the existing OQ-125.

| ID | Question | Needed by |
|---|---|---|
| OQ-137 | Java representation of the object ID (packed `int` or `long`, bits for index and generation, maximum live objects, value type vs primitive); shared with the camera lookup contract (OQ-132). | Before the first ECS batch |
| OQ-138 | Deferred-change semantics: whether an ID returned by a deferred create is usable at once with the command buffer, behavior of operations on a not-yet-applied object, order of commands inside one flush, conflicting commands (add then remove), explicit flush outside the loop, and where the flush sits relative to the update step (OQ-020). | Before the first ECS batch |
| OQ-139 | Benchmark and budget: reference JVM and hardware, the Android reference device, how the 1,000 × 4 target (REQ-ECS-009) and allocation-free steady state are measured in CI without fragile wall-clock assertions, and the benchmark tool (a library needs a license and Android record, AGENTS.md); related to OQ-004, OQ-070. | Before the benchmark batch |
| OQ-140 | Trait storage layout and lifecycle: pooled instances in dense arrays vs primitive arrays per field for hot standard traits, reset on removal, growth policy and memory on Android (OQ-033). | Before the storage batch |
| OQ-141 | System scheduling: form of the ordering slot, dependencies between systems, enabling and disabling, how systems are driven by the `runtime` loop, fixed step vs variable delta (OQ-020), and exception policy during a step (OQ-023). | Before the systems batch |
| OQ-142 | Collision details: uniform-grid cell size (fixed or derived), shapes beyond axis-aligned boxes, detection only vs response, layer/mask semantics, trigger event delivery, fast-object tunneling, and where the physics contracts live (OQ-043); tile collision is OQ-125. | Before the collision batch |
| OQ-143 | Jump and ground details: how `grounded` is set (collision contact or game code), default coyote and buffer durations, variable jump height, and the sign of the jump impulse and gravity given the Y direction (OQ-010). | Before the jump batch |
| OQ-144 | Further standard traits (friction/drag, health, parent link, controller intent, render link, tag) and the numeric type and interpolation use of `Transformable` (OQ-013, OQ-022): which are standard and in which batch. | When one is requested |
| OQ-145 | Iteration-order guarantee: swap-removal reorders dense arrays; whether the order must be stable and reproducible for deterministic simulation or replay, and its consequence for any later network mode (OQ-060). | Before the query batch |
| OQ-146 | **RESOLVED** by [ADR-0021](decisions/0021-scene-world-viewport-and-object-model.md): `World` is the container type of the `ecs` module; no separate `world` module. Original question: Relation of `world` (scenes, objects, queries) to `ecs`: wrapper, shared contract or replacement, and whether classic objects and ECS objects share a contract (OQ-041, OQ-042, OQ-044). | Phase 3 |
