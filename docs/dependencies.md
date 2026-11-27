# Dependency register

**Status: VALIDATED — register required by ADR-0032 and REQ-DOC-007. Add a row in the same pull request that adds a dependency; never add a dependency without its row.**

## Rules (ADR-0032)

- **Shipped** dependencies (on the runtime classpath of an engine module) must have a GPL-3.0-compatible license: MIT, BSD-2-Clause, BSD-3-Clause, ISC, zlib, Apache-2.0, MPL-2.0, LGPL-2.1-or-later, LGPL-3.0, GPL-3.0-or-later, CC0, Unlicense. Refused: GPL-2.0-only, non-commercial, proprietary, unknown.
- **Unshipped** tools (build plugins, test libraries) may use any OSI-approved license.
- Android status uses the vocabulary of [`android-compatibility.md`](android-compatibility.md): `compatible`, `compatible with desugaring`, `platform module only`, `not compatible yet`, `rejected`, or `not shipped` for build and test tools.
- Code ported from a reference implementation (for example an `HQ2X` reference) is recorded in the second table with its license. An algorithm rewritten from a published description is not a dependency.

## Libraries and tools

| Name | Version | Module | Shipped | Purpose | License (SPDX) | Android status | Checked |
|---|---|---|---|---|---|---|---|
| `com.b3dgs:b3dgs-parent` | 3.0.0-SNAPSHOT (ADR-0051; resolved from the Sonatype Central snapshot repository declared in the aggregator; a released version is required before the first engine release) | aggregator `pom.xml` | no | Parent POM: plugin versions, checkstyle, JaCoCo, Sonar profile, publishing | GPL-3.0-or-later | not shipped | 2026-11-24 |
| `com.b3dgs.radialencapsulation:radial-encapsulation-maven-plugin` | 1.0.2 (from the parent POM) | aggregator `pom.xml`, `basePackage` per module (ADR-0037) | no | Package dependency rule check inside each module (ADR-0012, ADR-0015, ADR-0037, REQ-CORE-005) | Apache-2.0 | not shipped | 2026-11-23 |
| JUnit Jupiter (`junit-jupiter`, `junit-platform-suite-engine`) | 6.1.3, the latest stable release, pinned by the `junit.version` property of the aggregator (ADR-0050; the parent provides the same version) | test scope of every module | no | Test framework (ADR-0012, ADR-0050) | EPL-2.0 | not shipped | 2026-11-24 |
| JaCoCo (`jacoco-maven-plugin`) | 0.8.16 (from the parent POM) | build | no | Coverage, XML report per module for SonarCloud (ADR-0012) | EPL-2.0 | not shipped | 2026-11-24 |
| Checkstyle with `com.b3dgs:b3dgs-buildtools` | 14.1.0 with buildtools 3.0.0-SNAPSHOT (from the parent POM, ADR-0051) | build; buildtools also inherited as a dependency, restricted to `provided` scope | no | Code style rules, license header and suppressions of `b3dgs-parent` ([`implementation-guide.md`](implementation-guide.md)) | LGPL-2.1-or-later (checkstyle), GPL-3.0-or-later (buildtools) | not shipped | 2026-11-24 |
| SLF4J (`slf4j-api`, `slf4j-simple`) | 2.0.20 (from the parent POM) | inherited by every module; restricted to test scope by the aggregator (REQ-CORE-006: no logging library in shared modules) | no | Logging of test tools only | MIT | not shipped | 2026-11-24 |
| Maven plugins of the parent: compiler 3.16.0, jar 3.5.1, source 3.4.0, javadoc 3.12.0, surefire and failsafe 3.6.1, enforcer 3.7.0 (Maven 3.9.0 or later required), dependency 3.11.0, resources 3.5.0, install and deploy 3.2.0; Apache Felix `maven-bundle-plugin` 6.2.0; `sonar-maven-plugin` 5.9.0.7382 | as listed | build | no | Build, packaging (OSGi manifest), Javadoc with doclint, tests, JDK 25 enforcement, Sonar analysis | Apache-2.0 (Sonar plugin: LGPL-3.0-only) | not shipped | 2026-11-24 |
| Android Gradle Plugin (`com.android.application`, `com.android.library`), with its D8, R8 and lint | 9.2.1 (ADR-0012; `android/gradle/libs.versions.toml`) | `android/` Gradle build: `api-lint`, `verification` | no | Lint `NewApi` on the shared sources, DEX production with D8 and R8, instrumentation tests (ADR-0012, ADR-0044) | Apache-2.0 | not shipped | 2026-11-25 |
| Gradle (wrapper) | 9.4.1, distribution checksum pinned in `android/gradle/wrapper/gradle-wrapper.properties` | `android/` | no | Build tool of the Android verification build (ADR-0012: Android through Gradle only) | Apache-2.0 | not shipped | 2026-11-25 |
| `com.android.tools:desugar_jdk_libs` | 2.1.5 (ADR-0012) | `android/` (`api-lint`, `verification`) and games | no (linked into Android applications by the Android Gradle Plugin) | Core library desugaring for API 21 (ADR-0012) | GPL-2.0-only WITH Classpath-exception-2.0 | compatible with desugaring | 2026-11-25 |
| AndroidX Test: `androidx.test:runner`, `androidx.test.ext:junit` (with `androidx.test:monitor`, `core`, `annotation`, `tracing`, `storage` as their dependencies) | runner 1.7.0, ext-junit 1.3.0 (minimum API 21 in their manifests) | `android/verification`, `androidTest` only | no | Instrumentation harness of the API 21 emulator tests: `AndroidJUnitRunner` runs JUnit 4 tests on the device, because JUnit 6 Jupiter needs a Java 17 runtime and has no instrumentation support on API 21 (ADR-0050) | Apache-2.0 | not shipped | 2026-11-25 |
| JUnit 4 (`junit:junit`, with `org.hamcrest:hamcrest-core` 1.3) | 4.13.2 | `android/verification`, `androidTest` only | no | Test API of the instrumentation tests (ADR-0050: the JVM tests stay on JUnit 6) | EPL-1.0 (Hamcrest: BSD-3-Clause) | not shipped | 2026-11-25 |
| `reactivecircus/android-emulator-runner` (GitHub Action) | v2.38.0, pinned to commit `a421e43855164a8197daf9d8d40fe71c6996bb0d` | `.github/workflows/build.yml`, `android` job | no | Starts the API 21 x86_64 emulator and runs the instrumentation tests (ADR-0044) | Apache-2.0 | not shipped | 2026-11-25 |

