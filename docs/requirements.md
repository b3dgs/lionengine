# Requirements

**Status: PROPOSED — requirements to be refined and prioritized, except rows explicitly marked VALIDATED.**

Each requirement has a stable identifier. Agents must reference these identifiers in their plans, commits and reports. Identifiers are never reused: removed requirements are listed under [Retired identifiers](#retired-identifiers).

Priorities follow MoSCoW: **Must**, **Should**, **Could**. "Won't (for now)" items belong in `docs/vision.md` as non-goals.

The traceability matrix (requirement → decision → test) is in [`traceability.md`](traceability.md). Unresolved points are in [`open-questions.md`](open-questions.md).

## Technical baseline

Source: [ADR-0001](decisions/0001-technical-baseline-and-conventions.md).

| ID | Requirement | Priority | Status | Expected verification |
|---|---|---|---|---|
| REQ-TECH-001 | The Java target is Java 25 or higher. | Must | VALIDATED | Compilation and tests on Java 25+. |
| REQ-TECH-002 | The build system is Maven 3 or higher. | Must | VALIDATED | Reproducible build with Maven 3+. |
| REQ-TECH-003 | World coordinates are two-dimensional, X and Y, expressed in meters. | Must | VALIDATED | Unit tests and coordinate-transformation tests. |
| REQ-TECH-004 | Engine time and durations are expressed in seconds. | Must | VALIDATED | Tests of time conversions and calculations. |
| REQ-TECH-005 | Speeds are expressed in meters per second (m/s) and accelerations in m/s². | Must | VALIDATED | Dimensional and movement tests. |
| REQ-TECH-006 | The code must remain compatible with the Android/DEX toolchain, despite a Java 25+ development baseline. | Must | VALIDATED | Android modules compiled to DEX; tests on the selected Android targets. |
| REQ-TECH-007 | No Java API, language feature or dependency may be considered Android-compatible without explicit verification. | Must | VALIDATED | Documented compatibility check ([`android-compatibility.md`](android-compatibility.md)) and automated Android build. |
| REQ-TECH-008 | Public APIs document their units and avoid implicit unit conversions. | Must | VALIDATED | Javadoc review; unit-conversion tests. |

## Core

| ID | Requirement | Priority | Status | Expected verification |
|---|---|---|---|---|
| REQ-CORE-002 | The core does not depend on a concrete frontend. | Must | PROPOSED | Dependency check + headless tests. |
| REQ-CORE-003 | Public APIs are documented in Javadoc, tags included. | Must | PROPOSED | Javadoc generation with doclint enabled, without warnings on public APIs (configuration `TO BE DEFINED`). |
| REQ-CORE-004 | Modules expose explicit, testable contracts. | Must | PROPOSED | Contract tests. |
| REQ-CORE-005 | Module dependency rules (no cycles, core independent of frontends) are verified automatically by the build. | Must | PROPOSED | Failing build on a forbidden dependency (tool: OQ-005). |
| REQ-TEST-001 | A controllable clock and a headless frontend are provided for tests. | Must | PROPOSED | Used by the contract and integration tests of REQ-LOOP-* and REQ-FE-001. |

## Documentation and usability

Goal: a developer or an AI agent that has never seen the engine can write a game from the repository alone (see [`game-development-guide.md`](game-development-guide.md)).

| ID | Requirement | Priority | Status | Expected verification |
|---|---|---|---|---|
| REQ-DOC-001 | Every public feature ships with a minimal runnable example in the `examples` module; examples are compiled and executed (headless where possible) by the build, so they cannot drift from the API. | Must | PROPOSED | CI job that compiles and runs all examples; failing example fails the build. |
| REQ-DOC-002 | The API index lists, by task, the public entry-point types and the example to copy, and stays consistent with the code. | Should | PROPOSED | Consistency check between [`api-index.md`](api-index.md) and the public API (method: OQ-083). |
| REQ-DOC-003 | Code snippets in the game development guide are included from compiled examples, never written by hand. | Should | PROPOSED | Check that the guide contains no snippet absent from `examples`. |
| REQ-DOC-004 | Engine sources and Javadoc are published as artifacts so that a game repository (and its agents) can read them at the pinned engine version. | Should | PROPOSED | Build produces sources and Javadoc artifacts; game repository can resolve them (OQ-080). |

## Frontends and rendering

| ID | Requirement | Priority | Status | Expected verification |
|---|---|---|---|---|
| REQ-FE-001 | The engine can be used with several frontends. | Must | PROPOSED | The same logical scenario runs with two test adapters. |
| REQ-FE-002 | AWT, OpenGL and Android are envisaged targets. | Should | PROPOSED | Capability matrix per frontend. |
| REQ-FE-003 | The frontend provides the concrete display and presentation capabilities. | Must | PROPOSED | No native type in the core API. |
| REQ-PLATFORM-001 | A desktop module (`platform-awt`) implements `platform-api` with the JDK AWT only (no third-party library); it is excluded from every Android build, and no AWT type leaves the module (ADR-0008). | Must | PROPOSED | The `platform-api` contract suites of `test-support` pass on it (platform tests, real display); dependency check (REQ-CORE-005); Android build contains no `java.desktop` dependency. |

## Display and output filtering

Source: [ADR-0002](decisions/0002-display-modes-resolution-and-output-filtering.md) (ACCEPTED). Features: [`features/display-modes.md`](features/display-modes.md), [`features/output-filtering.md`](features/output-filtering.md). Placement: screen and display modes in `platform-api`, output filtering in `render-api`. Requests are best-effort and the effective result is reported (owner decisions 1B, 2A, 3A, 4A of issue #850; ADR accepted on 2026-11-22; the requirements below stay PROPOSED until validated). Multi-monitor and HiDPI are outside the first batch (OQ-096).

| ID | Requirement | Priority | Status | Expected verification |
|---|---|---|---|---|
| REQ-DISPLAY-001 | The platform API offers the screen modes `WINDOWED`, `FULL_WINDOW` and `FULL_SCREEN`, with a query telling which ones the platform supports. | Must | PROPOSED | Contract tests on each platform and on a fake-capability platform. |
| REQ-DISPLAY-002 | A request for an unsupported screen mode falls back following a documented order (ADR-0006) and reports the effective mode, without failing. | Must | PROPOSED | Contract tests: effective result and reason reported, no exception on a missing capability. |
| REQ-DISPLAY-003 | Display modes (width and height in pixels, refresh rate in Hz, color depth in bits) can be enumerated and selected through engine-defined types. | Must | PROPOSED | Unit tests of the value types; contract tests of enumeration and selection. |
| REQ-DISPLAY-004 | The logical resolution is independent of the output display mode; a scaling policy governs presentation and never changes world units (m, m/s, m/s²). | Must | PROPOSED | Unit tests of scaling (`FIT`, `INTEGER`, `STRETCH`); policy set and owner: REQ-DISPLAY-007 (ADR-0006). |
| REQ-DISPLAY-005 | Screen mode, display mode, resolution and filter changes are applied at a frame boundary (between two presentations, outside any simulation update; see the glossary) and signaled by an event; they never alter simulation state. | Must | PROPOSED | Contract tests of frame-boundary application and event; simulation-state invariance test (REQ-LOOP-006). |
| REQ-DISPLAY-006 | No AWT, OpenGL or Android type appears in `core`, `platform-api` or `render-api` display and filter contracts. | Must | PROPOSED | Dependency check (REQ-CORE-005) and API review. |
| REQ-DISPLAY-007 | The scaling policies `FIT` (default), `INTEGER` (opt-in; falls back to `FIT` when the surface is smaller than the logical frame) and `STRETCH` are offered; the policy type is in `render-api`, the surface size comes from `platform-api`, and the effective policy is reported (ADR-0006). | Must | PROPOSED | Unit tests of the scaling math for each policy, including the `INTEGER` fallback; contract test of the reported policy. |
| REQ-DISPLAY-008 | Applying a display or filter request returns the accepted request without blocking the update step; the effective result (requested vs effective per field, `degraded` flag, reason) is an immutable value delivered at the frame boundary where the change is applied and readable as the current state (ADR-0006). | Must | PROPOSED | Contract tests in `test-support`: no blocking, delivery at the frame boundary, requested vs effective values, `degraded` flag; delivery mechanism: OQ-122. |
| REQ-FILTER-001 | Output filtering offers at least `NONE`, bilinear, `HQ2X` and CRT as standard identifiers, with a capability query. Filtering applies to the logical frame presented on the output surface only. | Must | PROPOSED | Unit tests of identifiers and capability query; platform golden-image tests (none, bilinear, `HQ2X` on AWT and OpenGL; CRT on OpenGL). |
| REQ-FILTER-002 | Filters are composable as an ordered chain (scaling filters, then post filters); an unsupported filter is skipped or replaced following the documented fallback of ADR-0006, and this is reported. | Should | PROPOSED | Unit tests of the chain; contract tests of fallback and report. |
| REQ-FILTER-003 | An extension point allows custom filters or shaders; its form is not fixed before validation. | Could | PROPOSED | To be defined with the extension design (OQ-094, OQ-120). |

Note: the packet placeholder `REQ-RENDER-FILTER-NNN` is numbered `REQ-FILTER-NNN` because `scripts/check-docs.py` recognizes identifiers made of one upper-case word and a number.

## Game loop

| ID | Requirement | Priority | Status | Expected verification |
|---|---|---|---|---|
| REQ-LOOP-001 | The game loop has a contract independent of its implementation. | Must | PROPOSED | Contract tests. |
| REQ-LOOP-002 | Several loop implementations can be provided. | Must | PROPOSED | Tests with a controllable clock and scheduler. |
| REQ-LOOP-003 | Update and render are distinct concepts. | Should | PROPOSED | Execution order verified by test. |
| REQ-LOOP-004 | The default implementation is independent of machine power and frequency. | Must | VALIDATED (principle) | Same simulation and same final state for an identical simulated time. |
| REQ-LOOP-005 | The default loop combines frameskipping and extrapolation to adapt presentation to the available time. Frameskipping may drop a presentation, never arbitrary simulation computations. | Must | VALIDATED (principle) | Tests of cadence, render skipping and extrapolation. |
| REQ-LOOP-006 | An extrapolation greater than 1 must not reduce simulation accuracy; an extrapolated state is never reused as input of the next simulation step. | Must | VALIDATED (principle) | Tests showing the simulated state is always computed from the reference simulation time/scheme, without integrating the extrapolated state. |

"VALIDATED (principle)": the principle is binding (ADR-0001); the formula, step duration and bounds remain `TO BE DEFINED` (OQ-020 to OQ-023).

## Resources and media

| ID | Requirement | Priority | Status | Expected verification |
|---|---|---|---|---|
| REQ-RES-001 | Resource access is independent of the resource origin. | Must | PROPOSED | Same contract tested on several providers. |
| REQ-RES-002 | Relative, Android, temporary, archive and stream origins are envisaged. | Should | PROPOSED | Tests per provider and error cases. |
| REQ-RES-003 | Resource ownership and closing rules are explicit; a resource is never assumed to be a `File`. | Must | PROPOSED | Tests of closing, double closing and non-filesystem providers. |
| REQ-RES-004 | Every module that loads or decodes resource content (audio, graphics, ...) follows one common convention for resource keys and release; `resources` stays media-agnostic and decoded caches live inside the consuming modules (owner rule of decision 3B, issue #853; ADR-0005; content: ADR-0007). | Must | PROPOSED | Contract tests reused by each consuming module: key equality, release, double release. Convention content: ADR-0007, REQ-RES-005. |
| REQ-RES-005 | Resource keys are normalized logical path strings (`/` separator, case-sensitive, no leading `/`, no `.` or `..`) for every origin; derived caches key on (resource key, module discriminator); release is owned by the consuming module and idempotent; a missing or unreadable resource raises one unchecked engine exception type (ADR-0007). | Must | PROPOSED | Unit tests of key normalization and rejection; contract suite in `test-support` run by each consuming module: key equality, idempotent release, missing-resource exception. |
| REQ-AUDIO-001 | The engine exposes a backend-independent audio API. Refined, not duplicated, by REQ-AUDIO-003 to REQ-AUDIO-009 (short and long sounds, ADR-0004). Its Should priority applies to the audio feature as a whole (roadmap Phase 2, "if prioritized"); once the feature is delivered, REQ-AUDIO-003 to REQ-AUDIO-009 are Must. | Should | PROPOSED | Testable fake backend. |
| REQ-AUDIO-002 | WAV, MP3 and OGG are target formats (formats and decoders per platform: OQ-115). | Should | PROPOSED | Codec/backend/platform matrix. |
| REQ-AUDIO-003 | Short sounds provide low-latency playback with a cache allowing heavy reuse without repeated loading or decoding. | Must | PROPOSED | Unit tests of cache hit and miss with a fake backend; latency threshold: OQ-116 (platform test later). |
| REQ-AUDIO-004 | A short sound can be played individually and stopped. | Must | PROPOSED | Contract suite in `test-support` run on every backend, with defined behavior on stop of an already finished sound. |
| REQ-AUDIO-005 | A variant set groups several short-sound files; playing it selects one variant randomly, or plays several simultaneously (composed sound, e.g. an explosion). Exact policies are `TO BE DEFINED` (OQ-112). | Must | PROPOSED | Unit tests with a seeded random source; selection and composition policies: OQ-112. |
| REQ-AUDIO-006 | Long sounds (music, ambient) can be preloaded for direct use and released when no longer needed. | Must | PROPOSED | Unit and contract tests of preload, use after preload, release and double release; strategy: OQ-113. |
| REQ-AUDIO-007 | A long sound can be played individually and stopped. | Must | PROPOSED | Contract suite in `test-support` run on every backend. |
| REQ-AUDIO-008 | A predefined playlist of long sounds starts the next track when the current one terminates, and can be stopped. The advance logic is backend-independent (ADR-0004, decision 2A); backends only expose play, stop and end-of-track. | Must | PROPOSED | Unit tests of playlist advance with a fake backend and a controllable clock; end behavior, failing track and callback thread: OQ-114. |
| REQ-AUDIO-009 | Audio contracts are testable headless with a fake backend and expose no platform type or concrete audio library type. | Must | PROPOSED | A fake backend in `test-support` passes the contract suites; dependency-rule check (REQ-CORE-005). |
| REQ-SPR-001 | The engine handles modifiable images/sprites. | Must | PROPOSED | Mutation and lifecycle tests. |
| REQ-SPR-002 | The engine handles animations and spritesheets. | Must | PROPOSED | Deterministic animation-progress tests. |

## Sprites, animations and sprite sheets

Source: [ADR-0003](decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md) (ACCEPTED), issue #852. Features: [`features/sprite-sheet.md`](features/sprite-sheet.md), [`features/sprite.md`](features/sprite.md), [`features/animated-sprite.md`](features/animated-sprite.md). Owner decisions given on issue #852: 1C (hybrid color transform), 2 (template palette mapping), 3B (render commands), 4 (shared immutable source plus lightweight instance); ADR accepted on 2026-11-22; the requirements below stay PROPOSED until validated. These requirements refine the broader REQ-SPR-001 and REQ-SPR-002, which are kept unchanged. For REQ-SPR-001, modifiable pixel data is a separately owned buffer: the cached shared source is never mutated, and a modified buffer becomes a new source (ADR-0003, OQ-100). Sprite coordinates are in pixels of the logical frame (ADR-0002); the conversion from world meters belongs to the camera/viewport (OQ-012).

Note: the packet placeholders `REQ-GFX-SPR-NNN`, `REQ-GFX-ANI-NNN` and `REQ-GFX-SHT-NNN` are numbered `REQ-SPRITE-NNN`, `REQ-ANIM-NNN` and `REQ-SHEET-NNN` because `scripts/check-docs.py` recognizes identifiers made of one upper-case word and a number.

| ID | Requirement | Priority | Status | Expected verification |
|---|---|---|---|---|
| REQ-SPRITE-001 | A sprite can be loaded from a resource identifier through `resources`, with defined errors for missing or invalid data. | Must | PROPOSED | Unit and integration tests: load, missing resource, invalid data (headless). |
| REQ-SPRITE-002 | Loaded images are cached by `graphics` with a key and release rule following the common resource convention (REQ-RES-004, ADR-0005); a cache hit shares the immutable source. | Must | PROPOSED | Unit tests of key, hit/miss, release and double release; contract tests of the cache. |
| REQ-SPRITE-003 | A sprite supports resize (scale), translation in pixels of the logical frame, and rotation following the engine angle convention (OQ-011), with documented order and pivot. | Must | PROPOSED | Unit tests of transforms and composition order (angle convention: OQ-011; order and pivot: OQ-104). |
| REQ-SPRITE-004 | A sprite supports color transformation: fade (alpha), mask, grading. Per ADR-0003 (decision 1C), the transform is a backend-independent description; per-frame effects (fade, tint, alpha) go through `render-api` draw parameters, static transforms (palette swap, grading, mask bake) are applied on the CPU and cached, and a backend can always fall back to the CPU path. | Must | PROPOSED | Unit tests of the description and of CPU transforms; contract tests of capability query and CPU fallback; platform parity tests (AWT, OpenGL, Android). |
| REQ-SPRITE-005 | A template-based color mapping (source color to target color, or indexed channel to target) yields multiple color variants of one sprite (e.g. team colors), sharing the source and caching variants keyed by (source image, mapping). | Should | PROPOSED | Unit tests of the palette mapping and of variant caching; authoring convention: OQ-103. |
| REQ-SPRITE-006 | A sprite has an internal location (origin/pivot), in pixels of the sprite image, used for placement, rotation and scaling; placement is in pixels of the logical frame (conversion from meters: OQ-012). | Must | PROPOSED | Unit tests of placement, rotation and scaling around the pivot (semantics: OQ-104). |
| REQ-SPRITE-007 | A sprite renders by emitting `render-api` commands, never by drawing directly, and no frontend or platform type appears in its public API. | Must | PROPOSED | Contract tests of command emission on a fake backend; dependency check (REQ-CORE-005) and API review. |
| REQ-SPRITE-008 | Sprite rendering does not prevent later batching of draw commands (batching itself is a later optimization behind the command abstraction). | Could | PROPOSED | To be defined with the render command contract (OQ-108). |
| REQ-ANIM-001 | Animations (frames, durations in seconds, loop mode) can be defined in code and loaded from a resource. | Must | PROPOSED | Unit tests of definition and loading, including invalid data (format: OQ-105). |
| REQ-ANIM-002 | An animated sprite supports play, pause, resume and stop with specified state transitions. | Must | PROPOSED | Unit tests of the animation state machine, including invalid transitions. |
| REQ-ANIM-003 | Animation time advances only from the simulation update step; render-time extrapolation or interpolation is never fed back. | Must | PROPOSED | Deterministic tests with a controllable clock; test that rendering does not alter animation state (REQ-LOOP-006). |
| REQ-SHEET-001 | A sprite sheet represents a tileset as a grid (cell size, columns, rows, optional margin/spacing) and gives access to cells by index. | Must | PROPOSED | Unit tests of grid index math, margins, spacing and out-of-range indexes. |
| REQ-SHEET-002 | Sprite sheets are grouped and retrieved by a unique id, with defined behavior for duplicate or unknown ids. | Should | PROPOSED | Unit tests of duplicate and unknown ids; id type and group lifecycle: OQ-106. |

## World and objects

| ID | Requirement | Priority | Status | Expected verification |
|---|---|---|---|---|
| REQ-MAP-001 | A tile map is a single-layer grid of cells (ADR-0009, decision 6A), each empty or referring to a tile of a tileset, i.e. a sprite sheet cell index (`REQ-SHEET-001`); its size is in tiles and its tile size in pixels of the logical frame. | Should | PROPOSED | Coordinate and cell-access tests. |
| REQ-MAP-002 | Maps can distinguish static and dynamic content. | Should | PROPOSED | Modification and rendering tests. |
| REQ-MAP-003 | Tiles can be created and set manually: `set`, `get` and `clear` by tile coordinates (`int` tile indices), with bounds checking and defined errors. | Must | PROPOSED | Unit tests of set/get/clear, out-of-bounds coordinates and empty cells. |
| REQ-MAP-004 | A tile map can be loaded from a resource through `resources` via an abstract loader contract, with defined errors for missing or invalid data; keys and release follow `REQ-RES-004` and `REQ-RES-005` (ADR-0007). The file format is `TO BE DEFINED` (OQ-126). | Must | PROPOSED | Loader contract tests in `test-support` with an in-memory test format; tests of missing and invalid data. |
| REQ-MAP-005 | Map rendering draws only the tiles that intersect a viewport rectangle (pixels of the logical frame, passed by the caller as a plain value), plus a margin, by emitting `render-api` commands and never drawing directly (`REQ-SPRITE-007`); the cost does not depend on the total map size. Rendering reads state and never mutates it (`REQ-LOOP-006`). | Must | PROPOSED | Unit tests of viewport-to-tile-range math; headless integration test checking that commands are emitted only for visible tiles. |
| REQ-MAP-006 | Destroying a tile updates its neighbors according to rule-based auto-tiling (a rule set maps a cell neighborhood to a tileset index; ADR-0009, decision 2A) so that rendering stays consistent. Destruction and neighbor updates run in the update phase only (`REQ-LOOP-003`). Rule format and neighborhood: OQ-127. | Must | PROPOSED | Unit tests of neighbor rules; headless integration test: destroy a tile, then render. |
| REQ-MAP-007 | A synchronous tile-changed notification, called from the update phase, reports each change with the cell and the old and new tile (ADR-0009, decision 3A); the contract is aligned later with the `core` event contract (OQ-122). | Should | PROPOSED | Unit tests of notification on set, clear and destroy, including neighbor updates. |
| REQ-OBJ-001 | The engine allows managing game objects. | Must | PROPOSED | Creation, update and deletion tests. |
| REQ-OBJ-002 | Several management strategies are possible, including simple and ECS. | Could | PROPOSED | Common contract or justification for distinct APIs (OQ-041). |
| REQ-TRAIT-001 | Objects can receive composable behaviors. | Should | PROPOSED | Attach, detach and interaction tests. |
| REQ-TRAIT-002 | Collision, gravity and mobility are envisaged traits/capabilities. | Should | PROPOSED | Unit tests of the behaviors. |

## Camera

Source: [ADR-0010](decisions/0010-camera-system.md) (PROPOSED), decision packet "Camera system". Feature: [`features/camera.md`](features/camera.md). The packet placeholders `REQ-CAM-NNN` are numbered `REQ-CAM-001` to `REQ-CAM-008`. All requirements stay PROPOSED: the packet states "Decision taken: pending". The packet expressed the world in pixels; ADR-0001 (meters) prevails, so the view is in world units and the conversion factor is `OQ-012`.

| ID | Requirement | Priority | Status | Expected verification |
|---|---|---|---|---|
| REQ-CAM-001 | A camera represents the view area on the world and exposes its view rectangle in world units (meters, ADR-0001); the size is the logical frame size (ADR-0002) converted to world units and divided by the zoom. | Must | PROPOSED | Unit tests of the view rectangle for several zoom factors and frame sizes (conversion factor: OQ-012). |
| REQ-CAM-002 | The zoom factor can be set and animated over a duration in seconds, is clamped to a `[min, max]` range, pivots on the camera center by default, and a change applies on the next presented frame. | Must | PROPOSED | Unit tests of clamping, animation progress with a controllable clock and pivot invariance (easing and other pivots: OQ-135). |
| REQ-CAM-003 | The camera supports translation, absolute (`moveTo`) and relative (`moveBy`), in world units. | Must | PROPOSED | Unit tests of absolute and relative moves and of the resulting view rectangle. |
| REQ-CAM-004 | The camera can follow an entity by its identifier through a lookup contract defined by `camera`, called from an ECS system; it never depends on ECS types. When the entity is missing or removed, following stops, the last position is kept and no error is raised. | Must | PROPOSED | Unit and headless integration tests with a fake lookup: follow, target removed, unknown identifier (adapter location and identifier type: OQ-132, OQ-041). |
| REQ-CAM-005 | The follow movement delay is configurable (time constant in seconds, `0` = rigid) and frame-rate independent: the same simulated time gives the same camera state whatever the update step size (REQ-LOOP-004). | Must | PROPOSED | Unit tests running the same simulated duration with several step sizes (semantics, default and extrapolation: OQ-133). |
| REQ-CAM-006 | The camera supports a shake effect with amplitude, duration and frequency; the amplitude decays linearly to zero; the offset is deterministic for a given seed and is held apart from the logical position, which it never changes. | Should | PROPOSED | Unit tests of decay, end of effect, seed determinism and logical-position invariance (randomness source: OQ-131; units and direction: OQ-134). |
| REQ-CAM-007 | The camera converts world to screen and screen to world, including zoom and shake, so that input and map culling can use it; a plain view rectangle can be handed to rendering consumers (REQ-MAP-005) without exposing a camera type. | Must | PROPOSED | Round-trip conversion tests; headless test that a tile map consumer receives the expected rectangle (consumer contract: OQ-130; picking with shake: OQ-134). |
| REQ-CAM-008 | Camera state changes (follow, delay, zoom animation, shake) happen in the update phase only, driven by the simulation step in seconds; rendering only reads the state (REQ-LOOP-003, REQ-LOOP-006); the camera is testable headless and exposes no frontend or platform type; a camera is an instance, so several cameras remain possible (REQ-MP-001). | Must | PROPOSED | Test that reading the view does not alter state; dependency check (REQ-CORE-005) and API review; two independent cameras in one test. |

## Input and multiplayer

| ID | Requirement | Priority | Status | Expected verification |
|---|---|---|---|---|
| REQ-IN-001 | Inputs are abstracted from physical devices. | Must | PROPOSED | Tests with simulated devices. |
| REQ-IN-002 | Keyboard, mouse and gamepad are supported. | Should | PROPOSED | Support matrix per frontend. |
| REQ-MP-001 | The model must be able to support several views/players in split-screen. | Could | PROPOSED | Demonstrator with two independent views. |
| REQ-MP-002 | The architecture must not block a later network mode. | Could | PROPOSED | Analysis of boundaries and state model. |

## Non-functional requirements

Non-functional constraints (Android versions and DEX toolchain, CPU/memory budgets, concurrency model, memory management, API compatibility policy, licenses, determinism level) are not requirements yet: they are `TO BE DEFINED` and tracked in [`open-questions.md`](open-questions.md) (OQ-001 to OQ-008, OQ-030 to OQ-033, OQ-070, OQ-071). Each one becomes a `REQ-*` once decided.

## Retired identifiers

| ID | Reason |
|---|---|
| REQ-CORE-001 | Duplicate of REQ-TECH-001 (Java as implementation language is already covered by the Java target). |
| REQ-MP-003 | Not a requirement: network protocol, synchronization and authority are an open question (OQ-060). |
