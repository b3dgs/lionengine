# Feature: sprite

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2, see `roadmap.md` (batch order: sprite sheet, sprite, animated sprite)
- Affected modules: `graphics`, `render-api`, `resources`, `test-support`, `examples`
- Requirements: `REQ-SPRITE-001` to `REQ-SPRITE-010`, `REQ-SPR-001`
- Applicable ADRs: [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md), [`ADR-0007`](../decisions/0007-resource-convention-keys-release-and-errors.md), [`ADR-0014`](../decisions/0014-meters-to-pixels-numeric-type-and-rotation.md), [`ADR-0026`](../decisions/0026-graphics-pixels-cache-transforms-animation-and-commands.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md)
- Open questions involved: none

## Objective

Load, cache, transform, color-transform and render a sprite through `render-api` commands, with many instances sharing one source.

## Out of scope

Animation (see [`animated-sprite.md`](animated-sprite.md)); actual batching (a later optimization behind the command buffer, `REQ-SPRITE-008`); mask-layer templates (later extension, ADR-0033).

## Expected behavior

- Load from a resource key through `resources`; a missing resource raises the resource exception, invalid data the `graphics` decoding exception.
- Shared immutable source (image + region) and a lightweight mutable instance (transform, color transform, pivot, alpha), changed in the update phase only, allocating nothing.
- Transform: scale, then rotation, then translation, all about the pivot. The pivot is in pixels from the frame top-left, default the frame center. Rotation in degrees, counter-clockwise as seen on screen (the sprite handles the screen Y flip). Position in pixels of the logical frame, given by the caller (usually from the camera view transform).
- Color transformation: a backend-independent description. Tint (multiplied color) and alpha are per-frame command parameters when the backend's typed capability says so; otherwise, and for palette swap, grading and mask bake, the effect is baked on the CPU once and cached.
- Template color variants: exact key colors mapped to target colors in the sheet definition's `templates`; each variant is cached by (source image, mapping).
- Rendering writes one entry in the command buffer: texture handle, source rectangle, position, scale, rotation, pivot, tint, alpha, layer. Commands are presented by ascending layer, then submission order. Texture sampling is nearest-neighbor.
- Modifiable pixels: a caller-owned copy of a source; turning it into a source copies it into a new cache entry; the cached source is never mutated.

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Render commands; Images, sprites and animations); canonical type names in [ADR-0037](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md); signatures written by the batch: sprite source, sprite instance, color transform description, color-transform capability, command buffer.

## Batches (order)

1. Source, instance, transform and pivot, command emission (`REQ-SPRITE-001`, `REQ-SPRITE-003`, `REQ-SPRITE-006`, `REQ-SPRITE-007`, `REQ-SPRITE-010`).
2. Cache sharing (`REQ-SPRITE-002`).
3. Color transforms: capability query, per-frame tint and alpha, CPU bake (`REQ-SPRITE-004`).
4. Template color variants (`REQ-SPRITE-005`).

## Lifecycle and concurrency

Sources are owned by the `graphics` cache (reference counted, released by their acquirer); instances own no image. Runtime thread only; rendering reads instances and never changes them.

## Units and Android/DEX compatibility

Pixels for images, positions and pivots; degrees for rotation; `float` only inside command entries (ADR-0014). No frontend type in the API. Optional decoders live in `graphics-imageio` and `graphics-android`.

## Dependencies

`core`, `resources`, `render-api`. Forbidden: AWT, OpenGL or Android types in `graphics` APIs.

## Tests

- Unit tests: transform order and pivot, rotation sign, palette mapping, color-transform description, CPU bake, command ordering, zero allocation per frame.
- Contract tests (`test-support`): sprite render on a fake backend with and without the tint/alpha capability, cache hit and release.
- Integration tests: headless load, cache, render of two color variants.
- Platform tests: AWT, OpenGL and Android color-transform parity.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API reviewed in the pull request.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
