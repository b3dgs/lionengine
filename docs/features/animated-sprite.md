# Feature: animated sprite

**Status: PROPOSED**

- Owner: to be filled in
- Roadmap phase: Phase 2, see `roadmap.md` (batch order: sprite sheet, sprite, animated sprite)
- Affected modules: `graphics`, `render-api` (sprite only), `resources`, `test-support`, `examples`
- Requirements: `REQ-ANIM-001` to `REQ-ANIM-003`
- Applicable ADRs: [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md) (PROPOSED)
- Open questions involved: `OQ-105` (`OQ-051` touched)

## Objective

Define, load and play animations on sprites driven by the simulation update step.

## Out of scope

Sprite transforms and color (see [`sprite.md`](sprite.md)); sheet representation (see [`sprite-sheet.md`](sprite-sheet.md)).

## Expected behavior

- Animation: frames, durations in seconds, loop mode; defined in code and loadable from a resource (format: `OQ-105`).
- Play, pause, resume, stop with specified state transitions; invalid transitions defined.
- Time advances only from the update step; extrapolated or interpolated render values are never fed back (`REQ-LOOP-006`).
- Frames come from sheet cells or regions of the same shared source (decision 4).

## Envisaged APIs / contracts

Responsibilities only; signatures are not fixed. See `architecture/contracts.md`.

## Lifecycle and concurrency

An animated sprite holds playback state only; images stay owned by the cache. Update on the runtime thread (`OQ-030`).

## Units and Android/DEX compatibility

Seconds for durations; pixels for frames. Any decoding API or dependency needs its purpose, license and DEX compatibility recorded first (`docs/android-compatibility.md`).

## Dependencies

Allowed: `core`, `resources`, `render-api` (`OQ-051`). Forbidden: AWT, OpenGL or Android types in `graphics` APIs.

## Tests

- Unit tests: animation state machine, frame selection, loop modes, invalid transitions.
- Contract tests (`test-support`): deterministic progress with a controllable clock.
- Integration tests: headless animation loaded from a resource, advanced by update steps.
- Platform/performance tests: color-transform parity and cache performance where relevant.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API validated.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
