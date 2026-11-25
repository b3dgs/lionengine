# Feature: sprite sheet (foundation)

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2, see `roadmap.md` (batch order: sprite sheet, sprite, animated sprite)
- Affected modules: `graphics`, `resources`, `core` (JSON reader), `test-support`, `examples`
- Requirements: `REQ-SHEET-001` to `REQ-SHEET-003`, `REQ-SPRITE-009`, `REQ-RES-004`, `REQ-RES-005`, `REQ-DEF-001`, `REQ-DEF-002`
- Applicable ADRs: [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md), [`ADR-0007`](../decisions/0007-resource-convention-keys-release-and-errors.md), [`ADR-0026`](../decisions/0026-graphics-pixels-cache-transforms-animation-and-commands.md), [`ADR-0032`](../decisions/0032-definition-files-registry-ids-resource-providers-dependency-policy.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md), [`ADR-0047`](../decisions/0047-render-api-texture-pixel-data-and-shader-resource-keys.md), [`ADR-0054`](../decisions/0054-decoded-image-size-limits.md), [`ADR-0055`](../decisions/0055-cache-thread-check-opt-in-limit-and-development-reload.md)
- Open questions involved: none

## Objective

Represent an image as a grid of cells and group sheets by unique id; foundation for sprites, animations and `tilemap` tilesets.

## Out of scope

Sprite rendering and animation playback (see [`sprite.md`](sprite.md), [`animated-sprite.md`](animated-sprite.md)); optional decoder modules (`graphics-imageio`, `graphics-android`), added in their own batch.

## Expected behavior

- The pixel buffer is packed `int` ARGB, row-major, top-left origin, straight alpha. The built-in pure-Java PNG decoder handles non-interlaced images of every color type, depths 1 to 8 and 16 (reduced to 8), and `tRNS`; interlaced images fail with the `graphics` decoding exception.
- Size limits (ADR-0054): every decoder refuses with the decoding exception, from the header and before allocating the pixels, an image whose width or height is above 1,000,000 pixels, or whose pixel count is above the decoder's limit, 89,478,485 by default and set when the decoder is created.
- Images are held by the cache of a `graphics` instance: key = resource key (plus a discriminator for derived images), explicit reference counting, idempotent release, no automatic eviction; an opt-in memory limit in bytes, none by default, refuses an image that would exceed it; a development reload decodes a cached image again; the thread of the first operation owns the cache and another thread is refused (ADR-0055).
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

Batches 1 to 4 are implemented in `graphics`, with the examples `ImageDecodingExample`, `ImageCacheExample`, `SpriteSheetExample` and `SheetLoaderExample` and their API-index rows: the feature is complete. Names and details chosen by batch 1, within this page, ADR-0026, ADR-0033 and ADR-0047:
- `PixelBuffer`: a final class created with its width and height (positive, at most `Integer.MAX_VALUE` pixels), transparent black; `getPixel(x, y)` and `setPixel(x, y, argb)` throw `IndexOutOfBoundsException` outside it; `copyPixels()` returns a new row-major array, ready for `TextureStore.create`, and `setPixels(int[])` copies exactly `width * height` values. The buffer never shares its array, so a cached image can never be changed through it.
- *Image decoder contract*: `ImageDecoder`, one method `decode(InputStream)` returning a new `PixelBuffer` owned by the caller. The caller opens and closes the stream; an `IOException` of the stream propagates unchanged, so that the loader reports an inaccessible resource (ADR-0007); invalid content is an `ImageDecodingException`. Implementations are thread-safe, for decoding on loading workers. Contract suite: the abstract `ImageDecoderContract` of the `graphics` test JAR (decoding, a new buffer per call, stream left open, read failure, empty and non-image content, `null`, two threads).
- `ImageDecodingException`: unchecked, message and optional cause, like `ResourceException`.
- `PngDecoder`: the built-in decoder. 16-bit samples keep their most significant byte; samples of 1, 2 and 4 bits are multiplied by 255, 85 and 17; a `tRNS` transparent color is compared at the depth of the image, before the reduction to 8 bits. The CRC of every chunk is checked. Rejected: Adam7 interlacing, animated PNG (any `acTL`, `fcTL` or `fdAT` chunk), an unknown critical chunk or a critical chunk out of place, a palette image without palette or with an index outside it, a palette of more than 256 entries, a `tRNS` chunk longer than the palette or before it. Accepted as other decoders do: a suggested palette of an RGB or RGBA image and a `tRNS` chunk of an image with an alpha channel (both ignored), empty `IDAT` chunks, and data chunks after the last row. The decoder holds two rows besides the result, reads the stream up to the `IEND` chunk and always ends its `Inflater`.
- Test fixture (ADR-0045): `PngWriter` in the `graphics` test JAR writes PNG images in memory from raw samples, with a chosen filter type, chunk split and extra chunks, valid or not, so that the later batches and the examples can build their images in memory instead of storing image files.
- Verification: the tests decode every color type and bit depth with each of the five filter types and with a filter per row, against pixels computed from the samples; during the batch, the decoder was also checked against an independent encoder (16 images written by Pillow: every color type and depth Pillow writes, with and without `tRNS`), all identical. The device test `GraphicsTest` runs the same cases on the API 21 emulator.

