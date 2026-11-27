# Android/DEX compatibility checklist

**Status: VALIDATED — operational procedure applying REQ-TECH-006 and REQ-TECH-007 (ADR-0001). Baseline of ADR-0012: minimum API 21, DEX through a separate Gradle module. Shared level: Java 21 (`--release 21`), minimum API 21, tested on an API 21 emulator (ADR-0044) by the owner and, since 2026-11-25, by the Android verification job on every pull request; items marked verified below are tested, every other item is `not compatible yet` until checked.**

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
- These requirements are met by the Android verification job described at the end of this page.

## Baseline (ADR-0012)

Observed in `master` `java/lionengine-core-android/build.gradle` on 2026-11-22: Android Gradle Plugin 9.2.1, `compileSdk`/`targetSdk` 37, `minSdk` 21, Java 21 source/target, `desugar_jdk_libs:2.1.5`. Proven in `master` and by the owner's API 21 emulator test: Java 21 source and target level, debug build (ADR-0044). The Android verification job uses the same values; its R8 release build passes with every engine class kept, and the release APK is not run on a device.

## Java features and APIs at API 21

Shared level: Java 21 source and target (ADR-0044). Verified means built with D8 and run on an API 21 emulator (debug build, desugaring `desugar_jdk_libs` 2.1.5), by the owner (ADR-0044) or by the probes of the Android verification job (`LanguageProbeTest`, `LibraryProbeTest`), which run again on every pull request. Everything else is **not compatible yet** until checked with the procedure above.

### Forbidden in shared code (observed failures)

| Do not use | Failure | Use instead |
|---|---|---|
| `case E.E1 ->` (qualified enum constant) in a pattern `switch` over a sealed interface | build fails (D8) | `case E e when e == E.E1`, or an `if` chain |
| Virtual threads (`Thread.ofVirtual()`, ...) | build fails (not in `android.jar`) | a regular executor |
| `String.lines()` | `NoSuchMethodError` at run time | `split("\\R")` |
| `java.util.HexFormat` | `NoClassDefFoundError` at run time | `String.format("%02x", ...)` or a manual loop |
| `List.getFirst()`, `getLast()`, `reversed()` (sequenced collections) | `NoSuchMethodError` at run time | `get(0)`, `get(size() - 1)`, `Collections.reverse` on a copy |
| `addFirst`, `addLast`, `removeFirst`, `removeLast` on a `List` or a `LinkedHashSet` (sequenced collections) | `NoSuchMethodError` at run time (job probes) | `add(0, e)`, `add(e)`, `remove(0)`, `remove(size() - 1)`; an `ArrayDeque` |
| `CharSequence.isEmpty()`, also called on a `StringBuilder` (Java 15, API 35) | `NoSuchMethodError` at run time (job probe; found by lint in `DisplayState`) | `String.isEmpty()` on a `String`; track the emptiness of a builder in a field |
| `String.formatted` (Java 15, API 34) | `NoSuchMethodError` at run time (job probe) | `String.format(Locale.ROOT, ...)` |
| `InputStream.readAllBytes` (Java 9, API 33), and as a precaution the other Java 9 and later `InputStream` methods (`readNBytes`, `transferTo`) | `NoSuchMethodError` at run time (job probe) | a read loop into a `ByteArrayOutputStream` |
| `Collectors.teeing` (Java 12) | `NoSuchMethodError` at run time: missing from `desugar_jdk_libs` 2.1.5 although the rest of `Collectors` is desugared, and **not reported by lint `NewApi`** (job probe) | two collectors or a loop |
| `java.nio.file` (`Path`, `Paths`, `Files`, API 26; not desugared by the default `desugar_jdk_libs`) | `NoClassDefFoundError` at run time (job probe) | `java.io.File` and streams |
| `java.util.SplittableRandom` (API 24) | `NoClassDefFoundError` at run time (job probe) | the engine `RandomSource` |
| `java.lang.ref.Cleaner` (API 33) | `NoClassDefFoundError` at run time (job probe) | explicit release (`close`, `dispose`), as every engine resource |
| `java.lang.invoke.VarHandle` (and any signature-polymorphic `MethodHandle.invoke` or `invokeExact` call) | build fails (D8: supported from API 26 only) | atomics of `java.util.concurrent.atomic`, plain fields |

Also avoided as a precaution (same family, not tested): the sequenced methods of `LinkedHashMap` and `SortedSet`. The `addFirst` family of `Deque` (an `ArrayDeque`, API 9) is not a sequenced-collection method and works (job probe).

### Verified at run time on API 21

