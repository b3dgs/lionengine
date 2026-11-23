# Cross-cutting contracts

**Status: VALIDATED — responsibilities, invariants and defaults of every contract (ADR-0001 to ADR-0035). Java signatures are written by each implementation batch under [`../conventions.md`](../conventions.md) and reviewed in its pull request.**

Each public contract must specify: responsibility, invariants, lifecycle, calling thread, units, errors, data ownership, behavior when a capability is absent, and compatibility (including Android/DEX). A plain-language summary with diagrams is in [`../how-the-engine-works.md`](../how-the-engine-works.md).

## Threads and events (ADR-0018, ADR-0031)

- One **runtime thread** owns the loop, the update phase, the world, cameras, animations, caches and, by default, the render phase. Every public type documents its thread; the default is "not thread-safe, owned by the runtime thread". The engine starts no thread; the platform runs the loop on the thread it requires (AWT: a dedicated thread with active rendering; OpenGL and Android: the context or renderer thread).
- Thread-safe entry points only enqueue: the platform **inbox**, pause, resume, stop and time-scale requests, asynchronous-load completion, audio callbacks.
- **Inbox:** FIFO, bounded (default 1,024 events), delivered on the runtime thread at the start of an iteration, before the update steps. Posting never blocks beyond a short lock; an overflowing event is rejected, counted and reported. Pause, resume, stop and time scale are atomic request fields, not inbox entries.
- **Internal events** (ECS, tile changed, collision) are synchronous in the update phase. Listeners run on the runtime thread; registration during a dispatch takes effect after it; no allocation per event; a listener exception follows fail-fast.

## Frontend, platform and rendering

### Platform lifecycle (ADR-0031)

- The application creates the platform object, configures it (logical frame size, initial screen mode, scaling policy, filter chain), registers its modules explicitly (ADR-0020) and starts it.
- Surface resize, focus change, background and foreground, close request, context loss and recovery are inbox events. Resize never changes the logical frame; scaling is recomputed at the next frame boundary. Background pauses and foreground resumes (ADR-0017). A close request stops the loop unless a close listener vetoes it. Context loss is recovered by the backend from CPU copies or by reloading (ADR-0019).
- Capabilities are found by a typed optional lookup on the platform object; absence means unsupported (ADR-0020).
- Desktop reference: `platform-awt` (JDK AWT only, excluded from Android builds, ADR-0008) with `render-awt`, which reaches the surface through the `AwtSurface` capability of `platform-awt` (ADR-0022, ADR-0033).

### Coordinate spaces (ADR-0013, ADR-0014)

| Space | Unit | Origin and axes | Owner |
|---|---|---|---|
| World | meters (`double`) | `(0, 0)`, X right, Y up | simulation (`ecs`, `traits`, `physics`, tile collision) |
| Tile | `int` tile indices | bottom-left cell, Y up (loaders flip top-down files) | `tilemap` |
| Logical frame (screen) | pixels | top-left, Y down | rendering, sprites, tile rendering, pointer input |
| Output surface | device pixels | top-left, Y down | platform and render backend, after scaling and filters |

The camera alone converts world to logical-frame pixels through its view transform (`screenX = x * scale + offsetX`, `screenY = offsetY - y * scale`, ADR-0034). The scaling policy alone maps the logical frame to the output surface (ADR-0006). `float` appears only at the render boundary.

### Render commands (ADR-0026, ADR-0033)

- Rendering code never draws directly: it writes entries into a preallocated command buffer of parallel primitive arrays (texture handle, source rectangle, position, scale, rotation in degrees, pivot, tint, alpha, layer). Entries are presented by ascending layer, then submission order. No allocation per frame.
- A **viewport** (rectangle in logical-frame pixels) clips and offsets the following commands; a scene with several views sets one viewport per view (ADR-0031).
- Pixel buffers are packed `int` ARGB with straight alpha; texture sampling is nearest-neighbor by default; per-frame tint and alpha are a typed capability of `render-api`, other color effects are baked on the CPU and cached.
- GPU upload happens on the render thread at first use or at an explicit prepare call, never on a worker (ADR-0026).

### Display modes, resolution and output filtering (ADR-0002, ADR-0006, ADR-0025, ADR-0033)

