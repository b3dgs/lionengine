# ADR-0059 — Overlay stack of the scene manager: one change per frame, switch replaces the stack, pop keeps a base scene, every scene left on failure

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-SCENE-002`, `REQ-SCENE-003`, `REQ-SCENE-001`
- Open questions resolved: none (details left open by ADR-0031 and [`../features/scenes.md`](../features/scenes.md), needed by its batch 2)
- Supersedes / superseded by: none; completes [ADR-0031](0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md) (section 3), [ADR-0036](0036-scene-flow-ids-triggers-conditions-transitions.md) and [ADR-0052](0052-background-frame-rate-and-disposal-after-a-failed-scene-call.md) (all unchanged)

## Context

ADR-0031 adds an overlay stack to the scene manager in its second batch:
- `push` and `pop` apply at a frame boundary and call `enter` and `exit` of the pushed or popped scene;
- only the top scene is updated, every scene of the stack is rendered from bottom to top;
- the covered scene keeps its state and is not exited.

Batch 1 of the scene manager keeps one active scene and one pending switch: the first request of a frame wins and later ones return `false`, as ADR-0036 decides for the triggers of the scene flow. ADR-0052 says a scene whose `enter` or `exit` threw is still disposed, and that the next scene is not entered when the exit of the current one throws. The scene flow of batches 3 and 4 will drive the manager with `SWITCH` ("replace the whole stack"), `PUSH` and `POP` ("close the top overlay").

No document says:
- how a push or a pop combines with a switch, or with another push or pop, requested in the same frame;
- what a switch does to the scenes covered by overlays;
- whether the last scene of the stack can be popped;
- what happens when several scenes leave together and one of them fails.

What other engines do:
- **libGDX** `Game.setScreen` replaces the one screen; stacks are written by games.
- **Godot** changes the whole scene tree with `change_scene_to_*`; overlays are added as children and freed with `queue_free`, applied at the end of the frame.
- **Unity** `SceneManager.LoadScene` in single mode unloads every loaded scene; additive scenes stay until unloaded.

## Decision

1. **One change per frame.** A switch, a push and a pop are all changes. At most one change is pending: the first request of a frame wins and the others return `false`, and their scenes receive no call. Batch 1's rule for switches thus covers the whole stack, and the triggers of ADR-0036 keep a single pending transition of any kind. Because the stack changes only at a frame boundary, it is the same at the request and at the boundary.
2. **Switch.** `switchTo(scene)` replaces the whole stack. At the boundary, every scene is left from the top to the bottom (exit, then dispose), then the new scene is entered, alone in the stack.
3. **Push.** `push(scene)` enters a scene on top of the stack at the boundary. The covered scenes receive no call but `render`, and keep their state. Pushing onto an empty stack enters the scene as the only one, as a switch would.
4. **Pop.** `pop()` exits and disposes the top scene at the boundary. The scene below becomes the top again without any call: it was never exited. A pop is valid only while the stack holds at least two scenes (`IllegalStateException` otherwise), because closing an overlay never leaves the game without a scene. The end of the game uses `clear()` or the scene flow's `EXIT`.
5. **Identity.** A scene given to `switchTo` or `push` must not be in the stack (`IllegalArgumentException`), so no instance is entered twice.
6. **Failures** (extends ADR-0052):
   - When several scenes leave together (a switch, or `clear()`), every one of them is left, top to bottom, even if one fails. Each scene whose exit threw is still disposed.
   - The first exception propagates once all are left; the later ones are added to it as suppressed exceptions.
   - After a failed switch the stack is empty and the new scene is not entered.
   - A pushed scene whose enter throws is disposed and not added; the stack below is unchanged.
   - A popped scene whose exit throws is still disposed and removed.
7. **Observation.** `getActive()` gives the top scene. `getSceneCount()` and `getScene(index)` (from the bottom, at `0`) give the stack. `isChangePending()` tells whether any change waits for the boundary; `isSwitchPending()` keeps telling whether a switch waits.

## Options considered

- Changes in one frame:
  - One pending change of any kind (chosen): one rule, the one of batch 1 and ADR-0036, and the stack seen by a request is the stack the change applies to.
  - A queue of changes applied in order (rejected: a pop requested after a push would apply to a stack no request saw, and the flow would need a queue too).
- Switch and overlays:
  - Replace the whole stack (chosen): what the flow's `SWITCH` says, and what Godot and Unity do.
  - Replace the top scene only (rejected: a switch from a pause overlay to the menu would leave the level running below it).
- Popping the last scene:
  - Refused (chosen): an overlay never closes the game by mistake.
  - Allowed, leaving an empty stack (rejected: a frame with no scene draws nothing, and the end of the game has `clear()`).
- Failures while leaving several scenes:
  - Leave every scene, first exception propagated (chosen): no scene is left half alive, and no failure is hidden.
  - Stop at the first failure (rejected: the scenes below would keep their resources until `clear()`, which could fail the same way).

## Consequences

- Positive consequences:
  - A pause overlay is a push and a pop; going back to the menu from it is one switch that releases the level below.
  - The scene flow maps `SWITCH`, `PUSH` and `POP` directly onto the manager.
- Costs / constraints:
  - A game cannot pop and push in one frame. It switches, or requests the push in the following frame.
  - A failed switch leaves no scene: the loop fails anyway (ADR-0017).
- Impact on modules and APIs: `runtime`: `SceneManager.push`, `pop`, `isChangePending`, `getSceneCount`, `getScene`; `switchTo` now replaces the whole stack.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: unit tests of push, pop, the rule of one change per frame, update and render calls per scene, switch over overlays, invalid requests and each failure; an allocation test of a push and a pop in steady state.
  - Documentation: `features/scenes.md`, `contracts.md`, `api-index.md`, `traceability.md` and `engine-features.md`.
  - Android/DEX: plain Java arrays; the `runtime` device test covers the stack.

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the batches; reviewed in the pull request of scenes batch 2.
