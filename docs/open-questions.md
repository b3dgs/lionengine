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
| OQ-011 | Angle conventions (radians/degrees, rotation direction). | Phase 0 |
| OQ-012 | Meters-to-pixels conversion: pixels per meter, camera, zoom, viewport, screen coordinate system. | Phase 2 |
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
| OQ-032 | Resource ownership, closing, caching and reloading policy. | Phase 1 |
| OQ-033 | Memory management policy (allocation in hot paths, release of resources). | Phase 1 |
| OQ-034 | Platform capability mechanism (interface, registry or configuration). | Phase 1 |

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
| OQ-050 | Are `platform-api` and `render-api` separate or merged? | Phase 1 |
| OQ-051 | Does `graphics` depend on `render-api`, or does it only describe image/sprite data? | Phase 2 |
| OQ-052 | Is `multiplayer` a base module or an optional extension? | Phase 5 |
| OQ-053 | Are audio backends split by library, codec or platform? Touched by issue #853 (see OQ-115), not resolved. | Phase 2 |
| OQ-054 | Rendering API style: abstract commands, drawing context, or specialized interface. | Phase 2 |
| OQ-055 | Which frontend is the first reference frontend? (A headless frontend for tests is already required by REQ-TEST-001.) | Phase 1 |

## Game development and workflow

| ID | Question | Needed by |
|---|---|---|
| OQ-080 | Layout of the game repository and how it consumes the engine: separate repository, Maven dependency and version pinning (snapshot vs release), sources/Javadoc artifacts available to agents (REQ-DOC-004). | Phase 1 |
| OQ-081 | Process for engine change requests raised by the game (who triages, how they become batches or ADRs). | Phase 3 |
| OQ-082 | Asset production pipeline (including locally AI-generated sprites): formats, naming, provenance and licensing. | Phase 2 |
| OQ-083 | How the API index is kept in sync with the code: manual review, generation, or a CI check (REQ-DOC-002). | Phase 1 |

## Display and output filtering

Raised by issue #850 and [ADR-0002](decisions/0002-display-modes-resolution-and-output-filtering.md) (PROPOSED). OQ-050 and OQ-051 are touched, not resolved: ADR-0002 assumes `platform-api` and `render-api` stay separate (owner decision 1B of issue #850) while the ADR is PROPOSED. Decisions 2A, 3A and 4A below are owner choices recorded in the ADR; the rows stay open until the ADR is `ACCEPTED`.