Build toolchain (not dependencies): JDK 25 or later (verified with Temurin 25.0.5.1), also running Gradle for the Android build; Maven 3.9 (3.9.11 locally, 3.9.14 in CI); the Android SDK (platform 37, build tools and emulator installed by the Android Gradle Plugin and the emulator action); Python 3 for the checks.

Planned candidates, not added yet (a row moves to the table above only in the pull request that adds the dependency):

| Name | Planned module | Purpose | Decision |
|---|---|---|---|
| JOrbis | `audio-codec-ogg` (desktop only) | OGG Vorbis decoding on desktop | ADR-0033; approved by the owner on 2026-11-27 (audio review) for audio batch 8, which records its version, license (LGPL) and status when it adds it |
| A desktop gamepad library | a later `input-*` adapter | Gamepads on desktop (the JDK has none) | ADR-0035; chosen with a game need |

Note on `desugar_jdk_libs`: it is distributed under GPL-2.0 with the Classpath Exception, like the OpenJDK class library it derives from, and is linked into Android applications by the Android Gradle Plugin, not into engine artifacts. The exception allows that linking; the row is recorded for games, which must enable desugaring (ADR-0012).

## Ported code and algorithms

| Algorithm | Source | Module | License (SPDX) | Checked |
|---|---|---|---|---|
| *(none yet; `HQ2X` is recorded here if code is ported from a reference implementation, ADR-0025, ADR-0033)* | | | | |
