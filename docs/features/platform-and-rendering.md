# Feature: platform, render commands, desktop AWT frontend and headless test frontend

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 1 (platform object, lifecycle, headless frontend, minimal `platform-awt` and `render-awt`), Phase 2 (command buffer complete, viewport)
- Affected modules: `platform-api`, `render-api`, `platform-awt`, `render-awt`, `test-support`, `examples`
- Requirements: `REQ-FE-001` to `REQ-FE-003`, `REQ-PLATFORM-001`, `REQ-PLATFORM-002`, `REQ-RT-001`, `REQ-RT-008`, `REQ-SPRITE-010`, `REQ-TEST-001`, `REQ-CORE-006`
- Applicable ADRs: [`ADR-0008`](../decisions/0008-awt-desktop-implementer-of-platform-api.md), [`ADR-0018`](../decisions/0018-runtime-thread-events-and-cache-thread-safety.md), [`ADR-0020`](../decisions/0020-no-reflection-factory-registry-and-capability-lookup.md), [`ADR-0022`](../decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md), [`ADR-0026`](../decisions/0026-graphics-pixels-cache-transforms-animation-and-commands.md), [`ADR-0031`](../decisions/0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md), [`ADR-0037`](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md), [`ADR-0040`](../decisions/0040-thread-hand-off-contracts-in-core-and-loop-wiring.md), [`ADR-0045`](../decisions/0045-test-fixtures-with-their-contracts-and-test-support-kit.md), [`ADR-0046`](../decisions/0046-display-state-owned-by-the-platform-and-presentation-geometry.md), [`ADR-0047`](../decisions/0047-render-api-texture-pixel-data-and-shader-resource-keys.md), [`ADR-0049`](../decisions/0049-interface-catalog-and-contract-skeleton.md)
- Open questions involved: none (HiDPI settled by [`ADR-0043`](../decisions/0043-display-v11-scope-default-monitor-device-pixels.md): `platform-awt` reports device pixels)

## Objective

A platform object that owns the window or surface, runs the loop on the right thread, reports lifecycle events and capabilities; a render command buffer executed by a backend; a desktop reference frontend (AWT) and a headless frontend for tests.

## Out of scope

Display modes and filters (see [`display-modes.md`](display-modes.md), [`output-filtering.md`](output-filtering.md)); sprites (they write commands, see [`sprite.md`](sprite.md)); input (see [`input.md`](input.md)); OpenGL and Android (Phase 4).

## Expected behavior

### Platform object (`Platform`, `platform-api`)

- Created by the application with its configuration (logical frame width and height in pixels, initial display request with screen mode, scaling policy and filter chain, logger sink); the application attaches the render backend (ADR-0046); then started with the game loop to run as a `core` `LoopDriver` (ADR-0040). The platform runs the loop on its required thread and returns or blocks as documented per platform.
- `getCapability(Class<T>)` returns the capability or `null`; `hasCapability(Class<?>)`. Modules (renderer, input, audio) are attached explicitly by the application, never discovered.
- Lifecycle events posted to the `core` `Inbox` handed over by the application, as `PlatformEvent`s (each an `InboxEvent`): surface resized (new size in pixels), focus gained or lost, background, foreground, close requested, context lost, context restored. Background pauses the loop, foreground resumes it, through `LoopControl`. A close request stops the loop unless a `CloseListener` vetoes it.
- The platform owns the display state and publishes the presentation geometry (the surface rectangle, in device pixels, where the logical frame is presented, and the scale on each axis), recomputed at the frame boundary of each change; render backends present with it, input adapters invert it (ADR-0046).
- The platform provides the loading worker pool as an implementation of the `core` `WorkerPool` (2 workers on desktop, 1 on Android by default) and the logging sink.

### Render commands (`render-api`)

