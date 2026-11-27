# ADR-0056 — Sprite color transforms: mask, grading, per-frame capability and CPU bake

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: project owner (decisions 1 and 2, answered on 2026-11-26); agent for the rest, under the owner's standing delegation of the sprite batches ("Go next batch with PR"), reversible by a superseding ADR
- Related requirements: `REQ-SPRITE-004`, `REQ-SPRITE-007`, `REQ-SPRITE-010`, `REQ-SPRITE-002`
- Open questions resolved: none (details left open by ADR-0003 and ADR-0026, needed by sprite batch 3 of [`../features/sprite.md`](../features/sprite.md))
- Supersedes / superseded by: none; completes [ADR-0003](0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [ADR-0026](0026-graphics-pixels-cache-transforms-animation-and-commands.md) and [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md) (all unchanged)

## Context

`REQ-SPRITE-004` asks for sprite color transformations, "fade (alpha), mask, grading", described independently of the backend. Tint and alpha are per-frame parameters of a render command behind a typed capability query of `render-api`, and static transforms (palette swap, grading, mask bake) are baked on the CPU and cached. A backend can always fall back to the CPU path (ADR-0003, ADR-0026 decision 3). The command entry already carries a tint (`int` ARGB, white for none) and an alpha (`float` in `[0, 1]`) (ADR-0033). No document says what "mask" and "grading" do, what the capability query looks like, how a transform is split between the two paths, or how a baked variant is keyed in the image cache.

What other engines do:
- **Tint and alpha.** Godot (`modulate`), Unity (`SpriteRenderer.color`) and libGDX (`Batch.setColor`) multiply the texture colors and the opacity by a color per draw.
- **Hit flash.** Godot and Unity games usually draw it with a shader that blends every visible pixel toward a color by an amount.
- **Grading.** Android `ColorMatrix`, SVG `feColorMatrix` and CSS filters apply a 4 by 5 matrix over red, green, blue and alpha plus offsets.
- **Typed capability queries.** OpenGL extension queries and Vulkan features report what the device does natively; the caller picks another path otherwise.

## Decision

1. **Mask (owner).** A mask is a silhouette fill: every pixel is blended toward one color by an amount in `[0, 1]`, keeping its alpha: `rgb' = rgb + (maskRgb - rgb) × amount`. An amount of `0` changes nothing; `1` gives the silhouette, as for a hit flash or a shadow. The alpha bits of the mask color are ignored.
2. **Grading (owner).** Grading is a 4 by 5 color matrix, row-major, as in Android `ColorMatrix`.
   - Formula: `R' = m0·R + m1·G + m2·B + m3·A + m4`, then `G'`, `B'` and `A'` from the next rows.
   - Units: channels in `[0, 255]`, offsets in channel units, each result clamped to `[0, 255]`.
   - The identity matrix changes nothing.
   - The saturation matrix uses Android's luminance weights (`0.213`, `0.715`, `0.072`): `0` gives gray, `1` changes nothing.
3. **Description.** `ColorTransform` (`graphics`) is an immutable value holding a grading matrix (`ColorMatrix`), a mask (color and amount), a tint (ARGB) and an alpha (`[0, 1]`); `ColorTransform.NONE` holds none of them.
   - Order: grading, then mask, then tint, then alpha.
   - Static part: grading and mask, rounded to 8 bits per channel before the per-frame part.
   - Per-frame part: tint and alpha. The tint multiplies the four channels (`c × t / 255`), as the command buffer does; the alpha multiplies the opacity.
   - Path independence: baking the whole transform gives the same pixels as baking its static part and then applying the per-frame part. The description is independent of the path.
   - Rounding: per channel, in `double`, half up, clamped.
   - Equality is by value, and `-0.0` is stored as `0.0`. The canonical text of the transform lists its non-default parts in that order.
4. **Capability query (`render-api`).**
   - Types: `ColorEffect` (`TINT`, `ALPHA`) names the per-frame effects of a command entry. `ColorCapability.isSupported(ColorEffect)` tells whether a backend applies one per frame.
   - Every `RenderBackend` is a `ColorCapability`, as it reports its output filters.
   - A backend that does not support an effect draws its entries as if the field kept its default (no tint, full opacity).
   - `render-awt` supports both effects. The recording backend of `test-support` supports both by default, and each can be switched off for the tests.
5. **Paths.**
   - Split: `ColorTransform.getFramePart(capability)` keeps the tint and the alpha the capability applies per frame. `getBakedPart(capability)` keeps everything else: grading and mask always, tint or alpha when the backend cannot apply them.
   - CPU path: `ImageCache.bake(image, transform)` returns a new handle on a variant computed on the CPU. The variant is cached under the key of the source and the discriminator of the source followed by `+` and the canonical text of the transform. Every code that bakes the same transform of the same image shares it and releases its own handle. An identity transform returns a new handle on the image itself.
   - Per-frame path: `Sprite.setTint(argb)` and `Sprite.setAlpha(alpha)`, written into every command and allocating nothing.
   - Variants of a sheet: `new Sprite(sheet, image)` draws the frames of a sheet from another image of the same size, such as a baked variant of the sheet's image.
6. **No implicit bake.** A sprite never bakes, so a render never allocates. A game that fades on a backend without per-frame alpha bakes the steps it wants; each distinct transform is one cached image. The engine does not quantize values.

## Options considered

- Mask:
  - Silhouette fill with an amount (chosen by the owner).
  - Color key making one exact color transparent (not chosen: PNG images carry their alpha).
  - Both (not chosen).
- Grading:
  - 4 by 5 color matrix (chosen by the owner): brightness, contrast, grayscale, sepia and channel swaps in one type.
  - Gain and offset per channel, as Flash `ColorTransform` (not chosen: no grayscale or saturation).
  - Lookup tables per channel (not chosen: no channel mixing).
- Capability:
  - An interface extended by `RenderBackend`, with an enum of effects (chosen): typed, extensible with more effects, and `graphics` depends only on the small interface.
  - Two boolean methods (rejected: one method per future effect).
  - A capability looked up by class (rejected: there is one value per effect, not one object per capability).
- Fallback:
  - Explicit split and bake by the game (chosen): no allocation in the render phase, and the game decides what it caches.
  - A sprite that bakes on demand (rejected: it would own images, against "instances own no image", and allocate when a value changes).
  - Quantized automatic fades (rejected: a value no document states).

## Consequences

- Positive consequences:
  - Tint and fade cost nothing per frame on every current backend.
  - A hit flash, a silhouette or a grayscale look is baked once and shared.
  - The same description runs on any backend, a CPU-only one included.
  - The palette swap of sprite batch 4 adds one more static part to the same description and the same bake.
- Costs / constraints:
  - Every `RenderBackend` implements one more method.
  - A call `isSupported(null)` is ambiguous at compile time and must be cast.
  - A fade on a backend without per-frame alpha costs one cached image per step baked.
- Impact on modules and APIs:
  - `render-api`: `ColorEffect`, `ColorCapability`, `RenderBackend` extending it; the contract suite checks the query.
  - `render-awt`: `AwtRenderBackend.isSupported(ColorEffect)`.
  - `test-support`: `RecordingBackend.setSupported(ColorEffect, boolean)`.
  - `graphics`: `ColorTransform`, `ColorMatrix`, `ImageCache.bake`, `Sprite` tint, alpha and the sheet-variant constructor.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: unit tests of the matrix, the mask, the order, the rounding and the split; bake and sharing tests; sprite command tests with and without the capability; an AWT parity test of the per-frame tint and alpha against the CPU formula.
  - Documentation: `features/sprite.md`, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md` and the traceability.
  - Android/DEX: plain Java arithmetic only.

## Validation

- Decisions 1 and 2: answered by the project owner on 2026-11-26.
- The rest: decided by the agent under the owner's delegation of the sprite batches.
