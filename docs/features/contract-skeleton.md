# Feature: contract skeleton

**Status: VALIDATED — content of the contract skeleton (ADR-0049); its Java signatures are proposed by the batch and reviewed in its pull request.**

- Owner: project owner
- Roadmap phase: Phase 0, second batch, right after [`build-skeleton.md`](build-skeleton.md) and before the Phase 1 feature batches
- Affected modules: `core`, `runtime`, `platform-api`, `render-api`, `resources`
- Requirements: `REQ-CORE-003`, `REQ-CORE-004`, `REQ-CORE-005`, `REQ-TECH-006`, `REQ-TECH-008`
- Applicable ADRs: [`ADR-0037`](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md), [`ADR-0040`](../decisions/0040-thread-hand-off-contracts-in-core-and-loop-wiring.md), [`ADR-0045`](../decisions/0045-test-fixtures-with-their-contracts-and-test-support-kit.md), [`ADR-0046`](../decisions/0046-display-state-owned-by-the-platform-and-presentation-geometry.md), [`ADR-0047`](../decisions/0047-render-api-texture-pixel-data-and-shader-resource-keys.md), [`ADR-0049`](../decisions/0049-interface-catalog-and-contract-skeleton.md)
- Open questions involved: none

## Objective

Declare, once and compiled, the contracts that cross module boundaries in Phases 1 and 2. Then the feature batches implement them instead of each designing its own side, and the Android job, doclint and the package rule check them before any behavior exists.

## Out of scope

- Any concrete engine class (`GameLoop`, `World`, `CommandBuffer`, `Resources`, `Log`, the JSON reader, ...).
- The seams the catalog assigns to a feature batch because they are built on one of its classes or have a hot-path shape to design: the render backend contract, the color-transform capability, the image, audio and input contracts, `ShapeQuery`, `FollowLookup` ([`../architecture/interfaces.md`](../architecture/interfaces.md), section 2).
- Examples and API-index entries: a declared contract becomes usable with its feature batch, which adds them (ADR-0049).

## Expected behavior

Each type below is declared in the root package of its module (ADR-0037) with complete Javadoc: description, `@param`, `@return`, `@throws`, `@since`, thread, units, ownership, errors and absence ([`../documentation.md`](../documentation.md)). Interfaces and callbacks declare methods only. Value types validate their arguments (`IllegalArgumentException`) and provide accessors, `equals`, `hashCode` and `toString`; they may be records (verified on API 21, ADR-0044). Enums list their constants. Exceptions extend `RuntimeException`. No `java.util.function`, `Optional`, streams or `java.time` (ADR-0037).

### `core` (`com.b3dgs.lionengine`)

| Type | Kind | Contract to state in the Javadoc |
|---|---|---|
| `LogLevel` | enum | `ERROR`, `WARNING`, `INFO`, `DEBUG` (ADR-0031). |
| `LogSink` | interface | Receives a level and a constant message with optional primitive arguments; nothing is allocated for a disabled level. The default sink writes `ERROR` and `WARNING` to `System.err`; a platform installs its own (`REQ-CORE-006`). |
| `DefinitionException` | exception | Carries the resource key or a caller-given source name, the line and column (both from 1) and the JSON path (`$.animations.walk.frames[3]`); for registry errors, the id and the parameter name (ADR-0032). |
| `InboxEvent` | callback | One method, `deliver()`, run on the runtime thread when the inbox delivers it; implemented by the producing module, which applies its own effect; instances may be reused (ADR-0040). |
| `Inbox` | interface | Bounded FIFO of `InboxEvent`s. `post` may be called from any thread, never blocks beyond a short lock and returns whether the event was accepted. When full, the event is rejected and counted, and the drop is logged once at the next delivery. Delivery runs on the runtime thread and handles every accepted event in arrival order. A listener registered during a delivery takes effect after it. Capacity is set by the loop (default 1,024) (ADR-0031). |
| `AsyncState` | enum | `PENDING`, `READY`, `FAILED`, `CANCELLED`. |
| `AsyncHandle<T>` | interface | State and progress (`double` in `[0, 1]`: bytes read over total when known, otherwise `0` then `1`) change only at inbox delivery, on the runtime thread. `get()` on a handle that is not `READY` throws `IllegalStateException`. A failed handle keeps its exception: the resource exception, or the decoding exception of the consuming module. `cancel()` is best effort, and a late result is discarded and released (ADR-0031). |
| `WorkerPool` and its unit of work | interface, callback | Work is submitted on the runtime thread and runs on a worker. Work only reads and decodes: no engine state, no GPU call, no cache access (ADR-0019). With `0` workers the work runs synchronously on the caller. The platform sets the count: 2 on desktop and 1 on Android by default. |
| `LoopControl` | interface | Thread-safe requests: pause, resume, stop, time scale (`double` in `[0.1, 4.0]`, otherwise `IllegalArgumentException`). These are atomic request fields read at the next frame boundary, never inbox entries, so they are never dropped. Stop is idempotent and never interrupts a step (ADR-0017, ADR-0031). |
| `LoopDriver` | interface | Extends `LoopControl`; runs one loop iteration on the calling thread and reports whether the loop is running, so a platform runs the loop on the thread it requires (ADR-0018, ADR-0040). |

