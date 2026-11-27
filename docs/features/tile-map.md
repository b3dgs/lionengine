# Feature: tile map

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 3, see `roadmap.md`
- Affected modules: `tilemap`, `graphics` (sprite sheet), `render-api`, `resources`, `physics`, `core` (JSON reader), `test-support`, `examples`
- Requirements: `REQ-MAP-001` to `REQ-MAP-008`, `REQ-DEF-001`, `REQ-DEF-002`
- Applicable ADRs: [`ADR-0009`](../decisions/0009-tile-map-contracts-in-tilemap.md), [`ADR-0013`](../decisions/0013-world-coordinates-and-angles.md), [`ADR-0021`](../decisions/0021-scene-world-viewport-and-object-model.md), [`ADR-0026`](../decisions/0026-graphics-pixels-cache-transforms-animation-and-commands.md), [`ADR-0028`](../decisions/0028-tile-map-collision-json-definitions-and-storage.md), [`ADR-0032`](../decisions/0032-definition-files-registry-ids-resource-providers-dependency-policy.md), [`ADR-0034`](../decisions/0034-camera-and-ecs-defaults-noise-clamp-slots-capacities-events-shapes.md)
- Open questions involved: none

## Objective

Single-layer tile maps built on the sprite sheet: manual editing, loading from a resource, viewport rendering, destroyable tiles with neighbor updates, and tile collision for the standard traits.

## Out of scope

Several layers (candidate); one-way tiles and slopes (later, ADR-0034); chunked storage and render caches (only after a benchmark shows a need, ADR-0028); the camera (the caller passes a pixel rectangle).

## Expected behavior

- Grid of `width × height` cells stored as a flat row-major `int[]`; each cell is empty or a tileset cell index. Tile coordinates are `int`, origin at the bottom-left, Y up. `set`, `get`, `clear` check bounds and throw `IndexOutOfBoundsException` outside the map.
- The tileset is a sprite sheet referenced by its string id (for Tiled, the resource key of the `.tsj` file, or of the map for an embedded tileset); the map holds a reference count on the sheet group and releases it at `close`; the scene that loaded the map closes it.
- Loading through an abstract loader contract over `resources`; missing data raises the resource exception, invalid data the definition exception. The Tiled JSON loader accepts orthogonal, finite maps with one tile layer and one tileset (embedded or `.tsj`), layer data as a JSON array or base64 (uncompressed, zlib or gzip), and flips Tiled's top-down rows. It rejects flip and rotation flags, infinite maps, other orientations and several tile layers; it ignores object, image and group layers.
- Tile collision property: `empty` or `solid`, from the Tiled tile property `collision` (or given in code); the map implements the `physics` shape-query contract (which solid tile boxes overlap a box) with a world placement: origin and tile size in meters, default 1 m per tile.
- Rendering takes a viewport rectangle in pixels of the logical frame and emits `render-api` commands only for the intersecting tiles plus a margin (default one tile, configurable); it never mutates the map; cost independent of the map size.
- Auto-tiling rules come from a `lionengine.autotile` file named by the Tiled tileset property `autotile` (or are given in code): an ordered list of `neighbors`, `care` and `tile`, bit 0 north then clockwise, a cell outside the map counting as not empty (ADR-0032).
- Destroying a tile clears it and re-evaluates its non-empty neighbors among the 8: the first rule with `(mask & care) == (neighbors & care)` gives the new tile, otherwise the tile is unchanged; every change (set, clear, destroy, neighbor update) updates collision at once and notifies the synchronous tile-changed listener with cell, old and new tile.

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Tile map); canonical type names in [ADR-0037](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md); signatures written by the batch: tile map, tileset reference, loader contract, Tiled loader, auto-tiling rule set, tile-changed listener, collision shape query implementation.

## Batches (order)

1. Grid, `set`/`get`/`clear`, tileset reference and release (`REQ-MAP-001`, `REQ-MAP-003`).
2. Viewport rendering (`REQ-MAP-005`).
3. Loader contract with an in-memory format, then the Tiled JSON loader (`REQ-MAP-004`).
4. Auto-tiling rules and destruction, tile-changed listener (`REQ-MAP-006`, `REQ-MAP-007`, `REQ-MAP-002`).
5. Tile collision through the `physics` shape query (`REQ-MAP-008`).

## Lifecycle and concurrency

Changes run in the update phase only (`REQ-LOOP-003`) on the runtime thread; rendering is read-only (`REQ-LOOP-006`).

## Units and Android/DEX compatibility

Tile indices `int`; tile size, viewport and positions in pixels of the logical frame for rendering; meters for collision only. Base64 is decoded by project code (`java.util.Base64` needs API 26 and is not used in shared code); zlib and gzip use `java.util.zip`, to verify on API 21 (`android-compatibility.md`).

## Dependencies

`core`, `resources`, `graphics`, `render-api`, `physics`. Forbidden: `ecs` or `traits`, frontend types, any dependency of `core` or an `*-api` module on `tilemap`.

## Tests

- Unit tests: bounds, set/get/clear, row flip, viewport-to-tile-range math, neighbor rules, shape query, collision after set and destroy, Tiled subset (each accepted and rejected feature).
- Contract tests (`tilemap` test JAR, ADR-0045): map loader suite with an in-memory test format.
- Integration tests: headless load, render commands only for visible tiles, destroy then render.
- Performance tests: a 1000 × 1000 map renders a viewport in constant time.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API reviewed in the pull request.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
