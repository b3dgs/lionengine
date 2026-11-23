# Test strategy

**Status: VALIDATED**

## Levels

1. **Unit**: isolated, deterministic logic, no platform.
2. **Contract**: the same suite executed on every implementation of an abstraction.
3. **Integration**: interactions between modules and providers.
4. **Platform**: tests with a real frontend, OS, codec or hardware.
5. **Performance/stability**: reproducible measurements, stress, leaks and duration.
6. **Android/DEX compatibility**: DEX transformation of the relevant artifacts, verification of available APIs and tests on the targeted Android versions (see [`android-compatibility.md`](android-compatibility.md)).

## Rules

- Every `REQ-*` requirement must be linked to one or more tests, or explicitly declared as not automatically testable (see [`traceability.md`](traceability.md)).
- A fixed bug receives a regression test.
- Prefer a simulated clock to real waiting when testing the game loop (REQ-TEST-001).
- Use temporary/fake resources for resource tests.
- Test errors and closing, not only the nominal path.
- Headless tests are the reference for engine logic.
- A successful Java build does not validate Android compatibility: the Android pipeline must run D8/R8 and produce the expected DEX artifacts.
- APIs requiring *core library desugaring* must be covered by a verification build and tests on the targeted Android versions.
- Tests depending on a device or graphical environment are identified as such and must not make unit tests fragile.
- Tests must be deterministic: no dependency on wall-clock time, real sleeping, execution order or random seeds that are not fixed.

## Envisaged test matrix

| Domain | Unit tests | Contract | Integration / platform |
|---|---|---|---|
| Game loop | clock, time step, accumulator without drift, lag cap and report, vsync snapping, pause, time scale, unscaled render delta (REQ-LOOP-007 to REQ-LOOP-012) | each implementation | frontend lifecycle |
| Runtime | inbox order, overflow and control requests, asynchronous handles and load groups with a fake worker pool, listener registration during dispatch (REQ-RT-001 to REQ-RT-010) | `platform-api` suites (test JAR) | platform events injected through the headless platform of `test-support` (REQ-PLATFORM-002) |
| Core | JSON reader (valid, invalid, numbers, escapes, duplicate keys, limits), definition errors with path, registry ids and typed configuration, logging facade, random generator reference vectors (REQ-DEF-*, REQ-CORE-006, REQ-DET-003) | — | definition files loaded through `resources` |
| Scenes | lifecycle order, switch at a frame boundary, overlay stack, flow resolution order, conditions, single pending transition, validation errors (REQ-SCENE-*) | — | manually driven loop: four-scene flow with pause and exit |
| Desktop platform (`platform-awt`) | mapping of AWT display modes to neutral values, fallback order | `platform-api` suites from its test JAR (REQ-PLATFORM-001) | real display required: platform tests, kept apart from headless tests |
| Resources | resolution, errors, streams, key normalization, idempotent release | each provider; key and release suite run by each consuming module (REQ-RES-005) | archive/Android/filesystem |
| Audio | playback state, commands, seeded variant selection, playlist advance, cache hit and miss | short and long sound suites of the `audio-api` test JAR, run on every backend (REQ-AUDIO-009) | headless fake backend with controllable clock; codec, latency and Android on device (platform tests, later) |
| Sprites | slicing, animation, mutation | data API | upload and rendering |
| Tilemap | coordinates, bounds, set/get/clear, viewport-to-tile-range math, neighbor rules | loader suite of the `tilemap` test JAR (REQ-MAP-004) | headless: load, render commands only for visible tiles, destroy then render |
| Camera | zoom clamp, translation, follow incl. missing entity, smoothing at several frame rates, seeded shake, conversions | lookup-contract fake (`camera` test JAR) | headless: follow an entity, then render with the view rectangle |
| Objects | lifecycle, order; ECS (ADR-0011): ID recycling and stale detection, trait add/remove, include/exclude queries, deferred changes during iteration, events, system order | management strategy | complete scene |
| Traits | isolated behavior, interactions; standard traits (ADR-0011): movement, gravity, jump with coyote and buffer, collision broad phase vs brute force, lifetime, at several step durations | trait and system conventions (`ecs` test JAR) | headless world with the standard pipeline and a controllable clock; benchmark 1,000 objects × 4 traits and allocation check (REQ-ECS-009, ADR-0030) |
| Sprites, animations, sprite sheets | grid index math, transforms, palette mapping, animation state machine | cache convention suite (`resources` test JAR); sprite render on the recording backend of `test-support` | headless load, cache, render; AWT/OpenGL/Android color-transform parity; cache and batching performance |
| Display and filtering | value types, scaling policies (including the `INTEGER` fallback), fallback orders, filter chain | effective result and reason, no throw on missing capability, frame-boundary application (`platform-api` and `render-api` test JARs) | headless with fake capabilities; golden-image tests on AWT/OpenGL (none, bilinear, `HQ2X`), CRT on OpenGL |
| Input | bindings, per-step edges with zero, one and several steps per iteration, taps within one step, dead zone, inverse scaling of pointers, focus loss, hot-plug (REQ-IN-001 to REQ-IN-005) | adapter suite of the `input-api` test JAR | keyboard, mouse, touch, gamepad on each platform |
| Multiplayer (local) | per-player device sets and views (REQ-MP-001) | — | split-screen demonstrator |
| Network | wire encoding, checksum, sequence wrap-around, acks, reliable ordering, resend, window, overflow, fragmentation, join checks and their order, names, properties, timeouts, chat filter and rate limit, statistics (REQ-NET-*) | transport suite of the `network` test JAR, run on loopback and UDP | server and clients over the conditioned transport with fixed seeds; UDP on the local address; real network and Android multicast as platform tests |
| Multiplayer (network) | codecs, content hash, delta against a baseline, removal by absence, input frames (missing, late, quantized), pacing, prediction comparison, replay and snap, epochs (REQ-MP-004 to REQ-MP-015) | codec suite of the `multiplayer` test JAR | convergence suite under loss, duplication, reordering and latency; predicted avatar; 1,000-unit RTS scenario; allocation of per-step paths |
| Java/Android compatibility | check of the APIs used | — | D8/R8 compilation, DEX and device/emulator tests |
| Architecture rules | — | — | forbidden-dependency and cycle check (REQ-CORE-005) |

