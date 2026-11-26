# ADR-0057 — Template color mapping: exact ARGB keys, applied first in the color transform, templates kept by the sheet

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the sprite batches ("Go for the next sprite batch with PR"), reversible by a superseding ADR
- Related requirements: `REQ-SPRITE-005`, `REQ-SPRITE-004`, `REQ-SPRITE-002`, `REQ-SHEET-003`
- Open questions resolved: none (details left open by ADR-0026 and ADR-0033, needed by sprite batch 4 of [`../features/sprite.md`](../features/sprite.md))
- Supersedes / superseded by: none; completes [ADR-0026](0026-graphics-pixels-cache-transforms-animation-and-commands.md) (decision 4), [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md) and [ADR-0056](0056-sprite-color-transforms-mask-grading-capability-and-bake.md) (all unchanged)

## Context

`REQ-SPRITE-005` asks for color variants of one sprite, such as team colors, from a template color mapping authored as exact key colors mapped to target colors in the sheet definition, sharing the source and caching each variant by (source image, mapping). ADR-0026 (decision 4) chose exact key colors; ADR-0033 named the `templates` field of the sheet definition, written `{"#RRGGBB": "#RRGGBB"}`; ADR-0056 said the palette swap is one more static part of `ColorTransform`, baked by `ImageCache.bake`. The sheet loader of sprite sheet batch 4 already accepts `#RRGGBB` and `#AARRGGBB` and checks the templates, but keeps nothing.

No document says:
- whether a key written `#RRGGBB` matches only opaque pixels or any alpha;
- where the mapping sits in the order of the transform;
- whether a mapping chains (`A → B`, `B → C`);
- what makes two mappings equal, and so share one cached variant;
- how a game reaches the templates of a loaded sheet.

What other tools do:
- **Indexed palettes.** Classic 2D games and editors such as Aseprite swap a palette by index, before any other effect.
- **Exact-color replacement.** Godot and Unity palette-swap shaders, and the "replace color" tools of image editors, replace each pixel equal to a key color, in one pass over the source colors.

## Decision

1. **Value type.** `ColorMapping` (`graphics`) is an immutable value of entries from a key color to a target color, packed `0xAARRGGBB`, straight alpha.
   - Built from `ColorMapping.NONE` with `with(key, target)`; a new entry for a key replaces its target.
   - An entry mapping a color to itself changes nothing and is not kept, so mappings that change the same colors the same way are equal.
   - Entries are kept by ascending key color read as an unsigned number; `size()`, `getKey(index)`, `getTarget(index)`.
2. **Matching.** A pixel matches a key color when its four channels are equal; it then takes the target color, alpha included. Any other pixel is kept.
   - In a definition, `#RRGGBB` is the opaque color `#FFRRGGBB`, for a key and for a target.
   - Every pixel is mapped once, from the source colors: entries do not chain, so two colors can be swapped.
3. **Place in the transform.** The mapping is the first part of `ColorTransform`: mapping, then grading, then mask, then tint, then alpha.
   - It belongs to the static part: baked on every backend, never per frame.
   - `withMapping(mapping)` and `getMapping()`; the canonical text starts with `mapping(#AARRGGBB=#AARRGGBB,...)`, entries in their kept order, upper case.
4. **Cache.** A variant is baked by `ImageCache.bake(image, transform)` as in ADR-0056. Its discriminator contains the text of the mapping, not the name of the template, so templates mapping the same colors share one variant.
5. **Templates of a sheet.** A sheet read from a definition keeps its templates as `ColorMapping` values:
   - `SpriteSheet.getTemplate(name)` returns one, or throws `IllegalStateException` for a name the sheet does not have, as a group does for an unknown id.
   - `hasTemplate(name)`, and `getTemplateNames()` in the order of the file.
   - A sheet added in code has no template; a game builds its mappings in code.
   - A key color given twice in one template is an error of the file, also when written in two forms (`#FF00FF` and `#FFFF00FF`).
6. **Drawing.** A sprite draws a variant with `new Sprite(sheet, variant)` (ADR-0056); a sprite never bakes, so a render never allocates.

## Options considered

- Matching of a key written `#RRGGBB`:
  - The opaque color, exact on four channels (chosen): one rule, the same for keys and targets, as written in the file.
  - Any alpha, keeping the pixel alpha (not chosen): two rules, and a key written `#AARRGGBB` would then mean something else.
- Place of the mapping:
  - First (chosen): the keys are the colors of the source file. A red soldier can then be grayed or flashed like any other color.
  - After the grading (rejected): the grading would change the key colors before they are matched.
- Chaining:
  - One pass over the source colors (chosen): a swap of two colors works, and the result does not depend on the order of the entries.
  - Entries applied one after the other (rejected): the result would depend on the order of the file.
- Where templates live:
  - On the sheet, read from its definition (chosen): the definition already holds them, and the sheet is what a game loads.
  - A separate template registry (rejected): a second lookup for one sheet.
  - Applied automatically per template at load (rejected): it would bake images nobody draws.

## Consequences

- Positive consequences:
  - Team colors cost one cached image per team and source, shared by every scene, without an image file per team.
  - A team color combines with a hit flash, a grading, a tint and a fade.
  - The same description runs on every backend.
- Costs / constraints:
  - Exact colors cannot express smooth gradients or anti-aliased art; the mask layer stays a later extension (ADR-0026, ADR-0033).
  - A definition giving one key color twice, in two forms, is now refused; before this batch it was accepted.
- Impact on modules and APIs:
  - `graphics`: `ColorMapping`; `ColorTransform.withMapping` and `getMapping`; `SpriteSheet.getTemplate`, `hasTemplate` and `getTemplateNames`.
  - The loader keeps the templates; the internal `SheetCheck` interface is replaced by the definition itself.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: unit tests of the mapping (exact match, single pass, order, equality, text, no allocation), of its place in the transform and of the split; loader and sheet tests of the templates; an integration test loading a definition and drawing two variants.
  - Documentation: `features/sprite.md`, `features/sprite-sheet.md`, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md`, the glossary and the traceability.
  - Android/DEX: plain Java arithmetic and arrays, `List.copyOf` (verified on API 21); the device test runs the mapping and a template variant.

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the sprite batches; reviewed in the pull request of sprite batch 4.
