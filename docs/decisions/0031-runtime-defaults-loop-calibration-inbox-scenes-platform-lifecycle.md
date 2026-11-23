# ADR-0031 — Runtime defaults: loop calibration, inbox, asynchronous handles, scenes, platform lifecycle, determinism, logging

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, under the project owner's explicit delegation of 2026-11-23 ("choose the best choice for all requirements and all sub pages related to them; mark them as VALIDATED with your choice"); reversible by a superseding ADR
- Related requirements: `REQ-LOOP-007` to `REQ-LOOP-012`, `REQ-RT-001` to `REQ-RT-010`, `REQ-SCENE-001` to `REQ-SCENE-003`, `REQ-PLATFORM-002`, `REQ-CORE-006`, `REQ-DET-001`, `REQ-DET-003`, `REQ-SUPPORT-001`
- Open questions resolved: `OQ-151`, `OQ-153`, `OQ-155`, `OQ-158` (the network model moves to `OQ-165`)
- Supersedes / superseded by: none; completes [ADR-0017](0017-fixed-step-game-loop.md), [ADR-0018](0018-runtime-thread-events-and-cache-thread-safety.md), [ADR-0019](0019-loading-resource-policy-and-memory.md), [ADR-0021](0021-scene-world-viewport-and-object-model.md) and [ADR-0024](0024-determinism-performance-budgets-and-desktop-support.md) (all unchanged)

## Context

The accepted runtime decisions left calibration values and contract details to "the batch". An implementing agent, possibly a small local model, would then have to invent them. This ADR fixes defaults so that every runtime batch can be implemented without a hidden decision. Values marked "default" are configuration defaults: changing a default after a measurement is an ordinary pull request that updates this ADR's consequences list, not a new decision.

## Decision

### 1. Loop calibration (`OQ-151`)

- The step is configured as an update rate in Hz, an `int` in `[1, 1000]`, default **60**. The step duration in nanoseconds is `round(1e9 / rate)` (a `long`); the step in seconds given to systems is that value divided by `1e9`, so simulated time and step seconds always agree.
- The accumulator is a `long` in nanoseconds. Scaled elapsed time is computed in `double` and rounded to `long` nanoseconds; the rounding remainder is carried to the next iteration, so no time is lost. `alpha = accumulator / (double) stepNanos`.
- Lag cap: default **0.25 s**, configurable in `[one step, 1 s]`.
- Lag report: the loop exposes counters (number of drops, total dropped seconds) and an optional listener called on the runtime thread, in the iteration where time was dropped, with the dropped seconds as a `double` (no allocation).
- Time scale: a `double` in **`[0.1, 4.0]`**, default `1.0`; a value outside the range throws `IllegalArgumentException`. Pause is a separate state, never a scale of `0`.
- Vsync snapping: when the measured elapsed time is within **0.25 ms** of 1, 2, 3 or 4 steps it is snapped to that multiple before scaling. The tolerance is configurable; `0` disables it. It removes the double-update and missed-update jitter of a 60 Hz loop on a 60 Hz display and never changes the simulation step.
- Unscaled time domain: the render call receives `alpha` and the **unscaled real delta** in seconds (capped by the lag cap, independent of pause and time scale). Presentation-only state (menus, UI tweens, a pause screen) may advance with it; simulation state never does (`REQ-LOOP-006`).

### 2. Inbox and asynchronous handles (`OQ-153`)

- The platform inbox is bounded: default capacity **1024** events, configurable. Posting never blocks the posting thread beyond a short lock; on overflow the event is rejected, a dropped-event counter increments and the drop is reported once on the runtime thread at the next delivery.
- Pause, resume, stop and time-scale requests are not inbox entries: they are atomic request fields read at the next frame boundary, so they are never dropped.
- Worker pool for asynchronous loading: provided by the platform; default **2** workers on desktop and **1** on Android; `0` means synchronous only and starts no thread.
- Asynchronous handle states: `PENDING`, `READY`, `FAILED`, `CANCELLED`. A handle changes state only on the runtime thread, at inbox delivery. Progress is a `double` in `[0, 1]` (bytes read over total when the size is known, otherwise `0` then `1`). Reading the value of a handle that is not `READY` throws `IllegalStateException`; a failed handle keeps the exception (ADR-0007 type or the module decoding exception). Cancellation is best effort: a result that arrives after cancellation is discarded and its resources released. A load group aggregates handles; its progress is the mean of its members and it is `READY` when all are.
- Memory budgets per tier are measured values recorded in [`../testing.md`](../testing.md) by the benchmark batches; they are not a design question.

### 3. Scenes and composition (`OQ-155`)

