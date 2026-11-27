# ADR-0019 — Loading, resource lifetime policy and memory policy

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner
- Related requirements: `REQ-RT-004` to `REQ-RT-006`, `REQ-RES-003`, `REQ-RES-004`, `REQ-ECS-009`
- Open questions resolved: `OQ-031`, `OQ-032`, `OQ-033` (follow-up: `OQ-153`)
- Supersedes / superseded by: none; completes [ADR-0005](0005-common-convention-for-modules-handling-resources.md) and [ADR-0007](0007-resource-convention-keys-release-and-errors.md) (which stay unchanged)

Source: owner confirmation of the proposal on issue #877.

## Context

ADR-0005 and ADR-0007 fixed ownership, keys, release and errors for resources, and left asynchronous loading, caching and reloading policy and the memory policy open. Android has heap limits and GC pauses, and no `Cleaner` before API 33.

## Decision

### Loading (OQ-031)

- **Synchronous loading comes first** (startup, tools, tests, headless). Asynchronous loading is an **opt-in contract**; synchronous loading is its special case and the contract must not make it harder.
- An asynchronous request returns a small handle with the states pending, ready and failed, plus progress for loading screens. Completion is delivered through the inbox on the runtime thread ([ADR-0018](0018-runtime-thread-events-and-cache-thread-safety.md)) and never blocks the update step. Cancellation is best-effort.
- Workers only read and decode (pure, no engine state, no GPU calls, no cache access). GPU upload and platform object creation happen on the owning thread. The worker pool is bounded and provided by the platform (small default, one to two workers).
- The asynchronous contract is implemented in a later batch than the synchronous one.

### Caching and reloading (OQ-032)

- Ownership, keys, release and errors follow ADR-0005 and ADR-0007 (the consuming module owns its cache; release is idempotent).
- **Explicit reference counting and explicit release.** No weak or soft references, no automatic eviction by default. Bounded caches only when configured (`OQ-101`, `OQ-110`). Resources can be grouped by scope (for example a level) and released together.
- **Reloading** is not automatic in the core (no file watching). It is supported only (a) for context-loss recovery by the backends (lost OpenGL context, recreated Android surface: re-upload from the CPU copy or reload from the resource) and (b) as an explicit reload at a frame boundary for development.

### Memory (OQ-033)

- **No allocation in per-step update and per-frame render paths in steady state**: primitives, pooled or preallocated objects and arrays, index loops instead of iterators, no boxing, no string building, guarded logging.
- Resources are released **explicitly**; no finalizers and no `Cleaner` (`Cleaner` needs API 33, `android-compatibility.md`). Direct buffers and native memory are released by their owner.
- Verification: allocation tests on the JVM in the headless suite; Android measured later (`OQ-070`, `OQ-139`). Memory budgets per platform tier remain `OQ-070`.

## Options considered

- Asynchronous loading only: forces every caller and test through callbacks. Rejected.
- Weak or soft reference caches: less code, but unpredictable memory behavior on Android. Rejected.
- Automatic hot reload in the core: a development convenience that adds file-system and thread concerns to every platform. Rejected for the core.

## Consequences

- Positive consequences: predictable memory and lifetimes; simple synchronous path for tests; loading never blocks the update step.
- Costs / constraints: callers must release explicitly (leaks are possible and must be tested); two loading paths to maintain; backends must implement context-loss recovery.
- Impact on modules and APIs: `resources` and every consuming module (`graphics`, audio, `tilemap`); backends.
- Impact on tests and documentation: contract tests for release, scope release and failed loads; allocation tests; `conventions.md` and `contracts.md` updated.

## Validation

Proposal confirmed by the project owner on 2026-11-22 (issue #877).
