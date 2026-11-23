# Feature: sprite sheet (foundation)

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2, see `roadmap.md` (batch order: sprite sheet, sprite, animated sprite)
- Affected modules: `graphics`, `resources`, `core` (JSON reader), `test-support`, `examples`
- Requirements: `REQ-SHEET-001` to `REQ-SHEET-003`, `REQ-SPRITE-009`, `REQ-RES-004`, `REQ-RES-005`, `REQ-DEF-001`, `REQ-DEF-002`
- Applicable ADRs: [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md), [`ADR-0007`](../decisions/0007-resource-convention-keys-release-and-errors.md), [`ADR-0026`](../decisions/0026-graphics-pixels-cache-transforms-animation-and-commands.md), [`ADR-0032`](../decisions/0032-definition-files-registry-ids-resource-providers-dependency-policy.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md)
- Open questions involved: none

## Objective

Represent an image as a grid of cells and group sheets by unique id; foundation for sprites, animations and `tilemap` tilesets.

## Out of scope

Sprite rendering and animation playback (see [`sprite.md`](sprite.md), [`animated-sprite.md`](animated-sprite.md)); optional decoder modules (`graphics-imageio`, `graphics-android`), added in their own batch.

## Expected behavior

- The pixel buffer is packed `int` ARGB, row-major, top-left origin, straight alpha. The built-in pure-Java PNG decoder handles non-interlaced images of every color type, depths 1 to 8 and 16 (reduced to 8), and `tRNS`; interlaced images fail with the `graphics` decoding exception.
- Images are held by the cache of a `graphics` instance: key = resource key (plus a discriminator for derived images), explicit reference counting, idempotent release, no automatic eviction.
- A sheet is a grid: cell width and height, columns, rows, optional margin and spacing, in pixels; cells are numbered row by row from the top-left, starting at 0; an out-of-range index fails with `IndexOutOfBoundsException`.
- Sheets are grouped and retrieved by a unique string id in the resource-key form; a duplicate id fails at registration, an unknown id fails at lookup, both with a defined exception naming the id. A group is reference-counted; whoever acquires it releases it, normally the scene on exit.
- Definition file (`"format": "lionengine.sheet"`, `"version": 1`): `image`, `cellWidth`, `cellHeight`, optional `columns`, `rows` (derived from the image when absent), `margin`, `spacing`, `pivotX`, `pivotY`, `animations`, `templates`, `comment`. Any other field is an error.
- Cells share the cached immutable source image.

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Images, sprites and animations); signatures written by the batch: pixel buffer, PNG decoder, image cache, sprite sheet, sheet group, sheet loader.

## Lifecycle and concurrency

Cache and group operations on the runtime thread only (ADR-0018). Synchronous loading first; asynchronous decoding on workers in a later batch (ADR-0019). GPU upload happens at first render or at an explicit prepare call, never on a worker.

## Units and Android/DEX compatibility

Pixels. The PNG decoder uses `java.util.zip.Inflater` (to verify on API 21, `android-compatibility.md`); no `java.awt` or `javax.imageio` in `graphics`.

## Dependencies

`core`, `resources`, `render-api` (ADR-0003). Forbidden: AWT, OpenGL or Android types in `graphics` APIs.

## Tests

- Unit tests: PNG decoding per color type and depth, rejection of interlaced files, grid index math, margin and spacing, out-of-range, duplicate and unknown ids, definition-file errors.
- Contract tests (`test-support`): the resource convention suite (key equality, idempotent release, missing resource) and the cache suite.
- Integration tests: headless load of a sheet through `resources` from a definition file.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API reviewed in the pull request.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