- `CommandBuffer`: preallocated parallel primitive arrays; one entry per draw: texture handle (`int`), source rectangle (`int` x, y, width, height), position (`float` x, y in logical-frame pixels, the pivot's location), scale x and y (`float`), rotation (`float` degrees, counter-clockwise on screen), pivot (`float` x, y from the source top-left), tint (`int` ARGB, `0xFFFFFFFF` = none), alpha (`float` `[0, 1]`), layer (`int`). Presented by ascending layer, then submission order. Capacity grows by doubling, between frames only; no allocation in steady state.
- `Viewport`: a rectangle of the logical frame in pixels; setting it clips and offsets the following commands until the next viewport; the default viewport is the whole frame.
- Textures are created through the `render-api` texture contract from primitive pixel data: width and height in pixels and an `int[]` of packed straight-alpha ARGB, row-major, stride equal to the width (the layout of the `graphics` `PixelBuffer`, which `render-api` cannot name), and identified by `TextureHandle` (`int`). The array is shared read-only; the backend may keep it as its context-loss copy until release, which is idempotent; upload at first use or on an explicit prepare, on the render thread (ADR-0047).
- Each frame: the scene renders (writes commands), then the backend presents them on the logical frame, then scales it to the surface (ADR-0006). Both calls are made by the loop's render callback as wired by the application or `composition` (ADR-0040).

### Desktop AWT (`platform-awt`, `render-awt`)

- `platform-awt`: a `java.awt` window on the default monitor, sizes reported in device pixels (AWT HiDPI scale converted, `render-awt` presenting at device-pixel resolution, ADR-0043), active rendering (no repaint events), the loop on a dedicated thread named `lionengine-runtime`, AWT events converted into inbox events; `AwtSurface` capability for `render-awt` and `input-awt`.
- `render-awt`: executes the command buffer into an offscreen image of the logical frame size, then scales it to the window with the scaling policy; nearest-neighbor sampling; CPU filters later (ADR-0033).
- Nothing of AWT leaves these modules except through `AwtSurface`.

### Headless frontend (`test-support`)

- A test platform driven manually: the test advances a controllable clock, injects platform events, runs one iteration at a time, and inspects a recording render backend (the list of command entries per frame).
- A fake capability set (each capability can be present or absent).

## Envisaged APIs / contracts

Canonical names (ADR-0037): `Platform`, `PlatformEvent`, `CommandBuffer`, `Viewport`, `TextureHandle`, `ScalingPolicy`; `AwtSurface` in `platform-awt`; `CloseListener`.

Declared by the contract skeleton (ADR-0049, [`contract-skeleton.md`](contract-skeleton.md)): `Platform`, `PlatformEvent`, `CloseListener`, the presentation geometry, `Viewport` and the texture contract; the batches below implement them. Batch 2 adds the render backend contract with `CommandBuffer`, and the backend attachment on `Platform`.

## Batches (order)

1. `Platform` contract, capability lookup, lifecycle events; headless test platform in `test-support`; the scaling function of `render-api`, brought forward from [`display-modes.md`](display-modes.md) batch 3 because the headless platform and `render-awt` need it (`REQ-FE-001`, `REQ-FE-003`, `REQ-RT-008`, `REQ-PLATFORM-002`, `REQ-TEST-001`, `REQ-DISPLAY-007`).
2. Render backend contract, `CommandBuffer`, `Viewport`, textures, backend attachment on `Platform`; recording backend in `test-support` (`REQ-SPRITE-010`).
3. `platform-awt` window and loop thread; `render-awt` implementing the render backend contract, presenting the logical frame with the scaling function; desktop example (`REQ-PLATFORM-001`).

Order changed by the owner on 2026-11-24: the render backend contract comes before the AWT frontend, because the recording backend of batch 1 and `render-awt` of batch 2, as first written, both needed it; nothing temporary is written.

## Lifecycle and concurrency

Runtime thread for all engine calls; platform threads only post to the inbox (ADR-0018). AWT objects are created and used on the runtime thread except where AWT requires the event thread (window creation is done through `EventQueue.invokeAndWait` at start, documented).

## Units and Android/DEX compatibility

Logical-frame pixels, `float` only in command entries. `platform-awt` and `render-awt` are desktop-only (excluded from Android builds).

## Dependencies

As in [`../architecture/modules.md`](../architecture/modules.md): `platform-api` on `core` and `render-api` (ADR-0046); `render-api` on `core` and `resources` (ADR-0047); `platform-awt` on `platform-api`; `render-awt` on `render-api`, `platform-api`, `platform-awt`.

## Tests

- Unit and contract tests with the headless platform: lifecycle events and their effect on the loop, close veto, capability presence and absence, command ordering, viewport clipping, buffer growth without steady-state allocation.
- Platform tests (real display, kept apart): `platform-awt` opens, presents and closes a window; resize keeps the logical frame.

## State (2026-11-25)

Batches 1 to 3 are implemented. Names and details chosen by the batches, within this page, ADR-0006, ADR-0008, ADR-0022, ADR-0026, ADR-0031, ADR-0043, ADR-0045, ADR-0046 and ADR-0047:
- Scaling function: `FrameScaling.of(policy, logical width, logical height, surface width, surface height)` in `render-api` returns the presented rectangle, the scale of each axis and the policy applied (`FIT` after an `INTEGER` fallback). `FIT`: the smaller axis ratio, the presented size rounded to the nearest pixel and never larger than the surface; `INTEGER`: the largest integer factor, `FIT` when the surface is smaller than the logical frame on an axis; `STRETCH`: the whole surface. Centering puts the extra pixel of an odd margin on the right or bottom; an empty surface gives an empty rectangle at `(0, 0)` with scales of `1`. Platforms build their `PresentationGeometry` from it.
- Contract suite (ADR-0045): the abstract `PlatformContract` of the `platform-api` test JAR. A platform binds it by creating a platform, posting events as its threads would, and running one frame on its runtime thread. It checks the creation arguments, a single start, an absent capability, the initial display state, background and foreground, informational events (focus, context), close with and without veto, a listener added during a delivery taking effect after it, resize, a request applied at the next frame boundary, and the `INTEGER` fallback being reported and ending.
- `HeadlessPlatform` (`test-support`): created with the logical frame size, the initial request (applied at once) and the log sink; `start` returns at once, and the test runs each frame with `iterate()` (apply a pending request, then one loop iteration). Events are injected with `post(kind)` and `resize(width, height)`, one reused instance per kind; capabilities are set with `setCapability(type, capability)`. The surface and the display mode of its imaginary monitor start at the logical frame size; every screen mode and display mode is accepted; filters other than `NONE` are not applied until a render backend can be attached (batch 2), which the effective result reports. Close listeners are asked in the order they were added, and the first veto ends the request. The worker pool is the `FakeWorkerPool` of the `core` test JAR with 2 workers.
- A pause or resume delivered through the inbox applies at the frame boundary after the delivery, because the game loop reads its control requests before delivering the inbox: one more step runs after a background event, and the resume discards the time spent in the background.
- The example is a test, `HeadlessGameTest` in the examples, because `test-support` is used in test scope only; `scripts/check-api-index.py` accepts the compiled test classes of the examples for that reason.
- Remaining gap: the effective result is readable at the frame boundary, but no inbox notification is sent yet; it comes with [`display-modes.md`](display-modes.md) batch 1, which also adds the screen-mode capability query and fallbacks.
- Batch 2, `CommandBuffer`: created with the logical frame size (the default viewport) and an initial capacity. `add(texture, x, y, width, height)` starts an entry with the defaults (position `(0, 0)`, scale `1`, rotation `0`, pivot `(0, 0)`, tint `NO_TINT` = opaque white, alpha `1`, layer `0`), and the chained setters `position`, `scale`, `rotation`, `pivot`, `tint`, `alpha` and `layer` complete the last entry. Each setter changes one field of the last entry only, in any order, and a second call replaces the first value. Chosen by the owner's delegation on 2026-11-24 over a fifteen-parameter method (with a Sonar S107 exception, whose limit is 7; the checkstyle parameter count is disabled): every value stays named at the call site, optional fields are omitted, and an `int` passed where a `float` is expected cannot silently swap a source and a position. Positions are relative to the entry's viewport; `setViewport` applies to the entries added after it, `resetViewport` restores the whole frame, `clear` starts a frame. Backends read the entries by rank with `getEntry(rank)` and the getters; the order (ascending layer, then submission) is a stable bottom-up merge sort into preallocated arrays, computed once per change and skipped when the layers are already in order.
- Growth: "capacity grows by doubling, between frames only" is implemented as a doubling in `add` when the buffer is full, never while a backend reads it; once the capacity fits the busiest frame, no frame allocates (allocation test). Doubling at the next clear instead would lose the draws of the frame that overflowed.
- `RenderBackend` (`render-api`): `getTextureStore()`, `isSupported(filter)` (`NONE` always) and `present(commands)`, which draws the entries on a cleared logical frame, each clipped to and offset by its viewport, then scales the frame with the platform's presentation geometry and filters; the buffer is never modified, and the game clears it. A backend reads the geometry from its platform, because `render-api` cannot name `platform-api` types. The clear color is chosen with `render-awt` (batch 3). Contract suite: the abstract `RenderBackendContract` of the `render-api` test JAR (textures, invalid data, filters, presentation leaving the buffer unchanged).
- `Platform.attach(backend)`, before the start: the effective filters are those the backend implements, in order, without `NONE`; the others are skipped and reported (`PlatformContract.testAttach`, `testFiltersOfBackend`). The fallback replacements of ADR-0006 (`HQ2X` to bilinear, bilinear to `NONE`) come with [`output-filtering.md`](output-filtering.md).
- `RecordingBackend` (`test-support`): records each frame as the list of its `RecordedCommand`s in presentation order, keeps the textures with their pixels until released, and implements `NONE` plus the filters chosen by the test.
- Example: `MovingRectangle`, a scene drawing a square crossing the frame at 60 pixels per second, with its headless test `MovingRectangleTest`; batch 3 shows it in a window.
- Batch 3, `AwtPlatform` (`platform-awt`): created like the headless platform (logical frame size, initial request, log sink). `start` opens the window through `EventQueue.invokeAndWait`, runs the loop on the thread `lionengine-runtime`, and returns when the loop stops, after closing the window and the worker pool; a failure of the loop is rethrown by `start`; an interrupted `start` asks the loop to stop and returns; `start` is refused on the AWT event thread. The window is resizable, without title, centered on the default monitor; its canvas starts at the logical frame size in device pixels. Sizes are AWT sizes multiplied by the scale of the canvas' graphics configuration, rounded (ADR-0043). Rendering is active (repaint ignored, two buffers). The loop is paced at the refresh rate of the default monitor, 60 Hz when unknown, because an AWT window has no vertical synchronization; a frame late by more than one period starts a new schedule instead of catching up.
- AWT events: closing the window is a close request; iconify and deiconify are background and foreground; window focus gives the focus events; a resize of the canvas gives the new surface in device pixels. Context events have no AWT source. Each event is a new immutable object, created on the AWT event thread.
- Display state of `platform-awt`, until the display-mode batches: always `WINDOWED`, in the current display mode of the default monitor (AWT's unknown refresh rate `0` and multiple bit depth `-1` become `0`, ADR-0022); a full-window or full-screen request, or another display mode, is reported as degraded. The worker pool has 2 daemon threads `lionengine-worker-N`, started at the first submissions, and is closed with the loop.
- `AwtSurface` (`platform-awt`): an interface with `getCanvas()`. The canvas exists from the creation of the platform, so `render-awt` and `input-awt` can take it before the start; it is shown in the window at the start, and only then has a buffer strategy.
- `AwtRenderBackend` (`render-awt`): created with a platform that has an `AwtSurface`. It draws the entries on an image of the logical frame cleared to opaque black (the clear color chosen by this batch), with nearest-neighbor sampling, then scales that image to the canvas with the presentation geometry, in device pixels (the default transform reset), black outside it. Rotation is counter-clockwise on screen; a tint multiplies the colors (a copy of the source rectangle on the CPU) and, with the alpha, the opacity; an entry whose texture is not alive draws nothing. Textures are images created from the pixel arrays at the first use or at `prepare`; handles are never reused. Only `NONE` until [`output-filtering.md`](output-filtering.md).
- Platform tests (tag `platform`, profile `platform-tests`, [`../testing.md`](../testing.md)): the `PlatformContract` suite bound on `AwtPlatform` with a real window (close, background and foreground dispatched as AWT window events, resize by resizing the window), the window and runtime-thread tests, a screen capture of a presented frame (`AwtRenderBackendWindowTest`), and the desktop example. The other tests of the two modules are headless.
- Desktop example: `MovingRectangleDesktop` shows `MovingRectangle` in a window; closing the window stops the game. The examples module is compiled at the Java 25 level because the desktop modules are (ADR-0044); it is never part of an Android build.
- Gaps found by the batch: no window title or icon in the configuration; no initial window size other than the logical frame (the window is resizable); a desktop game needs nine objects to be wired by hand (`composition` will do it).

## Acceptance criteria

- [ ] Requirements covered; complete Javadoc; headless example and a desktop example (a colored rectangle moving at a fixed speed); API-index entries.
