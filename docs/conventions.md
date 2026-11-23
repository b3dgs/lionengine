# Code conventions

**Status: VALIDATED — conventions every implementation follows; each one can be amended by an ADR.**

## Naming and units

- Names carry the unit when the type does not (`deltaSeconds`, `speedMetersPerSecond`, `positionX`, `angleDegrees`).
- No `ms`, `px` or unit-less `time`/`speed`/`size` in world-facing APIs.
- Group `com.b3dgs.lionengine`, artifacts `lionengine-XXX` (`XXX` = module name), base package `com.b3dgs.lionengine` (ADR-0012); package tree per module verified at the build skeleton (ADR-0037, ADR-0042).
- World: origin `(0, 0)`, Y up, meters, `double`; screen (logical frame): origin top left, Y down, pixels; angles in degrees in public APIs and data files (ADR-0013, ADR-0014).
- Rotation is counter-clockwise positive, `0°` along `+X`; absolute angles in `[0, 360)`, signed differences in `(-180, 180]`; normalization only through explicit helpers, never in setters (ADR-0016).
- Registry ids are `namespace:name` in lower case (`lionengine:movable`); resource keys are normalized logical paths (`sprites/hero.png`) (ADR-0007, ADR-0032).

## API design

- Small public surface: package-private or module-private by default; `public` only for contracts.
- Interfaces for contracts that have several plausible implementations; no interface for a single internal class.
- Document nullability on every public parameter and return value (`@param`, `@return`). Non-null by default; `null` only when the Javadoc says so.
- Validate arguments at the public boundary and fail fast with a documented exception (`@throws`): `IllegalArgumentException` for an invalid argument, `IllegalStateException` for a call in the wrong state.
- Prefer immutable value types for results and configurations; mutable state has a documented owner and lifecycle. In hot paths, primitives and preallocated mutable objects replace value objects (ADR-0019).
- Do not expose collections for mutation; return unmodifiable views or copy into a caller-provided buffer, documented.
- Configuration has documented defaults; every default value of ADR-0031 to ADR-0039 and of the feature pages is a named constant in the code and stated in the Javadoc.
- Listeners are interfaces with primitive arguments where they run in hot paths (ADR-0034).

## Packages, names and callbacks

- Each module lives in its root package of [ADR-0037](decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md); inside a module a subpackage may use its ancestors only, never a sibling or a descendant.
- Use the canonical type names of ADR-0037 for the concepts it lists.
- No `java.util.function`, `Optional`, streams or `java.time` in shared public APIs or hot paths: callbacks are small role-named interfaces with one abstract method (`SceneFactory<C>`, `TransitionCondition<C>`, `CloseListener`), absence is `null` plus a `has...` check, documented.
- Interfaces carry no `I` prefix and implementations no `Impl` suffix: an implementation is named by what distinguishes it (`AwtPlatform`, `FileProvider`, `FakeAudioBackend`).
- Listeners are `XxxListener` with `onYyy` methods; factories `XxxFactory` with `create`; exceptions `XxxException` extending `RuntimeException`.
- Engine value types are `final` classes until records are verified for Android by the Android job (ADR-0041).

## Errors

- Exceptions are for contract violations and unrecoverable failures; expected absence is expressed in the return type or by a check method (`isAlive`, `has`, `isKnown`).
- Resource errors: missing or inaccessible content raises the unchecked resource exception of `resources` with a `MISSING` or `INACCESSIBLE` reason; invalid format and corruption raise the decoding exception of the consuming module; definition files raise the definition exception of `core` with key, line, column and JSON path (ADR-0007, ADR-0032).
- Never swallow an exception silently; closing a resource must not hide the primary failure. The loop is fail-fast (ADR-0017).

## Resources and lifecycle

- The owner of every closable resource is documented; the owner closes it.
- Closing and releasing are idempotent unless the Javadoc says otherwise.
- Caches use explicit reference counting and release, no weak references, no automatic eviction by default; reloading only for context loss and explicit development reload (ADR-0019).
- No finalizers or `Cleaner` (ADR-0019).

## Performance (game engine specifics)

- No allocation in the per-step update and per-frame render paths in steady state: primitives, pooled or preallocated objects and arrays, index loops instead of iterators, no boxing, no string building, guarded logging (ADR-0019). This matters for garbage-collection pauses on Android.
- No `HashMap` iteration or identity hashing where the order is observable (ADR-0030).
- Measure before optimizing; record the measurement method in the test (see `testing.md`, performance level).
- `float` only at the render boundary (ADR-0014).
- One version number for all modules; a package never depends on a descendant package (ADR-0015).
- No reflection, `ServiceLoader`, class scanning or runtime bytecode generation in the engine; data-driven instantiation uses an explicit registry of ids and factories (ADR-0020).
- Randomness in simulation code comes from the world's seeded generator only (ADR-0024, ADR-0031).

## Concurrency

- One runtime thread owns the loop, the update phase, the world, the caches and by default the render phase; the platform runs the loop on the thread it requires (ADR-0018). Every public type documents its thread; the default is "not thread-safe, owned by the runtime thread". The engine starts no thread by itself. Platform events reach the runtime thread through the inbox at the start of an iteration.

## Data files

- Definition files are strict standard JSON with `"format"` and `"version"`, lower camel case field names, `"comment"` for documentation, colors as `"#RRGGBB"` or `"#AARRGGBB"`, seconds, degrees and pixels as units (ADR-0028, ADR-0032).
- Asset and data file names are lower case with hyphens (ADR-0023).

## Java language and APIs

- Java 25+ for development, restricted by ADR-0001 to what is verified as Android/DEX-compatible; check each new language feature or API with [`android-compatibility.md`](android-compatibility.md) and record dependencies in [`dependencies.md`](dependencies.md).
- Preview features are forbidden in shared code.
- Platform-specific code stays in the platform modules.

## Logging

- Shared modules log only through the `core` logging facade: levels `ERROR`, `WARNING`, `INFO`, `DEBUG`, guarded by `isEnabled`, constant messages with primitive arguments (ADR-0031). No logging library in shared modules.
