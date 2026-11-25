# ADR-0054 — Decoded image size limits

- Status: ACCEPTED
- Date: 2026-11-25
- Decision makers: agent, under the project owner's explicit delegation of 2026-11-25 ("I let you define a decent limit, close to standard"); reversible by a superseding ADR
- Related requirements: `REQ-SPRITE-009`, `REQ-RES-004`
- Open questions resolved: none (gap found by sprite sheet batch 1, recorded in [`../features/sprite-sheet.md`](../features/sprite-sheet.md))
- Supersedes / superseded by: none; completes [ADR-0026](0026-graphics-pixels-cache-transforms-animation-and-commands.md) and [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md) (both unchanged)

## Context

An image file declares its size in its header, and a decoder allocates the pixels from that size before it reads the data. A file of a few hundred bytes can therefore declare an image of billions of pixels and exhaust the memory: the classic "decompression bomb". Sprite sheet batch 1 refused only images that do not fit one `int[]` (more than `Integer.MAX_VALUE` pixels, 8 GiB as a `PixelBuffer`), and recorded the missing limit as a gap: a game that loads untrusted images (mods, downloaded content) needs one, and no document gave its value.

The reference decoders bound the size in two ways:
- libpng refuses a width or a height above its user limits, `PNG_USER_WIDTH_MAX` and `PNG_USER_HEIGHT_MAX`, both 1,000,000 by default;
- Pillow bounds the pixel count with `Image.MAX_IMAGE_PIXELS`, 89,478,485 by default (2^30 / 4 / 3, that is 256 MiB at 3 bytes per pixel): it warns above it and refuses above twice that value.

The engine also holds per-row buffers whose size grows with the width (two rows of up to 8 bytes per pixel for 16-bit RGBA), so a pixel count alone does not bound them.

## Decision

1. **Two limits, checked from the header before any pixel is allocated.** Every image decoder (the `ImageDecoder` contract) refuses with its decoding exception (`ImageDecodingException`) an image:
   - whose width or height is above **1,000,000 pixels** (libpng's default user limits); this limit is fixed;
   - whose pixel count (width × height) is above the decoder's **pixel limit**, by default **89,478,485 pixels** (Pillow's default `MAX_IMAGE_PIXELS`).
2. **The pixel limit is set per decoder instance**, when the decoder is created: any positive value up to `Integer.MAX_VALUE`; a game that loads untrusted images, or that targets devices with little memory, sets a lower one. There is no warning level: an image above the limit is refused, as the engine is fail-fast (ADR-0017).
3. **Constants.** `PngDecoder.MAX_DIMENSION` (1,000,000) and `PngDecoder.DEFAULT_MAX_PIXELS` (89,478,485); the optional decoder modules (`graphics-imageio`, `graphics-android`) use the same values, through their own constructors.

## Options considered

- Pixel count and dimension limits, with the values of Pillow and libpng (chosen): the two most used open-source image libraries, so the engine refuses what a user would expect to be refused; the dimension limit also bounds the row buffers.
- A pixel count limit only (Pillow): leaves the row buffers of very wide images unbounded (up to 16 bytes per pixel of width for two 16-bit RGBA rows).
- A dimension limit only (libpng): 1,000,000 × 1,000,000 pixels is 4 TB as a `PixelBuffer`, so it does not stop a bomb.
- A game-oriented limit, for example the 16,384 × 16,384 texture size of current GPUs: 268 million pixels, larger than Pillow's limit and not a standard for decoders; the texture size limit belongs to the render backend.
- A memory budget in bytes: depends on the pixel format of each backend; the pixel count is what the header gives and what `PixelBuffer` holds (4 bytes per pixel).

## Consequences

- Positive consequences: a small file can no longer make the decoder allocate more than about 341 MiB (89,478,485 pixels × 4 bytes) by default, plus about 20 MB of row buffers; the values match what users know from other tools; a game can lower the pixel limit for untrusted content.
- Costs / constraints: images above 89,478,485 pixels need a decoder created with a higher limit; the default may still exceed the memory of small Android devices (an API 21 device often has a heap of 128 to 512 MiB), where a game lowers it.
- Impact on modules and APIs: `graphics` (`ImageDecoder` contract, `PngDecoder` constructor with a pixel limit and its two constants); the later decoder modules follow it.
- Impact on tests and documentation (including Android/DEX compatibility): `ImageDecoderContract` checks the pixel limit of every decoder; `PngDecoderTest` checks both limits; `GraphicsTest` on API 21; `REQ-SPRITE-009`, `features/sprite-sheet.md`, `contracts.md`, `api-index.md` and traceability updated. No new JDK API.

## Validation

Decided by the agent on 2026-11-25 under the owner's explicit delegation ("I let you define a decent limit, close to standard"); reversible by a superseding ADR.
