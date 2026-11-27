# Feature: screen and display modes

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2 (after the first rendering backend), see `roadmap.md`
- Affected modules: `platform-api`, `platform-awt`, `render-api`, `render-awt`, `render-opengl`, `platform-android`, `test-support`, `examples`
- Requirements: `REQ-DISPLAY-001` to `REQ-DISPLAY-009`, `REQ-PLATFORM-001`, `REQ-PLATFORM-002`
- Applicable ADRs: [`ADR-0002`](../decisions/0002-display-modes-resolution-and-output-filtering.md), [`ADR-0006`](../decisions/0006-display-scaling-effective-result-and-fallback-orders.md), [`ADR-0008`](../decisions/0008-awt-desktop-implementer-of-platform-api.md), [`ADR-0018`](../decisions/0018-runtime-thread-events-and-cache-thread-safety.md), [`ADR-0022`](../decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md), [`ADR-0025`](../decisions/0025-filter-implementation-shaders-and-display-settings.md), [`ADR-0031`](../decisions/0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md), [`ADR-0046`](../decisions/0046-display-state-owned-by-the-platform-and-presentation-geometry.md), [`ADR-0049`](../decisions/0049-interface-catalog-and-contract-skeleton.md), [`ADR-0060`](../decisions/0060-screen-mode-capability-fallback-display-listener-awt-modes.md), [`ADR-0061`](../decisions/0061-display-mode-list-nearest-selection-awt-full-screen-only.md)
- Open questions involved: none (multi-monitor and HiDPI scope of version 11: [`ADR-0043`](../decisions/0043-display-v11-scope-default-monitor-device-pixels.md))

## Objective

Let a game request a screen mode, a display mode and a logical resolution through one platform-independent API, and read what the platform actually applied.

## Out of scope

Output filtering (see [`output-filtering.md`](output-filtering.md)); monitor selection (not in version 11: default monitor, device pixels, ADR-0043, `REQ-DISPLAY-010`); persistence of user settings (game responsibility, ADR-0025); a "required" request flag (ADR-0002, decision 2A).

## Expected behavior

- Capability query for `WINDOWED`, `FULL_WINDOW`, `FULL_SCREEN`.
- Unsupported screen mode: fallback `FULL_SCREEN` → `FULL_WINDOW` → `WINDOWED`, `WINDOWED` → `FULL_WINDOW` → `FULL_SCREEN`, `FULL_WINDOW` → `WINDOWED` → `FULL_SCREEN`; effective mode and reason reported; never an exception.
- Enumerate and select display modes (width, height in pixels; refresh rate in Hz; color depth in bits). An unknown or multiple rate or depth is `0`; `isRefreshRateKnown()` and `isBitDepthKnown()` tell it; `platform-awt` maps AWT's `0` and `-1` to `0`. Selection falls back to the exact mode, then the nearest size, then rate, then depth.
- Logical resolution independent of the display mode; scaling policy `FIT` (default), `INTEGER` (opt-in, falls back to `FIT` when the surface is smaller than the logical frame) or `STRETCH`.
- The platform owns these settings (ADR-0046): its configuration and `DisplayRequest` carry the screen mode, display mode, scaling policy and filter chain, and `EffectiveResult` reports them all. The presentation geometry (the surface rectangle, in device pixels, where the logical frame is presented, and the scale on each axis) is recomputed at the frame boundary of every change and read by render backends and input adapters.
- Apply returns the accepted request without blocking; the effective result (requested vs effective per field, `degraded` flag, reason) is delivered through the inbox at the frame boundary where it is applied, and stays readable as the current state.
- A surface resize is a platform event: the logical frame never changes, the scaling is recomputed at the next frame boundary (ADR-0031).
- Simulation state is never touched.

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Display modes); Canonical type names in [ADR-0037](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md); Java signatures are written by the batch under `conventions.md`: screen-mode enum, display-mode value type, capability query, request and effective-result value types, scaling-policy enum (in `render-api`). Declared by the contract skeleton (ADR-0049, [`contract-skeleton.md`](contract-skeleton.md)): `ScreenMode`, `DisplayMode`, `DisplayRequest`, `EffectiveResult`, the presentation geometry and `ScalingPolicy`; the batches below implement the behavior, including the scaling function of `render-api` (batch 3).

## Batches (order)

1. Value types, capability query, screen modes with fallback and effective result through the inbox (`REQ-DISPLAY-001`, `REQ-DISPLAY-002`, `REQ-DISPLAY-005`, `REQ-DISPLAY-006`, `REQ-DISPLAY-008`).
2. Display-mode enumeration and selection on `platform-awt` (`REQ-DISPLAY-003`).
3. Scaling policies and resize handling (`REQ-DISPLAY-004`, `REQ-DISPLAY-007`, `REQ-PLATFORM-002`). The scaling function (`FrameScaling` of `render-api`) and the resize of the headless platform were brought forward by [`platform-and-rendering.md`](platform-and-rendering.md) batch 1 (2026-11-24); this batch keeps the resize handling of `platform-awt`.
4. Review of the settings and HiDPI constraints (`REQ-DISPLAY-009`).

