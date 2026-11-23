# Feature: core foundation (logging, JSON, definitions, registry, random)

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 1, first batches after the build skeleton
- Affected modules: `core`, `test-support`, `examples`
- Requirements: `REQ-CORE-006`, `REQ-DEF-001` to `REQ-DEF-003`, `REQ-RT-007`, `REQ-DET-003`
- Applicable ADRs: [`ADR-0020`](../decisions/0020-no-reflection-factory-registry-and-capability-lookup.md), [`ADR-0028`](../decisions/0028-tile-map-collision-json-definitions-and-storage.md), [`ADR-0031`](../decisions/0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md), [`ADR-0032`](../decisions/0032-definition-files-registry-ids-resource-providers-dependency-policy.md), [`ADR-0037`](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md), [`ADR-0040`](../decisions/0040-thread-hand-off-contracts-in-core-and-loop-wiring.md)
- Open questions involved: none

## Objective

The dependency-free building blocks every other module uses: a logging facade, a strict JSON reader with precise errors, typed access to definition data, an explicit registry of data-driven types, and a seeded random generator.

## Out of scope

The thread hand-off contracts of `core` (`Inbox`, `InboxEvent`, `AsyncHandle<T>`, `AsyncState`, `LoadGroup`, `WorkerPool`, `LoopControl`, `LoopDriver`, ADR-0040): they live in `core` but are specified and delivered by the batches of [`game-loop.md`](game-loop.md). Reading files (the JSON reader takes characters or bytes; opening resources belongs to `resources`); concrete definition formats (each owning module defines its own: sheets in `graphics`, maps in `tilemap`, objects in `ecs`).

## Expected behavior

### Logging (`Log`, `LogLevel`, `LogSink`)

- Levels `ERROR`, `WARNING`, `INFO`, `DEBUG`; `isEnabled(level)`; messages are constant strings with optional primitive arguments; a disabled level allocates nothing.
- Default sink: `ERROR` and `WARNING` to `System.err`, others dropped; a sink can be replaced (a platform installs its own). The logger is an instance passed explicitly or held by the platform object, not a static singleton.

### JSON reader (`JsonValue`, `JsonObject`, `JsonArray`)

- RFC 8259 only, UTF-8 input (a leading byte-order mark is ignored). Errors: duplicate keys, comments, trailing commas, `NaN`, `Infinity`, invalid escapes, unterminated values, content after the root value.
- Numbers: an integral literal without fraction or exponent that fits a `long` is a `long`; any other number is a `double`.
- Object keys keep their document order.
- Limits, configurable: 16 MiB per document, nesting depth 64, 1 MiB per string; exceeding one is an error.
- Every error is a `DefinitionException` with the resource key (or a caller-given source name), line, column (both from 1) and JSON path (`$.animations.walk.frames[3]`).

### Definition files and typed configuration (`Configuration`)

- An engine definition file has `"format"` (for example `lionengine.sheet`) and `"version"` (`int` from 1); a loader rejects an unknown format or a version above the one it supports.
- `Configuration` is a read-only view of one JSON object with getters by name: `getInt`, `getLong`, `getDouble`, `getBoolean`, `getString`, `getObject`, `getArray`, each with and without a default. Missing required values, wrong types and out-of-range values throw `DefinitionException` naming the parameter (and the id when created by the registry).
- Strict fields: after a factory returns, any field it did not read except `"comment"` is an error.

### Registry (`Registry<T>`)

- An instance (several may coexist), filled at startup: `register(id, factory)`. Ids are `namespace:name`, each part `[a-z][a-z0-9_-]*`; the engine uses `lionengine`.
- `create(id, configuration)` calls the factory. Unknown id, duplicate registration and invalid parameters throw `DefinitionException` naming the id (and parameter).
- No reflection, `ServiceLoader` or class scanning.

### Random source (`RandomSource`)

- SplitMix64 expands a `long` seed into the state of xoshiro256\*\*; methods: next `long`, next `int` in `[0, bound)` without modulo bias, next `double` in `[0, 1)`, next `boolean`. Integer operations only; results identical on every platform.
- Tests compare with the published reference outputs of both algorithms.

## Envisaged APIs / contracts

Canonical names from ADR-0037: `Log`, `LogLevel`, `LogSink`, `JsonValue`, `JsonObject`, `JsonArray`, `DefinitionException`, `Configuration`, `Registry<T>`, `RandomSource`. Factories are role-named interfaces (`XxxFactory` with `create`), not `java.util.function`.

## Batches (order)

1. Logging facade (`REQ-CORE-006`).
2. JSON reader and `DefinitionException` (`REQ-DEF-001`, part of `REQ-DEF-002`).
3. `Configuration` and `Registry<T>` (`REQ-DEF-002`, `REQ-DEF-003`, `REQ-RT-007`).
4. `RandomSource` (`REQ-DET-003`).

## Lifecycle and concurrency

All types are not thread-safe except immutable parsed values, which may be read by any thread after publication; the reader itself can run on a loading worker (ADR-0019).

## Units and Android/DEX compatibility

No units. Pure Java on `java.lang` and `java.util` collections; no `java.util.function`, `Optional`, streams or `java.time` (ADR-0037).

## Dependencies

None (JDK only).

## Tests

- Unit tests: every reader error with its line, column and path; numbers at `long` bounds; Unicode escapes and surrogate pairs; limits; typed getters with and without defaults; strict fields; id validation; duplicate and unknown ids; two independent registries; random reference vectors and bound without bias; logging levels and allocation with a disabled level.

## Acceptance criteria

- [ ] Requirements covered; complete Javadoc; headless example (read a definition, create an object from a registry); API-index entry.
