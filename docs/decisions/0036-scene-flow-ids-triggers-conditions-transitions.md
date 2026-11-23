# ADR-0036 — Scene flow: scene ids, triggers, conditions and transitions

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner (need: "link scenes such as Introduction, Menu, GameScene, Credits with links based on conditions which can be triggered; same as game engine standard but avoid useless complexity"); design choices by the agent under the owner's delegation of 2026-11-23; reversible by a superseding ADR
- Related requirements: `REQ-SCENE-001` to `REQ-SCENE-008`, `REQ-LOOP-010`, `REQ-RT-001`
- Open questions resolved: none
- Supersedes / superseded by: none; completes [ADR-0021](0021-scene-world-viewport-and-object-model.md) and [ADR-0031](0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md) (both unchanged)

## Context

ADR-0021 and ADR-0031 define the scene lifecycle, a manager that switches scene instances at a frame boundary, and an overlay stack. A game still has to hard-code which scene follows which. Engines usually offer a scene graph or state machine for this (Godot `change_scene`, Unity `SceneManager` plus game-side state machines, libGDX `Game.setScreen`). A full visual state machine (nested states, parallel regions, history states, editor) would be more than a 2D game needs.

## Decision

1. **Scene flow.** A *scene flow* is a small finite-state machine over scenes, in `runtime`, built on top of the scene manager (which stays usable alone). It is built in code with a builder, validated when built, then immutable. No data file in this ADR.
2. **Scene ids and factories.** Each scene of the flow has an id of the registry form `namespace:name` (ADR-0032, for example `game:menu`) and a **factory** that creates a new scene instance each time the scene is entered. A scene instance is never reused after `dispose`. The flow has one **start** scene id.
3. **Game context.** The flow is typed by a game-owned context object `C` (for example the save slot, the selected level, the score). Factories and conditions receive it; scenes share data only through it. The engine never reads it.
4. **Triggers.** A trigger is a string name (`[a-z][a-z0-9_-]*`, for example `finished`, `start`, `pause`, `back`, `quit`). Code running on the runtime thread (a scene's `update`, an input action, an inbox listener) calls `trigger(name)`.
5. **Transitions.** A transition is `(from, trigger, condition, kind, to)`:
   - `from`: a scene id, or **any scene**;
   - `condition`: optional predicate on the context, default always true;
   - `kind`: `SWITCH` (replace the whole stack by `to`), `PUSH` (open `to` as an overlay, ADR-0031), `POP` (close the top overlay; no `to`), or `EXIT` (stop the loop, ADR-0017; no `to`).
6. **Resolution.** `trigger(name)` resolves immediately, on the runtime thread, against the **top** scene: transitions from that scene are tried in declaration order, then transitions from *any scene* in declaration order; the first whose trigger matches and whose condition is true is selected. `trigger` returns `true` when a transition was selected, `false` otherwise (no match is not an error). Conditions must not change state.
7. **One pending transition.** The selected transition is applied at the next frame boundary, never inside a step (ADR-0021). While one is pending, further triggers in the same iteration return `false` and are ignored, so the result does not depend on how many steps ran.
8. **Lifecycle on application.** `SWITCH`: every scene of the stack is exited then disposed from top to bottom, then `to` is created and entered. `PUSH`: `to` is created and entered above; the covered scene stops updating and keeps rendering. `POP`: the top scene is exited and disposed; the scene below resumes updating; a `POP` with no overlay fails validation when possible and otherwise throws `IllegalStateException` at application. `EXIT`: the stack is exited and disposed, then the loop stop is requested.
9. **Validation at build.** The builder fails with `IllegalStateException` naming the problem when: an id is duplicated or invalid, the start id or a `to` id is not declared, a trigger name is invalid, or a transition has the wrong shape for its kind (a `to` on `POP` or `EXIT`, none on `SWITCH` or `PUSH`).
10. **Observation.** The flow exposes the id of every scene of the stack (top first) and a listener called on the runtime thread when a transition is applied, with the source id, the trigger and the target id (or none). Errors thrown by a factory or a scene follow fail-fast (ADR-0017).
11. **Not included:** nested flows, parallel regions, history states, automatic timers (a scene that wants to advance after 3 s counts time and calls `trigger("finished")`), visual transitions (drawn by the game, ADR-0031), scene preloading (a loading scene, ADR-0031), data-file flows (can be added later by a new ADR with conditions named by registry ids).

Example (Introduction, Menu, Game, Credits):

```text
start game:intro
game:intro   --finished--> SWITCH game:menu
game:menu    --start-----> SWITCH game:level     (condition: a save slot is selected)
game:menu    --credits---> SWITCH game:credits
game:level   --pause-----> PUSH   game:pause
game:pause   --resume----> POP
game:level   --won-------> SWITCH game:credits   (condition: last level)
game:level   --won-------> SWITCH game:level     (otherwise: next level, set in the context)
game:credits --finished--> SWITCH game:menu
any scene    --quit------> EXIT
```

## Options considered

- A (chosen): flat state machine with ordered guarded transitions, triggers by name, context object. Covers menus, levels, pause, credits and exit with a handful of concepts; deterministic.
- B: hierarchical state machine (nested and parallel states, history). More expressive, rarely needed in 2D games, much more to test.
- C: no flow, scenes call `switchTo(new Scene())` directly. Simplest, but links are scattered in code, cannot be validated or listed, and every scene knows its successors.
- D: data-file flow from the start. Needs named conditions in a registry; deferred until a game needs it.

## Consequences

- Positive consequences: the whole navigation of a game is declared in one place and validated at startup; scenes do not know each other; deterministic; testable headless with a manually driven loop.
- Costs / constraints: a generic context type; conditions must be side-effect free; transitions from overlays need explicit rules (the top scene decides).
- Impact on modules and APIs: `runtime` (scene flow, builder, transition kinds, listener); `composition` may provide ready-made scenes used in a flow.
- Impact on tests and documentation: `REQ-SCENE-004` to `REQ-SCENE-008` added; [`../features/scenes.md`](../features/scenes.md) created; `contracts.md`, `modules.md`, `glossary.md`, `how-the-engine-works.md`, `api-index.md`, `testing.md`, `roadmap.md` updated.

## Validation

Need stated by the project owner on 2026-11-23; design decided by the agent under the owner's delegation; reversible by a superseding ADR.
