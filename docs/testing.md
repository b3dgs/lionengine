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
- Android/DEX tests (level 6) live in the `android/` Gradle build ([`android-compatibility.md`](android-compatibility.md), section "Android verification job"): lint `NewApi` on the shared sources, D8 and R8 builds of the installed shared modules with a check of their DEX content, and instrumentation tests on an API 21 emulator. Their harness is AndroidX Test with JUnit 4, because the JUnit 6 Jupiter tests run on the JVM only (ADR-0050): the device tests in `android/verification/src/androidTest` take their cases and expected values from the JVM tests of each shared module, so a shared behavior is tested on both sides with the same values.
- Tests depending on a device or graphical environment are identified as such and must not make unit tests fragile: they carry the JUnit tag `platform`, which the default build leaves out (`excludedGroups` in the aggregator `pom.xml`), and the Maven profile `platform-tests` runs them. They also skip themselves without a display. The Linux CI jobs, the blocking build and Sonar, run them on the virtual display of `xvfb-run`; the Windows and macOS jobs run the default build.
- Tests that need a sound device carry the JUnit tag `audio-device` (ADR-0070). Every build leaves them out, the `platform-tests` profile and so every CI job included, since no runner has a sound device; the Maven profile `audio-device-tests` runs them with every other test, on a desktop with a display and a sound device (`mvn -B verify -Paudio-device-tests`). They also skip themselves when no line opens. Their headless counterparts run in every build: the suite of a backend bound on its mixer and on its thread with a manual output instead of the device.
- Tests must be deterministic: no dependency on wall-clock time, real sleeping, execution order or random seeds that are not fixed.

## Envisaged test matrix

