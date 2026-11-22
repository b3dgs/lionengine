# ADR-0006 — Display scaling policy, effective-result report and fallback orders

- Status: PROPOSED
- Date: 2026-11-22
- Decision makers: project owner (decisions of issue #859, 2026-11-22). This ADR stays `PROPOSED` until explicitly approved.
- Related requirements: `REQ-DISPLAY-002`, `REQ-DISPLAY-004`, `REQ-DISPLAY-005`, `REQ-DISPLAY-007`, `REQ-DISPLAY-008`, `REQ-FILTER-001`, `REQ-FILTER-002`, `REQ-LOOP-006`
- Open questions resolved: `OQ-091`, `OQ-120` (creates `OQ-122`)
- Supersedes / superseded by: none. Extends [ADR-0002](0002-display-modes-resolution-and-output-filtering.md) (ACCEPTED), which left these points `TO BE DEFINED`; ADR-0002 is not modified.

## Context

ADR-0002 decided best-effort negotiation of screen modes, display modes and output filters, with the effective result reported. It left open the scaling policy set, its default and its owner (`OQ-091`), the form of the effective-result report, and the fallback orders required by `REQ-DISPLAY-002` and `REQ-FILTER-002` (`OQ-120`). The display contracts cannot be written without them.

## Decision

Decisions taken by the owner (issue #859):

1. **Scaling policies:** `FIT` (aspect-preserving, the default), `INTEGER` (opt-in, integer scale factors only) and `STRETCH`. When the output surface is smaller than the logical frame, `INTEGER` falls back to `FIT`, and this is reported.
2. **Scaling owner:** the policy type lives in `render-api`; the output surface size is supplied by `platform-api`. The request is best-effort and the effective policy is reported.
3. **Filter chain and scaling:** scaling filters (`NONE`, which samples nearest-neighbor, bilinear, `HQ2X`) define how the logical frame is resampled to the output size; post filters (CRT) run on the scaled output. No filter changes the scaling policy. Filtering still applies from the logical frame to the output surface only, as ADR-0002 decided.
4. **Effective-result report:** applying a request returns the accepted request and never blocks the update step. The effective result is an immutable value holding, per field, the requested and the effective value, a `degraded` flag and a reason. It is delivered at the frame boundary where the change is applied, and the current effective result can be read at any time. The delivery mechanism follows the future `core` event contract (`OQ-122`).
5. **Fallback orders:**
   - Screen mode: `FULL_SCREEN` → `FULL_WINDOW` → `WINDOWED`; `WINDOWED` → `FULL_WINDOW` → `FULL_SCREEN`; `FULL_WINDOW` → `WINDOWED` → `FULL_SCREEN`.
   - Display mode: the exact mode, then the nearest size, then the nearest refresh rate, then the nearest color depth.
   - Filters, per filter: CRT is skipped; `HQ2X` falls back to bilinear; bilinear falls back to `NONE`. Each fallback is reported.

Not decided (stay `TO BE DEFINED`): the `core` event contract (`OQ-122`); the distance used for "nearest" display modes; Java type, method and package names.

## Options considered

### Scaling policy set

- A (retained): `FIT` default, `INTEGER` opt-in, `STRETCH`. Covers pixel-art and general games; the default never distorts.
- B: `INTEGER` as default. Pixel-exact, but leaves large borders on many surfaces and fails when the surface is smaller than the logical frame.

### Report delivery

- A (retained): asynchronous effective result at the frame boundary, plus a readable current state. Compatible with `REQ-DISPLAY-005` and with an update step that never blocks.
- B: synchronous result returned by apply. Impossible without blocking until the next frame boundary.

## Consequences

- Positive consequences: display contracts can be specified; the same fallback behavior on every platform; testable headless with fake capabilities.
- Costs / constraints: every platform implementation must compute and report the effective value per field; two filter categories to document; delivery depends on the `core` event contract (`OQ-122`).
- Impact on modules and APIs: `render-api` owns the policy type and the filter categories; `platform-api` supplies the surface size and applies the mode fallback. No new module or dependency. No AWT, OpenGL or Android type (`REQ-DISPLAY-006`).
- Impact on tests and documentation (including Android/DEX compatibility): unit tests of the policy math (including the `INTEGER` fallback) and of the fallback orders; contract suites in `test-support` for the report (requested vs effective, `degraded`, delivery at the frame boundary, no blocking). Android may refuse some modes or rates: this is reported, never thrown. The game-loop invariant is untouched: nothing here feeds back into simulation state (`REQ-LOOP-006`).

## Validation

Not approved yet. The decisions were given by the owner on issue #859; the ADR remains `PROPOSED` until the owner explicitly accepts it.