## State (2026-11-26)

Batch 1 (2026-11-26), screen modes and the display event, with the example `ScreenModeExample` and its API-index row. The details this page leaves open follow ADR-0060 (owner delegation), within this page, ADR-0006 and ADR-0046:
- Capability query: `Platform.isSupported(ScreenMode)`, fixed while the platform lives, at least one mode supported, `null` refused.
- Fallback: in the shared `DisplayState` (`setSupported(modes)`, all by default; `isSupported`; `selectScreenMode`), so every platform falls back the same way, in the orders of ADR-0006. `resolve(request, displayMode, reasons)` now selects the screen mode itself (the former argument is gone) and puts the fallback first among the reasons: `"FULL_SCREEN not supported, FULL_WINDOW used"`; the platform realizes the effective mode it reads from the result. A request is never refused.
- Display event: `DisplayListener.onDisplayChanged(EffectiveResult)`, added and removed with `Platform.addDisplayListener` and `removeDisplayListener`. The platform posts one inbox event each time it applies a request at a frame boundary, and each time a resize changes the effective result (the `INTEGER` fallback starting or ending); at delivery the listeners are called in order over a snapshot (`DisplayState.addListener`, `removeListener`, `post(inbox)`). Nothing is posted before the start; the result stays readable with `getEffectiveResult()`.
- Headless platform: every mode supported by default; `HeadlessPlatform.setScreenModeSupported(mode, supported)` sets a fake capability (refusing the last mode).
- `platform-awt`: `WINDOWED` and `FULL_WINDOW` always, `FULL_SCREEN` when the default monitor's `GraphicsDevice.isFullScreenSupported()`, otherwise it falls back to `FULL_WINDOW`. The mode is realized on the AWT event thread at the frame boundary, when it changes: a decorated window of the logical size, centered; an undecorated window over the monitor bounds; an undecorated full-screen window. The window is hidden and shown again, the canvas gets a new buffer strategy, and its new size arrives as a resize event. The display mode stayed the monitor's until batch 2.
- Tests: `DisplayStateTest` (`testPlatformModes`, `testFallbackOrders`, `testListeners`); `PlatformContract` (`testScreenModeCapability`, `testScreenModeFallback`, `testDisplayListener`, `testDisplayListenerOnResize`) bound on the headless platform and, under a display, on `AwtPlatform`; `HeadlessPlatformTest.testFakeScreenModes`; `AwtDisplayTest` (`testFallbackAndDisplayMode`, `testFullScreenSupported`, resize reporting a change); window test `AwtPlatformWindowTest.testScreenModes` (an undecorated window over the monitor bounds, then decorated again, then full screen or its fallback); `ScreenModeExampleTest`; device test `PlatformTest.testScreenModesAndListener` on API 21.

Batch 2 (2026-11-26), display-mode list and selection, with the example `DisplayModeExample` and its API-index row. The details this page leaves open follow ADR-0061 (owner delegation), within this page, ADR-0006, ADR-0022 and ADR-0046:
- List: `Platform.getDisplayModes()`, the modes of the default monitor, unmodifiable, in the system's order, without duplicate, never empty, fixed while the platform lives.
- Selection: in the shared `DisplayState` (`setDisplayModes`, `getDisplayModes`, `selectDisplayMode(requested, current)`): the smallest size distance (sum of the width and height differences), then refresh-rate difference, then depth difference, then the first listed; an unknown requested rate or depth is compared to the current mode's; a `null` request keeps the current mode. `DisplayState.matches` compares a mode to a request, an unknown requested value matching any; `resolve` reports a mode that does not match as `"Display mode 800x600 75Hz 32bits not available, 800x600 60Hz 32bits used"`, after the screen-mode fallback and before the platform's reasons.
- Headless platform: one fake mode of the logical size, unknown rate and depth; `HeadlessPlatform.setDisplayModes(modes)` sets others (the current mode kept if listed, otherwise the first); a requested mode is selected among them. A requested mode is no longer accepted as is.
- `platform-awt`: the list of `GraphicsDevice.getDisplayModes()`, or the current mode if none. The display mode changes only in `FULL_SCREEN`, when `isDisplayChangeSupported()`, to the selected mode, on the AWT event thread at the frame boundary; otherwise the desktop mode is kept ("Display mode change needs FULL_SCREEN, the desktop mode is kept"), and a refusal keeps the current mode ("Display mode change refused by the system, the current mode is kept"). The result reports the mode in effect, read after each realization. The desktop mode is set back before the window leaves full screen and when it is disposed; the loop is paced at the refresh rate of the mode in effect.
- Tests: `DisplayStateTest` (`testMatches`, `testDisplayModes`, `testSelectDisplayMode`, `testDisplayModeReason`); `PlatformContract` (`testDisplayModes`, `testDisplayModeNotListed`) on the headless platform and, under a display, on `AwtPlatform`; `HeadlessPlatformTest.testFakeDisplayModes`; `AwtDisplayTest.testFullScreenDisplayMode`; window test `AwtPlatformWindowTest.testDisplayModes` (the desktop mode kept in a window, then the desktop size requested in full screen, so the monitor of the machine is not changed); `DisplayModeExampleTest`; device test `PlatformTest.testDisplayModes` on API 21. The real change of a monitor's mode is not tested automatically.

