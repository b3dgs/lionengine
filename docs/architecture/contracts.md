# Cross-cutting contracts

**Status: PROPOSED — conceptual contracts, no Java signature is fixed yet.**

Each public contract must specify: responsibility, invariants, lifecycle, calling thread, units, errors, data ownership, behavior when a capability is absent, and compatibility (including Android/DEX).

## Frontend and rendering

The frontend manages native resources and the platform lifecycle. Rendering receives presentation requests without exposing AWT/OpenGL/Android to the core.

To specify:
- creation, resizing, suspension and closing;
- rendering surface/context and associated thread;
- screen, world and viewport coordinates;
- GPU resources and their invalidation on context loss;
- synchronization between update and render.

### Display modes, resolution and output filtering (ADR-0002, ACCEPTED)

Capability-based best-effort negotiation: the game requests, the platform applies what it can and reports what it actually applied (effective value and reason), so game code is identical everywhere. A missing capability never throws.

- **Screen mode** (`WINDOWED`, `FULL_WINDOW`, `FULL_SCREEN`) and **display mode** (width and height in pixels, refresh rate in Hz, color depth in bits): `platform-api`, with engine-defined neutral value types and a capability query. Unsupported requests fall back in a documented order (REQ-DISPLAY-001 to REQ-DISPLAY-003).
- **Logical resolution** is independent of the output display mode; a scaling policy governs presentation: `FIT` (default), `INTEGER` (opt-in, falls back to `FIT` when the surface is too small) or `STRETCH`; the policy type is in `render-api`, the surface size comes from `platform-api`. Scaling never changes world units (REQ-DISPLAY-004, REQ-DISPLAY-007, ADR-0006).
- **Output filtering** (`NONE`, bilinear, `HQ2X`, CRT; extensible; `NONE` samples nearest-neighbor when the logical frame is scaled): `render-api`, an ordered chain applied from the logical frame to the output surface only, not per layer (REQ-FILTER-001 to REQ-FILTER-003). Scaling filters (`NONE`, bilinear, `HQ2X`) resample the logical frame to the output size; post filters (CRT) run on the scaled output; no filter changes the scaling policy (ADR-0006).
- Changes are applied at a frame boundary (between two presentations, outside any simulation update) and signaled by an event; they never alter simulation state, and an extrapolated state is never reused as simulation input (REQ-DISPLAY-005, REQ-LOOP-006).
- No AWT, OpenGL or Android type in these contracts (REQ-DISPLAY-006). Multi-monitor and HiDPI are outside the first batch (OQ-096). On desktop, `platform-awt` implements `platform-api` with the JDK AWT only (ADR-0008); the render side and the surface hand-off are open (OQ-123), and the representation of an unknown refresh rate or bit depth is OQ-124.
- **Effective result and fallback (ADR-0006):** apply returns the accepted request and never blocks the update step; the effective result (requested vs effective per field, `degraded` flag, reason) is an immutable value delivered at the frame boundary and readable as current state (REQ-DISPLAY-008; delivery mechanism: OQ-122). Fallback orders: screen mode `FULL_SCREEN` → `FULL_WINDOW` → `WINDOWED`, `WINDOWED` → `FULL_WINDOW` → `FULL_SCREEN`, `FULL_WINDOW` → `WINDOWED` → `FULL_SCREEN`; display mode exact, then nearest size, rate, depth; filters CRT skipped, `HQ2X` → bilinear → `NONE`.

Java signatures are not fixed. Details: [`features/display-modes.md`](../features/display-modes.md), [`features/output-filtering.md`](../features/output-filtering.md).

## Unit conventions

The world uses `(x, y)` in meters. Durations are in seconds. Speeds are in `m/s`, accelerations in `m/s²`. Any API that converts to pixels must belong to the presentation boundary and state the conversion factor explicitly.

A time value must not be expressed sometimes in milliseconds, sometimes in seconds without explicit conversion. Prefer parameter names that include the unit when the Java type does not remove the ambiguity.

## Game loop

Conceptual contract:
- start, stop, suspend and resume;
- run the simulation against a time reference independent of machine speed;
- trigger rendering separately;
- be able to skip a presentation (frameskipping) without skipping or altering the required simulation calculations;
- extrapolate a state for presentation only;
- expose a lifecycle state;
- allow a deterministic, test-driven implementation.

**Mandatory invariant:** an extrapolated state never becomes the source state of the next simulation update. Extrapolation, including with a factor greater than `1`, must not introduce cumulative error in world calculations.

