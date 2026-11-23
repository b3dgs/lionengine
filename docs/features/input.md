# Feature: input actions

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 3 (`input-api`, `input-awt`), Phase 4 (`input-android`, desktop gamepads)
- Affected modules: `input-api`, `input-awt`, `input-android`, `test-support`, `examples`
- Requirements: `REQ-IN-001` to `REQ-IN-005`, `REQ-MP-001`, `REQ-DET-001`
- Applicable ADRs: [`ADR-0006`](../decisions/0006-display-scaling-effective-result-and-fallback-orders.md), [`ADR-0018`](../decisions/0018-runtime-thread-events-and-cache-thread-safety.md), [`ADR-0024`](../decisions/0024-determinism-performance-budgets-and-desktop-support.md), [`ADR-0035`](../decisions/0035-input-actions-devices-and-step-edges.md), [`ADR-0037`](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md)
- Open questions involved: none

## Objective

Let game logic read actions ("jump", "move") instead of devices, with every press seen by exactly one simulation step, on desktop and Android.

## Out of scope

Text entry (a later character stream); desktop gamepads (need a recorded dependency, ADR-0035); gestures beyond pointer press, move and release.

## Expected behavior

- **Devices and codes:** keyboard keys, pointer buttons and position (mouse and touch, one contract with a pointer index; mouse is pointer 0), gamepad buttons and axes in a standard layout (A, B, X, Y, shoulders, triggers, sticks, D-pad, start, back). Codes are engine-defined `int` constants; adapters map platform codes to them.
- **Actions and bindings:** the game declares actions, digital or analog, each with an id; `Bindings` maps codes to actions (several codes per action); built in code or read from a definition file (`"format": "lionengine.bindings"`, `"version": 1`, an object `actions` of action id → array of code names). A digital action is down when any binding is down; an analog action takes the binding of largest magnitude; a key bound to an analog action gives `-1`, `0` or `1` as declared by the binding.
- **Per-step edges (`InputState`):** raw events arrive through the inbox and update a pending state at the start of an iteration. At each update step the action state exposes `isDown`, `wasPressed`, `wasReleased`. An edge belongs to exactly one step: the first step that runs after it arrived; with no step in an iteration it waits; a press and release before the same step make `wasPressed` and `wasReleased` both true in that step while `isDown` is false.
- **Analog:** sticks in `[-1, 1]` per axis with a radial dead zone, default 0.15: magnitudes below it are 0, above it are rescaled to `[0, 1]`; triggers in `[0, 1]`.
- **Pointer:** positions in logical-frame pixels after the inverse of the scaling policy; outside the presented frame the position is flagged outside. World positions come from the camera (`REQ-CAM-007`).
- **Focus loss** releases every held input (edges emitted at the next step). **Hot-plug:** gamepad connected and disconnected events; a device keeps its index while connected. Key repeat is ignored.
- **Players:** a `Player` is a set of devices plus a `Bindings`; each player has its own `InputState`.
- **Recording:** the action states of each step are plain values that can be recorded and replayed (ADR-0024); an `InputState` can be driven from such recorded values instead of devices, which `multiplayer` uses for remote players and for replay (ADR-0038).

## Envisaged APIs / contracts

Canonical names (ADR-0037): `Action`, `Bindings`, `InputState`, `Player`; adapters in `input-awt` (through `AwtSurface`) and `input-android`.

## Batches (order)

1. Codes, actions, bindings, per-step edges with a fake device in `test-support` (`REQ-IN-001`, `REQ-IN-003`).
2. `input-awt`: keyboard and mouse (`REQ-IN-002`).
3. Analog values, dead zone, pointer inverse scaling (`REQ-IN-004`).
4. Focus loss, hot-plug, players (`REQ-IN-005`, `REQ-MP-001`).
5. `input-android` (Phase 4).

## Lifecycle and concurrency

Adapters post from platform threads to the inbox; everything else runs on the runtime thread.

## Units and Android/DEX compatibility

Pixels for pointers, dimensionless axes. `input-awt` desktop only; `input-android` uses platform APIs.

## Dependencies

`input-api` on `core`; `input-awt` on `input-api`, `platform-api`, `platform-awt`; `input-android` on `input-api`, `platform-api`.

## Tests

Controllable-clock tests with zero, one and several steps per iteration; a tap within one step; several bindings per action; analog largest-magnitude rule; dead zone and rescale; inverse scaling for `FIT`, `INTEGER`, `STRETCH`; focus loss; hot-plug; two players; record and replay give identical states.

## Acceptance criteria

- [ ] Requirements covered; complete Javadoc; headless example (bind keys, read edges); API-index entry.
