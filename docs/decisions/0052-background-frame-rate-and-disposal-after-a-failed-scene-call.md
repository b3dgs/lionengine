# ADR-0052 — Background frame rate of the desktop platform and disposal of a scene whose call failed

- Status: ACCEPTED
- Date: 2026-11-25
- Decision makers: project owner, who delegated the choice to the agent ("I let you choose the best and explain why", 2026-11-25, answering `OQ-168` and `OQ-169`)
- Related requirements: `REQ-SCENE-001`, `REQ-SCENE-002`, `REQ-PLATFORM-002`, `REQ-LOOP-010`
- Open questions resolved: `OQ-168`, `OQ-169`
- Supersedes / superseded by: none; completes [ADR-0017](0017-fixed-step-game-loop.md) (fail-fast) and [ADR-0031](0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md) (platform lifecycle). The rule of [`../features/scenes.md`](../features/scenes.md) "a scene whose `enter` or `exit` threw receives no further call" is replaced; no ADR stated it.

## Context

Two gaps were found by the consistency review of 2026-11-25:

- `OQ-168`: an iconified desktop window pauses the loop, but the platform kept pacing frames at the refresh rate and rendering them to a window nobody sees.
- `OQ-169`: when `exit` of the current scene or `enter` of the next one throws, the loop fails (ADR-0017) and `SceneManager` called nothing more on that scene, so the resources it had acquired (sprite sheet groups, sounds, REQ-SHEET-002) were never released by their acquirer, which is the scene on exit.

## Decision

1. **Background frame rate.** While the window is in the background, the desktop platform paces the loop at 10 Hz, or at the refresh rate if that is lower, and returns to the refresh rate at the first frame after the foreground event. A resume therefore waits at most 100 ms. The loop is still paused, so the simulation does not advance; only the rendering of the paused frame is slowed. The value is a constant of the platform, not a game setting.
2. **Disposal after a failed call.** A scene whose `exit` threw is still disposed, once. A scene whose `enter` threw is disposed but not exited, because it never finished entering. A scene whose `dispose` throws is not called again. If the disposal after a failure also throws, its exception is added to the first one as a suppressed exception, and the first exception is the one that propagates. When the exit of the current scene throws, the next scene is not entered and receives no call. The loop still fails (ADR-0017), and `clear()` follows the same rule. A scene whose `update` or `render` threw is unchanged: it stays active, so `clear()` exits and disposes it.

## Options considered

- Background rate 10 Hz (chosen): saves about 80 % of the frame work of a hidden window, keeps the window able to repaint and to react to the foreground within 100 ms.
- Keep the refresh rate: no change, but wastes CPU and battery for nothing.
- Stop rendering and block until the foreground event: saves the most, but the platform would need a wake-up path from the AWT thread, and a window restored from the task bar would show a stale frame until then.
- Disposal after failure (chosen): the acquirer of the resources releases them whatever happens, which is the rule of REQ-RES-003; the cost is one call on a scene in a failed state, which `dispose` must tolerate.
- Keep "no further call" (rejected): simpler contract, but every failure in a scene transition leaks its resources until the process ends, which matters on Android where the process outlives the game.
- Also call `exit` after a failed `enter` (rejected): `exit` pairs with a completed `enter` and may assume the state `enter` was to build.

## Consequences

- Positive consequences: no resource left behind after a failed transition; a hidden window costs little.
- Costs / constraints:
  - `dispose` of a scene must work on a scene that did not finish `enter` or `exit`.
  - A resume from the background has up to 100 ms of latency.
- Impact on modules and APIs: `runtime` (`SceneManager` behavior only, no signature) and `platform-awt` (a private constant).
- Impact on tests and documentation (including Android/DEX compatibility):
  - `SceneManagerTest`: the exit failure, enter failure and clear failure tests now expect the disposal, and a new test covers a failing disposal;
  - documents updated: [`../features/scenes.md`](../features/scenes.md), [`../features/platform-and-rendering.md`](../features/platform-and-rendering.md), [`../open-questions.md`](../open-questions.md), [`../agent-workflow.md`](../agent-workflow.md);
  - no Android/DEX impact: no new API, `addSuppressed` exists on Android since API 19.

## Validation

Decided on the owner's delegation. `mvn -B verify -Pplatform-tests` and the structural checks pass with the change.
