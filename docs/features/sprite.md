# Feature: sprite

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2, see `roadmap.md` (batch order: sprite sheet, sprite, animated sprite)
- Affected modules: `graphics`, `render-api`, `resources`, `test-support`, `examples`
- Requirements: `REQ-SPRITE-001` to `REQ-SPRITE-010`, `REQ-SPR-001`
- Applicable ADRs: [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md), [`ADR-0007`](../decisions/0007-resource-convention-keys-release-and-errors.md), [`ADR-0014`](../decisions/0014-meters-to-pixels-numeric-type-and-rotation.md), [`ADR-0026`](../decisions/0026-graphics-pixels-cache-transforms-animation-and-commands.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md), [`ADR-0056`](../decisions/0056-sprite-color-transforms-mask-grading-capability-and-bake.md)
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
4. Template color variants (`REQ-SPRITE-005`). The sheet loader of [`sprite-sheet.md`](sprite-sheet.md) batch 4 already checks the `templates` of a definition and keeps nothing; this batch builds the color mappings from them.

## State (2026-11-26)

Batch 1 (2026-11-26), source, instance, transform and pivot, command emission, with the example `SpriteExample` and its API-index row. Names and details chosen by the batch, within this page, ADR-0003, ADR-0014, ADR-0016, ADR-0026 and ADR-0033:
- Source: no type of its own. The shared immutable source is an `Image` of the image cache, whose whole area is the only frame, or a `SpriteSheet`, whose cells are the frames (cell indexes, row by row from the top-left). `new Sprite(image)` and `new Sprite(sheet)` refuse `null` (`IllegalArgumentException`) and a released image (`IllegalStateException`). Loading a sprite is loading its image, so the errors of REQ-SPRITE-001 are those of the cache: `ResourceException` (`MISSING`) for a missing key, `ImageDecodingException` for invalid data.
- `Sprite`: a final class, the instance; it holds no image and releases nothing, the image staying owned by its acquirer (the handle of the cache or the sheet group). The frame size is read from the source at creation and never changes (as the grid of a sheet). `setFrame(index)` selects a frame (`IndexOutOfBoundsException` outside `[0, getFrameCount())`, `getFrameCount()` is `1` for an image).
- Transform and pivot: `setPosition(x, y)`, `setScale(x, y)` (negative mirrors, zero hides), `setRotation(degrees)` (counter-clockwise on screen, kept as given: no normalization in a setter, ADR-0016), `setPivot(x, y)` (pixels from the frame top-left, default the image center or the pivot of the sheet, which is the cell center unless its definition says otherwise; may lie outside the frame), `setLayer(layer)` (default `0`); every `double` must be finite (`IllegalArgumentException`). Values are `double` in the API and converted to `float` only in the command entry (ADR-0014).
- Commands: `render(commands)` adds one entry at the position of the sprite, `render(commands, x, y)` at a position computed for the frame (interpolated between two steps, or converted from the world by the camera) without changing the sprite, so the render phase only reads sprites. The entry carries the texture of the image (read at each render, so a development reload is drawn), the source rectangle of the frame, the position, scale, rotation, pivot and layer; tint and alpha keep the defaults of the buffer until batch 3. A released image throws `IllegalStateException` and adds nothing. Setters and rendering allocate nothing.
- Composition order: the `CommandBuffer` Javadoc now states that an entry is scaled, then rotated, then translated, about its pivot, as the backends already drew it; `AwtRenderBackendTest.testCompositionOrder` checks it on pixels with a non-uniform scale and a rotation. The rotation is handed to the command unchanged: the command is already counter-clockwise on screen and the backends apply the screen Y flip (`AwtFrame` negates it for AWT).
- Tests: `SpriteTest` (loading errors, default pivot, frames, every command field, render at a given position, placement about the pivot, composition order, rotation kept as given, layers, released sources, invalid values, no allocation in 1,000 frames of 64 sprites); `SpriteExampleTest`; device test `GraphicsTest.testSprite` on API 21.