- First scene-manager batch: exactly one active scene, switched at a frame boundary (`REQ-SCENE-002`). The manager receives scene **instances** created by the game; there is no scene registry or lookup by id. A scene may expose an optional name used only for logs.
- Second batch: an **overlay stack** (push, pop) for pause and overlay menus. Push and pop apply at a frame boundary and call `enter` and `exit` of the pushed or popped scene. Only the top scene is updated; all scenes of the stack are rendered from bottom to top. The covered scene keeps its state and is not exited.
- No built-in visual transition: a fade is drawn by the game, as part of a scene or as an overlay.
- A loading screen is an ordinary scene: it starts asynchronous loads in `enter` (ADR-0019), renders the load-group progress, and requests the switch when the group is `READY`.
- The optional composition module is named **`composition`**: a ready-made scene composition (world, views, tile map, background, music) and the camera follow adapter over the world (ADR-0029). It depends on `runtime`, `ecs`, `camera`, `graphics`, `tilemap`, `audio-api` and `render-api`, and nothing depends on it. It is delivered in Phase 3 after the camera, ECS and tile-map batches.
- Several views at render time: `render-api` offers a viewport (a rectangle in pixels of the logical frame) that clips and offsets the following commands. The scene's render code, for each view, sets the viewport, computes the camera view transform with `alpha` (ADR-0029) and emits its layers. The `View` type pairing one camera with one viewport lives in `composition`, not in `render-api` or `camera`.
- `physics` starts as a single package named after the module; the package tree of every module is verified at the build skeleton (`OQ-149`).

### 4. Platform lifecycle (frontend contract)

- The application creates the platform object, configures it (logical frame size, initial screen-mode request, scaling policy, filter chain) and starts it; the platform creates the window or surface and runs the loop on its required thread (ADR-0018).
- A surface resize, a focus change, going to the background or the foreground, a close request and a lost and recovered rendering context are platform events posted to the inbox.
- Resize never changes the logical frame; the scaling is recomputed at the next frame boundary (ADR-0006).
- Background maps to pause and foreground to resume (ADR-0017); audio backends pause and resume their output with it.
- A close request stops the loop by default; the game may register a close listener that vetoes it (for example to ask for confirmation).
- Context loss is handled by the backend (re-upload from the CPU copy or reload, ADR-0019); the recovery event is informational.

### 5. Determinism details (`OQ-158`, non-network part)

- The world's seeded random source is project code using integer operations only: **SplitMix64** to expand the seed and **xoshiro256\*\*** for the sequence, identical on the JVM and Android. `java.util.Random` and `SplittableRandom` (API 24) are not used in shared code. Audio variants and camera shake keep their own generators (ADR-0027, ADR-0029).
- Whole-state snapshots are not built before the network phase; the requirement stays that state can be read and replaced as a whole (ADR-0024).
- Windows and macOS CI jobs are added at the build skeleton as non-blocking jobs and claimed supported once green (`REQ-SUPPORT-001`).
- `double` behavior through DEX is part of the Android verification (`OQ-147`).
- The network model itself (protocol, authority, prediction, rollback) is Phase 5 and is tracked by `OQ-165`.

### 6. Logging

- `core` defines a minimal logging facade: levels `ERROR`, `WARNING`, `INFO`, `DEBUG`; an `isEnabled(level)` check; messages are constant strings with optional primitive arguments, so a disabled level allocates nothing.
- The default sink writes `ERROR` and `WARNING` to `System.err`; a platform module may install another sink (for example Android `Log`). No logging library is used by any shared module.

## Options considered

- Lag cap: 0.25 s (chosen; 15 steps at 60 Hz, a common choice) vs 0.1 s (slows down too early on Android garbage-collection pauses) vs 1 s (long freezes during catch-up).
- Vsync snapping: on by default (chosen; visible stutter otherwise on the most common setup) vs off (simplest, but every 60 Hz game would need to enable it).
- Inbox overflow: reject and count (chosen; never blocks a platform thread) vs block the producer (can freeze the AWT or Android UI thread) vs unbounded (memory growth while the runtime thread stalls).
- Scene stack: single scene first, overlay stack second (chosen) vs a stack from the first batch (more to test before anything runs) vs no stack (every game reimplements pause menus).
- Random generator: own xoshiro256\*\* (chosen; fast, small, specified, platform-identical) vs `java.util.Random` (synchronized, weak) vs `SplittableRandom` (not available at API 21).

## Consequences

- Positive consequences: every runtime value has a documented default; the loop is smooth on 60 Hz displays; presentation can animate during pause and slow motion without touching the simulation; one random algorithm everywhere.
- Costs / constraints: a snapping rule to test; a dropped-event path to test; the overlay stack is a second batch; a small random generator to write and test against reference vectors.
- Impact on modules and APIs: `runtime` (loop configuration, lag report, unscaled delta, scene manager and overlay stack), `platform-api` (lifecycle events, close listener, worker pool), `render-api` (viewport), `core` (logging facade, random generator), new optional module `composition`.
- Impact on tests and documentation: `REQ-LOOP-011`, `REQ-LOOP-012`, `REQ-RT-009`, `REQ-RT-010`, `REQ-SCENE-003`, `REQ-PLATFORM-002`, `REQ-CORE-006` and `REQ-DET-003` added; `contracts.md`, `modules.md`, `conventions.md`, `glossary.md`, `testing.md` updated.

## Validation

Decided by the agent on 2026-11-23 under the owner's explicit delegation; the owner may change any default or reverse any choice by a superseding ADR.
