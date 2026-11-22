# Feature: output filtering

**Status: PROPOSED**

- Owner: to be filled in
- Roadmap phase: Phase 2, after screen and display modes
- Affected modules: `render-api`, `render-awt`, `render-opengl`, `test-support`, `examples`
- Requirements: `REQ-FILTER-001` to `REQ-FILTER-003`, `REQ-DISPLAY-004`, `REQ-DISPLAY-005`
- Applicable ADRs: [`ADR-0002`](../decisions/0002-display-modes-resolution-and-output-filtering.md) (PROPOSED)
- Open questions involved: `OQ-092`, `OQ-093`, `OQ-094` (`OQ-006` related)

## Objective

Let a game choose how the logical frame is filtered when presented on the output surface, with the same code on every platform.

## Out of scope

Per-layer filtering (decision 3A); screen and display modes (see [`display-modes.md`](display-modes.md)).

## Expected behavior

- Standard identifiers: `NONE`, nearest, bilinear, `HQ2X`, CRT, with a capability query.
- Filters form an ordered chain; an unsupported filter is skipped or replaced per a documented fallback, and this is reported.
- Changes are applied at a frame boundary and signaled by an event; simulation state untouched.
- An extension point for custom filters or shaders exists; its form is not fixed.

## Envisaged APIs / contracts

Responsibilities only; signatures are not fixed. See `architecture/contracts.md`.

## Lifecycle and concurrency

Filter resources (shaders, buffers) belong to the backend and are invalidated on context loss (see the frontend contract). Calling thread: `OQ-030`.

## Units and Android/DEX compatibility

Pixels only. `HQ2X`/CRT sources and licenses: `OQ-093`; shader portability GLSL vs GLSL ES and shader assets through `resources`: `OQ-094`. Any third-party filter library needs license and DEX verification before use.

## Dependencies

Allowed: `core`. Forbidden: AWT, OpenGL or Android types in `render-api`.

## Tests

- Unit tests: identifiers, capability query, chain ordering and fallback.
- Contract tests (`test-support`): fallback reported, never throws on a missing capability, frame-boundary application.
- Integration tests: headless, fake capabilities.
- Platform tests: golden images for none, bilinear, `HQ2X` (AWT, OpenGL); CRT on OpenGL.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API validated.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example (request a filter, print the effective result), API-index entry, traceability updated.
- [ ] No forbidden coupling.
- [ ] Android/DEX compatibility verified.