Batch 2 (2026-11-26), cache sharing, with the example `SpriteSharingExample` and its API-index row. No new API: the image cache of [`sprite-sheet.md`](sprite-sheet.md) batch 2 already shares an image between its handles (REQ-SPRITE-002), and batch 1 made the sprite an instance that holds no image. The batch checks how the two work together, within this page, ADR-0019, ADR-0026 and ADR-0055:
- Sharing: any number of sprites over one handle, and the sprites of several users over their own handles of one key (a cache hit), draw one texture from one decoding; the memory of the image is counted once. A sheet over an image already loaded shares it too, so a sprite over a cell and a sprite over the whole image draw the same texture.
- Release: each user releases its own handle; a sprite over a released handle refuses to draw (`IllegalStateException`) even while another user keeps the image cached, so a scene that leaves cannot keep drawing through another scene's handle. The texture leaves with the last handle; a second release changes nothing.
- Modified pixels (REQ-SPR-001): `copyToBuffer()` gives a copy owned by the caller; `ImageCache.add(key, discriminator, copy)` makes it a source of its own, with its texture and its sprites; the cached image and the sprites over it never change, and the copy can be changed further without changing the source made from it.
- Development reload: every sprite over the reloaded image draws its new texture from the next frame without being created again (the texture is read at each render); the previous texture is released.
- Tests: `SpriteCacheTest` (users sharing a source and releasing it, a crowd of 1,000 sprites over one handle, a sheet sharing an image, a miss, a modified copy, a reload); `SpriteSharingExampleTest`; device test `GraphicsTest.testSpriteSharing` on API 21.

Batch 3 (2026-11-26), color transforms, with the example `ColorTransformExample` and its API-index row. The owner chose the meaning of mask and grading; the rest follows ADR-0056, within this page, ADR-0003, ADR-0026 and ADR-0033:
- Description: `ColorTransform` (`graphics`), immutable, built from `ColorTransform.NONE` with `withGrading(matrix)`, `withMask(rgb, amount)`, `withTint(argb)` and `withAlpha(alpha)`. The parts apply in this order: grading, then mask, then tint, then alpha. Mask is a silhouette fill (every pixel blended toward a color by an amount in `[0, 1]`, alpha kept). Grading is a `ColorMatrix`, a 4 by 5 matrix as in Android `ColorMatrix` (`IDENTITY`, `saturation(s)` with Android's luminance weights, or any 20 finite values). On the CPU (`apply(argb)`), the static part (grading, mask) is rounded to 8 bits before the per-frame part (tint, alpha), half up and clamped, so the result does not depend on the path. The canonical text (`toString()`) lists the parts other than their default.
- Capability (`render-api`): `ColorEffect` (`TINT`, `ALPHA`) and `ColorCapability.isSupported(effect)`, which every `RenderBackend` now implements. A backend draws the field of an effect it does not support as if it kept its default. `render-awt` supports both, and so does `RecordingBackend` until a test calls `setSupported(effect, false)`; `RenderBackendContract.testColorEffects` checks the query. A call `isSupported(null)` must now be cast: two test calls were.
- Paths: `getFramePart(capability)` keeps the tint and alpha the backend applies per frame, and `getBakedPart(capability)` keeps the rest. `ImageCache.bake(image, transform)` caches the CPU result as an image derived from the source: discriminator of the source + `+` + canonical text, shared by every code baking the same transform, released by each handle, refused over the limit, checked for the thread. An identity transform gives a new handle on the source, and a reload of the source leaves its variants unchanged. `Sprite.setTint(argb)` and `setAlpha(alpha)` write the per-frame part into each command, allocating nothing. `new Sprite(sheet, image)` draws the frames of a sheet from a variant of its image of the same size. A sprite never bakes.
- Tests: `ColorMatrixTest`; `ColorTransformTest` (mask, tint and alpha, order, path independence on 10,000 random pixels and transforms, split for every capability, text); `ColorBakeTest` (variant, sharing, identity, derived images, reload, limit, invalid arguments, other thread); `SpriteTest` (`testColor`, `testCapability`, `testSheetVariant`); `RecordingBackendTest.testColorEffects`; `AwtRenderBackendTest.testColorParity` (per-frame tint and alpha within one unit of the CPU bake); `ColorTransformExampleTest` with and without the capability; device test `GraphicsTest.testColorTransform` on API 21. OpenGL and Android parity come with those backends.

## Lifecycle and concurrency

Sources are owned by the `graphics` cache (reference counted, released by their acquirer); instances own no image. Runtime thread only; rendering reads instances and never changes them.

## Units and Android/DEX compatibility

Pixels for images, positions and pivots; degrees for rotation; `float` only inside command entries (ADR-0014). No frontend type in the API. Optional decoders live in `graphics-imageio` and `graphics-android`.

## Dependencies

`core`, `resources`, `render-api`. Forbidden: AWT, OpenGL or Android types in `graphics` APIs.

## Tests

- Unit tests: transform order and pivot, rotation sign, palette mapping, color-transform description, CPU bake, command ordering, zero allocation per frame.
- Contract tests: sprite render on the recording backend of `test-support` with and without the tint/alpha capability; cache hit and release with the cache convention suite of the `resources` test JAR (ADR-0045).
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
