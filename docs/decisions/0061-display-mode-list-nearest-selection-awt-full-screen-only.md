# ADR-0061 — Display modes: list of the platform, nearest selection in the display state, changed by AWT only in full screen

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-DISPLAY-003`, `REQ-DISPLAY-008`
- Open questions resolved: none (the distance used for "nearest" display modes, left `TO BE DEFINED` by ADR-0006, and details left open by ADR-0022 and ADR-0046, needed by batch 2 of [`../features/display-modes.md`](../features/display-modes.md))
- Supersedes / superseded by: none; completes [ADR-0006](0006-display-scaling-effective-result-and-fallback-orders.md) (decision 5), [ADR-0046](0046-display-state-owned-by-the-platform-and-presentation-geometry.md) and [ADR-0060](0060-screen-mode-capability-fallback-display-listener-awt-modes.md) (all unchanged)

## Context

ADR-0006 orders the display-mode fallback: the exact mode, then the nearest size, then the nearest refresh rate, then the nearest color depth. It leaves the distance used for "nearest" `TO BE DEFINED`. ADR-0022 and ADR-0033 define `DisplayMode`: width and height in device pixels, refresh rate in Hz, depth in bits, `0` when unknown or multiple. ADR-0043 limits version 11 to the default monitor. ADR-0060 puts the screen-mode fallback in the shared `DisplayState`, so platforms cannot drift.

Batch 1 left the display mode as the monitor's current one on `platform-awt`, reported as "not implemented yet", while the headless platform accepted any mode. No document says:
- how a game lists the display modes;
- what "nearest" means, and how an unknown requested value is compared;
- when a mode that differs from the request is reported as degraded;
- in which screen modes `platform-awt` changes the display mode, and how it restores it.

What other engines and toolkits do:
- **libGDX** has `Graphics.getDisplayModes()` and `setFullscreenMode(DisplayMode)`; a display mode applies only to full screen.
- **SDL** has `SDL_GetDisplayMode` and `SDL_GetClosestDisplayMode`, which picks the closest size first, then the refresh rate, then the format; the mode applies to an exclusive full-screen window only.
- **AWT** has `GraphicsDevice.getDisplayModes()` and `setDisplayMode`, allowed only when `isDisplayChangeSupported()` returns `true`, which needs exclusive full screen. Leaving full screen with `setFullScreenWindow(null)` restores the original mode on most systems.

## Decision

1. **List.** `Platform.getDisplayModes()` returns the display modes of the default monitor the platform can select: unmodifiable, in the order the system reports them, without duplicate, never empty, fixed while the platform lives.
2. **Selection in the display state.** `DisplayState` holds the list (`setDisplayModes`, `getDisplayModes`) and selects a mode with `selectDisplayMode(requested, current)`, so every platform selects the same way.
   - The selected mode minimizes, in this order: the size distance (the sum of the absolute width and height differences), the refresh-rate difference, the depth difference. Ties go to the first mode in the list. An offered mode equal to the request is thus selected.
   - An unknown (`0`) requested refresh rate or depth is compared to the current mode's value, so the monitor keeps it when it can.
   - A `null` requested mode keeps the current one.
3. **Match and reason.** A mode matches a request when the width and height are equal, and the refresh rate and depth are equal where the requested value is known (`DisplayState.matches`).
   - `resolve` reports a display mode given by the platform that does not match the request as degraded, with the reason `"Display mode <requested> not available, <effective> used"`, written `800x600 60Hz 32bits`.
   - This reason comes after the screen-mode fallback and before the platform's own reasons, which explain why.
   - An unknown requested value is therefore never a degradation.
4. **Headless platform** (`test-support`): the fake monitor offers one mode, of the logical frame size with an unknown refresh rate and depth. `HeadlessPlatform.setDisplayModes(modes)` sets other fake modes before the next request: the current mode is kept if listed, otherwise it becomes the first. A requested mode is selected among them in every screen mode.
5. **AWT display modes** (`platform-awt`):
   - The list is the default monitor's `GraphicsDevice.getDisplayModes()`, converted (ADR-0022), or its current mode if the system lists none.
   - The display mode changes only in `FULL_SCREEN`, to the offered mode nearest to the request, and only when `isDisplayChangeSupported()` says so. It is set on the AWT event thread at the frame boundary, after the window becomes the full-screen window.
   - In the other screen modes the desktop mode is kept: the monitor's mode when the platform was created. A requested mode that does not match it gets the reason "Display mode change needs FULL_SCREEN, the desktop mode is kept".
   - A refusal of the system keeps the current mode, reported as "Display mode change refused by the system, the current mode is kept".
   - The window reads the mode in effect after each realization, and the result reports that mode.
   - The desktop mode is set back before the window leaves full screen and when it is disposed.
   - The loop is paced at the refresh rate of the mode in effect.

## Options considered

- Distance of "nearest":
  - Lexicographic: size (sum of the width and height differences), then rate, then depth (chosen): the order of ADR-0006 read literally, the same as SDL's closest mode, and integer arithmetic only.
  - A weighted sum of every field (rejected: weights are arbitrary, and a closer refresh rate could win over a closer size against ADR-0006).
  - Size by area or by aspect ratio (rejected: two modes of the same area can differ widely in shape; the sum of differences keeps both sides close).
- Unknown requested value:
  - Compared to the current mode's value (chosen): a request for "1280x720, any rate" keeps the monitor's rate when it can.
  - Matched by any value, first in the list (rejected: the result would depend on the system's listing order).
- Where AWT changes the display mode:
  - Only in `FULL_SCREEN` (chosen): AWT allows it only there, and libGDX and SDL do the same.
  - In every screen mode (rejected: AWT refuses it outside exclusive full screen, and changing the desktop mode under a window disturbs every other application).
- Restoring the desktop mode:
  - Set back explicitly before leaving full screen and on disposal (chosen): it does not depend on each system's `setFullScreenWindow(null)`.
  - Left to AWT (rejected: the behavior is not specified by the AWT documentation).

## Consequences

- Positive consequences:
  - A game offers the monitor's modes in a settings menu, and a request never fails: it gets the nearest mode, with the reason when it differs.
  - Every platform selects the same way, and a test fixes the selection with fake modes.
  - A desktop game changes the resolution and refresh rate in exclusive full screen, and the desktop gets its mode back.
- Costs / constraints:
  - Every platform implements one more method.
  - A display mode has no effect in a window or a full window on the desktop.
  - The real mode change is not tested automatically, because changing the monitor of a build machine is unsafe; the window test requests the desktop size only.
- Impact on modules and APIs:
  - `platform-api`: `Platform.getDisplayModes()`; `DisplayState.setDisplayModes`, `getDisplayModes`, `selectDisplayMode`, `matches`, and the display-mode reason of `resolve`. The contract suite checks the list and a mode that is not listed.
  - `test-support`: `HeadlessPlatform.setDisplayModes`; a requested mode is now selected among the offered ones instead of accepted as is.
  - `platform-awt`: the list, the change in full screen and the restoration.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: unit tests of the match and of the selection order; contract tests on both platforms; headless tests with fake modes; unit tests of the AWT targets and reasons; a window test under a display.
  - Documentation: `features/display-modes.md`, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md` and the traceability.
  - Android/DEX: `ArrayList`, `Collections.unmodifiableList`, `List.of` (desugared, verified) and plain arithmetic in `platform-api`, run by the device test of the platform module.

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the batches; reviewed in the pull request of display-modes batch 2.
