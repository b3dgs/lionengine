# Feature: screen and display modes

**Status: PROPOSED**

- Owner: to be filled in
- Roadmap phase: Phase 2 (after the first rendering backend), see `roadmap.md`
- Affected modules: `platform-api`, `render-awt`, `render-opengl`, `platform-android`, `test-support`, `examples`
- Requirements: `REQ-DISPLAY-001` to `REQ-DISPLAY-008`
- Applicable ADRs: [`ADR-0002`](../decisions/0002-display-modes-resolution-and-output-filtering.md) (ACCEPTED), [`ADR-0006`](../decisions/0006-display-scaling-effective-result-and-fallback-orders.md) (ACCEPTED)
- Open questions involved: `OQ-096`, `OQ-119`, `OQ-122` (`OQ-090`, `OQ-091`, `OQ-120` resolved)

## Objective

Let a game request a screen mode, a display mode and a logical resolution through one platform-independent API, and read what the platform actually applied.

## Out of scope

Output filtering (see [`output-filtering.md`](output-filtering.md)); multi-monitor and HiDPI (`OQ-096`); persistence of user settings (game responsibility, ADR-0025); a "required" request flag (decision 2A).

## Expected behavior

- Capability query for `WINDOWED`, `FULL_WINDOW`, `FULL_SCREEN`.
- Unsupported screen mode: documented fallback order (ADR-0006), effective mode reported, no failure.
- Enumerate and select display modes (width, height in pixels; refresh rate in Hz; color depth in bits).
- Logical resolution independent of the display mode; scaling policy `FIT` (default), `INTEGER` (opt-in) or `STRETCH` (ADR-0006).
- Apply returns the accepted request without blocking; the effective result is delivered at the frame boundary and readable as current state (`REQ-DISPLAY-008`).
- Changes applied at a frame boundary and signaled by an event; simulation state untouched.

## Envisaged APIs / contracts

Responsibilities only; signatures are not fixed. See `architecture/contracts.md`.

## Lifecycle and concurrency

Changes are requested at any time and applied at the next frame boundary on the rendering thread (thread rules: `OQ-030`). Ownership of native windows stays with the platform implementation.

## Units and Android/DEX compatibility

Pixels, Hz, bits; world units unchanged. Own value types only; Android specifics isolated in `platform-android`. Check required per `android-compatibility.md`: not done yet.

## Dependencies

Allowed: `core`. Forbidden: AWT, OpenGL or Android types in `core` or `platform-api`.

## Tests

- Unit tests: value types, fallback order, scaling computation.
- Contract tests (`test-support`): effective result and reason, never throws on a missing capability, frame-boundary application.
- Integration tests: headless, fake capabilities.
- Platform/performance tests: real window per frontend, identified separately.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API validated.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example (list capabilities, request a mode, print the effective result), API-index entry, traceability updated.
- [ ] No forbidden coupling.
- [ ] Android/DEX compatibility verified.
