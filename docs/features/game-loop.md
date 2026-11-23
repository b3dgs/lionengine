# Feature: game loop, inbox and asynchronous loading

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 1 (loop, inbox), Phase 2 (asynchronous handles and load groups)
- Affected modules: `runtime`, `test-support`, `examples`
- Requirements: `REQ-LOOP-001` to `REQ-LOOP-012`, `REQ-RT-001` to `REQ-RT-004`, `REQ-RT-009`, `REQ-RT-010`, `REQ-TEST-001`
- Applicable ADRs: [`ADR-0001`](../decisions/0001-technical-baseline-and-conventions.md), [`ADR-0017`](../decisions/0017-fixed-step-game-loop.md), [`ADR-0018`](../decisions/0018-runtime-thread-events-and-cache-thread-safety.md), [`ADR-0019`](../decisions/0019-loading-resource-policy-and-memory.md), [`ADR-0031`](../decisions/0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md), [`ADR-0037`](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md)
- Open questions involved: none

## Objective

Run the simulation at a fixed step independent of machine speed, render smoothly between steps, deliver events from other threads safely, and complete asynchronous loads without blocking.

## Out of scope

Scenes (see [`scenes.md`](scenes.md)); the platform threads themselves (see [`platform-and-rendering.md`](platform-and-rendering.md)); a separate render thread (deferred, ADR-0018).

## Expected behavior

### Clock and configuration

- `Clock`: monotonic nanoseconds (`System.nanoTime` in production; a controllable clock in `test-support`).
- `LoopConfig` defaults: update rate 60 Hz (`int` in `[1, 1000]`); lag cap 0.25 s (in `[one step, 1 s]`); time scale 1.0 (in `[0.1, 4.0]`); vsync snapping tolerance 0.25 ms (`0` disables); inbox capacity 1,024. Out-of-range values throw `IllegalArgumentException`.

### One iteration (exact order)

1. Read the control requests (pause, resume, stop, time scale); a time-scale change applies here.
2. Deliver the inbox: every event in arrival order, on the runtime thread.
3. If paused: elapsed time is discarded (no accumulation), go to step 6.
4. `elapsed` = now − last; snap to 1 to 4 steps when within the tolerance; cap at the lag cap (add the excess to the drop counters and call the lag listener); multiply by the time scale and round to `long` nanoseconds, carrying the remainder; add to the accumulator.
5. While `accumulator ≥ stepNanos`: run one update with `stepSeconds = stepNanos / 1e9`, subtract `stepNanos`.
6. Render once with `alpha = accumulator / stepNanos` (frozen while paused) and the unscaled delta in seconds (real elapsed, capped, independent of pause and scale).
7. If a stop was requested: stop here (state `STOPPED`).

On resume the clock reference is reset and the accumulator cleared. The first iteration after start uses an elapsed time of 0.

### States and errors

- `CREATED` → `RUNNING` → (`PAUSED` ⇄ `RUNNING`) → `STOPPED`, or → `FAILED` when an update, render or listener throws; the exception is rethrown to the owner of the loop; no render follows a failed update. `stop` is idempotent and never interrupts a step.

### Inbox

- Thread-safe, bounded FIFO; `post` from any thread never blocks beyond a short lock; when full the event is rejected, counted, and the drop is logged once at the next delivery. Control requests are not inbox entries.
- Listener registration or removal during a delivery takes effect after it.

### Asynchronous handles (Phase 2)

- `AsyncHandle<T>` states `PENDING`, `READY`, `FAILED`, `CANCELLED`; progress `double` in `[0, 1]`; state changes only at inbox delivery. `get()` when not `READY` throws `IllegalStateException`; `cancel()` is best effort and a late result is released. `LoadGroup` aggregates handles (mean progress, `READY` when all are).
- Workers (platform pool: 2 on desktop, 1 on Android, 0 = synchronous only) only read and decode.

## Envisaged APIs / contracts

Canonical names (ADR-0037): `Clock`, `GameLoop`, `LoopConfig`, `Inbox`, `AsyncHandle<T>`, `AsyncState`, `LoadGroup`; listeners `LagListener` and the update and render callbacks as role-named interfaces.

## Batches (order)

1. `Clock`, controllable clock, `LoopConfig`, loop with update/render, catch-up, `alpha`, states and fail-fast (`REQ-LOOP-001` to `REQ-LOOP-007`, `REQ-LOOP-010`, `REQ-TEST-001`).
2. Lag cap and report, pause, time scale, remainder carry, snapping, unscaled delta (`REQ-LOOP-008`, `REQ-LOOP-009`, `REQ-LOOP-011`, `REQ-LOOP-012`).
3. Inbox and runtime-thread rules (`REQ-RT-001`, `REQ-RT-002`, `REQ-RT-009`).
4. Asynchronous handles and load groups (`REQ-RT-004`, `REQ-RT-010`).

## Lifecycle and concurrency

The loop and everything it calls run on the runtime thread; only `post` and the control requests are thread-safe.

## Units and Android/DEX compatibility

Nanoseconds inside the loop, seconds in every callback. `System.nanoTime` to verify on API 21 (`android-compatibility.md`).

## Dependencies

`core` only.

## Tests

Controllable-clock tests: exact update counts for given elapsed times; `alpha` bounds; no drift over 10⁶ steps; long frame capped and reported; pause and resume with zero catch-up; time scale 0.25 gives a quarter of the updates with unchanged render calls; snapping at and beyond the tolerance; failure in update, render and listener; stop during an iteration; inbox order, overflow, registration during delivery; handle states with a fake worker pool.

## Acceptance criteria

- [ ] Requirements covered; complete Javadoc; headless example (start, update 3 seconds of simulated time, stop); API-index entry.
