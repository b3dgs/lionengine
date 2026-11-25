# ADR-0055 — Caches: runtime-thread check, opt-in memory limit and development reload

- Status: ACCEPTED
- Date: 2026-11-25
- Decision makers: agent, under the project owner's explicit delegation of 2026-11-25 ("I let you decide what is the best option to solve it. Check standards and choose what is most adapted to the engine"); reversible by a superseding ADR
- Related requirements: `REQ-RT-003`, `REQ-RT-005`, `REQ-SPRITE-002`, `REQ-RES-004`
- Open questions resolved: none (gaps left by sprite sheet batch 2, recorded in [`../features/sprite-sheet.md`](../features/sprite-sheet.md))
- Supersedes / superseded by: none; completes [ADR-0018](0018-runtime-thread-events-and-cache-thread-safety.md), [ADR-0019](0019-loading-resource-policy-and-memory.md) and [ADR-0026](0026-graphics-pixels-cache-transforms-animation-and-commands.md) (all unchanged)

## Context

Sprite sheet batch 2 delivered the image cache and left five points open, each allowed or required by an accepted decision but without the details needed to write it:
- ADR-0018 confines caches to the runtime thread, and `REQ-RT-003` is verified by "thread-confinement assertion tests", but no engine type checks its thread: the rule was documented only.
- ADR-0019 and ADR-0026 allow a cache size limit "only when configured", without unit, value or behavior. ADR-0033 already fixed the one of the short-sound cache: opt-in, in bytes, none by default.
- ADR-0019 allows reloading for context-loss recovery and as an explicit development reload at a frame boundary; `REQ-RT-005` is verified by contract tests of "reload at a frame boundary". Neither had an API.
- ADR-0019 groups resources by scope and releases them together; the sheet groups of ADR-0026 are such a scope, not written yet.
- ADR-0019 makes asynchronous loading opt-in, "in a later batch".

What other engines do: Android views and JavaFX check their owner thread on every call and throw `IllegalStateException` (`CalledFromWrongThreadException` on Android); libGDX, Godot and MonoGame asset managers count references or memory but do not cap a cache that holds only resources in use; capped caches (Android `LruCache`, Glide) evict unused entries, which ADR-0019 forbids; libGDX "managed" textures and Android `GLSurfaceView` games recover a lost context by uploading again from a CPU copy.

## Decision