Batch 2 (2026-11-25), the image cache, with the cache convention suite of [`resources.md`](resources.md) batch 3. Names and details chosen by the batch, within this page, ADR-0005, ADR-0007, ADR-0018, ADR-0019, ADR-0026 and ADR-0047:
- `ImageCache` is created by the game with its `Resources` and the `TextureStore` of its render backend; no static state. An image is cached under its resource key and a discriminator: `ImageCache.DECODED` (empty) for the image decoded from the resource, any other string for a derived image.
- `load(key)` decodes the image at its first acquisition and shares it afterwards; `add(key, discriminator, pixels)` caches a copy of a caller's `PixelBuffer` as a derived image (`IllegalStateException` if already cached, `IllegalArgumentException` for the empty discriminator), which is how a modified copy becomes a source (REQ-SPR-001); `acquire(key, discriminator)` shares an image already cached without reading anything (`IllegalStateException` if absent, `has` tells); `size()` counts the cached images.
- Each acquisition returns a new `Image` handle: shared pixels and texture, `getKey`, `getDiscriminator`, `getWidth`, `getHeight`, `getPixel`, `copyToBuffer` (a modifiable copy), `getTexture`, `prepare`, `release` and `isReleased`. A handle, not the shared image, is what the acquirer releases, so that a release can be idempotent with reference counting: a second release of the same handle never releases another acquisition. A released handle gives only its key and discriminator (`IllegalStateException` otherwise).
- Textures: created with the image when it enters the cache (`TextureStore.create` does no GPU work), uploaded at the first draw or `Image.prepare()`, released when the image leaves the cache with its last handle; the pixels are shared read-only with the store (ADR-0047). A decoded buffer is taken over without copy; a derived image is a copy.
- Decoders are chosen by the extension of the key (the end of its last segment after its last dot, compared exactly, as asset names are lower case, ADR-0023): `PngDecoder` with its default limits is registered for `png`; `setDecoder(extension, decoder)` replaces it or adds a format. A key without a registered decoder is an `ImageDecodingException`, raised before anything is read; the decoding errors of the cache start with the key (`sprites/hero.png: PNG truncated`) and keep the decoder's error as cause; a read failure is a `ResourceException` with reason `INACCESSIBLE`.
- Runtime thread only, documented like every runtime-thread type of the engine: the cache does not check the calling thread. Loading is synchronous.
- Test fixtures (ADR-0045): `ImageCacheConventionTest` binds the `CacheConventionContract` of the `resources` test JAR; `ImageCacheTest` checks textures, derived images, released handles, decoders and errors with the `RecordingBackend` of `test-support`.

