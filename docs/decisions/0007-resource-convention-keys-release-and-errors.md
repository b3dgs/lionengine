# ADR-0007 — Resource convention: keys, release and errors

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner (decision 6 of issue #859, 2026-11-22); accepted on 2026-11-22.
- Related requirements: `REQ-RES-001`, `REQ-RES-002`, `REQ-RES-003`, `REQ-RES-004`, `REQ-RES-005`, `REQ-SPRITE-002`, `REQ-SPRITE-005`, `REQ-AUDIO-003`, `REQ-AUDIO-006`, `REQ-SHEET-002`
- Open questions resolved: `OQ-118` (except thread safety, moved to `OQ-121`)
- Supersedes / superseded by: none. Extends [ADR-0005](0005-common-convention-for-modules-handling-resources.md) (ACCEPTED), which fixed the rule and its location but left its content `TO BE DEFINED`; ADR-0005 is not modified.

## Context

ADR-0005 requires every module that loads or decodes resource content to follow one common convention for cache keys and release. Its content was open (`OQ-118`), and the module cache policies (`OQ-101`, `OQ-110`) and the sprite sheet id (`OQ-106`) depend on it.

## Decision

Decisions taken by the owner (issue #859):

1. **Key:** a logical resource identifier, a normalized path-like string in the resource space: `/` as separator, case-sensitive, no leading `/`, no `.` or `..` segment. It is the same for every origin (`REQ-RES-001`); a stream or memory resource supplied by the application is registered under such a name. A key is never a platform type, a `File` or an absolute path.
2. **Derived caches:** a cache holding derived content keys on the pair (resource key, module-defined discriminator), for example (image key, palette mapping) for color variants.
3. **No decoded cache in `resources`:** unchanged from ADR-0005.
4. **Release:** owned by the consuming module that holds the decoded content. Release and close are idempotent: a second release has no effect and does not throw.
5. **Errors:** a missing or unreadable resource raises one unchecked, engine-defined exception type. Decoding errors (invalid format, corrupted content) belong to the consuming module.

Not decided (stay `TO BE DEFINED`): the thread-safety rule shared by module caches (`OQ-121`); asynchronous loading (`OQ-031`); reloading (`OQ-032`); Java type, method and package names.

## Options considered

### Key form

- A (retained): normalized logical path string. Readable, portable to Android assets and archives, independent of the origin.
- B: opaque typed identifier. Stronger typing, but every module and provider would need a mapping from paths.

### Double release

- A (retained): idempotent. Safe with shared ownership paths and error handling.
- B: fail on double release. Detects bugs, but makes cleanup code fragile.

## Consequences

- Positive consequences: one key and release model for `graphics`, audio and later consumers; sprite sheet ids, image caches and sound caches can use the same key form; reusable contract tests.
- Costs / constraints: providers must map their physical locations to normalized keys; case-sensitivity must be honored even on case-insensitive filesystems.
- Impact on modules and APIs: `resources` defines the key value type and the exception type; consuming modules key their caches on it. No new module or dependency; no platform type in any API.
- Impact on tests and documentation (including Android/DEX compatibility): unit tests of key normalization (separator, case, `.` and `..` rejected, no leading `/`) and of idempotent release; a contract suite in `test-support` run by each consuming module (`REQ-RES-004`, `REQ-RES-005`). Keys are plain strings, so no Android/DEX risk. The game-loop invariant is untouched.

## Validation

Accepted by the project owner on 2026-11-22 (explicit request in the review session that followed PR #862). The decisions were given by the owner on issue #859. Acceptance confirms the resolution of `OQ-118`; thread safety stays `OQ-121`.