Capability-based best-effort negotiation: the game requests, the platform applies what it can and reports what it actually applied, so game code is identical everywhere. A missing capability never throws.

- **Screen mode** (`WINDOWED`, `FULL_WINDOW`, `FULL_SCREEN`) and **display mode** (width and height in pixels, refresh rate in Hz, color depth in bits; `0` means unknown or multiple, with `isRefreshRateKnown()` and `isBitDepthKnown()`): `platform-api`, engine-defined value types, capability query.
- **Logical resolution** is independent of the display mode. Scaling policy `FIT` (default), `INTEGER` (opt-in, falls back to `FIT` when the surface is smaller than the logical frame) or `STRETCH`; the type is in `render-api`, the surface size comes from `platform-api`. Scaling never changes world units.
- **Output filtering** (`NONE` nearest-neighbor, bilinear, `HQ2X`, CRT, custom): `render-api`, an ordered chain from the logical frame to the output surface only. Scaling filters resample, post filters (CRT) run on the scaled output. Shaders use the GLSL ES 1.00 feature set, compiled as `#version 100` or `#version 120` with headers prepended by the backend, delivered through `resources`. `render-awt` implements `NONE`, bilinear and `Scale2x` on the CPU (standing in for `HQ2X` until its license record) and reports CRT unsupported.
- **Custom filters:** a registry id, explicit registration on the backend, a scaling or post kind, a fragment shader resource and/or a CPU implementation over `int[]` ARGB; a backend lacking the needed implementation skips it and reports the skip.
- **Fallback orders:** screen mode `FULL_SCREEN` → `FULL_WINDOW` → `WINDOWED`, `WINDOWED` → `FULL_WINDOW` → `FULL_SCREEN`, `FULL_WINDOW` → `WINDOWED` → `FULL_SCREEN`; display mode exact, then nearest size, rate, depth; filters: CRT skipped, `HQ2X` → bilinear → `NONE`.
- **Effective result:** apply returns the accepted request without blocking; the effective result (requested vs effective per field, `degraded` flag, reason) is an immutable value delivered through the inbox at the frame boundary where it is applied and readable as the current state.
- Changes apply at a frame boundary and never alter simulation state. No AWT, OpenGL or Android type in these contracts. Settings persistence is the game's job. Multi-monitor and HiDPI: OQ-096 (contracts assume neither one monitor nor scale 1.0).

Details: [`features/display-modes.md`](../features/display-modes.md), [`features/output-filtering.md`](../features/output-filtering.md).

## Unit conventions

The world uses `(x, y)` in meters, `double`. Durations are in seconds. Speeds are in `m/s`, accelerations in `m/s²`. Angles are degrees, counter-clockwise positive, `0°` along `+X`; absolute angles in `[0, 360)`, signed differences in `(-180, 180]`, normalized only by explicit helpers (ADR-0013, ADR-0014, ADR-0016). Any API that converts to pixels belongs to the presentation boundary and states the conversion factor.

A time value is never expressed sometimes in milliseconds and sometimes in seconds; parameter names carry the unit when the Java type does not.

## Game loop (ADR-0017, ADR-0031)

