# Feature: animated sprite

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2, see `roadmap.md` (batch order: sprite sheet, sprite, animated sprite)
- Affected modules: `graphics`, `render-api`, `resources`, `test-support`, `examples`
- Requirements: `REQ-ANIM-001` to `REQ-ANIM-003`, `REQ-SPR-002`
- Applicable ADRs: [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [`ADR-0017`](../decisions/0017-fixed-step-game-loop.md), [`ADR-0026`](../decisions/0026-graphics-pixels-cache-transforms-animation-and-commands.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md)
- Open questions involved: none

## Objective

Define, load and play animations on sprites driven by the simulation update step.

## Out of scope

Sprite transforms and color (see [`sprite.md`](sprite.md)); sheet representation (see [`sprite-sheet.md`](sprite-sheet.md)).

## Expected behavior

- An animation is a list of cell indices (`frames`), a frame duration in seconds (`frameDuration`), optional per-frame durations (`frameDurations`, one per frame, overriding `frameDuration`) and a loop mode: `once` (stops on the last frame), `loop` (restarts at the first) or `ping-pong` (plays forward then backward, the end frames shown once per turn).
- Animations are built in code or read from the `animations` object of the sprite sheet definition; invalid data (empty frames, negative or zero duration, index out of range, unknown loop mode) fails with the definition exception.
- States: stopped, playing, paused, finished (`once` only). `play` starts from the first frame; `pause` keeps the frame; `resume` continues from a pause; `stop` returns to the first frame in the stopped state. A call that makes no sense in the current state (for example `resume` while playing) throws `IllegalStateException`.
- Time advances only from the update step with the constant step in seconds; several frames may be skipped in one step when the step is longer than a frame; rendering never changes the animation state (`REQ-LOOP-006`).
- Frames come from cells of the same shared source.

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Images, sprites and animations); canonical type names in [ADR-0037](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md); signatures written by the batch: animation value, animator state machine, animated sprite.

## Batches (order)

1. Animation value and loader from the sheet definition (`REQ-ANIM-001`). The sheet loader of [`sprite-sheet.md`](sprite-sheet.md) batch 4 already checks the `animations` of a definition, frames against the cells included, and keeps nothing; this batch builds the animation values from them.
2. Animator state machine and loop modes (`REQ-ANIM-002`).
3. Animated sprite driven by the update step (`REQ-ANIM-003`).

## Lifecycle and concurrency

An animated sprite holds playback state only; images stay owned by the `graphics` cache. Runtime thread only.

## Units and Android/DEX compatibility

Seconds for durations; cell indices for frames. Pure Java, no platform type.

## Dependencies

`core`, `resources`, `render-api`. Forbidden: AWT, OpenGL or Android types in `graphics` APIs.

## Tests

- Unit tests: state machine including invalid transitions, frame selection for each loop mode, per-frame durations, frame skipping with a long step.
- Contract tests (`test-support`): deterministic progress with a controllable clock.
- Integration tests: headless animation loaded from a sheet definition, advanced by update steps and rendered.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API reviewed in the pull request.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
