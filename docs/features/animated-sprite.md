# Feature: animated sprite

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2, see `roadmap.md` (batch order: sprite sheet, sprite, animated sprite)
- Affected modules: `graphics`, `render-api`, `resources`, `test-support`, `examples`
- Requirements: `REQ-ANIM-001` to `REQ-ANIM-003`, `REQ-SPR-002`
- Applicable ADRs: [`ADR-0003`](../decisions/0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [`ADR-0017`](../decisions/0017-fixed-step-game-loop.md), [`ADR-0026`](../decisions/0026-graphics-pixels-cache-transforms-animation-and-commands.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md), [`ADR-0058`](../decisions/0058-animator-transitions-finish-and-frame-advance.md)
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

## State (2026-11-26)

Batch 1 (2026-11-26), the animation value and its loader, with the example `AnimationDefinitionExample` and its API-index row. Names and details chosen by the batch, within this page, ADR-0026 and ADR-0033:
- `Animation` (`graphics`, canonical name of ADR-0037): an immutable value of frames (cell indices, at least one, each zero or positive, a cell may repeat), a duration per frame in seconds (positive and finite) and a loop mode. `new Animation(frames, frameDuration, loop)` gives every frame the same duration, `new Animation(frames, frameDurations, loop)` one per frame; the arrays are copied. Invalid values are an `IllegalArgumentException`. `getFrameCount()`, `getFrame(index)`, `getFrameDuration(index)` (`IndexOutOfBoundsException` outside the frames), `getLoop()`. Equality is by frames, resolved durations and loop mode, so one duration for every frame equals the same duration given per frame.
- `AnimationLoop` (`graphics`): `ONCE`, `LOOP`, `PING_PONG`, with `getName()` giving the word of the definition (`once`, `loop`, `ping-pong`).
- Loading: the sheet loader keeps the `animations` of a definition as `Animation` values, `frameDurations` overriding `frameDuration`, frames checked against the cells of the sheet; `SpriteSheet.getAnimation(name)` (`IllegalStateException` for a name the sheet does not have), `hasAnimation(name)` and `getAnimationNames()` (order of the file). A sheet added in code has none. The errors of the file stay those of [`sprite-sheet.md`](sprite-sheet.md) batch 4 (`DefinitionException` with file, line, column and path); a duration that is not finite is refused too.
- An animation built in code is not bound to a sheet: batch 3 checks its frames against the cells of the sheet of the sprite that plays it. An animation only describes; time advances in the animator of batch 2.
- Tests: `AnimationTest` (both constructors, copies, equality, text, loop names, invalid values, indexes outside); `SheetLoaderTest.testFullDefinition` and `testDefaults` (animations kept, durations resolved); `SpriteSheetTest` (`testNoAnimation`, `testAnimations`); `AnimationDefinitionExampleTest`; device test `GraphicsTest.testAnimation` on API 21.

Batch 2 (2026-11-26), the animator, with the example `AnimatorExample` and its API-index row. The details this page leaves open follow ADR-0058 (owner delegation), within this page and ADR-0026:
- `Animator` (`graphics`, canonical name of ADR-0037) and `AnimatorState` (`STOPPED`, `PLAYING`, `PAUSED`, `FINISHED`). An animator is created stopped, without animation. `play(animation)` and `stop()` are valid in every state: `play` starts from the first frame, also when already playing; `stop` returns to the first frame. `pause()` is valid only while playing and keeps the frame and the time spent in it; `resume()` only while paused. Any other call throws `IllegalStateException` naming the state, and changes nothing.
- `update(stepSeconds)` advances only a playing animator; the step is zero or positive and finite (`IllegalArgumentException` otherwise). The time spent in the frame grows by the step; each time it reaches the duration of the frame, that duration is taken from it and the next frame starts, so the time left over carries into the next frame and a long step crosses several frames.
- Loop modes: `loop` restarts at the first frame after the last; `ping-pong` turns back at both ends, each end frame shown once per turn (`0 1 2 1 0 1 …`; two frames alternate; one frame stays); `once` becomes finished when its last frame has been shown for its full duration, stays on it and drops the time left over.
- Reading: `getState()`, `getAnimation()` (`null` before the first play), `getFrameIndex()`, `getFrame()` (the cell to draw; `IllegalStateException` without animation), `getFrameTime()`. Reading changes nothing; updating and reading allocate nothing.
- Tests: `AnimatorTest` (every call in every state, each loop mode, per-frame durations, carried time, long steps, the end of `once`, 1,000 short steps against 250 long ones, reading, invalid steps, no allocation in 100,000 updates); `AnimatorExampleTest`; device test `GraphicsTest.testAnimator` on API 21.

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
