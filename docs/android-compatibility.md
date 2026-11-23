# Android/DEX compatibility checklist

**Status: PROPOSED — operational procedure applying the VALIDATED requirements REQ-TECH-006 and REQ-TECH-007 (ADR-0001). Baseline of ADR-0012 (ACCEPTED): minimum API 21, DEX through a separate Gradle module. Java 25 support on that baseline is not verified (OQ-147).**

Java compilation alone is not proof of DEX compatibility. Verification is continuous, not a final step.

## Before using a Java language feature, a Java API or a dependency in shared code

1. Identify where it will be used: shared code (must be Android-compatible) or a platform-specific module (isolated, may use platform APIs).
2. Check that the targeted toolchain supports it: language level accepted by the Android build, D8/R8 handling of the construct, availability of the API at the minimum Android API level (OQ-001).
3. If the API is only available through *core library desugaring*: confirm it is supported, record the configuration, and cover it with a verification build and tests on the targeted Android versions.
4. For a third-party dependency: confirm it contains no unsupported APIs or native code, and record its license (OQ-006).
5. Record the outcome in the pull request (or feature file): `compatible`, `compatible with desugaring`, `platform module only`, or `rejected`. If unable to verify, the answer is **not compatible yet**, never "probably fine".

## Pipeline requirements

- A CI job builds the Android-targeted modules through D8/R8 and produces the expected DEX artifacts (REQ-TECH-006, REQ-TECH-007).
- A verification build covers APIs requiring desugaring.
- Tests run on the targeted Android versions (devices or emulators); these are platform tests and are kept separate from the headless unit tests.
- Pipeline design (ADR-0012): Maven builds and tests the modules; a separate Gradle module consumes the Maven artifacts. An Android *library* (AAR) build does not run D8, so the job also needs a verification module that dexes the artifacts (OQ-147).
- Games must enable core library desugaring on the application side too.

## Baseline (ADR-0012)

Observed in `master` `java/lionengine-core-android/build.gradle` on 2026-11-22: Android Gradle Plugin 9.2.1, `compileSdk`/`targetSdk` 37, `minSdk` 21, Java 21 source/target, `desugar_jdk_libs:2.1.5`. Proven in `master`: Java 21 bytecode. Not verified: Java 25 class files and APIs (OQ-147).

## Java features and APIs at API 21 (working list, NOT verified)

General knowledge of D8/ART, to be confirmed by the Android CI job (OQ-147). Until confirmed, each item is **not compatible yet** in shared code (procedure above).

| Area | Expectation at API 21 |
|---|---|
| `java.desktop` (AWT, Swing, ImageIO, `javax.sound`, `java.beans`) | absent on every Android level: platform module only |
| `java.lang.foreign`, virtual threads, structured concurrency, scoped values, `java.net.http`, module system (`module-info` ignored) | unavailable |
| `java.time`, streams, `Optional`, `java.util.function`, much of `java.util.concurrent` (about JDK 11 level) | through core library desugaring only |
| APIs added after about JDK 11 (sequenced collections, `Math.clamp`, `HexFormat`, `Cleaner`, `VarHandle`, `MethodHandles`, record reflection) | only on recent Android levels, not desugared: avoid in shared code |
| Records, sealed types, switch expressions, text blocks, `instanceof` patterns | compile-time, handled by D8; verify |
| Pattern matching for `switch`, record patterns, Java 22 to 25 language features | rely on `invokedynamic` bootstraps or new class file versions: verify one by one |
| Preview features | forbidden in shared code |
| Class file version 69 (Java 25) | unknown; proven path is `--release 21` |

## Pending checks of ADR-0002 to ADR-0004

Recorded for the accepted decisions; nothing here is verified yet, so every item is **not compatible yet** until it is checked with the procedure above.

| Feature | Item to verify | Status |
|---|---|---|
| Screen and display modes (ADR-0002) | Mode selection through Android APIs, isolated in `platform-android`; refresh-rate and color-depth selection may be unavailable and is then reported through the effective result | not compatible yet |
| Output filtering (ADR-0002) | common GLSL/GLSL ES shader subset and own filter implementations (ADR-0025); `HQ2X` license check (OQ-159) | not compatible yet |
| Sprites and sprite sheets (ADR-0003) | Pure-Java PNG decoder in shared code (`java.util.zip.Inflater`, to verify on API 21); optional platform decoders (ADR-0026) | not compatible yet |
| Audio (ADR-0004) | No `javax.sound` in `audio-api`; decoder libraries, licenses and desugaring (OQ-115); latency target (OQ-116) | not compatible yet |
| Desktop platform (ADR-0008) | `platform-awt` uses `java.desktop`, which does not exist on Android: it must stay out of every Android build | platform module only |

## What to record once decided

Minimum Android API level, Android Gradle Plugin version (if used), D8/R8 version, desugaring library and its version, list of Java language features and APIs verified. Put them in an ADR and reference it here.