To define before implementation (OQ-020 to OQ-023): simulation step duration, monotonic clock, target render cadence, extrapolation formula and factor, maximum tolerated lag, catch-up strategy, pause, slow motion, exceptions and shutdown policy. The test loop must allow time to be advanced artificially and exact states to be verified.

## Resources

A resource must be identified independently of its physical location.

Envisaged origins:
- path relative to a resource space;
- filesystem;
- Android assets;
- temporary/memory storage;
- archive;
- stream supplied by the application.

To specify:
- resolution and priority between providers;
- distinction between logical URI and physical path;
- read/write opening;
- stream closing;
- concurrent access;
- caching, invalidation and reloading;
- errors: missing, inaccessible, invalid format, corruption.

Do not assume that a resource is a `File`: a resource in an archive or an Android asset may have no filesystem path.

**Common convention for modules handling resources (ADR-0005, ACCEPTED; owner rule stated with decision 3B of issue #853):** `resources` stays media-agnostic and holds no decoded cache. Every module that loads or decodes resource content (today `graphics` and the audio modules; candidates `tilemap` for map files and rendering backends for shader assets, OQ-094) depends on `resources`, keeps its own decoded cache, and follows one common convention for keys and release, to limit divergence between implementations (REQ-RES-004). Content of the convention (ADR-0007; REQ-RES-005): the key is a normalized logical path string (`/` separator, case-sensitive, no leading `/`, no `.` or `..`) for every origin, a stream supplied by the application being registered under such a name; derived caches key on (resource key, module discriminator); release is owned by the consuming module and idempotent; a missing or unreadable resource raises one unchecked engine exception type, while decoding errors belong to the consuming module. The module cache policies (OQ-101, OQ-110) and the sprite sheet id (OQ-106) use this key form. Thread safety of module caches: OQ-121; asynchronous loading: OQ-031; reloading: OQ-032.

## Audio

The audio API describes operations and their lifecycle, not the concrete audio engine.

### Short and long sounds (ADR-0004, ACCEPTED)

Owner decisions of issue #853 (ADR accepted on 2026-11-22):

- **Two families (decision 1A):** short sounds (low-latency effects, cached for heavy reuse, play, stop, variant sets) and long sounds (music/ambient, preloadable and releasable, play, stop, playlists) are separate contract families with distinct handles and lifecycles.
- **Backend-independent logic (decision 2A):** variant selection and playlist advance live in `audio-api` (or a shared helper); backends only expose play, stop and end-of-track.
- **Resources (decision 3B):** `audio-api` and `audio-*` depend on `resources`; decoded caches exist only inside the audio modules (REQ-RES-004, ADR-0005).
- Durations and positions in seconds, sample rate in Hz; no platform or concrete audio library type in the API (REQ-AUDIO-009). Playlist-advance callbacks must not mutate simulation state from the audio thread (OQ-114).

Not decided: cache policy (OQ-110), voice limit (OQ-111), variant policies (OQ-112), long-sound strategy (OQ-113), playlist end, failure and thread (OQ-114), formats and decoders (OQ-115), latency target (OQ-116). Java signatures are not fixed. Details: [`features/audio-short-and-long-sounds.md`](../features/audio-short-and-long-sounds.md).

Out of this scope, to specify later (OQ-117): pause, resume, volume, looping, fades, possible spatialization, mixer channels. WAV/MP3/OGG are target formats, not a guarantee of universal decoding on every platform.

## Images, sprites and animations

Separate as much as necessary:
- image data and modifiable pixels;
- resource/texture consumable by rendering;
- sprite (region, transformation, display state);
- spritesheet (slicing);
- animation (sequence, duration, loop, state).

To specify: pixel format, alpha, mutation, copy/sharing, dimensions, origin, texture sampling (distinct from output filtering, ADR-0002), CPU/GPU synchronization and release.

### Sprites, animations and sprite sheets (ADR-0003, ACCEPTED)

Owner decisions of issue #852 (ADR accepted on 2026-11-22):

- **Sprite model (decision 4):** a shared immutable **source** (image + region, loaded through `resources`, held by the `graphics` cache per ADR-0005) is separate from a lightweight **sprite instance** (transform, color transform, location, alpha). Animated sprites and sheet cells produce instances or regions from the same source.
- **Color transformation (decision 1C):** a backend-independent color-transform *description* lives in `graphics`; per-frame effects (fade, tint, alpha) go through `render-api` draw parameters; static transforms (palette swap, grading, mask bake) run on the CPU and are cached. A backend can always fall back to the CPU path; capability query and fallback rules: OQ-102.
- **Template color variants (decision 2):** a template palette mapping yields variants cached by (source image, mapping); authoring convention: OQ-103.
- **Rendering (decision 3B):** sprites emit `render-api` commands and never draw directly; batching is a later optimization behind the command abstraction (OQ-108). This fixes the abstract-command style (OQ-054, resolved by ADR-0003).
- **Units (review choice, ADR-0003):** sprite location, translation, pivot and size are in pixels of the logical frame; rotation follows the engine angle convention (OQ-011); the conversion from world meters belongs to the camera/viewport (OQ-012).
- Animation time advances only from the simulation update step (REQ-ANIM-003, REQ-LOOP-006). Durations in seconds, sizes in pixels, no frontend type in the API (REQ-SPRITE-007).

Java signatures are not fixed. Details: [`features/sprite-sheet.md`](../features/sprite-sheet.md), [`features/sprite.md`](../features/sprite.md), [`features/animated-sprite.md`](../features/animated-sprite.md).

## Tilemap

Distinguish:
- tileset: tile definitions and image regions;
- grid/map: tile placement;
- layers: order, visibility, properties;
- static content and dynamic modifications.

Decisions of [ADR-0009](../decisions/0009-tile-map-contracts-in-tilemap.md) (PROPOSED; owner decisions of issue #865):

- **Model:** single-layer grid of cells, each empty or a tileset cell index (`REQ-SHEET-001`); tile coordinates are `int` tile indices; tile size in pixels of the logical frame; `set`, `get`, `clear` with bounds checking and defined errors (`REQ-MAP-003`). A per-tile properties hook exists, with no collision type.
- **Loading:** through an abstract loader contract over `resources`; keys and release per ADR-0007 (`REQ-MAP-004`). File format: OQ-126; tileset reference and ownership: OQ-128.
- **Rendering:** draws only tiles intersecting a viewport rectangle (pixels of the logical frame, a plain value, no camera type) plus a margin, by emitting `render-api` commands; read-only (`REQ-MAP-005`).
- **Destruction:** rule-based auto-tiling updates neighbors in the update phase (`REQ-MAP-006`; rule format OQ-127); a synchronous tile-changed listener reports cell, old and new tile (`REQ-MAP-007`; OQ-122).

Still to specify: layers (candidate), chunks and render cache (OQ-129), collisions static and dynamic (OQ-125, OQ-043).

## Camera

Decisions of [ADR-0010](../decisions/0010-camera-system-in-camera.md) (ACCEPTED; owner decisions of issue #868):

- **Placement:** single `camera` module depending on `core` only.
- **View:** a plain view rectangle in world units (meters, ADR-0001), no `Camera` type seen by rendering (`REQ-CAM-001`); consumer contract OQ-131. World-to-screen and screen-to-world conversion (`REQ-CAM-007`; factor OQ-012).
- **Zoom and translation:** zoom dimensionless, clamped, animated, applied from the next frame; absolute and relative translation (`REQ-CAM-002`, `REQ-CAM-003`).
- **Follow:** an entity-ID lookup contract defined by `camera`; the ECS system or adapter lives outside, no dependency on `ecs`; a missing entity is safe (`REQ-CAM-004`; OQ-132).
- **Delay:** exponential smoothing, time constant in seconds, 0 is rigid, frame-rate independent (`REQ-CAM-005`; OQ-133).
- **Shake:** amplitude, duration, frequency; per-camera seeded noise, deterministic (`REQ-CAM-006`; OQ-130, OQ-134).
- **Lifecycle:** update phase only; rendering reads only; no singleton (`REQ-CAM-008`).

Still to specify: zoom details (OQ-135), out-of-scope candidates (OQ-136).

## Objects and traits

The simple model and the ECS model are candidate strategies, not necessarily a single common API (OQ-041, OQ-042).

An object must have an explicit lifecycle. A trait represents a composable capability/behavior and must define its preconditions, its state, its execution order and its interactions.

Envisaged examples: `Movable`, `Gravity`, `Collision`. Their names and semantics remain to be defined. Avoid circular dependencies between traits.

## Input

Normalize physical inputs into events and/or logical states. Distinguish device (keyboard/mouse/gamepad), game action and player.

To specify: mapping, key repeat, analog axes, dead zone, hot-plug, focus/loss of focus, multiple devices and event ordering.

## Multiplayer

Clearly distinguish:
- several local players;
- split-screen (several viewports/cameras);
- network (transport, session, replication/synchronization).

Split-screen must not be confused with networking. Protocol, authority, prediction, rollback and synchronization are outside the contract until decided (OQ-060).
