# ADR-0035 — Input: devices, actions, per-step edges, analog values and focus

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, under the project owner's explicit delegation of 2026-11-23 ("choose the best choice for all requirements and all sub pages related to them; mark them as VALIDATED with your choice"); reversible by a superseding ADR
- Related requirements: `REQ-IN-001` to `REQ-IN-005`, `REQ-RT-002`, `REQ-DET-001`, `REQ-MP-001`, `REQ-CAM-007`
- Open questions resolved: none (the input contract had no open question; `contracts.md` listed its points as "to specify")
- Supersedes / superseded by: none; completes [ADR-0018](0018-runtime-thread-events-and-cache-thread-safety.md) and [ADR-0024](0024-determinism-performance-budgets-and-desktop-support.md) (both unchanged)

## Context

`input-api` and `input-*` are in the module table and ADR-0024 requires inputs to go through `input-api` actions, but nothing defined devices, actions, how a press is seen by a fixed-step simulation, analog values, focus loss or hot-plug. With a fixed step (ADR-0017), an iteration may run zero, one or several updates, so a naive "pressed this frame" flag loses or duplicates presses.

## Decision

1. **Devices.** `input-api` defines three device kinds: keyboard, pointer (mouse and touch, one contract with a pointer index) and gamepad (a standard layout of buttons and axes). Physical inputs are identified by engine-defined `int` codes; no platform key code leaves an adapter. Device availability is a typed capability (ADR-0020).
2. **Actions.** The game declares actions, each digital (button) or analog (axis), and binds physical inputs to them in a binding table built in code or loaded from a definition file (ADR-0032). An action may have several bindings; a digital action is down when any binding is down; an analog action takes the binding with the largest magnitude. Game logic reads actions, not devices.
3. **Delivery.** Adapters post raw events to the platform inbox (ADR-0018). The runtime thread applies them to the input state at the start of an iteration, before the update steps.
4. **Per-step edges.** For each digital action the update step sees `down`, `pressed` and `released`. An edge is visible to **exactly one** update step: if an iteration runs no step, the edge waits for the next step; if it runs several, only the first sees it. A press and a release arriving before the same step are both reported in that step, so a short tap is never lost.
5. **Analog values.** Sticks give axes in `[-1, 1]`, triggers in `[0, 1]`. A radial dead zone, default `0.15`, configurable per binding, is applied to sticks and the remaining range is rescaled to `[0, 1]` in magnitude.
6. **Pointer positions** are in pixels of the logical frame, after the inverse of the scaling policy (ADR-0006); a position outside the presented frame is reported as outside. World coordinates come from the camera (`REQ-CAM-007`).
7. **Key repeat** is ignored by actions. Text entry is a separate stream of character events, added with the first text-input need.
8. **Focus and hot-plug.** On focus loss every held input is released, emitting `released` edges. Gamepad connection and disconnection are events; a connected gamepad keeps its device index until it disconnects.
9. **Players.** A player is a set of assigned devices plus a binding table; several local players (split-screen, `REQ-MP-001`) are several such sets.
10. **Determinism.** The action state seen by each step can be recorded and replayed (ADR-0024).
11. **Adapters.** `input-awt` (keyboard and mouse, on `platform-awt` through its capability, like `render-awt`) and `input-android` (touch, keys, gamepads through platform APIs). The JDK has no gamepad API: a desktop gamepad adapter needs a third-party or native library and comes with its register entry (ADR-0032); it is not part of the first input batch.

## Options considered

- Polling per frame (rejected: presses lost or duplicated with a fixed step) vs per-step edges (chosen).
- Event callbacks into game code from the platform thread (rejected by ADR-0018) vs inbox application (chosen).
- Devices read directly by game logic (rejected: no rebinding, no recording) vs actions (chosen).

## Consequences

- Positive consequences: no lost taps; rebinding and local multiplayer by configuration; inputs recordable; same game code on desktop and Android.
- Costs / constraints: a small edge-tracking state per action; desktop gamepads need a later dependency.
- Impact on modules and APIs: `input-api`, `input-awt`, `input-android`; `modules.md` (`input-awt` depends on `platform-awt`).
- Impact on tests and documentation: `REQ-IN-003` to `REQ-IN-005` added, `REQ-IN-002` made precise; `contracts.md`, `testing.md`, `glossary.md`, `roadmap.md` updated.

## Validation

Decided by the agent on 2026-11-23 under the owner's explicit delegation; reversible by a superseding ADR.
