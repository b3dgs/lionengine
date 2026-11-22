# Code conventions

**Status: PROPOSED — conventions an agent may follow by default; each one can be amended by an ADR. Anything marked TO BE DEFINED must not be guessed.**

## Naming and units

- Names carry the unit when the type does not (`deltaSeconds`, `speedMetersPerSecond`, `positionX`).
- No `ms`, `px` or unit-less `time`/`speed`/`size` in world-facing APIs.
- Group `com.b3dgs.lionengine`, artifacts `lionengine-XXX` (`XXX` = module name), base package `com.b3dgs.lionengine` (ADR-0012).
- World: origin `(0, 0)`, Y up; screen: origin top left, Y down; angles in degrees in public APIs (ADR-0013). Name angle parameters with the unit (`angleDegrees`).

## API design

- Small public surface: package-private or module-private by default; `public` only for contracts.
- Interfaces for contracts that have several plausible implementations; no interface for a single internal class.
- Document nullability on every public parameter and return value (`@param`, `@return`). Prefer non-null by default; `null` only when the Javadoc says so.
- Validate arguments at the public boundary and fail fast with a documented exception (`@throws`).
- Prefer immutable value types for positions, sizes and durations; mutable state must have a documented owner and lifecycle.
- Do not expose collections for mutation; return unmodifiable views or copies, documented.

## Errors

- Exceptions are for contract violations and unrecoverable failures; expected absence is expressed in the return type.
- Resource errors distinguish at least: missing, inaccessible, invalid format, corruption (see `contracts.md`).
- Never swallow an exception silently; closing a resource must not hide the primary failure.

## Resources and lifecycle

- The owner of every closable resource is documented; the owner closes it.
- Closing is idempotent unless the Javadoc says otherwise.
- Caches use explicit reference counting and release, no weak references, no automatic eviction by default; reloading only for context loss and explicit development reload (ADR-0019).
- No allocation in per-step and per-frame paths in steady state; no finalizers or `Cleaner` (ADR-0019).

## Performance (game engine specifics)

- Avoid allocation in the per-step update and per-frame render paths; this matters for garbage-collection pauses on Android.
- Measure before optimizing; record the measurement method in the test (see `testing.md`, performance level).
- World quantities are `double`; `float` only at the render boundary (ADR-0014). Rotation is counter-clockwise positive, `0°` along `+X`. Absolute angles are in `[0, 360)`, signed differences in `(-180, 180]` (ADR-0016).
- One version number for all modules; a package never depends on a descendant package (ADR-0015).

- No reflection, `ServiceLoader` or class scanning in the engine; data-driven instantiation uses an explicit registry of ids and factories (ADR-0020).

## Concurrency

- One runtime thread owns the loop, the update phase, the world, the caches and by default the render phase; the platform runs the loop on the thread it requires (ADR-0018). Every public type documents its thread; the default is "not thread-safe, owned by the runtime thread". The engine starts no thread by itself. Platform events reach the runtime thread through an inbox at the start of an iteration.

## Java language and APIs

- Java 25+ for development, restricted by ADR-0001 to what is verified as Android/DEX-compatible; check each new language feature or API with [`android-compatibility.md`](android-compatibility.md).
- Platform-specific code stays in the platform modules.

## Logging

- Core modules do not depend on a concrete logging library. The logging abstraction: **TO BE DEFINED**.