- Fixed step from an update rate in `[1, 1000]` Hz (default 60); abstract monotonic clock in nanoseconds (`System.nanoTime` on the JVM and Android, a controllable clock in tests); `long` accumulator with catch-up; the rounding remainder of scaled time is carried.
- Lag cap 0.25 s by default (configurable in `[one step, 1 s]`); excess dropped and reported (counters and an allocation-free listener). Frameskipping drops renders, never updates below the cap.
- Vsync snapping: elapsed time within 0.25 ms of 1 to 4 steps is snapped.
- `alpha = accumulator / step`, always in `[0, 1)`. Interpolation is the default presentation, extrapolation is opt-in; both are presentation-only and never fed back.
- Pause: no catch-up and no time shift on resume; rendering may continue. Time scale in `[0.1, 4.0]` multiplies elapsed time; the step never changes. Render receives `alpha` and the unscaled real delta for presentation-only state.
- Fail-fast: an exception in update or render stops the loop in a failed state and reaches the owner. Stop is honored at a frame boundary and is idempotent.
- **One iteration:** read control requests → deliver the inbox (input, platform events, load completions) → run zero or more fixed steps (each: the active scene's update, which runs the world — systems by slot, then the flush — and then its cameras, which sample their targets) → render once with `alpha` and the unscaled delta.

## Scenes (ADR-0021, ADR-0031, ADR-0036)

- A scene is a lifecycle contract in `runtime`: `enter`, `update`, `render`, `exit`, `dispose`. It knows nothing of world, camera, graphics or audio; the game (or the optional `composition` module) composes its content.
- The scene manager receives scene instances, keeps one active scene and switches at a frame boundary: the previous scene is exited and disposed before the next is entered. A second batch adds an overlay stack: only the top scene updates, every stacked scene renders bottom to top.
- A loading screen is an ordinary scene driving asynchronous handles. Visual transitions are drawn by the game.
- **Scene flow:** a code-built, validated, immutable state machine over scene ids (`namespace:name`, each with a factory creating a new instance per entry) and a game context object. Transitions `(from id or any scene, trigger, condition, kind, target)` with kinds `SWITCH`, `PUSH`, `POP`, `EXIT`; `trigger(name)` resolves at once against the top scene (its transitions, then any-scene ones, in declaration order), returns whether one was selected, and the selected one applies at the next frame boundary; one pending transition at a time. Details: [`features/scenes.md`](../features/scenes.md).

## Resources (ADR-0005, ADR-0007, ADR-0019, ADR-0032)

- A resource is identified by a **key**: a normalized logical path string (`/` separator, case-sensitive, no leading `/`, no `.` or `..`) for every origin; a stream supplied by the application is registered under such a key. A resource is never assumed to be a `File`.
- **Providers** (`resources-*`: filesystem, archive, memory/temporary, Android assets, application stream) form an ordered list configured by the application; the first provider that has the key wins. Providers are read-only unless declared writable; they are thread-safe for reading. An opened stream belongs to the caller, who closes it.
- Errors: missing or inaccessible content raises the one unchecked resource exception of `resources`, with the key and a `MISSING` or `INACCESSIBLE` reason; invalid format and corruption are decoding errors of the consuming module.
- **Consuming modules** (`graphics`, audio, `tilemap`, render backends for shaders) keep their own decoded caches; `resources` is media-agnostic. Derived caches key on (resource key, module discriminator). Release is owned by the consuming module and idempotent.
- **Lifetime:** explicit reference counting and release, no weak references, no automatic eviction by default, optional size limits, scope release (for example a level). Cache operations on the runtime thread only; decoded data immutable.
- **Loading:** synchronous by default. Opt-in asynchronous requests return a handle (`PENDING`, `READY`, `FAILED`, `CANCELLED`, progress in `[0, 1]`) completed through the inbox; workers (default 2 on desktop, 1 on Android) only read and decode. Reloading only for context loss and as an explicit development reload at a frame boundary.

## Definition files and registry (ADR-0020, ADR-0028, ADR-0032)

- Every definition file is standard JSON read by the strict reader of `core`: duplicate keys, comments, trailing commas, `NaN` and `Infinity` are errors; integral literals are `long`, others `double`; limits 16 MiB, depth 64, 1 MiB strings by default.
- Engine files declare `"format"` (for example `lionengine.sheet`) and `"version"` (from 1). Field names are lower camel case; colors `"#RRGGBB"` or `"#AARRGGBB"`; durations seconds, angles degrees, image positions pixels. An unread field is an error except `"comment"`. Errors carry the key, line, column and JSON path.
- Data-driven types are created through an explicit registry instance of ids `namespace:name` (engine namespace `lionengine`) and factories registered at startup; factories read a typed configuration. No reflection, `ServiceLoader` or class scanning.

## Audio (ADR-0004, ADR-0022, ADR-0027, ADR-0033)

- Two families with distinct handles: **short sounds** (effects, at most 10 s, preloaded explicitly, cached, low latency) and **long sounds** (music, ambient, streamed through a bounded buffer by default, or fully decoded on request).
- Variant sets: random with no immediate repeat and optional weights, or composed (simultaneous layers); own seeded generator. Playlists: next track prepared ahead; stop at the end by default, loop on request; a failing track is skipped and reported; end-of-track goes through the inbox and advance runs on the runtime thread.
- Voices: 16 by default; overflow steals the oldest voice of the lowest priority (priority `0` to `100`, default `50`); drop-new and refuse selectable.
- First batch: play, stop, volume (gain in `[0, 1]`), looping, pause, resume. Second batch: mixer groups (`music`, `effects`, `ui` and game groups; gain = master × group × sound) and linear fades. Spatial audio deferred.
- Formats: PCM WAV baseline parsed by project code; OGG Vorbis by the Android platform and by `audio-codec-ogg` on desktop; no MP3 baseline. Backends per output library or platform (`audio-javasound`, `audio-android`).
- No platform or audio library type in `audio-api`; durations and positions in seconds, sample rate in Hz.

Details: [`features/audio-short-and-long-sounds.md`](../features/audio-short-and-long-sounds.md).

## Images, sprites and animations (ADR-0003, ADR-0026, ADR-0033)

- A shared immutable **source** (image + region, held by the reference-counted cache of a `graphics` instance) is separate from a lightweight mutable **sprite instance** (transform, color transform, pivot, alpha) changed in the update phase only.
- Decoding: built-in pure-Java PNG decoder (non-interlaced, every color type, depths 1 to 8 and 16 reduced to 8, `tRNS`); other formats through optional decoder modules (`graphics-imageio`, `graphics-android`) registered explicitly. Modifiable pixels are a separate caller-owned buffer; turning it into a source copies it.
- Transform: scale, then rotation, then translation, about the pivot (pixels from the frame top-left, default the frame center); rotation counter-clockwise on screen, in degrees.
- Template color variants: exact key colors mapped to targets in the sheet definition, cached by (source image, mapping).
- Animation: frame indices, frame duration in seconds (optional per-frame durations), loop mode `once`, `loop` or `ping-pong`; time advances with the simulation step only.
- Sprite sheet: grid (cell size, columns, rows, margin, spacing) and cells by index; sheets grouped by a unique string id, reference-counted, released by their acquirer (normally the scene). Definition file format `lionengine.sheet`.

Details: [`features/sprite-sheet.md`](../features/sprite-sheet.md), [`features/sprite.md`](../features/sprite.md), [`features/animated-sprite.md`](../features/animated-sprite.md).

## Tile map (ADR-0009, ADR-0028, ADR-0032)

- Single-layer grid of cells, each empty or a tileset cell index; `int` tile coordinates with the origin at the bottom-left, Y up; tile size in logical-frame pixels; `set`, `get`, `clear` with bounds checking; flat row-major `int[]` storage.
- Tileset referenced by sprite sheet id and reference-counted by the map; the owning scene closes the map, which releases the sheet.
- Loading through an abstract loader over `resources`; first real format Tiled JSON (orthogonal, finite, one tile layer, one tileset, array or base64 data with optional zlib or gzip; unsupported features rejected).
- Rendering: only tiles intersecting a viewport rectangle in logical-frame pixels plus a margin, by emitting commands; read-only.
- Destruction: ordered auto-tiling rules (8-neighbor bitmask plus care mask, bit 0 north then clockwise, first match wins; file format `lionengine.autotile`) update neighbors in the update phase; a synchronous tile-changed listener reports cell, old and new tile.
- Collision: per-tile `empty` or `solid` from the tileset; `tilemap` implements the `physics` shape-query contract with a world placement (origin and tile size in meters, default 1 m); no dependency on `ecs`. Later: one-way tiles, then slopes (ADR-0034). Several layers stay a candidate.

## Camera (ADR-0010, ADR-0029, ADR-0034)

- Headless `camera` module on `core` only; no singleton; state changes in the update phase only; rendering reads.
- View rectangle in meters (logical frame size converted with the camera's pixels per meter, divided by the zoom). Per frame, an immutable view transform (`scale`, `offsetX`, `offsetY`) computed from the state interpolated with `alpha`; rendering never sees a `Camera` type.
- Zoom: multiplier, default 1.0, range 0.25 to 4.0, curves `LINEAR`, `SMOOTHSTEP` (default), `EASE_IN`, `EASE_OUT`, center pivot or a world anchor (ignored while following).
- Follow: lookup contract keyed by a primitive `long` (the ECS object ID); a missing object stops the follow safely; smoothing time constant default 0 (rigid); explicit snap; target sampled at the end of the step. Bounds clamp, dead zone (fraction of the view) and follow offset (meters).
- Shake: amplitude in meters, frequency in Hz, duration in seconds, linear decay, fixed-point integer value noise (bit-exact), per-camera seed (default `0x4C494F4E`), strongest shake wins; picking ignores it unless `includeShake`.

## Objects and traits (ADR-0011, ADR-0021, ADR-0030, ADR-0034)

The ECS is the single object model: a `World` (module `ecs`) owns objects, traits, queries, systems, the flush and a seeded random source. Traits attach to ECS objects only. Collision shapes, overlap tests, the shape-query contract and the broad phase live in `physics`. Several worlds may coexist; none is a singleton.

- **Object:** a packed `long` ID (generation high 32 bits, index low 32 bits, `0` invalid). A created object is pending until the flush (liveness false, accessors fail fast, commands may already be queued). Stale IDs are detected; the liveness check never throws.
- **Trait:** data only, registered once with an `int` type ID; pooled instances in dense arrays, reset on removal; capacities 1,024 objects and 256 per trait type by default, doubling at the flush.
- **Query:** include and exclude sets, cached, updated incrementally, allocation-free iteration; deterministic order for the same operation sequence, no creation-order guarantee; explicit ID copy in ascending order into a caller buffer.
- **Structural changes:** recorded in a command buffer and applied at one flush per step, at the end, after all systems and before rendering; commands in recorded order; explicit flush allowed outside the loop.
- **Events:** listeners with primitive arguments, synchronous: created and destroyed, trait added and removed (at the flush), collision enter and exit (after the `Collision` system, `objectA < objectB`).
- **Systems:** `int` ordering slot, lower first, ties by registration order; enabled flag applied at the next step; constant step in seconds; exceptions propagate.
- **Standard traits** (`traits`): `Transformable`, `Movable`, `Gravity`, `Jumpable`, `Collidable`, `Lifetime`; systems `Jump` (1000), `Gravity` (2000), `Movement` (3000), `Collision` (4000), `Lifetime` (5000). Collision first batches: grid cell 2 m, boxes, layers and masks both ways, triggers, detection only for objects, tile push-out setting `grounded`, no swept detection.
- **Not allowed:** a platform type in `ecs` or `traits`, a singleton world, an allocation per step in the query, system and flush paths, an ECS dependency of `core` or of an `*-api` module.

Details: [`features/ecs.md`](../features/ecs.md), [`features/standard-traits.md`](../features/standard-traits.md).

## Input (ADR-0035)

- Devices: keyboard, pointer (mouse and touch), gamepad (standard layout); engine-defined `int` codes; availability through capabilities.
- Game logic reads **actions** (digital or analog) bound to codes by a binding table; several bindings per action.
- Raw events go through the inbox. Each digital action exposes `down`, `pressed`, `released` per update step; an edge is seen by exactly one step; a tap within one step is never lost.
- Sticks in `[-1, 1]` with a radial dead zone (default 0.15), triggers in `[0, 1]`; pointer positions in logical-frame pixels after inverse scaling.
- Focus loss releases everything; gamepad hot-plug events keep device indices; key repeat ignored; text input is a separate later stream.
- Players are device sets plus bindings; per-step action states can be recorded.

## Logging (ADR-0031)

`core` provides a facade with levels `ERROR`, `WARNING`, `INFO`, `DEBUG`, an `isEnabled` check and constant messages with primitive arguments; disabled levels allocate nothing; the default sink writes errors and warnings to `System.err`; platforms may install another sink.

## Multiplayer

- Several local players and split-screen are several views in one scene and per-player device sets (ADR-0021, ADR-0035); no module needed.
- Network: the optional `multiplayer` module (ADR-0022), Phase 5. Binding today: deterministic simulation, whole-state read and replace, inputs through actions (ADR-0024). Protocol, authority, prediction and rollback: OQ-165.
