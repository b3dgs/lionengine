# Android/DEX compatibility checklist

**Status: VALIDATED — operational procedure applying REQ-TECH-006 and REQ-TECH-007 (ADR-0001). Baseline of ADR-0012: minimum API 21, DEX through a separate Gradle module. Shared bytecode level: Java 25, then 21, then 17, the first one the Android job accepts, fixed at the build skeleton (ADR-0041); until then every item below is `not compatible yet` unless stated otherwise.**

Java compilation alone is not proof of DEX compatibility. Verification is continuous, not a final step.

## Before using a Java language feature, a Java API or a dependency in shared code

1. Identify where it will be used: shared code (must be Android-compatible) or a platform-specific module (isolated, may use platform APIs).
2. Check that the targeted toolchain supports it: language level accepted by the Android build, D8/R8 handling of the construct, availability of the API at the minimum Android API level, 21 (ADR-0012).
3. If the API is only available through *core library desugaring*: confirm it is supported, record the configuration, and cover it with a verification build and tests on the targeted Android versions.
4. For a third-party dependency: confirm it contains no unsupported APIs or native code, check its license against the allow-list, and record it in [`dependencies.md`](dependencies.md) (ADR-0032).
5. Record the outcome in the pull request (or feature file): `compatible`, `compatible with desugaring`, `platform module only`, or `rejected`. If unable to verify, the answer is **not compatible yet**, never "probably fine".

## Pipeline requirements

- A CI job builds the Android-targeted modules through D8/R8 and produces the expected DEX artifacts (REQ-TECH-006, REQ-TECH-007).
- A verification build covers APIs requiring desugaring.
- Tests run on the targeted Android versions (devices or emulators); these are platform tests and are kept separate from the headless unit tests.
- Pipeline design (ADR-0012): Maven builds and tests the modules; a separate Gradle module consumes the Maven artifacts. An Android *library* (AAR) build does not run D8, so the job also needs a verification module that dexes the artifacts (ADR-0041).
- Games must enable core library desugaring on the application side too.

## Baseline (ADR-0012)

Observed in `master` `java/lionengine-core-android/build.gradle` on 2026-11-22: Android Gradle Plugin 9.2.1, `compileSdk`/`targetSdk` 37, `minSdk` 21, Java 21 source/target, `desugar_jdk_libs:2.1.5`. Proven in `master`: Java 21 bytecode. Not verified: Java 25 class files and APIs; the owner reports a working build with JDK 17 (ADR-0041).

## Java features and APIs at API 21 (working list, NOT verified)

General knowledge of D8/ART, to be confirmed by the Android CI job (ADR-0041). Until confirmed, each item is **not compatible yet** in shared code (procedure above).

| Area | Expectation at API 21 |
|---|---|
| `java.desktop` (AWT, Swing, ImageIO, `javax.sound`, `java.beans`) | absent on every Android level: platform module only |
| `java.lang.foreign`, virtual threads, structured concurrency, scoped values, `java.net.http`, module system (`module-info` ignored) | unavailable |
| `java.time`, streams, `Optional`, `java.util.function`, much of `java.util.concurrent` (about JDK 11 level) | through core library desugaring only; not used in shared public APIs or hot paths (ADR-0037) |
| APIs added after about JDK 11 (sequenced collections, `Math.clamp`, `HexFormat`, `Cleaner`, `VarHandle`, `MethodHandles`, record reflection) | only on recent Android levels, not desugared: avoid in shared code |
| Records, sealed types, switch expressions, text blocks, `instanceof` patterns | compile-time, handled by D8; verify |
| Pattern matching for `switch`, record patterns, Java 22 to 25 language features | rely on `invokedynamic` bootstraps or new class file versions: verify one by one |
| Preview features | forbidden in shared code |
| Class file version 69 (Java 25) | unknown; tried first, then `--release 21` (proven in `master`), then `--release 17` (ADR-0041) |

## Pending checks of the accepted decisions

Nothing here is verified yet, so every item is **not compatible yet** until it is checked with the procedure above, except where the status says otherwise.

| Feature | Item to verify | Status |
|---|---|---|
| Screen and display modes (ADR-0002) | Mode selection through Android APIs, isolated in `platform-android`; refresh-rate and color-depth selection may be unavailable and is then reported through the effective result | not compatible yet |
| Output filtering (ADR-0002) | common GLSL/GLSL ES shader subset and own filter implementations (ADR-0025, ADR-0033); headers `#version 100` on Android | not compatible yet |
| Sprites and sprite sheets (ADR-0003) | Pure-Java PNG decoder in shared code (`java.util.zip.Inflater`, to verify on API 21); optional platform decoders (ADR-0026) | not compatible yet |
| Audio (ADR-0004) | No `javax.sound` in `audio-api`; PCM WAV baseline, OGG Vorbis decoded by the platform; latency target 100 ms best effort to measure (ADR-0027, ADR-0033, ADR-0041) | not compatible yet |
| Desktop platform (ADR-0008) | `platform-awt` uses `java.desktop`, which does not exist on Android: it must stay out of every Android build | platform module only |
| Desktop renderer and input (ADR-0022, ADR-0035) | `render-awt`, `input-awt`, `audio-javasound`, `graphics-imageio` use `java.desktop` | platform module only |
| JSON reader and definition files (ADR-0028, ADR-0032) | Pure Java string and number parsing in `core`; no `java.util.Base64` (API 26) in shared code, base64 decoded by project code | not compatible yet |
| Tiled loader (ADR-0032) | `java.util.zip` `Inflater` and `GZIPInputStream` on API 21 | not compatible yet |
| Random generator (ADR-0031) | Own SplitMix64 and xoshiro256\*\*, integer operations only; `SplittableRandom` (API 24) not used | not compatible yet |
| Loop clock (ADR-0017) | `System.nanoTime` on API 21 | not compatible yet |
| Network transport (ADR-0039) | `java.nio.channels.DatagramChannel` through `open()`, `socket().bind(...)`, `configureBlocking(false)`, `receive`, `send` (API 1; `DatagramChannel.bind` and `open(ProtocolFamily)` are API 24 and are not used); `socket().setBroadcast(true)` for discovery; `java.util.zip.CRC32`, `java.security.SecureRandom`, `StandardCharsets` (API 19); the game declares `android.permission.INTERNET`; calls never run on the Android main thread (the runtime thread is the render thread); a hosting device holds the Wi-Fi multicast lock for LAN discovery (`platform-android` capability) | not compatible yet |
| Multiplayer (ADR-0038) | Pure Java over `network`, `ecs`, `traits`, `input-api`; no new JDK API | not compatible yet |
| Allocation tests (ADR-0030) | `com.sun.management.ThreadMXBean` is JVM-only: test code only, never in shared main code | platform module only |
| Desktop OGG decoder (ADR-0033) | JOrbis in `audio-codec-ogg`, desktop only; Android uses the platform decoder | platform module only |

## What to record at the build skeleton (ADR-0041)

The baseline (minimum API level, Android Gradle Plugin, desugaring library and versions) is in ADR-0012. The Android verification job records here: the class file version and language level accepted for shared modules, the D8/R8 version actually used, the JDK of the job, and the status of each row of the two tables above, changed from `not compatible yet` to its verified status.
