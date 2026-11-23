# Feature: resources and providers

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 1 (`resources`, `resources-memory`, `resources-file`), then `resources-archive`; `resources-android` in Phase 4
- Affected modules: `resources`, `resources-file`, `resources-archive`, `resources-memory`, `resources-android`, `examples` (suites in the `resources` test JAR, ADR-0045)
- Requirements: `REQ-RES-001` to `REQ-RES-006`, `REQ-RT-003`, `REQ-RT-005`
- Applicable ADRs: [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md), [`ADR-0007`](../decisions/0007-resource-convention-keys-release-and-errors.md), [`ADR-0018`](../decisions/0018-runtime-thread-events-and-cache-thread-safety.md), [`ADR-0019`](../decisions/0019-loading-resource-policy-and-memory.md), [`ADR-0032`](../decisions/0032-definition-files-registry-ids-resource-providers-dependency-policy.md), [`ADR-0037`](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md)
- Open questions involved: none

## Objective

Open any game data by a logical key, whatever its origin, with explicit ownership, and give every consuming module the same key, release and error rules.

## Out of scope

Decoding and decoded caches (they belong to `graphics`, audio, `tilemap`); file watching and hot reload (ADR-0019).

## Expected behavior

- **`ResourceKey`:** created from a string and normalized: `/` separator (a `\` is rejected, not converted), case-sensitive, no leading `/`, no empty segment, no `.` or `..` segment, not empty. Invalid input throws `IllegalArgumentException`. Two keys are equal when their normalized strings are equal.
- **`Resources`:** the application builds it with an ordered list of `ResourceProvider`s. `open(key)` asks each provider in order and returns an `InputStream` from the first that has the key; the caller owns and closes the stream. `exists(key)` never throws for a missing key.
- **Writing:** `openWrite(key)` is allowed only on a provider declared writable (`resources-memory`, or an application storage provider); otherwise it throws `IllegalStateException`. Writing happens on the runtime thread only.
- **Application streams:** the application can register a stream supplier under a key in `resources` (for example downloaded content); it behaves like a provider placed where the application inserts it.
- **Errors:** missing everywhere → `ResourceException` with reason `MISSING`; present but unreadable (I/O error, permission) → reason `INACCESSIBLE`; both carry the key and the cause. Decoding errors are the consuming module's exceptions (`ImageDecodingException`, `AudioDecodingException`, `MapLoadingException`).
- **Providers:** `resources-file` maps keys under a configured root directory and never escapes it; `resources-archive` reads a ZIP archive through `java.util.zip`; `resources-memory` stores byte arrays (writable); `resources-android` reads Android assets.
- **Consuming modules' caches** (the cache convention suite of the `resources` test JAR, ADR-0045): key = (resource key, module discriminator); reference counted; `release` idempotent; no automatic eviction; operations on the runtime thread only; decoded data immutable.

## Envisaged APIs / contracts

Canonical names (ADR-0037): `ResourceKey`, `ResourceProvider`, `Resources`, `ResourceException` with `Reason`; signatures written by the batch. `ResourceKey`, `ResourceProvider` and `ResourceException` are declared by the contract skeleton (ADR-0049, [`contract-skeleton.md`](contract-skeleton.md)); the batches below write `Resources`, the providers and the suites.

## Batches (order)

1. `ResourceKey`, `ResourceException`, `ResourceProvider`, `Resources` with ordered lookup; `resources-memory`; provider contract suite (`REQ-RES-001`, `REQ-RES-003`, `REQ-RES-005`, `REQ-RES-006`).
2. `resources-file`; application streams (`REQ-RES-002`).
3. Cache convention contract suite for consuming modules (`REQ-RES-004`, `REQ-RT-003`, `REQ-RT-005`).
4. `resources-archive`; later `resources-android`.

## Lifecycle and concurrency

Providers are thread-safe for reading (loading workers read through them); writing and cache operations run on the runtime thread.

## Units and Android/DEX compatibility

No units. `java.io` streams and `java.util.zip`; no `java.nio.file` in shared modules (it needs API 26); `resources-file` is used on desktop, Android uses `resources-android`.

## Dependencies

`resources` on `core`; each provider on `resources`.

## Tests

- Unit tests: key normalization and every rejection; provider order and override; read-only refusal; error reasons; directory escape refused.
- Contract suites (`resources` test JAR, ADR-0045): provider suite run by every provider; cache convention suite run by every consuming module.
- Concurrency test: parallel reads from two threads on each provider.

## Acceptance criteria

- [ ] Requirements covered; complete Javadoc; headless example (memory provider overriding a file provider); API-index entry.
