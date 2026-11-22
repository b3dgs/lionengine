# Open questions

**Status: PROPOSED — single registry of every `TO BE DEFINED` item.**

Rules:
- Do not guess the answer to an open question in code or documentation. Ask, or record the assumption you made and keep it reversible.
- When a question is answered, record the answer in an ADR (or a `REQ-*`), then mark the row `RESOLVED` with a link. Never delete a row; identifiers are stable.
- "Needed by" is the roadmap phase before which the question must be settled.

## Platform and build

| ID | Question | Needed by |
|---|---|---|
| OQ-001 | Minimum Android API level and targeted Android versions. | Phase 1 |
| OQ-002 | How is DEX produced from a **Maven** build? Android Gradle Plugin is Gradle-based: options include a Maven-driven D8/R8 invocation or a separate Gradle module consuming the Maven artifacts. Also: D8/R8 versions and *core library desugaring* policy. | Phase 0 |
| OQ-003 | Physical layout: single or multiple Maven modules, group/artifact identifiers, base package name, versioning of artifacts, build/test commands. *Baseline observed (not a decision): `pom.xml` is an aggregator (`packaging pom`), group `com.b3dgs.lionengine`, artifact `lionengine`, version `11.0.0-SNAPSHOT`, parent `com.b3dgs:b3dgs-parent`, base package `com.b3dgs.lionengine`; no modules yet.* | Phase 0 |
| OQ-004 | Test framework and configuration (JUnit 5 is a candidate), coverage tooling. | Phase 0 |
| OQ-005 | Tool enforcing module dependency rules automatically (REQ-CORE-005). *Baseline observed: the parent POM already configures `radial-encapsulation-maven-plugin` with `maxViolations` 0; evaluate whether it covers REQ-CORE-005.* | Phase 0 |
| OQ-006 | Project license, and policy for third-party libraries and codecs. *Baseline observed: the repository `LICENSE` is GPL-3.0 (confirm it stays the project license); the third-party policy remains to be defined.* | Phase 0 |
| OQ-007 | API compatibility and versioning policy (pre-1.0 stability, deprecation delay). | Phase 1 |
| OQ-008 | Continuous-integration platform, including the Android/DEX job required by ADR-0001. | Phase 0 |

## Coordinates and units

| ID | Question | Needed by |
|---|---|---|
| OQ-010 | Origin of the coordinate system and positive direction of the Y axis. | Phase 0 |
| OQ-011 | Angle conventions (radians/degrees, rotation direction). Also governs sprite rotation (ADR-0003, OQ-104). | Phase 0 |
| OQ-012 | Meters-to-pixels conversion: pixels per meter, camera, zoom, viewport, screen coordinate system. Sprites are placed in pixels of the logical frame (ADR-0003, ADR-0002); this question owns the conversion from meters. | Phase 2 |
| OQ-013 | Numeric type and precision for world quantities (`float` vs `double`), including on Android. | Phase 0 |

## Game loop

| ID | Question | Needed by |
|---|---|---|
| OQ-020 | Simulation step duration, monotonic clock, catch-up strategy and maximum tolerated lag. | Phase 0 |
| OQ-021 | Exact mathematical definition of the extrapolation factor, its bounds, and the behavior under extreme lag (factor greater than 1). | Phase 0 |
| OQ-022 | Interpolation: whether it is offered, how it differs from extrapolation, and the limits of prediction. | Phase 2 |
| OQ-023 | Pause, slow motion, exceptions during a step, and shutdown policy. | Phase 1 |

## Runtime

| ID | Question | Needed by |
|---|---|---|
| OQ-030 | Owning thread of rendering and runtime; overall concurrency model and thread rules. | Phase 1 |
| OQ-031 | Asynchronous loading policy. | Phase 2 |
| OQ-032 | Resource ownership, closing, caching and reloading policy. Cache key and release convention of the consuming modules: OQ-118 (ADR-0005). | Phase 1 |
| OQ-033 | Memory management policy (allocation in hot paths, release of resources). | Phase 1 |
| OQ-034 | Platform capability mechanism (interface, registry or configuration). | Phase 1 |
| OQ-121 | Thread-safety rule shared by the caches of the modules handling resources (ADR-0005, ADR-0007): which threads may load, read and release. Split from OQ-118 by issue #859. Asynchronous loading stays OQ-031; reloading stays OQ-032. | Phase 2 (before the first consuming module) |
| OQ-122 | `core` event contract: how engine events (such as the effective result of a display or filter change, ADR-0006) are delivered, on which thread, and in which order relative to the update step. Raised by issue #859. | Phase 1 |

## World and objects

| ID | Question | Needed by |
|---|---|---|
| OQ-040 | Definition of a scene, a world and a viewport. | Phase 3 |
| OQ-041 | Relationship between simple object management and ECS. The project goal describes an ECS-based engine, whereas `docs/vision.md` makes ECS optional (REQ-OBJ-002 is `Could`). Decide whether ECS is the core model or an optional module. | Phase 0 |
| OQ-042 | Are traits attached to classic objects, ECS entities, or both through a common contract? | Phase 3 |
| OQ-043 | Where do the "physics contracts" required by `traits` live (collision shapes, bodies)? No module owns them today. | Phase 3 |
| OQ-044 | Is `world` independent of `tilemap`? | Phase 3 |

## Modules and APIs

