# ADR-0063 — CPU filters of render-awt: nearest and bilinear by Java2D, Scale2x standing in for HQ2X at a scale of 2 or more

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-FILTER-004`, `REQ-FILTER-001`, `REQ-FILTER-002`
- Open questions resolved: none (details left open by ADR-0025 and ADR-0033, needed by batch 2 of [`../features/output-filtering.md`](../features/output-filtering.md))
- Supersedes / superseded by: none; completes [ADR-0025](0025-filter-implementation-shaders-and-display-settings.md) (decision 1), [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md) and [ADR-0062](0062-filter-chain-shape-and-per-filter-fallback.md) (all unchanged)

## Context

ADR-0025 makes the project write its own filters, with no third-party filter library. `HQ2X` ships only once the license of its algorithm is recorded (`OQ-148`); until then a neighbour-based scaler such as `Scale2x` stands in.

ADR-0033 has `render-awt` implement `NONE` (nearest neighbor), bilinear and `Scale2x` on the CPU, and report CRT as unsupported. ADR-0062 gives the chain shape (one scaling filter, then post filters) and the fallback the platform resolves against the backend.

`render-awt` so far presents the logical frame with nearest-neighbor sampling only, and supports only `NONE`. No document says:
- which identifier `Scale2x` answers to, and whether the stand-in is visible;
- how a 2× scaler meets a presentation scale that is not 2;
- whether bilinear is written by hand or drawn by Java2D;
- how the scaling is tested without a window.

What others do:
- **Scale2x** (AdvMAME2x) is the public algorithm of Andrea Mazzoleni: each pixel becomes four, from its four neighbours. RetroArch and ScummVM use it as a cheap pixel-art scaler, followed by an ordinary resampling to the output size.
- **Java2D** draws a scaled image with nearest-neighbor or bilinear interpolation, chosen by `RenderingHints.KEY_INTERPOLATION`.

## Decision

1. **Supported filters.** `render-awt` implements `NONE`, `BILINEAR` and `HQ2X`, and no post filter (CRT and custom filters are reported unsupported, so they are skipped). `HQ2X` is drawn by `Scale2x` until the license record of `OQ-148`; the class documentation, the feature page and the engine features say so. The effective result names `HQ2X`, the identifier the game asked for, because no other scaler is applied in its place.
2. **Nearest and bilinear by Java2D.** The logical frame is drawn into the presentation geometry with the interpolation hint `NEAREST_NEIGHBOR` for `NONE` and `BILINEAR` for `BILINEAR`. Java2D is part of the JDK, not a third-party filter library (ADR-0025).
3. **Scale2x.** Own implementation of AdvMAME2x over packed `int` pixels, as a `CpuFilter`:
   - for each pixel `E` with neighbours `B` (above), `D` (left), `F` (right) and `H` (below), the edges repeating `E`: when `B != H` and `D != F`, the four target pixels are `D` if `D == B`, `F` if `B == F`, `D` if `D == H`, `F` if `H == F`, otherwise `E`;
   - otherwise all four are `E`;
   - the target must be twice the source on each axis.

   At present, when the presentation scale is at least 2 on both axes, the logical frame is scaled twice into a buffer kept by the backend, then drawn to the geometry with nearest-neighbor sampling. Below 2, the doubled pixels would be sampled down again, so the frame is drawn with nearest-neighbor sampling as for `NONE`.
4. **No allocation per frame.** The pixel arrays and the doubled image are created at the first `HQ2X` frame and reused. Pixels are copied with the raster's `getDataElements` and `setDataElements` into those arrays, which keeps Java2D's acceleration of the logical frame.
5. **Tests without a window.** The scaling step is a package-private presenter that draws on any `Graphics2D`. Golden images of each filter are therefore checked on an off-screen image, headless; a window test checks one presented filter on screen.

## Options considered

- The identifier of the stand-in:
  - `HQ2X` (chosen): what ADR-0025 and ADR-0033 call "standing in for `HQ2X`"; a game asking for `HQ2X` gets the nearest available pixel-art scaler, and the real one replaces it later without a change in games.
  - A new standard identifier `SCALE2X` (rejected: a public identifier outside `REQ-FILTER-001`, to keep after `HQ2X` arrives).
  - `HQ2X` unsupported, so it falls back to bilinear (rejected: `Scale2x` would never be used).
- Scale2x below a scale of 2:
  - Nearest-neighbor instead (chosen): the doubled pixels would be sampled down again, and the result would differ from the frame without being smoother.
  - Always `Scale2x`, then resampling (rejected for scales below 2, for that reason).
- Bilinear:
  - Java2D interpolation (chosen): exact, fast, and accelerated where Java2D is.
  - A hand-written `CpuFilter` (rejected: slower for the same result, and a copy of the frame at each present).

## Consequences

- Positive consequences:
  - Desktop games get smooth and pixel-art scaling.
  - A request for `HQ2X` works everywhere; the CRT request is skipped with a reason, as decided.
  - The filters are tested headless, pixel by pixel.
- Costs / constraints:
  - Until `OQ-148`, `HQ2X` on the desktop looks like `Scale2x`, not like `hq2x`.
  - Below a scale of 2, `HQ2X` looks like `NONE`.
  - The doubled image costs four times the logical frame in memory once `HQ2X` is used.
- Impact on modules and APIs: `render-awt` only: `isSupported(OutputFilter)` and the presentation; no public type is added.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: unit tests of `Scale2x` on reference images and of its allocation; headless golden images of the three filters; a window test of a presented filter.
  - Documentation: `features/output-filtering.md`, `contracts.md`, `engine-features.md` and the traceability.
  - Android/DEX: none (`render-awt` is desktop only).

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the batches; reviewed in the pull request of output-filtering batch 2.
