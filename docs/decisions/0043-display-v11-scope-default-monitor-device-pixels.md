# ADR-0043 — Display scope of version 11: default monitor, device pixels

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner (answer of 2026-11-23 in the architecture-review session); written by the agent
- Related requirements: `REQ-DISPLAY-004`, `REQ-DISPLAY-007`, `REQ-DISPLAY-009`, `REQ-DISPLAY-010`, `REQ-PLATFORM-001`
- Open questions resolved: `OQ-096`
- Supersedes / superseded by: none; completes [ADR-0002](0002-display-modes-resolution-and-output-filtering.md), [ADR-0006](0006-display-scaling-effective-result-and-fallback-orders.md), [ADR-0008](0008-awt-desktop-implementer-of-platform-api.md) and [ADR-0025](0025-filter-implementation-shaders-and-display-settings.md) (all unchanged)

## Context

ADR-0002 left multi-monitor and HiDPI out of the first batch (`OQ-096`), and ADR-0025 requires contracts to assume neither one monitor nor a pixel scale of 1.0. AWT applies HiDPI scaling by default on some systems, so `platform-awt` could report sizes in scaled AWT units instead of device pixels (ADR-0008), which would break `INTEGER` scaling and pixel-exact filters.

## Decision

1. **Monitor.** In version 11 the window, full window and full screen use the system's default monitor (on Android, the device display). There is no API to list or choose monitors.
2. **Device pixels.** The surface size reported by a platform, the display modes and the output surface space of [`../architecture/contracts.md`](../architecture/contracts.md) are in device pixels. `platform-awt` converts AWT sizes to device pixels with the scale of the window's graphics configuration, and `render-awt` presents at device-pixel resolution, so the scaling policy (`FIT`, `INTEGER`, `STRETCH`) and the filters see real pixels.
3. **No scale factor in the game.** The logical frame and the scaling policy already make the game independent of the device pixel density; no density value is exposed in version 11.
4. **Constraint kept.** Contracts still assume neither a single monitor nor a scale of 1.0 (`REQ-DISPLAY-009`), so monitor selection can be added later by a new ADR without breaking them.

## Options considered

- Version 11 scope as above (chosen by the owner): closes the question with no new contract to review.
- Specify monitor selection now: a monitor list and choice in `platform-api` with a scale per monitor; more contracts before the freeze, with no game need yet.
- Keep `OQ-096` open until Phase 4.

## Consequences

- Positive consequences: no open question left; `INTEGER` scaling is exact on HiDPI desktops.
- Costs / constraints: `platform-awt` and `render-awt` must handle the AWT scale transform and test it on a scaled display (a hardware test, `testing.md`); games cannot pick a monitor in version 11.
- Impact on modules and APIs: `platform-awt`, `render-awt` (behavior); no public API change.
- Impact on tests and documentation (including Android/DEX compatibility): `REQ-DISPLAY-010` added; `features/display-modes.md`, `features/platform-and-rendering.md`, `contracts.md`, `open-questions.md`, `traceability.md`, `roadmap.md` updated. Android reports device pixels natively.

## Validation

Owner answer of 2026-11-23 ("Close with v11 scope").