### `runtime` (`com.b3dgs.lionengine.runtime`)

| Type | Kind | Contract to state in the Javadoc |
|---|---|---|
| `Clock` | interface | Monotonic time as a `long` in nanoseconds; `System.nanoTime` in production, a controllable clock in the `runtime` test JAR (ADR-0045). |
| *update callback* | callback | Called once per fixed step on the runtime thread with the constant step in seconds (`double`). The application or `composition` advances every `InputState`, then updates the scene manager (ADR-0040). |
| *render callback* | callback | Called once per iteration on the runtime thread with `alpha` (`double` in `[0, 1)`) and the unscaled real delta in seconds; presentation only, never feeds the simulation (`REQ-LOOP-006`). |
| `Scene` | interface | `enter()` once before the first update; `update(stepSeconds)` once per step while on top; `render(alpha, unscaledDeltaSeconds)` once per presentation while in the stack; `exit()` once when leaving the stack; `dispose()` once after `exit()`, after which the instance is never used. It knows no world, camera, graphics or audio type. It may expose a name used only for logs. Runtime thread (ADR-0021, ADR-0031). |

### `resources` (`com.b3dgs.lionengine.resources`)

| Type | Kind | Contract to state in the Javadoc |
|---|---|---|
| `ResourceKey` | value | Created from a string and normalized: `/` separator (a `\` is rejected), case-sensitive, no leading `/`, no empty, `.` or `..` segment, not empty; invalid input throws `IllegalArgumentException`; equal when the normalized strings are equal (ADR-0007). |
| `ResourceException` with `Reason` | exception, enum | Unchecked; carries the key and the cause; `MISSING` (no provider has the key) or `INACCESSIBLE` (present but unreadable) (ADR-0007). |
| `ResourceProvider` | interface | Tells whether it has a key and opens it as an `InputStream` that the caller owns and closes. Thread-safe for reading, because loading workers read through it. Read-only unless declared writable; writing runs on the runtime thread (ADR-0005, ADR-0032). |

### `render-api` (`com.b3dgs.lionengine.render`)

| Type | Kind | Contract to state in the Javadoc |
|---|---|---|
| `ScalingPolicy` | enum | `FIT` (aspect-preserving, the default), `INTEGER` (integer factors only, falls back to `FIT` when the surface is smaller than the logical frame, reported), `STRETCH`; scaling never changes world units (ADR-0006). The scaling function comes with [`display-modes.md`](display-modes.md) batch 3. |
| `OutputFilter` | value or enum | Standard identifiers `NONE` (nearest-neighbor), bilinear, `HQ2X`, CRT, and custom filters by registry id `namespace:name`. Scaling filters resample the logical frame to the output size; post filters (CRT) run on the scaled output. Fallbacks: CRT skipped; `HQ2X` → bilinear → `NONE` (ADR-0006, ADR-0025). |
| `Viewport` | value | A rectangle of the logical frame in pixels; it clips and offsets the following commands; the default viewport is the whole frame (ADR-0031). |
| *texture contract* | interface | Creates a texture from a width and height in pixels and an `int[]` of packed `0xAARRGGBB`, row-major, top-left origin, stride equal to the width, straight alpha; returns an `int` texture handle. The array is shared read-only: neither side modifies it, and the backend may keep it as its context-loss copy until release. Prepare uploads before first use. Release is idempotent. Called on the runtime thread; the upload runs on the render thread (ADR-0026, ADR-0047). |
| *custom filter contract*, its kind, *CPU filter callback* | interface, enum, callback | A registry id; a kind, scaling or post. It provides a fragment shader `ResourceKey` (GLSL ES 1.00 features, no `#version` or precision line; uniforms: source texture, source and output size in pixels, presentation time in seconds) and/or a CPU implementation from one `int[]` ARGB buffer to another. It is registered explicitly on the backend at startup; a backend lacking the needed implementation skips the filter and reports the skip (ADR-0033, ADR-0047). |

### `platform-api` (`com.b3dgs.lionengine.platform`)

| Type | Kind | Contract to state in the Javadoc |
|---|---|---|
| `ScreenMode` | enum | `WINDOWED`, `FULL_WINDOW`, `FULL_SCREEN`; fallback orders `FULL_SCREEN` → `FULL_WINDOW` → `WINDOWED`, `WINDOWED` → `FULL_WINDOW` → `FULL_SCREEN`, `FULL_WINDOW` → `WINDOWED` → `FULL_SCREEN` (ADR-0006). |
| `DisplayMode` | value | Width and height in pixels, refresh rate in Hz, color depth in bits, all `int`; `0` means unknown or multiple; `isRefreshRateKnown()` and `isBitDepthKnown()` are true when the value is not `0` (ADR-0022, ADR-0033). |
| `DisplayRequest` | value | Screen mode, display mode, `ScalingPolicy` and the ordered `OutputFilter` chain (ADR-0046). |
| `EffectiveResult` | value | Immutable; per field, the requested and the effective value, a `degraded` flag and a reason. It is delivered through the inbox at the frame boundary where the change applies, and stays readable as the current state (`REQ-DISPLAY-008`). |
| *presentation geometry* | value | The rectangle of the output surface, in device pixels, where the logical frame is presented, and the scale from logical-frame pixels to device pixels on each axis. It changes only at a frame boundary, and render backends and input adapters use it (ADR-0043, ADR-0046). |
| `PlatformEvent` | event type, an `InboxEvent` | Surface resized (new size in device pixels), focus gained, focus lost, background, foreground, close requested, context lost, context restored. Background pauses the loop and foreground resumes it; resize never changes the logical frame (ADR-0031). |
| `CloseListener` | callback | Called on the runtime thread for a close request; it may veto the stop (ADR-0031). |
| `Platform` | interface | `<T> T getCapability(Class<T>)` returns `null` when absent, and `hasCapability(Class<?>)` tells it (ADR-0020, ADR-0037). It is started with a `LoopDriver` and runs the loop on the thread it requires, returning or blocking as each implementation documents. It posts `PlatformEvent`s to the `Inbox` the application hands it, and maps background, foreground and close to `LoopControl` requests. It provides the `WorkerPool` and close-listener registration. It applies a `DisplayRequest` without blocking and returns the accepted request; the current `EffectiveResult` and presentation geometry are readable on the runtime thread. Render-backend attachment is added by [`platform-and-rendering.md`](platform-and-rendering.md) batch 3 with the backend contract (ADR-0046). |

## Envisaged APIs / contracts

The types above, with the canonical names of ADR-0037 and ADR-0040. The italic concepts are named by this batch under [`../conventions.md`](../conventions.md); the chosen names are written into [`../architecture/interfaces.md`](../architecture/interfaces.md) and [`../glossary.md`](../glossary.md) in the same pull request.

## Batches (order)

1. `core` seams.
2. `runtime` and `resources` seams.
3. `render-api` and `platform-api` seams.

Each batch is a pull request that compiles under the build skeleton's checks.

## Lifecycle and concurrency

Every type states its thread as above; the default is "not thread-safe, owned by the runtime thread" (ADR-0018).

## Units and Android/DEX compatibility

Seconds (`double`) in callbacks, nanoseconds (`long`) in `Clock`, pixels and device pixels as stated, Hz and bits for display modes. Shared modules at `--release 21`; only `java.lang`, `java.io` (`InputStream`) and the role-named callbacks; the Android job dexes the declared types (ADR-0044).

## Dependencies

No new dependency; module rows as in [`../architecture/modules.md`](../architecture/modules.md) after ADR-0046 and ADR-0047.

## Tests

- Unit tests of the value types: `ResourceKey` normalization and every rejection; `DisplayMode` and the `0` convention; argument validation of every value type.
- The build checks pass: doclint `all`, the package rule, the module-dependency check, the Android job.

## Acceptance criteria

- [ ] Every type of this page declared with complete Javadoc in its module's root package.
- [ ] Value-type unit tests pass; doclint, package rule, module-dependency check and Android job green.
- [ ] Chosen names recorded in `interfaces.md` and `glossary.md`; no example or API-index entry added (ADR-0049).