## Feature acceptance criteria

- requirements covered;
- relevant automated tests added;
- existing tests still green;
- no forbidden coupling introduced;
- public API documented;
- errors and limits described;
- minimal example or demonstrator where it helps validation;
- traceability matrix updated.

## Test tooling

Fixtures (ADR-0045): the fakes and abstract contract suites of a module's contracts are in that module's test sources, published as its test JAR; a module depends in test scope only on the test JARs of modules it already depends on, and on `test-support` (the headless platform, recording render backend and manually driven loop, built on `core`, `runtime`, `platform-api` and `render-api`) when `test-support` does not depend on it. The fixture placement table is in ADR-0045.

Framework: **JUnit 5** (ADR-0012); coverage with JaCoCo merged for SonarCloud. Exact versions and build commands are set with the build skeleton (ADR-0042) and recorded in `README.md` and `AGENTS.md`; until then no test command exists.

- Allocation tests use the per-thread allocation counter of the JVM (`com.sun.management.ThreadMXBean`), in JVM-only test code; they assert zero bytes over many steady-state steps after warm-up (ADR-0030).
- Benchmarks use the project's own harness (warm-up, median over many steps); CI asserts allocation and linear scaling only; wall-clock results go to a non-blocking trend job on the standard Linux runner (ADR-0030, ADR-0034).
- Determinism tests run the same scenario twice and compare states and iteration orders.

## Measured budgets

Filled by the benchmark batches (REQ-PERF-001); a value appears here only once measured.

| Tier | Reference | Step budget | Memory | Measured on |
|---|---|---|---|---|
| Desktop | CI trend-job runner (standard Linux) | ≤ 1 ms per step for 1,000 objects × 4 traits (REQ-ECS-009) | not measured yet | — |
| Android | mid-range API 21-era device (ADR-0044) | frame ≤ 16.6 ms (REQ-PERF-002) | not measured yet | — |
