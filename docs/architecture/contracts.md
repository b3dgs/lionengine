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

### Display modes, resolution and output filtering (PROPOSED, ADR-0002)

Capability-based best-effort negotiation: the game requests, the platform applies what it can and reports what it actually applied (effective value and reason), so game code is identical everywhere. A missing capability never throws.

- **Screen mode** (`WINDOWED`, `FULL_WINDOW`, `FULL_SCREEN`) and **display mode** (width and height in pixels, refresh rate in Hz, color depth in bits): `platform-api`, with engine-defined neutral value types and a capability query. Unsupported requests fall back in a documented order (REQ-DISPLAY-001 to REQ-DISPLAY-003).
- **Logical resolution** is independent of the output display mode; a scaling policy (stretch, aspect-preserving, integer scale) governs presentation. Scaling never changes world units (REQ-DISPLAY-004, OQ-091).
- **Output filtering** (`NONE`, nearest, bilinear, `HQ2X`, CRT; extensible): `render-api`, an ordered chain applied from the logical frame to the output surface only, not per layer (REQ-FILTER-001 to REQ-FILTER-003).
- Changes are applied at a frame boundary and signaled by an event; they never alter simulation state, and an extrapolated state is never reused as simulation input (REQ-DISPLAY-005, REQ-LOOP-006).
- No AWT, OpenGL or Android type in these contracts (REQ-DISPLAY-006). Multi-monitor and HiDPI are outside the first batch (OQ-096).

Java signatures are not fixed. Details: [`features/display-modes.md`](../features/display-modes.md), [`features/output-filtering.md`](../features/output-filtering.md).

### Sprites, animations and sprite sheets (PROPOSED, ADR-0003)

Owner decisions of issue #852 (the ADR stays PROPOSED):

- **Sprite model (decision 4):** a shared immutable **source** (image + region, cached, resource-owned) is separate from a lightweight **sprite instance** (transform, color transform, location, alpha). Animated sprites and sheet cells produce instances or regions from the same source.
- **Color transformation (decision 1C):** a backend-independent color-transform *description* lives in `graphics`; per-frame effects (fade, tint, alpha) go through `render-api` draw parameters; static transforms (palette swap, grading, mask bake) run on the CPU and are cached. A backend can always fall back to the CPU path; capability query and fallback rules: OQ-102.
- **Template color variants (decision 2):** a template palette mapping yields variants cached by (source image, mapping); authoring convention: OQ-103.
- **Rendering (decision 3B):** sprites emit `render-api` commands and never draw directly; batching is a later optimization behind the command abstraction (OQ-108).
- Animation time advances only from the simulation update step (REQ-ANIM-003, REQ-LOOP-006). Durations in seconds, sizes in pixels, no frontend type in the API (REQ-SPRITE-007).

Java signatures are not fixed. Details: [`features/sprite-sheet.md`](../features/sprite-sheet.md), [`features/sprite.md`](../features/sprite.md), [`features/animated-sprite.md`](../features/animated-sprite.md).

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

**Common convention for modules handling resources (PROPOSED, ADR-0004, owner decision 3B of issue #853):** `resources` stays media-agnostic and holds no decoded cache. Every module that handles a resource (an item reachable from a path: audio, graphics, ...) depends on `resources`, keeps its own decoded cache, and follows one common convention for keys and release, to limit divergence between implementations (REQ-RES-004). The content of the convention (key form, release and double-release rule) is `TO BE DEFINED` (OQ-118, OQ-032); it must be consistent with REQ-RES-003 and OQ-101 and OQ-110.

## Audio

The audio API describes operations and their lifecycle, not the concrete audio engine.

### Short and long sounds (PROPOSED, ADR-0004)

Owner decisions of issue #853 (the ADR stays PROPOSED):

- **Two families (decision 1A):** short sounds (low-latency effects, cached for heavy reuse, play, stop, variant sets) and long sounds (music/ambient, preloadable and releasable, play, stop, playlists) are separate contract families with distinct handles and lifecycles.
- **Backend-independent logic (decision 2A):** variant selection and playlist advance live in `audio-api` (or a shared helper); backends only expose play, stop and end-of-track.
- **Resources (decision 3B):** `audio-api` and `audio-*` depend on `resources`; decoded caches exist only inside the audio modules (REQ-RES-004).
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

To specify: pixel format, alpha, mutation, copy/sharing, dimensions, origin, filtering, CPU/GPU synchronization and release.

## Tilemap

Distinguish:
- tileset: tile definitions and image regions;
- grid/map: tile placement;
- layers: order, visibility, properties;
- static content and dynamic modifications.

To specify: coordinates, tile size, missing tiles, layers, chunks, collisions and rendering strategy.

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
