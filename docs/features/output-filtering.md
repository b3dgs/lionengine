# Feature: output filtering

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2, after screen and display modes
- Affected modules: `render-api`, `render-awt`, `render-opengl`, `resources`, `test-support`, `examples`
- Requirements: `REQ-FILTER-001` to `REQ-FILTER-004`, `REQ-DISPLAY-004`, `REQ-DISPLAY-005`, `REQ-DISPLAY-008`
- Applicable ADRs: [`ADR-0002`](../decisions/0002-display-modes-resolution-and-output-filtering.md), [`ADR-0006`](../decisions/0006-display-scaling-effective-result-and-fallback-orders.md), [`ADR-0025`](../decisions/0025-filter-implementation-shaders-and-display-settings.md), [`ADR-0032`](../decisions/0032-definition-files-registry-ids-resource-providers-dependency-policy.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md), [`ADR-0046`](../decisions/0046-display-state-owned-by-the-platform-and-presentation-geometry.md), [`ADR-0047`](../decisions/0047-render-api-texture-pixel-data-and-shader-resource-keys.md), [`ADR-0049`](../decisions/0049-interface-catalog-and-contract-skeleton.md), [`ADR-0062`](../decisions/0062-filter-chain-shape-and-per-filter-fallback.md)
- Open questions involved: none

## Objective

Let a game choose how the logical frame is filtered when presented on the output surface, with the same code on every platform.

## Out of scope

Per-layer filtering (ADR-0002, decision 3A); screen and display modes (see [`display-modes.md`](display-modes.md)); texture sampling of sprites (nearest-neighbor, ADR-0033).

## Expected behavior

- Standard identifiers: `NONE`, bilinear, `HQ2X`, CRT, with a capability query. `NONE` samples nearest-neighbor when the logical frame is scaled; there is no separate nearest identifier.
- Filters form an ordered chain: scaling filters (`NONE`, bilinear, `HQ2X`) resample the logical frame to the output size; post filters (CRT) run on the scaled output; no filter changes the scaling policy.
- Fallback per filter: CRT skipped; `HQ2X` → bilinear → `NONE`; the platform resolves the chain against the filters of the attached render backend and reports each fallback in its effective result (ADR-0046).
- `render-awt` implements `NONE`, bilinear and `Scale2x` on the CPU (`Scale2x` stands in for `HQ2X` until its license record exists) and reports CRT unsupported. `render-opengl` implements all four with shaders.
- Shaders: GLSL ES 1.00 feature set, no `#version` line or precision qualifier in the file (the backend prepends `#version 100` or `#version 120`), `.vert` and `.frag` resources loaded through `resources`; standard uniforms: source texture, source size, output size (pixels), presentation time (seconds).
- Custom filters: a registry id (`namespace:name`), registered explicitly on the backend at startup, declared as scaling or post filter, providing a fragment shader resource (`ResourceKey`, ADR-0047), a CPU implementation over `int[]` ARGB buffers, or both; a backend lacking the needed implementation skips it and reports the skip.
- Changes apply at a frame boundary and are reported through the effective result; simulation state is untouched.

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Display modes, resolution and output filtering); canonical type names in [ADR-0037](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md) (`OutputFilter`, `ScalingPolicy`); signatures are written by the batch: filter identifier, chain, capability query, custom filter contract (CPU and shader forms). `OutputFilter` and the custom filter contract are declared by the contract skeleton (ADR-0049, [`contract-skeleton.md`](contract-skeleton.md)); the batches below implement them.

## Batches (order)

1. Identifiers, chain, capability query and fallback with fake backends (`REQ-FILTER-001`, `REQ-FILTER-002`).
2. CPU filters in `render-awt`: `NONE`, bilinear, `Scale2x` (`REQ-FILTER-004`).
3. Custom filter extension point (`REQ-FILTER-003`).
4. `HQ2X` after its license record; GPU filters and CRT with `render-opengl` (Phase 4).

## State (2026-11-26)

Batch 1 (2026-11-26), identifiers, chain, capability query and fallback, with the example `OutputFilterExample` and its API-index row. The identifiers (`OutputFilter`, `FilterKind`), the capability query (`RenderBackend.isSupported(OutputFilter)`, `NONE` always implemented) and the resolution against the attached backend in the shared `DisplayState` came with the contract skeleton and platform and rendering batch 1. This batch adds what the page leaves open, decided by ADR-0062 (owner delegation) within ADR-0006 and ADR-0033:
- Chain shape: at most one scaling filter, before every post filter, no filter twice. `DisplayRequest` refuses another shape with an `IllegalArgumentException`, a programming error and not a missing capability. Without a scaling filter, the frame is scaled with `NONE`.
- Fallback order on the identifier: `OutputFilter.fallback()` gives bilinear for `HQ2X`, `NONE` for bilinear and every other scaling filter (custom ones included), and `null` for `NONE` and every post filter.
- Resolution: `DisplayState` walks each requested filter along its fallbacks to the first one the backend implements. A replaced scaling filter is reported as `"Output filter lionengine:hq2x not implemented by the render backend, lionengine:bilinear used"`, and a missing post filter is skipped as before (`"Output filter not implemented by the render backend, skipped: lionengine:crt"`). The reasons follow the chain order, and `NONE` stays out of the effective chain. A missing capability never throws.
- Tests: `OutputFilterTest.testFallback`; `DisplayRequestTest.testChainShape`; `DisplayStateTest` (`testFilterWithBackend`, `testFilterFallback`: `HQ2X` to bilinear, to `NONE`, a custom scaling filter to `NONE`); `PlatformContract` (`testFiltersOfBackend`, `testFilterFallback`: applied at the frame boundary and announced to the display listeners) on the headless platform and, under a display, on `AwtPlatform`; `AwtDisplayTest.testFilters`; `OutputFilterExampleTest` with the fake `RecordingBackend`; device tests `RenderTest.testFilterFallback` and `PlatformTest` on API 21.

## Lifecycle and concurrency

Filter resources (shaders, intermediate buffers) belong to the backend, are created on the render thread and recreated after a context loss (ADR-0019). Requests come from the runtime thread and apply at the next frame boundary.

## Units and Android/DEX compatibility

Pixels; time uniform in seconds. Own implementations only, no third-party filter library; `HQ2X` code ported from a reference implementation needs its row in [`../dependencies.md`](../dependencies.md) before merge. Shaders use the common subset of desktop OpenGL 2.1 and OpenGL ES 2.0.

## Dependencies

`render-api` on `core` and `resources` (shader keys, ADR-0047); `platform-api` on `render-api` (filter chain in the display request, ADR-0046); backends on `render-api`, `platform-api`; `render-opengl` on `resources` to open shaders. Forbidden: AWT, OpenGL or Android types in `render-api`.

## Tests

- Unit tests: identifiers, capability query, chain ordering and fallback, CPU scalers on small reference images.
- Contract tests (`render-api` test JAR, ADR-0045): fallback reported, never throws on a missing capability, frame-boundary application, custom filter on fake GPU and CPU backends.
- Integration tests: headless, fake capabilities.
- Platform tests: golden images for `NONE`, bilinear, `HQ2X`/`Scale2x` (AWT, OpenGL); CRT on OpenGL.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API reviewed in the pull request.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example (request a filter, print the effective result), API-index entry, traceability updated.
- [ ] No forbidden coupling.
- [ ] Android/DEX compatibility verified.