Not in batch 2, then settled by ADR-0055 (2026-11-25, owner delegation):
- Runtime-thread check: `ThreadConfinement` (`core`) makes the thread of the first operation the owner of the cache; every operation of the cache, and `Image.release` and `Image.prepare`, throw an `IllegalStateException` naming both threads on another thread, before changing anything. Reading the pixels of a handle is not checked.
- Memory limit: `getUsedBytes()` counts 4 bytes per cached pixel, derived images included; `new ImageCache(resources, textures, maxBytes)` sets an opt-in limit (`NO_LIMIT` by default, `getMaxBytes()`); an image that would exceed it is refused with an `IllegalStateException` and nothing is cached (a decoded image is refused once decoded, when its size is known).
- Development reload: `reload(key)` decodes the image of a cached key again and gives every handle the new size, pixels and texture (a new texture is created, the previous one released); derived images and copies keep their pixels; a failure, a key not cached and an image above the limit leave the image unchanged. Called between two frames, outside the render phase.
- Context loss: nothing to do in the cache, the texture store keeps the pixel arrays it received (ADR-0047); scope release: the sheet groups of batch 3; asynchronous loading: with the asynchronous handles of [`game-loop.md`](game-loop.md), batch 4.

Gap found by the batch, then closed: there was no limit on the size of a decoded image, so a small file declaring a huge image could exhaust the memory. ADR-0054 (2026-11-25, owner delegation) sets the limits: `PngDecoder.MAX_DIMENSION` (1,000,000 pixels of width or height, libpng's default) and a pixel limit per decoder, `PngDecoder.DEFAULT_MAX_PIXELS` (89,478,485, Pillow's default) unless given to `new PngDecoder(maxPixels)`; `ImageDecoderContract` checks the pixel limit of every decoder.

Batch 3 (2026-11-25), grid, cells, groups and ids. Names and details chosen by the batch, within this page, ADR-0007, ADR-0019, ADR-0026, ADR-0028, ADR-0033 and ADR-0055:
- `SheetGrid`: a record of the cell width and height (positive), the columns and rows (positive, or `SheetGrid.DERIVED`, `0`), the margin and the spacing (zero or positive), all in pixels; `new SheetGrid(cellWidth, cellHeight)` derives both counts without margin or spacing. The first cell starts at (`margin`, `margin`); cells are `spacing` pixels apart in both directions. A derived count is what Tiled and Godot compute, `(imageSize - margin + spacing) / (cellSize + spacing)`, so the right and bottom margins may be missing or partial.
- `SpriteSheet`: created by a group only, over the group's handle on its image. `getId`, `getImage` (the group's handle: the group releases it, never the caller), the grid values with the counts resolved (`getColumns`, `getRows`, `getCellCount`), and `getCellX(index)` and `getCellY(index)`, the top-left pixel of a cell in the image, without allocation; cells are numbered row by row from `0`, an index outside `[0, getCellCount())` throws `IndexOutOfBoundsException` naming the sheet. A grid in which no cell fits, or whose given columns or rows exceed the image, is an `IllegalArgumentException` naming the id. The geometry stays readable after the release of the group.
- `SheetGroup`: sheets under ids of type `ResourceKey` (the key form of ADR-0007), usually the key of the sheet's definition file or, for a Tiled tileset, of its `.tsj` file, unique in the group. `new SheetGroup(imageCache)` creates an empty group whose first handle the creator holds; `add(id, imageKey, grid)` loads the image through the cache (so sheets over one image share its pixels and texture, in one group or several) and returns the sheet; `get(id)`, `has(id)`, `size()`. An id already in the group is refused by `add` before anything is read, an id not in the group by `get`, both with an `IllegalStateException` naming the id, as `ImageCache` refuses an image already cached or not cached; a failed `add` (missing or invalid image, grid outside the image, cache limit) adds nothing and leaves nothing cached.
- Reference counting: `acquire()` returns a new handle on the same group, for another user such as the tile map that keeps its tileset (ADR-0028); each handle is released by its acquirer, idempotently; the last release releases the images of every sheet, in the order they were added (the scope release of ADR-0019 and ADR-0055). A released handle refuses every call but `isReleased` and `release`. The group checks the thread of its cache (ADR-0055).
- A sheet keeps the grid computed when it was added: after a development reload that changes the size of its image, the game releases and adds it again.
- No group id and no group cache: a group is shared by handing a handle (`acquire()`) to its other users, such as the map loader.

