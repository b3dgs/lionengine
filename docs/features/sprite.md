# Feature: sprite

**Status: PROPOSED**

- Owner: to be filled in
- Roadmap phase: Phase 2, see `roadmap.md` (batch order: sprite sheet, sprite, animated sprite)
- Affected modules: `graphics`, `render-api` (sprite only), `resources`, `test-support`, `examples`
- Requirements: `REQ-SPRITE-001` to `REQ-SPRITE-008`
- Applicable ADRs: [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md) (ACCEPTED), [`ADR-0007`](../decisions/0007-resource-convention-keys-release-and-errors.md) (ACCEPTED)
- Open questions involved: `OQ-121`, `OQ-160` (`OQ-118` resolved; `OQ-011`, `OQ-012` touched)

## Objective

Load, cache, transform, color-transform and render a sprite through `render-api` commands, with many instances sharing one source.

## Out of scope

Animation (see [`animated-sprite.md`](animated-sprite.md)); actual batching, which stays a note here: sprites emit commands so batching can be added later behind the command abstraction (`REQ-SPRITE-008`, `OQ-108`).

## Expected behavior

- Load from a resource identifier through `resources`; defined errors for missing or invalid data.
- Shared immutable source (image + region) and lightweight instance (transform, color transform, location, alpha) (decision 4).
- Resize, translation, rotation with documented units, order and pivot (`OQ-104`); internal location used for placement, rotation and scaling.
- Color transformation (decision 1C): description in `graphics`; fade, tint, alpha through `render-api` parameters; palette swap, grading, mask bake on the CPU with cache; CPU fallback always possible (`OQ-102`).
- Template palette mapping yields cached color variants of one sprite (decision 2, `OQ-103`).
- Rendering emits `render-api` commands, never draws directly (decision 3B).

## Envisaged APIs / contracts

Responsibilities only; signatures are not fixed. See `architecture/contracts.md`.

## Lifecycle and concurrency

Sources are loaded through `resources` and held by the `graphics` cache, following the common convention (ADR-0005, `REQ-RES-004`, `OQ-118`, `OQ-101`); instances are lightweight and own no image. Loading thread and GPU upload: `OQ-107`.

## Units and Android/DEX compatibility

Pixels for images, and pixels of the logical frame for location, translation and pivot (conversion from meters: `OQ-012`); rotation follows the engine angle convention (`OQ-011`). Image decoders are platform-specific (`OQ-100`). No frontend type in the API (`REQ-SPRITE-007`). Any decoding API or dependency needs its purpose, license and DEX compatibility recorded first (`docs/android-compatibility.md`).

## Dependencies

Allowed: `core`, `resources`, `render-api` (ADR-0003; `OQ-051` resolved). Forbidden: AWT, OpenGL or Android types in `graphics` APIs.

## Tests

- Unit tests: transforms and order, pivot, palette mapping, color-transform description.
- Contract tests (`test-support`): sprite render on a fake backend, cache hit and release, CPU fallback.
- Integration tests: headless load, cache, render of two color variants; AWT/OpenGL/Android color parity.
- Platform/performance tests: color-transform parity and cache performance where relevant.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API validated.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
