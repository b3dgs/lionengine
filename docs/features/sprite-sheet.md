# Feature: sprite sheet (foundation)

**Status: PROPOSED**

- Owner: to be filled in
- Roadmap phase: Phase 2, see `roadmap.md` (batch order: sprite sheet, sprite, animated sprite)
- Affected modules: `graphics`, `render-api` (sprite only), `resources`, `test-support`, `examples`
- Requirements: `REQ-SHEET-001`, `REQ-SHEET-002`
- Applicable ADRs: [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md) (ACCEPTED)
- Open questions involved: `OQ-100`, `OQ-101`, `OQ-106`, `OQ-118`

## Objective

Represent a tileset image as a grid and group sheets by unique id; foundation for sprites and for `tilemap`.

## Out of scope

Sprite rendering and animation (see [`sprite.md`](sprite.md), [`animated-sprite.md`](animated-sprite.md)).

## Expected behavior

- A sheet is a grid: cell size, columns, rows, optional margin and spacing, in pixels; access to a cell by index.
- Out-of-range index: defined error.
- Sheets are grouped and retrieved by unique id; duplicate and unknown ids have defined behavior (`OQ-106`).
- Cells share the cached immutable source image (decision 4).

## Envisaged APIs / contracts

Responsibilities only; signatures are not fixed. See `architecture/contracts.md`.

## Lifecycle and concurrency

Sheets and their images are held by the `graphics` cache under the common resource convention (ADR-0005, `OQ-118`, `OQ-101`); group lifecycle and id form: `OQ-106`. Calling thread: `OQ-030`.

## Units and Android/DEX compatibility

Pixels. Image decoding and pixel buffer must not use `java.awt` in shared code (`OQ-100`). Any decoding API or dependency needs its purpose, license and DEX compatibility recorded first (`docs/android-compatibility.md`).

## Dependencies

Allowed: `core`, `resources`, `render-api` (ADR-0003; `OQ-051` resolved). Forbidden: AWT, OpenGL or Android types in `graphics` APIs.

## Tests

- Unit tests: grid index math, margin and spacing, out-of-range, duplicate and unknown ids.
- Contract tests (`test-support`): sheet access and group behavior.
- Integration tests: headless load of a sheet through `resources`.
- Platform/performance tests: color-transform parity and cache performance where relevant.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API validated.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