1. **Runtime-thread check of the caches (`REQ-RT-003`).** `core` gets `ThreadConfinement`, a small final class: the thread of its first `check()` becomes the owner, and a later check on another thread throws `IllegalStateException` naming the object, both threads and the rule. Every cache checks it at the start of each operation (acquire, add, lookup, size, reload, decoder registration, memory query) and at the release and preparation of its handles, before changing anything. The owner is the first user, not the creator, because the application creates its caches while wiring the game, before the platform starts the loop on the runtime thread (ADR-0040). The owner never changes. The check is always on: it costs one volatile read and a comparison and allocates nothing, and caches are not on the per-step paths. Reading the immutable pixels of a handle is not checked (ADR-0018 lets any thread read published immutable data). Other runtime-thread types keep the documented rule only.
2. **Opt-in memory limit.** A cache counts the memory of its decoded content; the image cache counts 4 bytes per cached pixel, derived images included (the CPU copy; the backend's texture memory is not counted). A limit in bytes is opt-in, none by default, set when the cache is created, as for the short-sound cache (ADR-0033). Since every cached entry is in use and nothing is evicted (ADR-0019), an entry that would exceed the limit is refused with `IllegalStateException`, as a capacity-restricted `java.util.Queue` refuses an element, and nothing is cached. The memory used is readable at any time, to check budgets and leaks in tests.
3. **Development reload.** A cache reloads a cached key on request: it reads and decodes the resource again and gives every handle the new content; the image cache replaces the pixels, size and texture of the decoded image together (new arrays, a new texture, the previous texture released) and leaves derived images unchanged. A failure leaves the content unchanged. A key that is not cached is refused with `IllegalStateException`. The game calls it between two frames (update phase or inbox event); the cache does not watch files.
4. **Context loss** needs nothing from the caches: the texture store keeps the pixel arrays it receives until their release (ADR-0047) and uploads them again after a lost context. The backends that can lose a context (`render-opengl`, Android, Phase 4) test it.
5. **Scope release** is done by the sheet groups of the next sprite sheet batch (ADR-0026, decision 7): a group holds its images and releases them together. No generic scope type is added: a scene releases the handles and groups it acquired.
6. **Asynchronous loading** stays with the asynchronous handles and load groups of the game loop (batch 4 of `features/game-loop.md`), shared by images, sounds and maps; the cache's asynchronous request follows it. The worker only decodes; the runtime thread inserts the result, so the thread check and the limit apply unchanged.

The cache convention suite of the `resources` test JAR (ADR-0045) checks decisions 1 and 3 for every cache.

## Options considered

- Thread check:
  - Always-on check bound to the first user (chosen): deterministic on every platform and in production, no wiring.
  - No check (rejected): `REQ-RT-003` asks for assertion tests, and a worker touching a cache corrupts it silently.
  - Bound to the creating thread (rejected): the application creates caches on its main thread, then the loop runs on `lionengine-runtime` (desktop) or the renderer thread (Android).
  - Java `assert` (rejected): disabled by default and on Android, so the behavior would differ between tests and games.
  - The loop's thread given to every cache, or a static registry of the runtime thread (rejected): one more object to wire into each cache, or static state (forbidden).
- Limit:
  - Opt-in limit in bytes with refusal (chosen): the unit of the audio cache, so every cache states its limit in the same unit.
  - Measurement only (rejected): ADR-0019 and ADR-0026 allow a configured limit, and a refused load is the earliest report of a budget overrun.
  - A limit with eviction of unused entries (rejected): nothing is unused in a cache that removes an entry with its last handle, and eviction is forbidden by default.
  - A limit in pixels (rejected for the cache): pixels do not compare with sounds or maps; decoders keep their pixel limit (ADR-0054).
- Reload:
  - Reload by key, applied at once on the runtime thread (chosen): simple, testable, at a frame boundary when called outside the render phase.
  - A reload request queued for the next frame boundary (rejected): the cache does not know the loop; the inbox already serves that purpose.
  - Reloading derived images (rejected): they are copies made by the game, which rebuilds them.

## Consequences

- Positive consequences: a cache used by a loading worker or a platform callback fails at once with both thread names; a game can bound the memory of its images like that of its sounds; artists see a changed image without restarting the game; `REQ-RT-003` and the reload part of `REQ-RT-005` are tested by every cache.
- Costs / constraints: a cache cannot move to another thread; if the Android platform has to run the loop on a new thread while keeping the caches (activity recreation), a later ADR adds a hand-off. A reload changes the texture of an image, so render commands written before it in the same frame name a released texture: the reload happens outside the render phase. An image refused by the limit is decoded before it is refused (the cache knows its size only then).
- Impact on modules and APIs: `core` (`ThreadConfinement`); `graphics` (`ImageCache`: constructor with a limit, `NO_LIMIT`, `getUsedBytes`, `getMaxBytes`, `reload`; handles checked); `resources` test JAR (`CacheConventionContract`: reload and thread hooks); the audio and tile map caches follow when they are written.
- Impact on tests and documentation (including Android/DEX compatibility): `ThreadConfinementTest`, `CacheConventionContract`, `ImageCacheTest`, `ImageCacheExample`; `features/sprite-sheet.md`, `features/resources.md`, `contracts.md`, `interfaces.md`, `conventions.md`, `api-index.md`, `engine-features.md` and traceability updated. `java.util.concurrent.atomic.AtomicReference` and `Thread.currentThread()` exist since API 1.

## Validation

Decided by the agent on 2026-11-25 under the owner's explicit delegation ("I let you decide what is the best option to solve it. Check standards and choose what is most adapted to the engine"); reversible by a superseding ADR.
