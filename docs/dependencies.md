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
| `com.b3dgs:b3dgs-parent` | 2.0.0 (2.1.0-SNAPSHOT is not published, `OQ-167`) | aggregator `pom.xml` | no | Parent POM: plugin versions, checkstyle, JaCoCo, Sonar profile, publishing | GPL-3.0-or-later | not shipped | 2026-11-24 |
| `com.b3dgs.radialencapsulation:radial-encapsulation-maven-plugin` | 1.0.2 (from the parent POM) | aggregator `pom.xml`, `basePackage` per module (ADR-0037) | no | Package dependency rule check inside each module (ADR-0012, ADR-0015, ADR-0037, REQ-CORE-005) | Apache-2.0 | not shipped | 2026-11-23 |
| JUnit Jupiter (`junit-jupiter`, `junit-platform-suite-engine`) | 6.0.3 (from the parent POM; ADR-0012 names JUnit 5, `OQ-166`) | test scope of every module | no | Test framework (ADR-0012) | EPL-2.0 | not shipped | 2026-11-24 |
| JaCoCo (`jacoco-maven-plugin`) | 0.8.14 (from the parent POM) | build | no | Coverage, XML report per module for SonarCloud (ADR-0012) | EPL-2.0 | not shipped | 2026-11-24 |
| Checkstyle with `com.b3dgs:b3dgs-buildtools` | 12.3.1 with buildtools 2.0.0 (from the parent POM) | build; buildtools also inherited as a dependency, restricted to `provided` scope | no | Code style rules, license header and suppressions of `b3dgs-parent` ([`implementation-guide.md`](implementation-guide.md)) | LGPL-2.1-or-later (checkstyle), GPL-3.0-or-later (buildtools) | not shipped | 2026-11-24 |
| SLF4J (`slf4j-api`, `slf4j-simple`) | 2.0.17 (from the parent POM) | inherited by every module; restricted to test scope by the aggregator (REQ-CORE-006: no logging library in shared modules) | no | Logging of test tools only | MIT | not shipped | 2026-11-24 |
| Maven plugins of the parent: compiler 3.15.0, jar 3.5.0, source 3.4.0, javadoc 3.12.0, surefire and failsafe 3.5.5, enforcer 3.6.2, dependency 3.10.0, resources 3.5.0; Apache Felix `maven-bundle-plugin` 6.0.2; `sonar-maven-plugin` 5.5.0.6356 | as listed | build | no | Build, packaging (OSGi manifest), Javadoc with doclint, tests, JDK 25 enforcement, Sonar analysis | Apache-2.0 (Sonar plugin: LGPL-3.0-only) | not shipped | 2026-11-24 |
| Android Gradle Plugin | 9.2.1 (ADR-0012) | Android verification module | no | DEX production and verification (ADR-0012, ADR-0044) | Apache-2.0 | not shipped | 2026-11-23 |
| `com.android.tools:desugar_jdk_libs` | 2.1.5 (ADR-0012) | Android verification module and games | no (linked into Android applications by the Android Gradle Plugin) | Core library desugaring for API 21 (ADR-0012) | GPL-2.0-only WITH Classpath-exception-2.0 | compatible with desugaring | 2026-11-23 |

Build toolchain (not dependencies): JDK 25 or later (verified with Temurin 25.0.5.1), Maven 3.9 (3.9.11 locally, 3.9.14 in CI), Python 3 for the checks.

Planned candidates, not added yet (a row moves to the table above only in the pull request that adds the dependency):

| Name | Planned module | Purpose | Decision |
|---|---|---|---|
| JOrbis | `audio-codec-ogg` (desktop only) | OGG Vorbis decoding on desktop | ADR-0033; license and status recorded when added |
| A desktop gamepad library | a later `input-*` adapter | Gamepads on desktop (the JDK has none) | ADR-0035; chosen with a game need |

Note on `desugar_jdk_libs`: it is distributed under GPL-2.0 with the Classpath Exception, like the OpenJDK class library it derives from, and is linked into Android applications by the Android Gradle Plugin, not into engine artifacts. The exception allows that linking; the row is recorded for games, which must enable desugaring (ADR-0012).

## Ported code and algorithms

| Algorithm | Source | Module | License (SPDX) | Checked |
|---|---|---|---|---|
| *(none yet; `HQ2X` is recorded here if code is ported from a reference implementation, ADR-0025, ADR-0033)* | | | | |
