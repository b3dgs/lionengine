# Feature: screen and display modes

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2 (after the first rendering backend), see `roadmap.md`
- Affected modules: `platform-api`, `platform-awt`, `render-api`, `render-awt`, `render-opengl`, `platform-android`, `test-support`, `examples`
- Requirements: `REQ-DISPLAY-001` to `REQ-DISPLAY-009`, `REQ-PLATFORM-001`, `REQ-PLATFORM-002`
- Applicable ADRs: [`ADR-0002`](../decisions/0002-display-modes-resolution-and-output-filtering.md), [`ADR-0006`](../decisions/0006-display-scaling-effective-result-and-fallback-orders.md), [`ADR-0008`](../decisions/0008-awt-desktop-implementer-of-platform-api.md), [`ADR-0018`](../decisions/0018-runtime-thread-events-and-cache-thread-safety.md), [`ADR-0022`](../decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md), [`ADR-0025`](../decisions/0025-filter-implementation-shaders-and-display-settings.md), [`ADR-0031`](../decisions/0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md), [`ADR-0046`](../decisions/0046-display-state-owned-by-the-platform-and-presentation-geometry.md), [`ADR-0049`](../decisions/0049-interface-catalog-and-contract-skeleton.md), [`ADR-0060`](../decisions/0060-screen-mode-capability-fallback-display-listener-awt-modes.md)
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
- `platform-awt`: `WINDOWED` and `FULL_WINDOW` always, `FULL_SCREEN` when the default monitor's `GraphicsDevice.isFullScreenSupported()`, otherwise it falls back to `FULL_WINDOW`. The mode is realized on the AWT event thread at the frame boundary, when it changes: a decorated window of the logical size, centered; an undecorated window over the monitor bounds; an undecorated full-screen window. The window is hidden and shown again, the canvas gets a new buffer strategy, and its new size arrives as a resize event. The display mode stays the monitor's (batch 2).
- Tests: `DisplayStateTest` (`testPlatformModes`, `testFallbackOrders`, `testListeners`); `PlatformContract` (`testScreenModeCapability`, `testScreenModeFallback`, `testDisplayListener`, `testDisplayListenerOnResize`) bound on the headless platform and, under a display, on `AwtPlatform`; `HeadlessPlatformTest.testFakeScreenModes`; `AwtDisplayTest` (`testFallbackAndDisplayMode`, `testFullScreenSupported`, resize reporting a change); window test `AwtPlatformWindowTest.testScreenModes` (an undecorated window over the monitor bounds, then decorated again, then full screen or its fallback); `ScreenModeExampleTest`; device test `PlatformTest.testScreenModesAndListener` on API 21.

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

- [ ] Requirements covered.
- [ ] API reviewed in the pull request.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example (list capabilities, request a mode, print the effective result), API-index entry, traceability updated.
- [ ] No forbidden coupling.
- [ ] Android/DEX compatibility verified.