Batch 3 (2026-11-26), scaling policies and resize handling: no new code, every part was delivered by earlier batches; this batch checks the coverage and records it.
- Scaling: `FrameScaling.of(policy, logical size, surface size)` in `render-api` (platform and rendering batch 1) computes `FIT`, `INTEGER` and `STRETCH`, and the `INTEGER` fallback to `FIT` on a surface smaller than the logical frame; the shared `DisplayState` resolves the effective policy, reports the fallback as `"INTEGER scaling falls back to FIT: the surface is smaller than the logical frame"` and recomputes the presentation geometry, so every platform scales the same way. World units are never touched.
- Resize on `platform-awt`: a resize of the canvas posts `SURFACE_RESIZED` with the new size in device pixels (ADR-0043); at its delivery on the runtime thread the platform sets the surface, resolves the request again and recomputes the geometry, and posts the result to the display listeners when it changed (ADR-0060). The logical frame never changes. The render backend reads the geometry at each present.
- Tests: `FrameScalingTest` (each policy, the fallback, an empty surface); `DisplayStateTest.testIntegerFallback`; `PlatformContract` (`testResize`, `testIntegerFallback`, `testDisplayListenerOnResize`) bound on the headless platform and, under a display, on `AwtPlatform`, whose binding resizes the real window; `AwtDisplayTest.testResize`; `AwtWindowEventsTest` (resize in device pixels); `AwtRenderBackendWindowTest` (a presented frame captured on screen).
- `REQ-PLATFORM-002` stays partial: rendering-context loss and recovery have no AWT source, and come with the OpenGL and Android backends.

Batch 4 (2026-11-26), review of the display settings and the HiDPI constraints (`REQ-DISPLAY-009`, ADR-0025 decisions 3 and 4, ADR-0043). No code change; findings:
- Settings persistence is the game's: the engine exposes only immutable values (`DisplayRequest` and `EffectiveResult` are records, the filter chain an unmodifiable copy) and has no storage code. No main source of `platform-*`, `render-*` or `test-support` writes a file or uses `java.util.prefs` or `Properties`. A game saves the `requested()` value it chose and applies it again at the next start.
- No single-monitor assumption: `Platform`, `DisplayState`, `DisplayRequest`, `EffectiveResult` and `PresentationGeometry` neither name nor count monitors. The words "default monitor" in their documentation state the version 11 scope of ADR-0043, so a monitor choice can be added later by a new ADR without breaking them. On `platform-awt` the window opens, fills and goes full screen on the default monitor; its events convert sizes with the scale of the canvas' own graphics configuration, so a window moved to another monitor still reports device pixels.
- No scale-1.0 assumption: surface sizes, display modes and the presentation geometry are in device pixels. `AwtWindowEventsTest` converts at the scales 1.0, 1.25, 1.5 and 2.0; `render-awt` draws with an identity transform, at device-pixel resolution; no density value is exposed (ADR-0043, decision 3).
- Gap found: the unit of the AWT display modes on a scaled macOS display is not verified, recorded as `OQ-174` with a check and a proposal. Until it is answered, sizes are taken as AWT reports them, which is right on Windows and Linux.

## Lifecycle and concurrency

Requests may come from the runtime thread at any time; they apply at the next frame boundary. The platform posts the effective result to the inbox; it is delivered on the runtime thread (ADR-0018). Native windows belong to the platform implementation.

## Units and Android/DEX compatibility

Pixels, Hz, bits; world units unchanged. Own value types with `int` fields only. Android specifics are isolated in `platform-android`; refresh-rate and depth selection may be unavailable there and are then reported in the effective result. Check per `android-compatibility.md` in the batch.

## Dependencies

`platform-api` on `core` and `render-api` (ADR-0046); scaling policy and scaling function in `render-api`. Forbidden: AWT, OpenGL or Android types in `core`, `platform-api` or `render-api`.

## Tests

- Unit tests: value types and the `0` convention, fallback orders, scaling math for each policy including the `INTEGER` fallback.
- Contract tests (`platform-api` test JAR, ADR-0045, run on the headless platform of `test-support` and on each platform): effective result and reason, no exception on a missing capability, frame-boundary application through the inbox, resize.
- Integration tests: headless, fake capabilities.
- Platform tests: real window per frontend, identified separately.

## Acceptance criteria

- [x] Requirements covered (`REQ-DISPLAY-001` to `REQ-DISPLAY-009`; `REQ-PLATFORM-002` partial until a backend can lose its context).
- [x] API proposed in the pull requests of the batches (#938, #939) and merged under the owner's delegation (ADR-0060, ADR-0061).
- [x] Automated tests added and passing.
- [x] Complete Javadoc.
- [x] Headless example (list capabilities, request a mode, print the effective result), API-index entry, traceability updated.
- [x] No forbidden coupling.
- [x] Android/DEX compatibility verified.