| ID | Question | Needed by |
|---|---|---|
| OQ-090 | Request exactness. Owner choice (decision 2A of issue #850): pure best-effort, the effective result is reported; no "required" flag in the first batch. Open until ADR-0002 is `ACCEPTED`; the form of the report (effective value and reason) remains `TO BE DEFINED`. | Before display contracts |
| OQ-091 | Scaling policy set (stretch, aspect-preserving, integer scale), its default, and whether it is owned by `platform-api` or `render-api`. | Before ADR-0002 is `ACCEPTED` |
| OQ-092 | Filter scope. Owner choice (decision 3A of issue #850): logical frame to output surface only, no per-layer filtering. Open until ADR-0002 is `ACCEPTED`; chain composition and fallback details remain `TO BE DEFINED`. | Before the filter feature |
| OQ-093 | Source and license of the `HQ2X` and CRT algorithms (own implementation or third-party library; license and Android/DEX compatibility to be recorded, see OQ-006). | Before filter implementation |
| OQ-094 | Shader portability (GLSL vs GLSL ES) and delivery of shader assets through `resources` (needed by CRT and custom filters). | Before CRT |
| OQ-095 | Persistence of user display settings: engine responsibility or game responsibility. | Roadmap (before Phase 4) |
| OQ-096 | Multi-monitor and HiDPI. Owner choice (decision 4A of issue #850): out of the first batch. Open until it is scheduled; the contracts must not preclude it. | Before the display feature is extended |

## Sprites, animations and sprite sheets

Raised by issue #852 and [ADR-0003](decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md) (PROPOSED). OQ-051 is touched, not resolved: the ADR assumes `graphics` emits `render-api` commands (owner decision 3B), which implies a `graphics` to `render-api` dependency, but the ADR is not accepted, so OQ-051 stays open. OQ-031 and OQ-032 are related to OQ-107 and OQ-101. All rows below are open.

| ID | Question | Needed by |
|---|---|---|
| OQ-100 | Backend-neutral image / pixel-buffer abstraction in `graphics` (no `java.awt` type in the API; Android/DEX compatibility of image decoding). | First implementation batch (sprite sheet) |
| OQ-101 | Image cache policy: key, scope, eviction, ownership relative to the `resources` lifecycle, thread safety (related to OQ-032). | First implementation batch (sprite sheet) |
| OQ-102 | Color-transform capability model and CPU fallback. Owner choice (decision 1C of issue #852): backend-independent description, per-frame effects through `render-api` parameters, static transforms on the CPU with cache; the capability query and the fallback rules remain `TO BE DEFINED`. Open until ADR-0003 is `ACCEPTED`. | Sprite batch |
| OQ-103 | Template authoring convention: key colors vs mask layer. Owner choice (decision 2): a template palette mapping applied as a static transform, cached per (source image, mapping); the authoring convention is not fixed. | Sprite batch |
| OQ-104 | Rotation unit (radians vs degrees), pivot semantics, transform composition order, sprite instance mutability. | Sprite batch |
| OQ-105 | Animation definition format and loader (own format vs reuse of an existing file format); frame timing in seconds. | Animated sprite batch |
| OQ-106 | Sprite sheet id type (string vs typed id) and group lifecycle (who owns and closes sheets). | Sprite sheet batch |
| OQ-107 | Loading thread (synchronous vs asynchronous) and where GPU upload occurs (related to OQ-031). | Before platform tests |
| OQ-108 | Render command contract in `render-api` for sprites. Owner choice (decision 3B): sprites emit commands, never draw directly; batching is a later optimization behind the command abstraction (related to OQ-054). | Before batch work |

## Audio

Raised by issue #853 and [ADR-0004](decisions/0004-audio-contracts-short-and-long-sounds.md) (PROPOSED). OQ-053 is touched, not resolved. All rows below are open.

| ID | Question | Needed by |
|---|---|---|
| OQ-110 | Short-sound cache policy: key, size, eviction, preload vs lazy, within the common resource convention (OQ-118). | Before short-sound implementation |
| OQ-111 | Maximum simultaneous short voices and overflow behavior (drop, steal oldest, refuse). | Before short-sound implementation |
| OQ-112 | Variant selection policy (pure random, no immediate repeat, weighted) and meaning of "composed" (simultaneous layers or offsets). | Before variant batch |
| OQ-113 | Long-sound strategy: fully decoded preload vs streamed, and Android memory budget (related to OQ-033). | Before long-sound implementation |
| OQ-114 | Playlist end behavior (stop vs loop), failing track (skip vs abort), and which thread triggers "next" (related to OQ-030; the callback must not mutate simulation state from the audio thread). | Before playlist batch |
| OQ-115 | Supported formats and decoders per platform (REQ-AUDIO-002), decoder licenses and Android compatibility (OQ-006, `android-compatibility.md`); touches OQ-053. | Before first real backend |
| OQ-116 | Android latency target and the threshold meant by "low latency". | Before Android backend |
| OQ-117 | Audio features out of scope of issue #853, listed as candidates only: pause/resume, volume, looping, fades, spatial audio, mixer channels. | Before any of these is requested |
| OQ-118 | Where the common resource convention (keys and release, owner statement of decision 3B) is documented: `resources` documentation, an ADR, or both. ADR-0004 proposes `contracts.md` (Resources section) plus ADR-0004; its content (key form, release rule) is not fixed. | Before the first consuming module implementation |

## Network and budgets

| ID | Question | Needed by |
|---|---|---|
| OQ-060 | Network protocol, synchronization, authority, prediction, rollback; actual need for replay/rollback determinism. | Phase 5 |
| OQ-070 | CPU/memory budget and target frame rates. | Phase 2 |
| OQ-071 | Supported desktop platforms and minimum versions. | Phase 1 |
