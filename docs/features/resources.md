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
- **Consuming modules' caches** (the cache convention suite of the `resources` test JAR, ADR-0045): key = (resource key, module discriminator); reference counted; `release` idempotent; no automatic eviction; development reload of a cached key; operations on the runtime thread only, checked: the thread of the first operation owns the cache (ADR-0055); decoded data immutable.

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

## State (2026-11-25)

Batches 1 to 3 are implemented, with the example `ResourcesExample` (memory provider overriding a file provider) and its API-index row; batch 4 (`resources-archive`, `resources-android`) is not. Batch 3 (2026-11-25, with [`sprite-sheet.md`](sprite-sheet.md) batch 2): the abstract `CacheConventionContract<H>` of the `resources` test JAR, where `H` is the handle type of the cache. A consuming module binds it by creating its cache over the `Resources` the suite gives (a `FakeResourceProvider` inside), naming keys it can decode, giving two valid contents and an invalid one with its decoding exception, and acquiring, releasing and comparing handles. It checks that equal keys share content decoded once, different keys do not, the content stays until its last release and is decoded again after, a second release of a handle has no effect, nothing is evicted (64 keys), missing and unreadable keys raise the `ResourceException` of the key, invalid content raises the decoding exception, and none of these errors leaves anything cached or a stream open. Since ADR-0055 it also checks the development reload (every handle sees the new content; a failed reload and a key not cached change nothing) and the runtime-thread check (an acquisition, a release, a reload or a count on another thread than the first user throws an `IllegalStateException` and changes nothing), through two more hooks: reload a key, and tell whether a handle gives a given content. Keys with a discriminator and the immutability of the decoded content are checked by each module with its own types. First binding: `ImageCacheConventionTest` (`graphics`). Names and details chosen by the batches, within this page, ADR-0007 and ADR-0032:
- `Resources` takes its providers as constructor arguments, in lookup order (copied). `exists` and `open` look the key up in each provider in turn; when the provider that has the key cannot read it, `open` reports `INACCESSIBLE` and does not fall back to the next provider, so a broken override is never hidden. `openWrite` writes to the first writable provider, `IllegalStateException` when there is none; `isWritable()` tells whether one exists. A `null` key or provider is an `IllegalArgumentException`, as in every provider.
- `ResourceProvider.openWrite` now states that the written content is readable once the stream is closed.
- Application streams: `StreamProvider` (read-only, thread-safe) holds `StreamSupplier` callbacks registered by key (`register` replaces a previous one); an `IOException` or a `null` stream from the supplier is `INACCESSIBLE`. The application places the provider in the list where its content must win.
- `MemoryProvider` (`resources-memory`, writable): content stored at the first close of the write stream, never modified afterwards, so a stream already opened keeps its content; bytes written after the close are ignored.
- `FileProvider` (`resources-file`, read-only, desktop): the root must be an existing directory and is kept canonical (`getRoot()`). A key matches a file only when every segment has the exact case of the name listed by its directory, so keys stay case-sensitive on case-insensitive filesystems; directories are not resources. A file that resolves outside the root through a symbolic link is present (`has` is `true`) but `open` refuses it as `INACCESSIBLE`; an unreadable file is `INACCESSIBLE` too. Writable file storage (save games) is not part of these batches.
- Test fixtures (ADR-0045) in the `resources` test JAR: the abstract `ResourceProviderContract` suite (content, missing keys, directories, case, new stream per open, double close, writing according to `isWritable()`, two threads reading in parallel), bound by `StreamProvider`, `MemoryProvider`, `FileProvider` and the fixture itself; and `FakeResourceProvider` (writable or not, unreadable keys, counts of opened and unclosed streams) for the tests of `Resources` and of the modules that read resources.

## Acceptance criteria

- [ ] Requirements covered; complete Javadoc; headless example (memory provider overriding a file provider); API-index entry.
