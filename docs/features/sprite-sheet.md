# Feature: sprite sheet (foundation)

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2, see `roadmap.md` (batch order: sprite sheet, sprite, animated sprite)
- Affected modules: `graphics`, `resources`, `core` (JSON reader), `test-support`, `examples`
- Requirements: `REQ-SHEET-001` to `REQ-SHEET-003`, `REQ-SPRITE-009`, `REQ-RES-004`, `REQ-RES-005`, `REQ-DEF-001`, `REQ-DEF-002`
- Applicable ADRs: [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md), [`ADR-0007`](../decisions/0007-resource-convention-keys-release-and-errors.md), [`ADR-0026`](../decisions/0026-graphics-pixels-cache-transforms-animation-and-commands.md), [`ADR-0032`](../decisions/0032-definition-files-registry-ids-resource-providers-dependency-policy.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md), [`ADR-0047`](../decisions/0047-render-api-texture-pixel-data-and-shader-resource-keys.md), [`ADR-0054`](../decisions/0054-decoded-image-size-limits.md)
- Open questions involved: none

## Objective

Represent an image as a grid of cells and group sheets by unique id; foundation for sprites, animations and `tilemap` tilesets.

## Out of scope

Sprite rendering and animation playback (see [`sprite.md`](sprite.md), [`animated-sprite.md`](animated-sprite.md)); optional decoder modules (`graphics-imageio`, `graphics-android`), added in their own batch.

## Expected behavior

- The pixel buffer is packed `int` ARGB, row-major, top-left origin, straight alpha. The built-in pure-Java PNG decoder handles non-interlaced images of every color type, depths 1 to 8 and 16 (reduced to 8), and `tRNS`; interlaced images fail with the `graphics` decoding exception.
- Size limits (ADR-0054): every decoder refuses with the decoding exception, from the header and before allocating the pixels, an image whose width or height is above 1,000,000 pixels, or whose pixel count is above the decoder's limit, 89,478,485 by default and set when the decoder is created.
- Images are held by the cache of a `graphics` instance: key = resource key (plus a discriminator for derived images), explicit reference counting, idempotent release, no automatic eviction.
- A sheet is a grid: cell width and height, columns, rows, optional margin and spacing, in pixels; cells are numbered row by row from the top-left, starting at 0; an out-of-range index fails with `IndexOutOfBoundsException`.
- Sheets are grouped and retrieved by a unique string id in the resource-key form; a duplicate id fails at registration, an unknown id fails at lookup, both with a defined exception naming the id. A group is reference-counted; whoever acquires it releases it, normally the scene on exit.
- Definition file (`"format": "lionengine.sheet"`, `"version": 1`): `image`, `cellWidth`, `cellHeight`, optional `columns`, `rows` (derived from the image when absent), `margin`, `spacing`, `pivotX`, `pivotY`, `animations`, `templates`, `comment`. Any other field is an error.
- Cells share the cached immutable source image.

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Images, sprites and animations); canonical type names in [ADR-0037](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md); signatures written by the batch: pixel buffer, PNG decoder, image cache, sprite sheet, sheet group, sheet loader.

## Batches (order)

1. `PixelBuffer` and the PNG decoder (`REQ-SPRITE-009`).
2. `ImageCache` with the resource convention suite.
3. Grid, cells, groups and ids (`REQ-SHEET-001`, `REQ-SHEET-002`).
4. Sheet definition loader (`REQ-SHEET-003`).

## State (2026-11-25)