| Domain | Unit tests | Contract | Integration / platform |
|---|---|---|---|
| Game loop | clock, time step, accumulator without drift, lag cap and report, vsync snapping, pause, time scale, unscaled render delta (REQ-LOOP-007 to REQ-LOOP-012) | each implementation | frontend lifecycle |
| Runtime | inbox order, overflow and control requests, asynchronous handles and load groups with a fake worker pool, listener registration during dispatch (REQ-RT-001 to REQ-RT-010) | `platform-api` suites (test JAR) | platform events injected through the headless platform of `test-support` (REQ-PLATFORM-002) |
| Core | JSON reader (valid, invalid, numbers, escapes, duplicate keys, limits), definition errors with path, registry ids and typed configuration, logging facade, random generator reference vectors (REQ-DEF-*, REQ-CORE-006, REQ-DET-003) | — | definition files loaded through `resources` |
| Scenes | lifecycle order, switch at a frame boundary, overlay stack, flow resolution order, conditions, single pending transition, validation errors (REQ-SCENE-*) | — | manually driven loop: four-scene flow with pause and exit |
| Desktop platform (`platform-awt`, `render-awt`) | mapping of AWT display modes and events to neutral values, device-pixel conversion, frame pacing, drawing of each command field on the logical frame (headless) | `platform-api` suite bound on `AwtPlatform` with a real window, `render-api` suite bound on `AwtRenderBackend` headless (REQ-PLATFORM-001) | real display required: window, runtime thread, screen capture of a presented frame and the desktop example, as platform tests (tag `platform`) |
| Resources | resolution, errors, streams, key normalization, idempotent release | each provider; key and release suite run by each consuming module (REQ-RES-005) | archive/Android/filesystem |
| Audio | playback state, commands, seeded variant selection, playlist advance, cache hit and miss | short and long sound suites of the `audio-api` test JAR, run on every backend (REQ-AUDIO-009) | headless fake backend with controllable clock; `audio-javasound` mixer and its thread on a manual output, headless; the Java Sound suite, its latency (request to first sample, 50 ms target) and its example on a sound device (tag `audio-device`, outside CI, ADR-0070); codec and Android on device |
| Sprites | slicing, animation, mutation | data API | upload and rendering |
| Tilemap | coordinates, bounds, set/get/clear, viewport-to-tile-range math, neighbor rules | loader suite of the `tilemap` test JAR (REQ-MAP-004) | headless: load, render commands only for visible tiles, destroy then render |
| Camera | zoom clamp, translation, follow incl. missing entity, smoothing at several frame rates, seeded shake, conversions | lookup-contract fake (`camera` test JAR) | headless: follow an entity, then render with the view rectangle |
| Objects | lifecycle, order; ECS (ADR-0011): ID recycling and stale detection, trait add/remove, include/exclude queries, deferred changes during iteration, events, system order | management strategy | complete scene |
| Traits | isolated behavior, interactions; standard traits (ADR-0011): movement, gravity, jump with coyote and buffer, collision broad phase vs brute force, lifetime, at several step durations | trait and system conventions (`ecs` test JAR) | headless world with the standard pipeline and a controllable clock; benchmark 1,000 objects × 4 traits and allocation check (REQ-ECS-009, ADR-0030) |
| Sprites, animations, sprite sheets | PNG decoding per color type, bit depth and filter type, rejections; grid index math, transforms, palette mapping, animation state machine | image decoder suite (`graphics` test JAR, PNG images written in memory by its `PngWriter`); cache convention suite (`resources` test JAR); sprite render on the recording backend of `test-support` | headless load, cache, render; AWT/OpenGL/Android color-transform parity; cache and batching performance |
| Display and filtering | value types, scaling policies (including the `INTEGER` fallback), fallback orders, filter chain | effective result and reason, no throw on missing capability, frame-boundary application (`platform-api` and `render-api` test JARs) | headless with fake capabilities; golden-image tests on AWT/OpenGL (none, bilinear, `HQ2X`), CRT on OpenGL |
| Input | bindings, per-step edges with zero, one and several steps per iteration, taps within one step, dead zone, inverse scaling of pointers, focus loss, hot-plug (REQ-IN-001 to REQ-IN-005) | adapter suite of the `input-api` test JAR | keyboard, mouse, touch, gamepad on each platform |
| Multiplayer (local) | per-player device sets and views (REQ-MP-001) | — | split-screen demonstrator |
| Network | wire encoding, checksum, sequence wrap-around, acks, reliable ordering, resend, window, overflow, fragmentation, join checks and their order, names, properties, timeouts, chat filter and rate limit, statistics (REQ-NET-*) | transport suite of the `network` test JAR, run on loopback and UDP | server and clients over the conditioned transport with fixed seeds; UDP on the local address; real network and Android multicast as platform tests |
| Multiplayer (network) | codecs, content hash, delta against a baseline, removal by absence, input frames (missing, late, quantized), pacing, prediction comparison, replay and snap, epochs (REQ-MP-004 to REQ-MP-015) | codec suite of the `multiplayer` test JAR | convergence suite under loss, duplication, reordering and latency; predicted avatar; 1,000-unit RTS scenario; allocation of per-step paths |
| Java/Android compatibility | check of the APIs used (lint `NewApi` on the shared sources) | — | D8/R8 compilation and DEX content of every shared module; device tests per shared module and Java feature probes on an API 21 emulator (`android/verification`) |
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

Framework: JUnit 6 (Jupiter), the latest stable release, 6.1.3, pinned in the aggregator `pom.xml` (ADR-0050); on the API 21 emulator, AndroidX Test 1.7.0 with JUnit 4.13.2 ([`dependencies.md`](dependencies.md)); coverage with JaCoCo 0.8.16 (XML report per module), analyzed by SonarCloud. Commands are in `README.md` (Getting started) and [`implementation-guide.md`](implementation-guide.md); the coverage target of new code is 80 %.

- Allocation tests use the per-thread allocation counter of the JVM (`com.sun.management.ThreadMXBean`), in JVM-only test code; they assert zero bytes over many steady-state steps after warm-up (ADR-0030).
- Benchmarks use the project's own harness (warm-up, median over many steps); CI asserts allocation and linear scaling only; wall-clock results go to a non-blocking trend job on the standard Linux runner (ADR-0030, ADR-0034).
- Determinism tests run the same scenario twice and compare states and iteration orders.

## Measured budgets

Filled by the benchmark batches (REQ-PERF-001); a value appears here only once measured.

| Tier | Reference | Step budget | Memory | Measured on |
|---|---|---|---|---|
| Desktop | CI trend-job runner (standard Linux) | ≤ 1 ms per step for 1,000 objects × 4 traits (REQ-ECS-009) | not measured yet | — |
| Android | mid-range API 21-era device (ADR-0044) | frame ≤ 16.6 ms (REQ-PERF-002) | not measured yet | — |
