# ADR-0047 — `render-api` boundary types: texture pixel data and shader resource keys

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, at the project owner's request of 2026-11-23 to review the architecture with the dedicated skills and apply the changes the review suggests, so that the base skeleton of each module and its interfaces can be defined; reversible by a superseding ADR
- Related requirements: `REQ-CORE-005`, `REQ-SPRITE-010`, `REQ-FILTER-003`, `REQ-RT-005`
- Open questions resolved: none (defect found by the skeleton-readiness review of 2026-11-23)
- Supersedes / superseded by: changes the `render-api` row of [`../architecture/modules.md`](../architecture/modules.md) (`core` → `core`, `resources`); completes [ADR-0019](0019-loading-resource-policy-and-memory.md), [ADR-0026](0026-graphics-pixels-cache-transforms-animation-and-commands.md) and [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md) (all unchanged)

## Context

Two `render-api` contracts name types that `render-api` cannot see:

- **Textures.** ADR-0026 (decision 1) gives `graphics` its own pixel buffer (`PixelBuffer`, ADR-0037), and [`features/platform-and-rendering.md`](../features/platform-and-rendering.md) says textures are created "from `graphics` pixel buffers through the backend". But `graphics` depends on `render-api`, and the render backends depend on `render-api` and `platform-api`, never on `graphics`. A texture contract in `render-api` that took a `PixelBuffer` would create a cycle.
- **Custom filters.** ADR-0033 (section 2) says a custom filter "provides a fragment shader resource key". `ResourceKey` is a `resources` type, and `render-api` depends on `core` only.

## Decision

1. **Texture pixel data is primitive at the render boundary.** The `render-api` texture contract creates a texture from a width and a height in pixels and an `int[]` in the layout of ADR-0033: packed `0xAARRGGBB`, row-major, origin at the top-left, stride equal to the width, straight alpha. It returns a texture handle (an `int`, ADR-0037). Releasing a handle is idempotent. An explicit prepare uploads before first use (ADR-0026, decision 8). `PixelBuffer` stays a `graphics` type; `graphics` passes the array of its cached source.
2. **Ownership.** The array is shared read-only, so no copy is made: neither side modifies it after the call. The backend may keep it as the CPU copy used to recover a lost context (ADR-0019) until the texture is released. The `graphics` cache releases the texture when it releases the source. Create, prepare and release are called on the runtime thread (cache rule, ADR-0018); the GPU upload runs on the render thread, which is the runtime thread by default (ADR-0026).
3. **`render-api` depends on `resources`** (row: `core`, `resources`). The custom filter contract names its fragment shader with a `ResourceKey`. `render-api` opens nothing itself: a GPU backend opens the key through `Resources` (`render-opengl` already depends on `resources`).

## Options considered

- Textures:
  - Primitive pixel data (chosen): no new edge, and the layout is already fixed by ADR-0033.
  - Move the pixel type to `render-api` (rejected): supersedes the placement of ADR-0026 and ADR-0037, and puts a decoded-image type into the rendering contracts.
  - Render backends depend on `graphics` (rejected): creates a cycle.
- Array ownership:
  - Shared read-only (chosen): no copy of large images, and cached sources are already immutable.
  - Copy at creation (rejected): every texture would be held twice in memory.
- Shader key:
  - `ResourceKey`, with the new edge (chosen): typed and validated, the canonical key type of ADR-0007; `resources` depends on `core` only, so no cycle.
  - A `String` in key form, without the edge (rejected): an invalid key would be detected only when a backend opens it.

## Consequences

- Positive consequences: the texture and custom-filter contracts compile as specified; no image is copied at upload.
- Costs / constraints: every render backend and, through ADR-0046, every platform implementation compiles against `resources`. A caller that modifies an array after handing it over breaks the contract; this is documented on the texture contract and in `graphics`.
- Impact on modules and APIs: `render-api` row and diagram; the texture contract (its name is chosen by the contract skeleton, ADR-0049); the custom filter contract names `ResourceKey`.
- Impact on tests and documentation (including Android/DEX compatibility): `modules.md`, `contracts.md`, `interfaces.md`, `features/platform-and-rendering.md`, `features/output-filtering.md`, `features/sprite-sheet.md`. No new JDK API.

## Validation

Decided by the agent on 2026-11-23 at the owner's request to apply the review's changes; reversible by a superseding ADR.