| ID | Question | Needed by |
|---|---|---|
| OQ-050 | **RESOLVED** by [ADR-0002](decisions/0002-display-modes-resolution-and-output-filtering.md): `platform-api` and `render-api` are separate modules (owner decision 1B of issue #850). Original question: are `platform-api` and `render-api` separate or merged? Desktop implementer of `platform-api`: OQ-119. | Phase 1 |
| OQ-051 | **RESOLVED** by [ADR-0003](decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md): `graphics` depends on `render-api`; sprites emit `render-api` commands (owner decision 3B of issue #852). Original question: does `graphics` depend on `render-api`, or does it only describe image/sprite data? | Phase 2 |
| OQ-052 | Is `multiplayer` a base module or an optional extension? | Phase 5 |
| OQ-053 | Are audio backends split by library, codec or platform? Touched by issue #853 (see OQ-115), not resolved. | Phase 2 |
| OQ-054 | **RESOLVED** by [ADR-0003](decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md): abstract commands (owner decision 3B of issue #852); the command contract itself is OQ-108. Original question: rendering API style: abstract commands, drawing context, or specialized interface. | Phase 2 |
| OQ-055 | Which frontend is the first reference frontend? (A headless frontend for tests is already required by REQ-TEST-001.) | Phase 1 |
| OQ-120 | **RESOLVED** by [ADR-0006](decisions/0006-display-scaling-effective-result-and-fallback-orders.md) (PROPOSED): effective result as an immutable value (requested vs effective per field, `degraded` flag, reason) delivered at the frame boundary and readable as current state; apply never blocks; fallback orders for screen modes, display modes and filters fixed in the ADR (issue #859). Delivery mechanism: OQ-122. Original question: form of the effective-result report and the documented fallback orders for screen modes (REQ-DISPLAY-002) and filter chains (REQ-FILTER-002). | Phase 2 |
| OQ-119 | Which module implements `platform-api` on desktop (AWT, OpenGL): `render-awt` / `render-opengl`, or separate desktop platform modules? Needed by the screen and display modes of ADR-0002. Raised by the joint review of packets #850, #852 and #853. | Phase 1 |

## Game development and workflow

| ID | Question | Needed by |
|---|---|---|
| OQ-080 | Layout of the game repository and how it consumes the engine: separate repository, Maven dependency and version pinning (snapshot vs release), sources/Javadoc artifacts available to agents (REQ-DOC-004). | Phase 1 |
| OQ-081 | Process for engine change requests raised by the game (who triages, how they become batches or ADRs). | Phase 3 |
| OQ-082 | Asset production pipeline (including locally AI-generated sprites): formats, naming, provenance and licensing. | Phase 2 |
| OQ-083 | How the API index is kept in sync with the code: manual review, generation, or a CI check (REQ-DOC-002). | Phase 1 |

## Display and output filtering

Raised by issue #850 and [ADR-0002](decisions/0002-display-modes-resolution-and-output-filtering.md) (ACCEPTED on 2026-11-22). Acceptance resolved OQ-050, OQ-090 and OQ-092; the parts of OQ-090 and OQ-092 still open moved to OQ-120. OQ-096 is deferred, not resolved. Packet placeholders `OQ-NEW-1` to `OQ-NEW-7` of issue #850 map to OQ-090 to OQ-096, in order.

| ID | Question | Needed by |
|---|---|---|
| OQ-090 | **RESOLVED** by [ADR-0002](decisions/0002-display-modes-resolution-and-output-filtering.md): pure best-effort, the effective result is reported; no "required" flag in the first batch (owner decision 2A of issue #850). The form of the report and the fallback orders moved to OQ-120. Original question: request exactness. | Phase 2 (before display contracts) |
| OQ-091 | **RESOLVED** by [ADR-0006](decisions/0006-display-scaling-effective-result-and-fallback-orders.md) (PROPOSED): `FIT` (default), `INTEGER` (opt-in, falls back to `FIT` when the surface is smaller than the logical frame), `STRETCH`; policy type in `render-api`, surface size from `platform-api` (issue #859). Original question: scaling policy set, its default, and whether it is owned by `platform-api` or `render-api`. | Phase 2 (before display contracts) |
| OQ-092 | **RESOLVED** by [ADR-0002](decisions/0002-display-modes-resolution-and-output-filtering.md): filtering applies from the logical frame to the output surface only, no per-layer filtering (owner decision 3A of issue #850). Chain fallback details moved to OQ-120. Original question: filter scope. | Phase 2 (before the filter feature) |
| OQ-093 | Source and license of the `HQ2X` and CRT algorithms (own implementation or third-party library; license and Android/DEX compatibility to be recorded, see OQ-006). | Phase 2 (before filter implementation) |
| OQ-094 | Shader portability (GLSL vs GLSL ES) and delivery of shader assets through `resources` (needed by CRT and custom filters); if confirmed, rendering backends follow the common resource convention (ADR-0005). | Phase 2 (before CRT) |
| OQ-095 | Persistence of user display settings: engine responsibility or game responsibility. | Phase 4 |
| OQ-096 | Multi-monitor and HiDPI. Owner decision 4A of issue #850 (ADR-0002, ACCEPTED): out of the first batch. Open until it is scheduled; the contracts must not preclude it. | Phase 4 (before the display feature is extended) |

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
| OQ-118 | **RESOLVED** by [ADR-0007](decisions/0007-resource-convention-keys-release-and-errors.md) (PROPOSED): key = normalized logical path string (`/`, case-sensitive, no leading `/`, no `.` or `..`) for every origin; derived caches key on (resource key, module discriminator); release owned by the consuming module and idempotent; one unchecked exception type for missing or unreadable resources (issue #859). Thread safety moved to OQ-121. Original question: content of the common resource convention of ADR-0005 (key form, release rule, double-release behavior, thread safety). | Phase 2 (before the first consuming module: sprite sheet or short-sound batch) |

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
