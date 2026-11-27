# ADR-0012 — Platform and build baseline

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner
- Related requirements: `REQ-TECH-001`, `REQ-TECH-002`, `REQ-TECH-006`, `REQ-TECH-007`, `REQ-CORE-003`, `REQ-CORE-005`
- Open questions resolved: `OQ-001`, `OQ-002`, `OQ-003`, `OQ-004`, `OQ-005`, `OQ-006`, `OQ-007`, `OQ-008`
- Supersedes / superseded by: none; completes [ADR-0001](0001-technical-baseline-and-conventions.md) (which stays unchanged)

Source: owner answers on issue #875.

## Context

[ADR-0001](0001-technical-baseline-and-conventions.md) fixed Java 25+, Maven 3+ and DEX compatibility, but left the Android level, the DEX production path, the module layout, the test framework, the license, the versioning policy and the CI platform open. The `master` branch already runs a working pipeline (Maven core, Gradle Android module, GitHub Actions) that the `rewrite/v11` branch reuses.

## Decision

### Android level and toolchain (OQ-001, OQ-002)

- Minimum Android API level: **21**. The level is raised only if Java 25 support makes it necessary; the owner wants the list of unsupported Java features at that level (see [`../android-compatibility.md`](../android-compatibility.md)).
- DEX is produced by a **separate Gradle module** that consumes the Maven artifacts. No Maven-driven D8/R8 invocation.
- Baseline toolchain, as observed in `master` `java/lionengine-core-android/build.gradle` on 2026-11-22: Android Gradle Plugin 9.2.1, `compileSdk` and `targetSdk` 37, `minSdk` 21, Java source/target 21 for the Gradle module, **core library desugaring** enabled with `com.android.tools:desugar_jdk_libs:2.1.5`, D8/R8 as bundled with the Android Gradle Plugin. These are starting values, updated by ordinary dependency bumps recorded in the pull request.
- Games running on Android must also enable core library desugaring on the application side.

### Physical layout (OQ-003)

- Multiple Maven modules under the aggregator `pom.xml`.
- Group `com.b3dgs.lionengine`; artifact `lionengine-XXX`, where `XXX` is the module name; base Java package `com.b3dgs.lionengine`.
- No module is declared yet: the architecture is still being specified. Modules are added in a step before implementation (build skeleton, `OQ-149`).

### Tests, dependency rules, license (OQ-004, OQ-005, OQ-006)

- Test framework: **JUnit 5**.
- Module dependency rules (`REQ-CORE-005`) are enforced by `radial-encapsulation-maven-plugin`, already configured in the parent POM with zero violations allowed. That it covers every rule of `REQ-CORE-005` is verified on the build skeleton.
- Project license: **GPL-3.0**. The detailed third-party policy remains open (`OQ-148`); until then `AGENTS.md` applies (purpose, license and Android compatibility recorded before adding a dependency).

### Versioning and deprecation (OQ-007)

- [Semantic versioning](https://semver.org/).
- Deprecation follows the Java standard practice (`@Deprecated` with `since` and `forRemoval`, and a Javadoc `@deprecated` naming the replacement). Delay and removal rules not stated here follow Java's practice and are detailed in `OQ-149` if needed.

### Continuous integration (OQ-008)

- GitHub Actions, following the `master` workflow `.github/workflows/ci-cd.yml`: a Maven build and test job on JDK 25 and Maven 3.9, a Maven plus Gradle Android job, coverage merged for SonarCloud. The same approach applies to `rewrite/v11`; the adaptation is part of the build skeleton (`OQ-149`).
- The Android job must prove DEX output (`REQ-TECH-006`): see `OQ-147`.

## Options considered

### DEX production

- A: separate Gradle module consuming Maven artifacts (chosen). Proven in `master`; follows the Android Gradle Plugin, the only supported way to run its tooling.
- B: Maven-driven D8/R8 invocation. Avoids Gradle, but is unsupported by the Android Gradle Plugin and re-implements its desugaring and resource handling.

### Minimum API level

- 21 (chosen): widest reach, achievable with core library desugaring.
- A higher level would drop desugaring needs but exclude devices without a verified need.

## Consequences

- Positive: the proven `master` pipeline is reused; a single DEX path.
- Costs / constraints: two build tools (Maven and Gradle) and two JDK versions in CI (25 for Maven, 21 for the Gradle module in `master`); an AAR build alone does not run D8 and does not prove DEX output, so a verification step must be added (`OQ-147`); whether Java 25 class files are accepted by the toolchain is not verified (`OQ-147`).
- Impact on modules and APIs: shared modules may use only Java language features and APIs verified for API 21 with desugaring; platform modules (`platform-awt`) stay out of Android builds.
- Impact on tests and documentation: [`../android-compatibility.md`](../android-compatibility.md), [`../conventions.md`](../conventions.md) and [`../testing.md`](../testing.md) updated; the CI file is adapted at the skeleton step.

## Validation

Decisions explicitly provided by the project owner on 2026-11-22 (issue #875).
