# Feature: platform, render commands, desktop AWT frontend and headless test frontend

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 1 (platform object, lifecycle, headless frontend, minimal `platform-awt` and `render-awt`), Phase 2 (command buffer complete, viewport)
- Affected modules: `platform-api`, `render-api`, `platform-awt`, `render-awt`, `test-support`, `examples`
- Requirements: `REQ-FE-001` to `REQ-FE-003`, `REQ-PLATFORM-001`, `REQ-PLATFORM-002`, `REQ-RT-001`, `REQ-RT-008`, `REQ-SPRITE-010`, `REQ-TEST-001`, `REQ-CORE-006`
- Applicable ADRs: [`ADR-0008`](../decisions/0008-awt-desktop-implementer-of-platform-api.md), [`ADR-0018`](../decisions/0018-runtime-thread-events-and-cache-thread-safety.md), [`ADR-0020`](../decisions/0020-no-reflection-factory-registry-and-capability-lookup.md), [`ADR-0022`](../decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md), [`ADR-0026`](../decisions/0026-graphics-pixels-cache-transforms-animation-and-commands.md), [`ADR-0031`](../decisions/0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md), [`ADR-0037`](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md), [`ADR-0040`](../decisions/0040-thread-hand-off-contracts-in-core-and-loop-wiring.md)
- Open questions involved: `OQ-096` (HiDPI: `platform-awt` may report scaled sizes)

## Objective

A platform object that owns the window or surface, runs the loop on the right thread, reports lifecycle events and capabilities; a render command buffer executed by a backend; a desktop reference frontend (AWT) and a headless frontend for tests.

## Out of scope

Display modes and filters (see [`display-modes.md`](display-modes.md), [`output-filtering.md`](output-filtering.md)); sprites (they write commands, see [`sprite.md`](sprite.md)); input (see [`input.md`](input.md)); OpenGL and Android (Phase 4).

## Expected behavior

### Platform object (`Platform`, `platform-api`)

- Created by the application with its configuration (logical frame width and height in pixels, initial screen mode, scaling policy, logger sink), then started with the game loop to run as a `core` `LoopDriver` (ADR-0040). The platform runs the loop on its required thread and returns or blocks as documented per platform.
- `getCapability(Class<T>)` returns the capability or `null`; `hasCapability(Class<?>)`. Modules (renderer, input, audio) are attached explicitly by the application, never discovered.
- Lifecycle events posted to the `core` `Inbox` handed over by the application, as `PlatformEvent`s (each an `InboxEvent`): surface resized (new size in pixels), focus gained or lost, background, foreground, close requested, context lost, context restored. Background pauses the loop, foreground resumes it, through `LoopControl`. A close request stops the loop unless a `CloseListener` vetoes it.
- The platform provides the loading worker pool as an implementation of the `core` `WorkerPool` (2 workers on desktop, 1 on Android by default) and the logging sink.

### Render commands (`render-api`)

- `CommandBuffer`: preallocated parallel primitive arrays; one entry per draw: texture handle (`int`), source rectangle (`int` x, y, width, height), position (`float` x, y in logical-frame pixels, the pivot's location), scale x and y (`float`), rotation (`float` degrees, counter-clockwise on screen), pivot (`float` x, y from the source top-left), tint (`int` ARGB, `0xFFFFFFFF` = none), alpha (`float` `[0, 1]`), layer (`int`). Presented by ascending layer, then submission order. Capacity grows by doubling, between frames only; no allocation in steady state.
- `Viewport`: a rectangle of the logical frame in pixels; setting it clips and offsets the following commands until the next viewport; the default viewport is the whole frame.
- Textures are created from `graphics` pixel buffers (`PixelBuffer`, straight ARGB) through the backend and identified by `TextureHandle` (`int`); upload at first use or on an explicit prepare, on the render thread.
- Each frame: the scene renders (writes commands), then the backend presents them on the logical frame, then scales it to the surface (ADR-0006). Both calls are made by the loop's render callback as wired by the application or `composition` (ADR-0040).

### Desktop AWT (`platform-awt`, `render-awt`)

- `platform-awt`: a `java.awt` window, active rendering (no repaint events), the loop on a dedicated thread named `lionengine-runtime`, AWT events converted into inbox events; `AwtSurface` capability for `render-awt` and `input-awt`.
- `render-awt`: executes the command buffer into an offscreen image of the logical frame size, then scales it to the window with the scaling policy; nearest-neighbor sampling; CPU filters later (ADR-0033).
- Nothing of AWT leaves these modules except through `AwtSurface`.

### Headless frontend (`test-support`)

- A test platform driven manually: the test advances a controllable clock, injects platform events, runs one iteration at a time, and inspects a recording render backend (the list of command entries per frame).
- A fake capability set (each capability can be present or absent).

## Envisaged APIs / contracts

Canonical names (ADR-0037): `Platform`, `PlatformEvent`, `CommandBuffer`, `Viewport`, `TextureHandle`, `ScalingPolicy`; `AwtSurface` in `platform-awt`; `CloseListener`.

## Batches (order)

1. `Platform` contract, capability lookup, lifecycle events; headless test platform and recording backend in `test-support` (`REQ-FE-001`, `REQ-FE-003`, `REQ-RT-008`, `REQ-PLATFORM-002`, `REQ-TEST-001`).
2. `platform-awt` window and loop thread; `render-awt` clearing and presenting the logical frame with `FIT` scaling (`REQ-PLATFORM-001`).
3. `CommandBuffer`, `Viewport`, textures; `render-awt` executes them (`REQ-SPRITE-010`).

## Lifecycle and concurrency

Runtime thread for all engine calls; platform threads only post to the inbox (ADR-0018). AWT objects are created and used on the runtime thread except where AWT requires the event thread (window creation is done through `EventQueue.invokeAndWait` at start, documented).

## Units and Android/DEX compatibility

Logical-frame pixels, `float` only in command entries. `platform-awt` and `render-awt` are desktop-only (excluded from Android builds).

## Dependencies

As in [`../architecture/modules.md`](../architecture/modules.md): `platform-api` and `render-api` on `core`; `platform-awt` on `platform-api`; `render-awt` on `render-api`, `platform-api`, `platform-awt`.

## Tests

- Unit and contract tests with the headless platform: lifecycle events and their effect on the loop, close veto, capability presence and absence, command ordering, viewport clipping, buffer growth without steady-state allocation.
- Platform tests (real display, kept apart): `platform-awt` opens, presents and closes a window; resize keeps the logical frame.

## Acceptance criteria

- [ ] Requirements covered; complete Javadoc; headless example and a desktop example (a colored rectangle moving at a fixed speed); API-index entries.
