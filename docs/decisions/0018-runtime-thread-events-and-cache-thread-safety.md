# ADR-0018 — Runtime thread, platform event inbox and cache thread safety

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner
- Related requirements: `REQ-RT-001` to `REQ-RT-003`, `REQ-LOOP-003`, `REQ-LOOP-010`, `REQ-ECS-010`, `REQ-DISPLAY-005`, `REQ-DISPLAY-008`, `REQ-RES-004`
- Open questions resolved: `OQ-030`, `OQ-121`, `OQ-122` (follow-up: `OQ-153`)
- Supersedes / superseded by: none; completes [ADR-0006](0006-display-scaling-effective-result-and-fallback-orders.md), [ADR-0007](0007-resource-convention-keys-release-and-errors.md) and [ADR-0017](0017-fixed-step-game-loop.md) (which stay unchanged)

Source: owner confirmation of the proposal on issue #877.

## Context

The engine targets platforms that disagree on threads: OpenGL contexts and Android `GLSurfaceView` require a specific render thread, AWT has its own event thread, audio backends call back from their own threads. Accepted decisions already assume a single update thread (ADR-0011), synchronous update-phase events (ADR-0009, ADR-0011), an effective result delivered at the frame boundary (ADR-0006) and caches owned by the consuming modules (ADR-0005, ADR-0007).

## Decision

### Runtime thread (OQ-030)

- One **runtime thread** runs the loop ([ADR-0017](0017-fixed-step-game-loop.md)), the update phase, the world and ECS, cameras, animations, the caches and, by default, the render phase called in-line by the loop.
- Every engine type documents its thread. The default is "not thread-safe, owned by the runtime thread".
- The platform adapts to the engine: where rendering must happen on a given thread (an OpenGL context thread, the Android `GLSurfaceView` renderer thread), the platform **runs the loop iteration on that thread**. On AWT the loop runs on a dedicated thread with active rendering, not on the AWT event thread.
- The thread-safe entry points are few, listed and documented: the platform inbox, pause, stop and time scale requests, asynchronous-load completion ([ADR-0019](0019-loading-resource-policy-and-memory.md)), and audio callbacks. Each only enqueues; the runtime thread acts on them.
- The engine does not start threads by itself; only platform backends and the optional asynchronous loader pool do.
- A separate render thread with a snapshot handoff is **deferred**. It stays possible later as another loop implementation (`REQ-LOOP-002`). Multithreaded systems inside a step are out of scope.

### Events (OQ-122)

- **Platform-originated events** (input, focus, background and foreground, surface lost, the display effective result of ADR-0006, audio completion) are posted from any thread to a thread-safe FIFO **inbox** and delivered **on the runtime thread at the start of an iteration, before the update steps**, in arrival order.
- **Engine-internal events** (ECS `onAdded` and `onRemoved`, tile changed) stay synchronous inside the update phase (ADR-0009, ADR-0011) and follow the same listener rules.
- Listener rules: called on the runtime thread only; registration and removal during a dispatch take effect at the end of that dispatch; no allocation per event in steady state; a listener exception follows the fail-fast rule of ADR-0017.
- The effective result of a display change stays readable as current state and is also delivered through the inbox (ADR-0006).

### Cache thread safety (OQ-121)

- Cache operations (load, lookup, release) are allowed **only on the runtime thread**; caches need no locks.
- Decoded data are immutable values (ADR-0003) and may be read by any thread after publication.
- Worker threads never touch a cache; a finished result is posted to the inbox and inserted by the runtime thread.
- Release of GPU or native objects is scheduled by the backend on the thread that owns them.

## Options considered

- Separate update and render threads with a snapshot handoff: more throughput on multicore, much more complexity and latency. Deferred.
- Concurrent caches and locks: allow loading from any thread, cost in hot paths and nondeterminism. Rejected.
- Callbacks invoked directly on platform threads: simple, but exposes simulation state to races. Rejected.

## Consequences

- Positive consequences: no locks in hot paths; deterministic simulation; events injected by tests or a replay arrive at a defined step boundary; works with the Android and OpenGL thread constraints.
- Costs / constraints: platform adapters must drive the loop on their required thread; a slow listener delays the iteration; inbox capacity and overflow policy remain to define (`OQ-153`).
- Impact on modules and APIs: `runtime`, `platform-api` and the platform modules post events; consuming modules never lock their caches.
- Impact on tests and documentation: tests inject events through the inbox with a controllable clock; `conventions.md`, `contracts.md` and the feature documents' thread references are updated.

## Validation

Proposal confirmed by the project owner on 2026-11-22 (issue #877).
