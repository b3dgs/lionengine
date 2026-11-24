# Feature: game loop, inbox and asynchronous loading

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 1 (loop, inbox), Phase 2 (asynchronous handles and load groups)
- Affected modules: `core` (inbox, handles, worker pool and loop contracts, ADR-0040), `runtime`, `test-support`, `examples`
- Requirements: `REQ-LOOP-001` to `REQ-LOOP-012`, `REQ-RT-001` to `REQ-RT-004`, `REQ-RT-009`, `REQ-RT-010`, `REQ-TEST-001`
- Applicable ADRs: [`ADR-0001`](../decisions/0001-technical-baseline-and-conventions.md), [`ADR-0017`](../decisions/0017-fixed-step-game-loop.md), [`ADR-0018`](../decisions/0018-runtime-thread-events-and-cache-thread-safety.md), [`ADR-0019`](../decisions/0019-loading-resource-policy-and-memory.md), [`ADR-0031`](../decisions/0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md), [`ADR-0037`](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md), [`ADR-0040`](../decisions/0040-thread-hand-off-contracts-in-core-and-loop-wiring.md)
- Open questions involved: none

## Objective

Run the simulation at a fixed step independent of machine speed, render smoothly between steps, deliver events from other threads safely, and complete asynchronous loads without blocking.

## Out of scope

Scenes (see [`scenes.md`](scenes.md)); the platform threads themselves (see [`platform-and-rendering.md`](platform-and-rendering.md)); a separate render thread (deferred, ADR-0018).

## Expected behavior

### Clock and configuration

- `Clock`: monotonic nanoseconds (`System.nanoTime` in production; a controllable clock in the `runtime` test JAR, reused by `test-support`, ADR-0045).
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

- `Inbox` and `InboxEvent` are `core` contracts (ADR-0040): an event is an object with one `deliver()` method, implemented by its producer and run on the runtime thread; the inbox never inspects event types. `GameLoop` owns its inbox (capacity from `LoopConfig`) and exposes it as the `core` type for the application to hand to producers.
- Thread-safe, bounded FIFO; `post` from any thread never blocks beyond a short lock; when full the event is rejected, counted, and the drop is logged once at the next delivery. Control requests are not inbox entries.
- Listener registration or removal during a delivery takes effect after it.

### Asynchronous handles (Phase 2)

- `AsyncHandle<T>` states `PENDING`, `READY`, `FAILED`, `CANCELLED`; progress `double` in `[0, 1]`; state changes only at inbox delivery. `get()` when not `READY` throws `IllegalStateException`; `cancel()` is best effort and a late result is released. `LoadGroup` aggregates handles (mean progress, `READY` when all are).
- `AsyncHandle<T>`, `AsyncState`, `LoadGroup` and `WorkerPool` are `core` contracts so that `graphics`, `audio-api`, `tilemap` and `network` can use them without `runtime` (ADR-0040).
- Workers (platform pool: 2 on desktop, 1 on Android, 0 = synchronous only) only read and decode.

## Envisaged APIs / contracts

Canonical names (ADR-0037, ADR-0040): in `runtime` `Clock`, `GameLoop`, `LoopConfig`; in `core` `Inbox`, `InboxEvent`, `AsyncHandle<T>`, `AsyncState`, `LoadGroup`, `WorkerPool`, `LoopControl`, `LoopDriver` (`GameLoop` implements `LoopDriver`); listeners `LagListener` and the update and render callbacks as role-named interfaces. The pause, resume, stop and time-scale requests are the `LoopControl` methods. Declared by the contract skeleton (ADR-0049, [`contract-skeleton.md`](contract-skeleton.md)): `Inbox`, `InboxEvent`, `AsyncHandle<T>`, `AsyncState`, `WorkerPool`, `LoopControl`, `LoopDriver`, `Clock` and the update and render callbacks; the batches below implement them.

Wiring (ADR-0040): the application or `composition` sets the update callback to advance every `InputState` then update the scene manager, and the render callback to render the scene manager then present the command buffer; `runtime` knows neither input nor rendering.

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

`runtime` on `core` only; the hand-off contracts are in `core` itself.

## Tests

Controllable-clock tests: exact update counts for given elapsed times; `alpha` bounds; no drift over 10⁶ steps; long frame capped and reported; pause and resume with zero catch-up; time scale 0.25 gives a quarter of the updates with unchanged render calls; snapping at and beyond the tolerance; failure in update, render and listener; stop during an iteration; inbox order, overflow, registration during delivery; handle states with a fake worker pool.

## State (2026-11-24)

Batches 1 to 3 are implemented in `runtime`, with the example `GameLoopExample` and its API-index row; batch 4 (asynchronous handles and load groups, Phase 2) is not. Names and details chosen by the batches, within this page, ADR-0017, ADR-0031 and ADR-0040:
- `LoopConfig` is a record of the five values with their default and range constants and `DEFAULT`; `stepNanos()` and `stepSeconds()` derive the step. The snapping tolerance must be below half a step (the default 0.25 ms is valid at every rate): from half a step every elapsed time would be snapped and the simulation would drift from real time. The elapsed time is compared with the nearest multiple of the step.
- `GameLoop` implements `LoopDriver`. Its constructors take the configuration, the clock (`System.nanoTime` by default), the `Log` of the inbox reports and the two callbacks. `LoopState` gives the state; `getDroppedLagCount()`, `getDroppedLagSeconds()` and `setLagListener()` report the lag; `getTimeScale()` returns the scale applied by the last iteration. The control requests are volatile fields. A pause is never lost: a pause requested and cancelled before the same frame boundary is applied there as a pause immediately resumed, so the elapsed time is discarded and a short trip to the background causes no catch-up.
- At the iteration where the loop resumes, the elapsed time is discarded and the accumulator cleared, so the factor is `0`. The lag listener is called before the updates of its iteration.
- The inbox implementation, package-private in `runtime`, is a ring buffer behind a short private lock; a delivery takes the events accepted so far and runs them outside the lock, so an event posted during it waits for the next one; an event that throws fails the loop and the rest of that delivery is discarded; a nested delivery is refused. Rejected events are reported as one warning per delivery: `Inbox full, events dropped: <count>`.
- Test fixtures (ADR-0045): `ControllableClock` in the `runtime` test JAR; `ManualLoopDriver`, `InboxSpy`, `FakeWorkerPool` and the abstract `LoopDriverContract` suite in the `core` test JAR. `GameLoop` and `ManualLoopDriver` both pass the contract suite.

## Acceptance criteria

- [ ] Requirements covered; complete Javadoc; headless example (start, update 3 seconds of simulated time, stop); API-index entry.