By the owner (ADR-0044), and run again by the probes of the Android verification job on every pull request:
- Lambdas, `var`, nestmates, switch expressions, text blocks, records, sealed classes.
- Pattern matching for `switch` on types, records, guards and `null`.
- `List.of`, `Map.of`, `Set.of`, `Stream.toList()`, `Math.clamp`.

By the probes of the Android verification job (2026-11-25, `LanguageProbeTest`, `LibraryProbeTest`):
- Default, static and private interface methods, and `Interface.super.method()` calls (desugared by D8 below API 24).
- Record patterns in `instanceof`, `switch` on strings, string concatenation of every primitive type and `null`; the text form of a record is the JVM one (`Circle[radius=1.5]`).
- `List.copyOf`, `Set.copyOf`, `Map.copyOf`.
- `String.isBlank()`, `strip()`, `repeat()`.
- Through core library desugaring: `java.util.Base64`, `Collectors.joining`, `java.util.function`, `Optional`, `java.time.Duration` (the rules of ADR-0037 below still keep the last three out of shared public APIs and hot paths).
- `java.util.zip`: `Deflater`, `Inflater`, `CRC32`, `GZIPInputStream`, `GZIPOutputStream`.
- `ArrayDeque` through `Deque`: `addFirst`, `addLast`, `removeFirst`, `removeLast`.

By the device tests of the shared modules (`CoreTest`, `RuntimeTest`, `ResourcesTest`, `RenderTest`, `PlatformTest`, `GraphicsTest`), with the expected values of their JVM tests: `System.nanoTime`, `Thread` start and join, `java.util.concurrent.atomic`, `java.io.File` and file streams in the application cache folder, `StandardCharsets` and the UTF-8 decoder, `Arrays.copyOf`, `HashMap`, and the `double` and `long` arithmetic of the random generator and of the scaling function.

The rules of ADR-0037 still apply on top: no `java.util.function`, `Optional`, streams or `java.time` in shared public APIs or hot paths.

### Not verified (general expectation)

| Area | Expectation at API 21 |
|---|---|
| `java.desktop` (AWT, Swing, ImageIO, `javax.sound`, `java.beans`) | absent on every Android level: platform module only |
| `java.lang.foreign`, structured concurrency, scoped values, `java.net.http`, module system (`module-info` ignored) | unavailable |
| `java.time`, `Optional`, `java.util.function`, much of `java.util.concurrent` (about JDK 11 level) | through core library desugaring only (a part verified, see above); not used in shared public APIs or hot paths (ADR-0037) |
| Any other API newer than API 21 (for example `MethodHandles`, record reflection, the sequenced methods of `LinkedHashMap` and `SortedSet`) | may fail at run time like the forbidden list: do not assume it works; lint `NewApi` reports most of them, not all (`Collectors.teeing`) |
| Java 22 to 25 language features and class files | not used in shared code (ADR-0044) |
| Preview features | forbidden in shared code |
| Release builds with R8 | the R8 build of every engine class passes (Android verification job); a release APK is not run on a device |

### Rules for agents and contributors

- Never use anything in the forbidden table; when a task seems to need one, use the listed alternative and say so in the pull request.
- Before using a Java API newer than Java 8 that is not in the verified list, check whether it exists on API 21 or is covered by `desugar_jdk_libs`; if that cannot be confirmed, use a basic alternative.
- The Android build enables the lint check `NewApi` as an error on the shared sources (`lint { error += "NewApi" }` in `android/api-lint`, accepted by the Android Gradle Plugin 9.2.1); fix every violation, never suppress it. Lint does not see every gap: `Collectors.teeing` passes lint and fails at run time, which is why the emulator tests run too.
- Run the library on an API 21 emulator after adding any suspicious API: a successful build does not prove it works, `NoSuchMethodError` only shows at run time.

## Pending checks of the accepted decisions

Nothing here is verified yet, so every item is **not compatible yet** until it is checked with the procedure above, except where the status says otherwise.