Batch 4 (2026-11-25), the sheet definition loader. Names and details chosen by the batch, within this page, ADR-0026, ADR-0032 and ADR-0033:
- `SheetLoader`, created with the `Resources` of the definitions and optionally a `JsonReader` with other limits (the defaults of ADR-0032 otherwise); `FORMAT` (`lionengine.sheet`) and `VERSION` (`1`). `load(group, key)` reads the file through the resources (at most one byte more than the document limit, so a larger file is refused without being read entirely), checks it, and adds the sheet to the group under the key of the file, which is the id a tile map or a game uses; it returns the sheet. It checks the group handle and the thread before reading anything.
- Fields: `image` (a valid resource key), `cellWidth` and `cellHeight` (positive `int`, required); `columns` and `rows` (positive `int` when present, derived when absent); `margin` and `spacing` (`int`, zero or positive, `0` by default); `pivotX` and `pivotY` (numbers, any value, the cell center by default, kept by the sheet: `SpriteSheet.getPivotX()` and `getPivotY()`, also given in code with `SheetGroup.add(id, image, grid, pivotX, pivotY)`).
- `animations`: an object of named animations, each with `frames` (an array of at least one `int`, each zero or positive and below the cell count of the sheet), `frameDuration` (positive seconds, required), `frameDurations` (optional, one positive number of seconds per frame) and `loop` (`once`, `loop` or `ping-pong`, required). `templates`: an object of named mappings, each mapping key colors to target colors written `#RRGGBB` or `#AARRGGBB` (hexadecimal digits in any case); an empty mapping is accepted, a key color given twice in another letter case is not. A member `comment` is documentation at every level, also inside `animations`, an animation, `templates` and a mapping. Animations and templates are checked and not kept: the animated-sprite batch 1 and the sprite batch 4 turn them into their own types.
- Errors: every mistake of the file is a `DefinitionException` with the key of the file, the line, the column and the JSON path of the faulty value (the object, for a missing field) and the field name; the grid is checked against the image (located at `columns` or `rows` when given, at `cellWidth` or `cellHeight` otherwise) and the frames against the cells, after the image is loaded and before the sheet is created, so a failed load adds nothing to the group and leaves nothing cached. A missing or unreadable file or image is the `ResourceException` of its key, an invalid image an `ImageDecodingException`, an id already in the group an `IllegalStateException`.

## Lifecycle and concurrency

Cache and group operations on the runtime thread only (ADR-0018), checked by the cache (ADR-0055). Synchronous loading first; asynchronous decoding on workers in a later batch (ADR-0019). GPU upload happens at first render or at an explicit prepare call, never on a worker: the cache hands the immutable `int[]` of a source to the `render-api` texture contract and releases the texture with the source (ADR-0047).

## Units and Android/DEX compatibility

Pixels. The PNG decoder uses `java.util.zip.Inflater` (to verify on API 21, `android-compatibility.md`); no `java.awt` or `javax.imageio` in `graphics`.

## Dependencies

`core`, `resources`, `render-api` (ADR-0003). Forbidden: AWT, OpenGL or Android types in `graphics` APIs.

## Tests

- Unit tests: PNG decoding per color type and depth, rejection of interlaced files, grid index math, margin and spacing, out-of-range, duplicate and unknown ids, definition-file errors.
- Contract tests (`resources` test JAR, ADR-0045): the resource convention suite (key equality, idempotent release, missing resource) and the cache suite.
- Integration tests: headless load of a sheet through `resources` from a definition file.

## Acceptance criteria

- [x] Requirements covered.
- [x] API reviewed in the pull request.
- [x] Automated tests added and passing.
- [x] Complete Javadoc.
- [x] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [x] No forbidden coupling (REQ-CORE-005).
- [x] Android/DEX compatibility verified.
