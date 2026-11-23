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
| `com.b3dgs:b3dgs-parent` | 2.1.0-SNAPSHOT | aggregator `pom.xml` | no | Parent POM: plugin versions and common build configuration | to record at the build skeleton (ADR-0042) | not shipped | — |
| `com.b3dgs.radialencapsulation:radial-encapsulation-maven-plugin` | from the parent POM (latest release 1.0.2) | aggregator `pom.xml`, `basePackage` per module (ADR-0037) | no | Package dependency rule check inside each module (ADR-0012, ADR-0015, ADR-0037, REQ-CORE-005) | Apache-2.0 | not shipped | 2026-11-23 |
| JUnit 5 | set at the build skeleton | test scope of every module | no | Test framework (ADR-0012) | EPL-2.0 | not shipped | 2026-11-23 |
| JaCoCo | set at the build skeleton | build | no | Coverage merged for SonarCloud (ADR-0012) | EPL-2.0 | not shipped | 2026-11-23 |
| Android Gradle Plugin | 9.2.1 (ADR-0012) | Android verification module | no | DEX production and verification (ADR-0012, ADR-0044) | Apache-2.0 | not shipped | 2026-11-23 |
| `com.android.tools:desugar_jdk_libs` | 2.1.5 (ADR-0012) | Android verification module and games | no (linked into Android applications by the Android Gradle Plugin) | Core library desugaring for API 21 (ADR-0012) | GPL-2.0-only WITH Classpath-exception-2.0 | compatible with desugaring | 2026-11-23 |

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