| Feature | Item to verify | Status |
|---|---|---|
| Screen and display modes (ADR-0002) | Mode selection through Android APIs, isolated in `platform-android`; refresh-rate and color-depth selection may be unavailable and is then reported through the effective result | not compatible yet |
| Output filtering (ADR-0002) | common GLSL/GLSL ES shader subset and own filter implementations (ADR-0025, ADR-0033); headers `#version 100` on Android | not compatible yet |
| Sprites and sprite sheets (ADR-0003) | Pure-Java PNG decoder in shared code (`java.util.zip.Inflater` and `CRC32`, verified on API 21 by the job probe); optional platform decoders (ADR-0026) | PNG decoder, pixel buffer, image cache (memory limit and reload included), sprite sheets, sheet groups, sheet loader, sprites, color transforms, template color variants, animations, the animator and animated sprites run by the device test `GraphicsTest` with the JVM expected values (sprite sheet batches 1 to 4, ADR-0055; sprite batches 1 to 4, ADR-0056, ADR-0057; animated-sprite batches 1 to 3, ADR-0058); the runtime-thread check by `CoreTest`; platform decoders not written yet |
| Audio (ADR-0004) | No `javax.sound` in `audio-api`; PCM WAV baseline, OGG Vorbis decoded by the platform; latency target 100 ms best effort to measure (ADR-0027, ADR-0033) | PCM types, decoder contract, WAV decoder, short-sound cache, voices, variant sets (with the reference vector of a seed), WAV streaming, streamed long sounds and a playlist advance, the controls (gain products, looping and a gapless streamed loop, pause and master pause) checked by the device test `AudioTest` with the JVM expected values, run by the Android job (batches 1 to 5, ADR-0065, ADR-0066, ADR-0067, ADR-0068, ADR-0069); the mixer groups and fades (batch 7, ADR-0071, `AudioTest.testGroupsAndFades`); batch 6 is desktop only; OGG Vorbis, the Android backend and the latency not verified yet |
| Desktop platform (ADR-0008) | `platform-awt` uses `java.desktop`, which does not exist on Android: it must stay out of every Android build | platform module only; enforced by the Android job (desktop modules are not in the Android build, and `java.desktop` in a shared module fails it) |
| Desktop renderer and input (ADR-0022, ADR-0035) | `render-awt`, `input-awt`, `audio-javasound`, `graphics-imageio` use `java.desktop` | platform module only |
| JSON reader and definition files (ADR-0028, ADR-0032) | Pure Java string and number parsing in `core`; no `java.util.Base64` (API 26) in shared code, base64 decoded by project code | compatible: reader, configuration and registry give the JVM results on API 21 (`CoreTest`); `java.util.Base64` works through desugaring (job probe), the ADR-0032 choice of project code is unchanged |
| Tiled loader (ADR-0032) | `java.util.zip` `Inflater` and `GZIPInputStream` on API 21 | APIs compatible (job probe); loader not written yet |
| Random generator (ADR-0031) | Own SplitMix64 and xoshiro256\*\*, integer operations only; `SplittableRandom` (API 24) not used | compatible: reference outputs identical on API 21 (`CoreTest`); `SplittableRandom` confirmed missing (job probe) |
| Loop clock (ADR-0017) | `System.nanoTime` on API 21 | compatible: the game loop runs on the system clock of the emulator (`RuntimeTest`) |
| Asynchronous loading (ADR-0019, ADR-0031) | Worker threads, a volatile flag and `Thread.sleep` in shared code; no `java.util.concurrent` executor in `core` | compatible: a request completed from a real worker thread and a load group give the JVM results on API 21 (`CoreTest.testAsyncLoading`); the platform pools are written by the platforms |
| Network transport (ADR-0039) | `java.nio.channels.DatagramChannel` through `open()`, `socket().bind(...)`, `configureBlocking(false)`, `receive`, `send` (API 1; `DatagramChannel.bind` and `open(ProtocolFamily)` are API 24 and are not used); `socket().setBroadcast(true)` for discovery; `java.util.zip.CRC32`, `java.security.SecureRandom`, `StandardCharsets` (API 19); the game declares `android.permission.INTERNET`; calls never run on the Android main thread (the runtime thread is the render thread); a hosting device holds the Wi-Fi multicast lock for LAN discovery (`platform-android` capability) | not compatible yet |
| Multiplayer (ADR-0038) | Pure Java over `network`, `ecs`, `traits`, `input-api`; no new JDK API | not compatible yet |
| Allocation tests (ADR-0030) | `com.sun.management.ThreadMXBean` is JVM-only: test code only, never in shared main code | platform module only |
| Desktop OGG decoder (ADR-0033, ADR-0073) | JOrbis 0.0.17 in `audio-codec-ogg`, desktop only (`--release 25`), never in Android builds; Android decodes OGG Vorbis with the platform (`audio-android`, batch 9); the shared modules are unchanged | platform module only: `audio-codec-ogg` done (audio batch 8, 2026-11-27), not shipped on Android |

## Android verification job (ADR-0012, ADR-0044)

Where: the Gradle build in `android/` and the `android` job of `.github/workflows/build.yml`, blocking, on every pull request and push of `rewrite/v11`. It consumes the modules installed in the local Maven repository by `mvn -B install`.

