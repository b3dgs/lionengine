# Android/DEX compatibility checklist

**Status: VALIDATED — operational procedure applying REQ-TECH-006 and REQ-TECH-007 (ADR-0001). Baseline of ADR-0012: minimum API 21, DEX through a separate Gradle module. Shared level: Java 21 (`--release 21`), minimum API 21, tested on an API 21 emulator (ADR-0044); items marked verified below are tested, every other item is `not compatible yet` until checked.**

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
- Pipeline design (ADR-0012): Maven builds and tests the modules; a separate Gradle module consumes the Maven artifacts. An Android *library* (AAR) build does not run D8, so the job also needs a verification module that dexes the artifacts and runs the shared modules' platform tests on an API 21 emulator; lint `NewApi` is an error (ADR-0044).
- Games must enable core library desugaring on the application side too.

## Baseline (ADR-0012)

Observed in `master` `java/lionengine-core-android/build.gradle` on 2026-11-22: Android Gradle Plugin 9.2.1, `compileSdk`/`targetSdk` 37, `minSdk` 21, Java 21 source/target, `desugar_jdk_libs:2.1.5`. Proven in `master` and by the owner's API 21 emulator test: Java 21 source and target level, debug build (ADR-0044). Not verified: release builds with R8.

## Java features and APIs at API 21

Shared level: Java 21 source and target (ADR-0044). Verified means built with D8 and run on an API 21 emulator by the owner (debug build, desugaring `desugar_jdk_libs` 2.1.5). Everything else is **not compatible yet** until checked with the procedure above.

### Forbidden in shared code (observed failures)

| Do not use | Failure | Use instead |
|---|---|---|
| `case E.E1 ->` (qualified enum constant) in a pattern `switch` over a sealed interface | build fails (D8) | `case E e when e == E.E1`, or an `if` chain |
| Virtual threads (`Thread.ofVirtual()`, ...) | build fails (not in `android.jar`) | a regular executor |
| `String.lines()` | `NoSuchMethodError` at run time | `split("\\R")` |
| `java.util.HexFormat` | `NoClassDefFoundError` at run time | `String.format("%02x", ...)` or a manual loop |
| `List.getFirst()`, `getLast()`, `reversed()` (sequenced collections) | `NoSuchMethodError` at run time | `get(0)`, `get(size() - 1)`, `Collections.reverse` on a copy |

Also avoided as a precaution (same family, not tested): `addFirst`, `addLast`, `removeFirst`, `removeLast` on `List`, `Deque` and `LinkedHashSet`, and the sequenced methods of `LinkedHashMap` and `SortedSet`.

### Verified at run time on API 21

- Lambdas, `var`, nestmates, switch expressions, text blocks, records, sealed classes.
- Pattern matching for `switch` on types, records, guards and `null`.
- `List.of`, `Map.of`, `Set.of`, `Stream.toList()`, `Math.clamp`.
- `String.isBlank()`, `strip()`, `repeat()`: probably fine, not tested alone.

The rules of ADR-0037 still apply on top: no `java.util.function`, `Optional`, streams or `java.time` in shared public APIs or hot paths.

### Not verified (general expectation)

| Area | Expectation at API 21 |
|---|---|
| `java.desktop` (AWT, Swing, ImageIO, `javax.sound`, `java.beans`) | absent on every Android level: platform module only |
| `java.lang.foreign`, structured concurrency, scoped values, `java.net.http`, module system (`module-info` ignored) | unavailable |
| `java.time`, `Optional`, `java.util.function`, much of `java.util.concurrent` (about JDK 11 level) | through core library desugaring only; not used in shared public APIs or hot paths (ADR-0037) |
| Any other API newer than API 21 (for example `Files.readString`, `String.formatted`, `Collectors.teeing`, `Cleaner`, `VarHandle`, `MethodHandles`, record reflection) | may fail at run time like the forbidden list: do not assume it works |
| Java 22 to 25 language features and class files | not used in shared code (ADR-0044) |
| Preview features | forbidden in shared code |
| Release builds with R8 | not tested |

### Rules for agents and contributors

- Never use anything in the forbidden table; when a task seems to need one, use the listed alternative and say so in the pull request.
- Before using a Java API newer than Java 8 that is not in the verified list, check whether it exists on API 21 or is covered by `desugar_jdk_libs`; if that cannot be confirmed, use a basic alternative.
- The Android module enables the lint check `NewApi` as an error (for example `lint { error += "NewApi" }`; not tested with AGP 9.5, the skeleton batch verifies it is accepted); fix every violation, never suppress it.
- Run the library on an API 21 emulator after adding any suspicious API: a successful build does not prove it works, `NoSuchMethodError` only shows at run time.

## Pending checks of the accepted decisions

Nothing here is verified yet, so every item is **not compatible yet** until it is checked with the procedure above, except where the status says otherwise.

| Feature | Item to verify | Status |
|---|---|---|
| Screen and display modes (ADR-0002) | Mode selection through Android APIs, isolated in `platform-android`; refresh-rate and color-depth selection may be unavailable and is then reported through the effective result | not compatible yet |
| Output filtering (ADR-0002) | common GLSL/GLSL ES shader subset and own filter implementations (ADR-0025, ADR-0033); headers `#version 100` on Android | not compatible yet |
| Sprites and sprite sheets (ADR-0003) | Pure-Java PNG decoder in shared code (`java.util.zip.Inflater`, to verify on API 21); optional platform decoders (ADR-0026) | not compatible yet |
| Audio (ADR-0004) | No `javax.sound` in `audio-api`; PCM WAV baseline, OGG Vorbis decoded by the platform; latency target 100 ms best effort to measure (ADR-0027, ADR-0033) | not compatible yet |
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

## What to record at the build skeleton (ADR-0044)

The baseline (minimum API level, Android Gradle Plugin, desugaring library and versions) is in ADR-0012; the shared level (Java 21) in ADR-0044. The Android verification job records here: the D8/R8 version and the JDK actually used, the emulator image, the result of the lint `NewApi` check, the first R8 release-build result, and the status of each row of the tables above, changed from `not compatible yet` to its verified status.
