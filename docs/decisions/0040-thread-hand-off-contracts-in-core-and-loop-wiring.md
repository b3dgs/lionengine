# ADR-0040 — Thread hand-off contracts in `core` and loop wiring

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, at the project owner's request of 2026-11-23 to review the architecture and apply the changes the review suggests; reversible by a superseding ADR
- Related requirements: `REQ-CORE-005`, `REQ-RT-001`, `REQ-RT-002`, `REQ-RT-004`, `REQ-RT-009`, `REQ-RT-010`, `REQ-PLATFORM-002`, `REQ-IN-001`, `REQ-NET-001`
- Open questions resolved: none (defect found by the architecture review of 2026-11-23)
- Supersedes / superseded by: supersedes the module placement of `Inbox`, `AsyncHandle<T>`, `AsyncState` and `LoadGroup` in the canonical-name table of [ADR-0037](0037-package-tree-per-module-callbacks-and-canonical-names.md) (`runtime` → `core`) and the placement of the worker-pool contract in `platform-api` stated by [ADR-0031](0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md) (consequences); every other point of both ADRs is unchanged

## Context

ADR-0018 and ADR-0031 require platform threads, input adapters, audio backends and loading workers to hand their results to the runtime thread through the inbox, and the platform to run the loop and to pause, resume or stop it. ADR-0037 places `Inbox`, `AsyncHandle<T>`, `AsyncState` and `LoadGroup` in `runtime`, and ADR-0031 places the worker pool in `platform-api`. But [`modules.md`](../architecture/modules.md) lets `runtime` be used only by `composition`:

- `platform-api`, `platform-awt`, `platform-android` must post `PlatformEvent`s and control the loop, and depend on `core` only;
- `input-awt`, `input-android` post raw input; they depend on `input-api` and `platform-api`;
- `audio-*` post end-of-track and fade completions; they depend on `audio-api` and `resources`;
- `graphics`, `audio-api` and `tilemap` return asynchronous handles and use the worker pool; none depends on `runtime` or `platform-api`;
- `network` plans host-name resolution through the worker pool and depends on `core` only.

As written, these modules cannot name the types they must use, so the module graph cannot be built. Making them depend on `runtime` would pull the loop, scene manager and scene flow into every media and adapter module, against the rule that business modules do not depend on each other without contractual need (ADR-0021).

## Decision

### 1. Thread hand-off contracts live in `core`

`core` defines the small, frontend-free contracts through which other threads reach the runtime thread. Each is documented with its thread rules (ADR-0018):

| Contract | Role | Thread |
|---|---|---|
| `Inbox` | Bounded FIFO of `InboxEvent` (ADR-0031 capacity and overflow rules unchanged): `post` returns whether the event was accepted; delivery runs every accepted event in arrival order | `post` from any thread; delivery on the runtime thread only |
| `InboxEvent` | One method, `deliver()`, run on the runtime thread at delivery; the producing module implements it and applies its own effect (input pending state, handle completion, playlist advance, platform event dispatch) | runtime thread |
| `AsyncHandle<T>`, `AsyncState`, `LoadGroup` | Unchanged contract of ADR-0031; a handle completes through an `InboxEvent` | runtime thread, except completion posting |
| `WorkerPool` | Contract of the loading worker pool (ADR-0019, ADR-0031): submit read-and-decode work; `0` workers means the work runs synchronously on the caller | submit on the runtime thread; work on a worker |
| `LoopControl` | The thread-safe control requests of ADR-0031 (pause, resume, stop, time scale) | any thread |
| `LoopDriver` | Extends `LoopControl` with one iteration on the calling thread and the running state, so a platform can run the loop on the thread it requires (ADR-0018) | runtime thread |

The event dispatch is inverted: the inbox never inspects event types, so `core` knows no producer. Producers may reuse event instances to stay within REQ-RT-006.

### 2. Implementations stay where they were

- `runtime`: `GameLoop` implements `LoopDriver`, owns its `Inbox` (capacity from `LoopConfig`, default 1,024) and delivers it at step 2 of each iteration; it exposes the inbox as the `core` type.
- `platform-api`: `Platform` is started with a `LoopDriver`, posts `PlatformEvent`s to an `Inbox`, maps background, foreground and close requests to `LoopControl` requests, and provides a `WorkerPool` (default 2 workers on desktop, 1 on Android).
- `test-support`: a manual `LoopDriver`, a fake `WorkerPool` and an inbox spy.

`runtime` still depends on `core` only; no module gains a dependency on `runtime`.

### 3. Wiring is explicit, by the application or `composition`

The application (or `composition`) creates the loop, then hands its inbox to the platform, the input adapters, the audio backend and the loaders, and the platform's worker pool to the loaders (ADR-0020: nothing is discovered). The loop's role-named callbacks (ADR-0037) are wired as:

- update callback, once per step: advance every `InputState` to the step (per-step edges, ADR-0035), then update the scene manager;
- render callback, once per iteration: render the scene manager (commands into the `CommandBuffer`), then present it through the render backend (ADR-0026, ADR-0006).

`runtime` keeps no knowledge of input, rendering or platforms.

## Options considered

- Contracts in `core` (chosen): matches the existing role of `core` ("events and listener rules"); no new module; every producer already depends on `core`.
- `platform-api`, `input-*`, `audio-*`, `graphics` and `network` depend on `runtime` (rejected): couples media and adapters to the loop and the scene flow; `network` would no longer be on `core` only (REQ-NET-001).
- A new `runtime-api` module holding these contracts (rejected): one more module for six small types, with the same dependents as `core`.
- Each producer module defines its own posting interface, adapted by the application (rejected): one adapter per module, the same thread rules restated many times.

## Consequences

- Positive consequences: the module graph of `modules.md` can be built; producers post without knowing the loop; tests drive any producer with a plain `Inbox`.
- Costs / constraints: six more public types in `core`; the application writes three wiring lines (or uses `composition`); the per-step input advance is a wiring rule the examples must show.
- Impact on modules and APIs: `core` (new contracts), `runtime` (`GameLoop` implements `LoopDriver`), `platform-api` (started with a `LoopDriver`, provides a `WorkerPool`), `test-support`; dependency rows unchanged.
- Impact on tests and documentation (including Android/DEX compatibility): the inbox, handle and worker-pool tests move to `core`; `modules.md`, `contracts.md`, `glossary.md`, `traceability.md`, `features/game-loop.md`, `features/core-foundation.md`, `features/platform-and-rendering.md`, `features/input.md` updated. No new JDK API: the contracts use only `java.lang` types and the role-named interfaces of ADR-0037.

## Validation

Decided by the agent on 2026-11-23 at the owner's request to apply the review's changes; reversible by a superseding ADR.