**Shared modules** are read from the aggregator `pom.xml` by `android/settings.gradle.kts`: every module that does not override `jdk.version` (the desktop-only modules and `examples` set 25), except `lionengine-test-support`, a test-scope kit never shipped (ADR-0045). A new module is covered as soon as the aggregator declares it.

| Check | Gradle task (in `android/`) | What fails it |
|---|---|---|
| Static API check | `:api-lint:lintDebug` | `api-lint` compiles the shared sources against `android.jar` (minimum API 21, desugaring on) and runs lint with `NewApi` as an error: a type Android does not have (`java.desktop`) fails the compilation, a call above API 21 that desugaring does not cover fails lint |
| DEX with D8 | `:verification:verifyDebugDex` | the `verification` application depends on the shared artifacts like a game; its debug build runs D8 with desugaring, then the task checks that every class of every shared JAR is in the DEX files of the APK |
| DEX with R8 | `:verification:verifyReleaseDex` | release build with R8 (shrinking and optimization on, every `com.b3dgs.lionengine` class kept by `proguard-rules.pro`), same check; a missing class is an R8 error |
| Run time on API 21 | `:verification:connectedDebugAndroidTest` | instrumentation tests on an API 21 emulator or device: the device tests of the shared modules (`CoreTest`, `RuntimeTest`, `ResourcesTest`, `RenderTest`, `PlatformTest`, `GraphicsTest`, with the expected values of the JVM tests) and the probes (`LanguageProbeTest`, `LibraryProbeTest`) |

Commands, from the repository root, with JDK 25 and the Android SDK (`ANDROID_HOME` set): `mvn -B install`, then `cd android` and `./gradlew :api-lint:lintDebug :verification:verifyDebugDex :verification:verifyReleaseDex`; with an API 21 emulator running or a device attached, `./gradlew :verification:connectedDebugAndroidTest`. The CI job starts the emulator with `reactivecircus/android-emulator-runner` ([`dependencies.md`](dependencies.md)).

Harness (ADR-0050): the engine tests are JUnit 6 Jupiter tests, which need a Java 17 runtime and have no instrumentation support on API 21, so the emulator tests use AndroidX Test (`AndroidJUnitRunner`) with JUnit 4. A probe of an API that works asserts its result; a probe of an API missing on API 21 asserts the `LinkageError` (`NoSuchMethodError`, `NoClassDefFoundError`), so a toolchain change that alters a row of the tables above fails the job until the tables are updated. Each probe is its own class, so a missing API fails only its own test.

### Versions and results (2026-11-25, pull request #909)

| Item | Value |
|---|---|
| Android Gradle Plugin | 9.2.1 (ADR-0012 baseline; 9.5.2 is the latest stable, a bump is an ordinary dependency change) |
| D8 and R8 | 9.2.14, bundled with the Android Gradle Plugin |
| Lint | 32.2.1, bundled with the Android Gradle Plugin |
| Gradle | 9.4.1 (wrapper, distribution checksum pinned) |
| JDK running Maven and Gradle | Temurin 25.0.5 (CI `setup-java`, and locally 25.0.5.1) |
| SDK | `compileSdk` and `targetSdk` 37 (platform `android-37.0`), `minSdk` 21, Java 21 source and target, `desugar_jdk_libs` 2.1.5 |
| Emulator | Android emulator 37.3.10.0, image `system-images;android-21;default;x86_64` (Android 5.0.2), on the `ubuntu-latest` runner with KVM |
| Lint `NewApi` | the setting `error += "NewApi"` is accepted by the Android Gradle Plugin 9.2.1; first run: one violation in shared code, `StringBuilder.isEmpty()` in `DisplayState` (`CharSequence.isEmpty()`, API 35), fixed |
| D8 | every class of the 17 shared modules is in the debug APK (70 classes; 10 modules hold only their package description so far) |
| R8 release build | passes with every engine class kept, no missing class; the release APK is not run on a device |
| Emulator tests | 50 tests: the 24 device tests and the 5 language probes pass; the library probes give the tables above |
| `java.desktop` in a shared module | refused, while the Maven build accepts it: in pull request #909 (commit `60fd53c`, then removed) the `android` job failed at the compilation of `api-lint` (`package java.awt does not exist`); run locally, R8 also stops on the missing class (`Missing class java.awt.Point`) |

Known limits: release builds are verified by R8 only, not run; the job runs on one emulator image (API 21, x86_64), not on a real device or another API level; the option `android.builtInKotlin=false` of `android/gradle.properties` (no Kotlin source in the build) is deprecated by the Android Gradle Plugin 9 and must be removed before the Android Gradle Plugin 10.
