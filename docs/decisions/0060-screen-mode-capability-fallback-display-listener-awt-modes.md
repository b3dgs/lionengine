# ADR-0060 — Screen modes: capability query, fallback in the display state, display listener through the inbox, AWT screen modes

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-DISPLAY-001`, `REQ-DISPLAY-002`, `REQ-DISPLAY-005`, `REQ-DISPLAY-006`, `REQ-DISPLAY-008`
- Open questions resolved: none (details left open by ADR-0002, ADR-0006 and ADR-0046, needed by batch 1 of [`../features/display-modes.md`](../features/display-modes.md))
- Supersedes / superseded by: none; completes [ADR-0006](0006-display-scaling-effective-result-and-fallback-orders.md) (decisions 4 and 5) and [ADR-0046](0046-display-state-owned-by-the-platform-and-presentation-geometry.md) (all unchanged)

## Context

ADR-0002 and ADR-0006 define the three screen modes, their fallback orders and the effective result. The effective result is an immutable value delivered through the inbox at the frame boundary where the change applies, and stays readable as the current state. ADR-0046 makes the platform own the display state: `DisplayState` in `platform-api` resolves a request for every platform, so platforms cannot drift.

The platform batches left two gaps, recorded in [`../features/platform-and-rendering.md`](../features/platform-and-rendering.md):
- The effective result can be read, but no event announces it.
- Neither platform answers which screen modes it supports. The headless platform accepts every mode; `platform-awt` always stays `WINDOWED` and reports any other mode as "not implemented yet".

No document says:
- the shape of the capability query;
- where the fallback is computed;
- how a game receives the effective result;
- which changes are announced;
- what `platform-awt` does for each mode.

What other engines and toolkits do:
- **libGDX** has `Graphics.supportsDisplayModeChange()` and `setFullscreenMode` / `setWindowedMode`, returning `false` on failure.
- **SDL** offers `SDL_SetWindowFullscreen` with a real full-screen flag and a "desktop" flag (a borderless window covering the monitor); the window event `SDL_WINDOWEVENT_SIZE_CHANGED` follows.
- **AWT** offers `GraphicsDevice.isFullScreenSupported()` and `setFullScreenWindow(window)` for exclusive full screen. A borderless window covering the monitor bounds needs an undecorated frame, and a frame can change its decoration only while it is not displayable.

## Decision

1. **Capability query.** `Platform.isSupported(ScreenMode)` tells whether the platform can apply a screen mode. The answer does not change while the platform lives, and at least one mode is supported. A `null` mode is an `IllegalArgumentException`.
2. **Fallback in the display state.** `DisplayState` holds the supported modes (`setSupported(Set<ScreenMode>)`, all modes by default) and resolves the screen mode itself, so every platform falls back the same way.
   - It follows the orders of ADR-0006: `FULL_SCREEN` → `FULL_WINDOW` → `WINDOWED`; `WINDOWED` → `FULL_WINDOW` → `FULL_SCREEN`; `FULL_WINDOW` → `WINDOWED` → `FULL_SCREEN`.
   - A fallback is reported as degraded, with the reason `"<requested> not supported, <effective> used"` placed first among the reasons.
   - `resolve(request, displayMode, reasons)` replaces the former signature that took the screen mode, and the platform applies the effective mode it reads from the result. A request is never refused: the fallback never throws.
3. **Display listener.** `DisplayListener.onDisplayChanged(EffectiveResult)`, added and removed with `Platform.addDisplayListener` and `removeDisplayListener`.
   - The platform posts one inbox event each time it applies a request at a frame boundary, and each time a resize changes the effective result, for example when the `INTEGER` fallback starts or ends. The event carries the result of that change.
   - At delivery, on the runtime thread, the listeners are called in the order they were added, over a snapshot, as for the close listeners.
   - Nothing is posted before the start, because there is no inbox yet. The result stays readable with `getEffectiveResult()`.
4. **Headless platform** (`test-support`): every mode is supported by default. `setScreenModeSupported(mode, supported)` sets a fake capability before the next request; refusing the last supported mode is an `IllegalStateException`.
5. **AWT screen modes** (`platform-awt`):
   - `WINDOWED` and `FULL_WINDOW` are always supported. `FULL_SCREEN` is supported when the default monitor's `GraphicsDevice.isFullScreenSupported()` says so; otherwise it falls back to `FULL_WINDOW`.
   - The mode is realized on the AWT event thread at the frame boundary where it is applied, and only when it changes.
   - `WINDOWED`: a decorated window, sized to the logical frame, centered.
   - `FULL_WINDOW`: an undecorated window covering the bounds of the default monitor.
   - `FULL_SCREEN`: an undecorated window made the full-screen window of the default monitor.
   - Changing the decoration hides and shows the window again; the canvas gets a new buffer strategy, which the render backend reads at each present. The new canvas size arrives as a resize event, as for any resize.
   - The display mode stays the current one of the monitor; changing it is batch 2.

## Options considered

- Capability query:
  - A method per mode on `Platform` (chosen): plain, typed, and the same shape as the filter query of render backends.
  - A capability object looked up with `getCapability` (rejected: screen modes are part of every platform, not an optional capability).
- Where the fallback runs:
  - In `DisplayState` (chosen): one implementation for every platform, as ADR-0046 wants.
  - In each platform (rejected: the orders could drift between platforms).
- Delivery of the result:
  - A listener called at inbox delivery (chosen): the same rule as every platform event, and no dependency of `platform-api` on a game type.
  - A new `PlatformEvent.Kind` (rejected: a platform event carries a surface size, not a result, and the game has no listener for platform events).
  - Polling `getEffectiveResult()` (kept as well; rejected as the only way, because the requirement asks for an event).
- Which changes are announced:
  - Every applied request, and resizes that change the result (chosen): the game learns the outcome of each of its requests, and every change of the effective state.
  - Only changes of the result (rejected: a request giving the same result as before would get no answer).
- AWT `FULL_WINDOW`:
  - Undecorated window over the monitor bounds (chosen): what SDL and most engines call borderless or desktop full screen.
  - Maximized decorated window (rejected: it keeps the title bar and depends on the window manager).

## Consequences

- Positive consequences:
  - A game knows the screen modes it can offer, and learns the outcome of every display request through the inbox, without polling.
  - The two platforms, and every later one, fall back identically.
  - A desktop game can switch between a window, a borderless full window and exclusive full screen.
- Costs / constraints:
  - Every platform implements three more methods.
  - Changing the AWT decoration hides the window for a moment.
  - Exclusive full screen depends on the system: under a virtual display, or on systems where AWT reports no support, `FULL_SCREEN` falls back to `FULL_WINDOW`.
- Impact on modules and APIs:
  - `platform-api`: `DisplayListener`; `Platform.isSupported(ScreenMode)`, `addDisplayListener`, `removeDisplayListener`; `DisplayState.setSupported`, `isSupported`, and `resolve` without the screen-mode argument. The contract suite checks the query, the fallback and the event.
  - `test-support`: `HeadlessPlatform.setScreenModeSupported`.
  - `platform-awt`: the three modes.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: unit tests of the fallback orders; contract tests on both platforms, with fake capabilities on the headless platform; window tests of the AWT modes under a display.
  - Documentation: `features/display-modes.md`, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md` and the traceability.
  - Android/DEX: `EnumSet` and plain Java in `platform-api`, run by the device test of the platform module.

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the batches; reviewed in the pull request of display-modes batch 1.
