# Feature: tile map

**Status: PROPOSED**

- Owner: to be filled in
- Roadmap phase: Phase 3, see `roadmap.md`
- Affected modules: `tilemap`, `graphics` (sprite sheet), `render-api`, `resources`, `test-support`, `examples`
- Requirements: `REQ-MAP-001`, `REQ-MAP-003`, `REQ-MAP-004`, `REQ-MAP-005`, `REQ-MAP-006`, `REQ-MAP-007`
- Applicable ADRs: [`ADR-0009`](../decisions/0009-tile-map-contracts-in-tilemap.md) (PROPOSED), [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md), [`ADR-0007`](../decisions/0007-resource-convention-keys-release-and-errors.md)
- Open questions involved: `OQ-012`, `OQ-040`, `OQ-043`, `OQ-044`, `OQ-101`, `OQ-122`, `OQ-125`, `OQ-126`, `OQ-127`, `OQ-128`, `OQ-129`

## Objective

Single-layer tile maps built on the sprite sheet: manual editing, loading from a resource, viewport rendering and destroyable tiles with neighbor updates.

## Out of scope

Collision, static and dynamic (`OQ-125`); layers; map file format (`OQ-126`); chunking and render cache (`OQ-129`); camera and meter conversion (`OQ-012`).

## Expected behavior

- Grid of cells, each empty or a tileset cell index; `set`, `get`, `clear` by `int` tile coordinates; out-of-bounds has a defined error.
- Loading through an abstract loader contract over `resources`; missing or invalid data has defined errors; keys and release per ADR-0007.
- Rendering takes a viewport rectangle in pixels of the logical frame and emits `render-api` commands only for intersecting tiles plus a margin; it never mutates the map.
- Destroying a tile applies auto-tiling rules to its neighbors (rule format: `OQ-127`) and notifies a synchronous listener with cell, old and new tile.

## Envisaged APIs / contracts

Responsibilities only; signatures are not fixed. See `architecture/contracts.md` (Tilemap).

## Lifecycle and concurrency

Changes run in the update phase only (`REQ-LOOP-003`); rendering is read-only (`REQ-LOOP-006`). Tileset ownership and release: `OQ-128`. Threads: `OQ-030`, `OQ-121`.

## Units and Android/DEX compatibility

Tile indices are `int`; tile size, viewport and positions are pixels of the logical frame. No `java.awt` or `javax.imageio`; any map-format library needs purpose, license and Android check first (`docs/android-compatibility.md`).

## Dependencies

Allowed: `core`, `graphics` contracts, `render-api`, `resources`. Forbidden: frontend types; any dependency of `core` or an `*-api` module on `tilemap`.

## Tests

- Unit tests: bounds, set/get/clear, viewport-to-tile-range math, neighbor rules.
- Contract tests (`test-support`): map loaders.
- Integration tests: headless load, render commands only for visible tiles, destroy then render.
- Platform/performance tests: large maps (`OQ-129`), later.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API validated.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
