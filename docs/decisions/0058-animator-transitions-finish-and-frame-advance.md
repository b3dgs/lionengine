# ADR-0058 — Animator: valid calls per state, end of a `once` animation, frame advance and ping-pong order

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the animated-sprite batches ("continue to next batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-ANIM-002`, `REQ-ANIM-003`, `REQ-SPR-002`
- Open questions resolved: none (details left open by ADR-0026 and [`../features/animated-sprite.md`](../features/animated-sprite.md), needed by its batch 2)
- Supersedes / superseded by: none; completes [ADR-0026](0026-graphics-pixels-cache-transforms-animation-and-commands.md) (decision 6, unchanged)

## Context

The feature page fixes the four states of an animation (stopped, playing, paused, finished), what `play`, `pause`, `resume` and `stop` do, and that "a call that makes no sense in the current state (for example `resume` while playing) throws `IllegalStateException`". It also says time advances from the update step only, several frames may be skipped in one step, and `ping-pong` shows the end frames once per turn.

It does not say:
- which calls make no sense in which state, apart from `resume` while playing;
- when a `once` animation becomes finished: on reaching its last frame, or once that frame has been shown for its duration;
- what happens to the time left over when a frame ends inside a step;
- the exact order of the frames of a `ping-pong` animation, including one with a single frame;
- what an update does outside the playing state.

What other engines do:
- **Godot** (`AnimatedSprite2D`) emits `animation_finished` after the last frame has been shown for its duration, and keeps the last frame. Pausing keeps the frame and the time spent in it.
- **Aseprite** exports ping-pong tags as `0 1 2 1 0 1 2 …`: the end frames are not repeated.
- **Fixed-step engines** carry the time left over by a frame into the next one, so the speed of an animation does not depend on the step length.

## Decision

1. **Valid calls per state.** `Animator` (`graphics`) is the state machine; `AnimatorState` names its states.

   | Call | Stopped | Playing | Paused | Finished |
   |---|---|---|---|---|
   | `play(animation)` | playing, first frame | playing, first frame | playing, first frame | playing, first frame |
   | `pause()` | `IllegalStateException` | paused, frame and time kept | `IllegalStateException` | `IllegalStateException` |
   | `resume()` | `IllegalStateException` | `IllegalStateException` | playing, from the frame and time kept | `IllegalStateException` |
   | `stop()` | stopped, first frame | stopped, first frame | stopped, first frame | stopped, first frame |

   - `play` and `stop` are valid in every state, because game logic issues them from its own state changes. `play` always restarts from the first frame, also with the animation already playing.
   - `pause` and `resume` come in pairs: a call out of pair is a mistake of the game and throws.
   - An animator is created stopped, without animation; its first `play` gives it one.
2. **Frame advance.** `update(stepSeconds)` advances only a playing animator; in another state it changes nothing.
   - The step is in seconds, zero or positive and finite (`IllegalArgumentException` otherwise).
   - The time spent in the current frame grows by the step. While it reaches the duration of the current frame, that duration is taken from it and the next frame starts: the time left over carries into the next frame, and several frames may pass in one step.
   - The update allocates nothing.
3. **End of a `once` animation.** It becomes finished when its last frame has been shown for its full duration. It keeps the last frame, and the time left over is dropped.
4. **`loop` order.** After the last frame comes the first: `0 1 2 0 1 2 …` for frames at indexes 0, 1 and 2.
5. **`ping-pong` order.** Forward to the last frame, then backward to the first, then forward again; the end frames are shown once per turn: `0 1 2 1 0 1 2 …`. With two frames: `0 1 0 1 …`. With one frame, that frame is shown forever, as with `loop`.

## Options considered

- `play` while playing:
  - Restart (chosen): the feature page says `play` starts from the first frame.
  - Continue when the animation is the same (rejected: two meanings for one call, and a game that wants a restart could not ask for one).
- `stop` while stopped:
  - Valid (chosen): stopping is a request of the game's own state machine, and the result is the same.
  - `IllegalStateException` (rejected: a game would have to read the state before every stop).
- End of `once`:
  - After the last frame's duration (chosen): the last frame is shown as long as the others; Godot does the same.
  - On reaching the last frame (rejected: the last frame would be shown for no time when the step is long).
- Time left over:
  - Carried into the next frame (chosen): the speed does not depend on the step length.
  - Dropped at each frame change (rejected: animations would run slower with longer steps).
- `ping-pong` ends:
  - Shown once per turn (chosen): stated by the feature page, as Aseprite exports it.
  - Shown twice (rejected: against the feature page).

## Consequences

- Positive consequences:
  - The same animation, steps and calls always give the same frames: animations are deterministic (REQ-SPR-002).
  - The speed of an animation does not depend on the length of the update step.
- Costs / constraints:
  - A game that calls `play` at every step restarts its animation at every step: it calls `play` when its own state changes.
  - An update crosses frames one by one, so its cost grows with the number of frames crossed in one step.
- Impact on modules and APIs:
  - `graphics`: `Animator`, `AnimatorState`.
  - Batch 3 of the feature page places an animator in an animated sprite, driven by the update step.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: unit tests of every transition, of each loop mode, of per-frame durations and of the time carried over; a long step crossing several frames; no allocation.
  - Documentation: `features/animated-sprite.md`, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md`, the glossary and the traceability.
  - Android/DEX: plain Java arithmetic.

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the animated-sprite batches; reviewed in the pull request of animated-sprite batch 2.