Batch 1 is implemented in `graphics`, with the example `ImageDecodingExample` and its API-index row; batches 2 to 4 are not. Names and details chosen by the batch, within this page, ADR-0026, ADR-0033 and ADR-0047:
- `PixelBuffer`: a final class created with its width and height (positive, at most `Integer.MAX_VALUE` pixels), transparent black; `getPixel(x, y)` and `setPixel(x, y, argb)` throw `IndexOutOfBoundsException` outside it; `copyPixels()` returns a new row-major array, ready for `TextureStore.create`, and `setPixels(int[])` copies exactly `width * height` values. The buffer never shares its array, so a cached image can never be changed through it.
- *Image decoder contract*: `ImageDecoder`, one method `decode(InputStream)` returning a new `PixelBuffer` owned by the caller. The caller opens and closes the stream; an `IOException` of the stream propagates unchanged, so that the loader reports an inaccessible resource (ADR-0007); invalid content is an `ImageDecodingException`. Implementations are thread-safe, for decoding on loading workers. Contract suite: the abstract `ImageDecoderContract` of the `graphics` test JAR (decoding, a new buffer per call, stream left open, read failure, empty and non-image content, `null`, two threads).
- `ImageDecodingException`: unchecked, message and optional cause, like `ResourceException`.
- `PngDecoder`: the built-in decoder. 16-bit samples keep their most significant byte; samples of 1, 2 and 4 bits are multiplied by 255, 85 and 17; a `tRNS` transparent color is compared at the depth of the image, before the reduction to 8 bits. The CRC of every chunk is checked. Rejected: Adam7 interlacing, animated PNG (any `acTL`, `fcTL` or `fdAT` chunk), an unknown critical chunk or a critical chunk out of place, a palette image without palette or with an index outside it, a palette of more than 256 entries, a `tRNS` chunk longer than the palette or before it. Accepted as other decoders do: a suggested palette of an RGB or RGBA image and a `tRNS` chunk of an image with an alpha channel (both ignored), empty `IDAT` chunks, and data chunks after the last row. The decoder holds two rows besides the result, reads the stream up to the `IEND` chunk and always ends its `Inflater`.
- Test fixture (ADR-0045): `PngWriter` in the `graphics` test JAR writes PNG images in memory from raw samples, with a chosen filter type, chunk split and extra chunks, valid or not, so that the later batches and the examples can build their images in memory instead of storing image files.
- Verification: the tests decode every color type and bit depth with each of the five filter types and with a filter per row, against pixels computed from the samples; during the batch, the decoder was also checked against an independent encoder (16 images written by Pillow: every color type and depth Pillow writes, with and without `tRNS`), all identical. The device test `GraphicsTest` runs the same cases on the API 21 emulator.

Not in this batch: how a loader chooses the decoder of a key (by extension or registration) and the default registration of `PngDecoder` come with the image cache (batch 2), which opens keys through `Resources`.

Gap found by the batch, then closed: there was no limit on the size of a decoded image, so a small file declaring a huge image could exhaust the memory. ADR-0054 (2026-11-25, owner delegation) sets the limits: `PngDecoder.MAX_DIMENSION` (1,000,000 pixels of width or height, libpng's default) and a pixel limit per decoder, `PngDecoder.DEFAULT_MAX_PIXELS` (89,478,485, Pillow's default) unless given to `new PngDecoder(maxPixels)`; `ImageDecoderContract` checks the pixel limit of every decoder.

## Lifecycle and concurrency

Cache and group operations on the runtime thread only (ADR-0018). Synchronous loading first; asynchronous decoding on workers in a later batch (ADR-0019). GPU upload happens at first render or at an explicit prepare call, never on a worker: the cache hands the immutable `int[]` of a source to the `render-api` texture contract and releases the texture with the source (ADR-0047).

## Units and Android/DEX compatibility

Pixels. The PNG decoder uses `java.util.zip.Inflater` (to verify on API 21, `android-compatibility.md`); no `java.awt` or `javax.imageio` in `graphics`.

## Dependencies

`core`, `resources`, `render-api` (ADR-0003). Forbidden: AWT, OpenGL or Android types in `graphics` APIs.

## Tests

- Unit tests: PNG decoding per color type and depth, rejection of interlaced files, grid index math, margin and spacing, out-of-range, duplicate and unknown ids, definition-file errors.
- Contract tests (`resources` test JAR, ADR-0045): the resource convention suite (key equality, idempotent release, missing resource) and the cache suite.
- Integration tests: headless load of a sheet through `resources` from a definition file.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API reviewed in the pull request.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
