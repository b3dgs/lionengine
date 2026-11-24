# ADR-0050 — Test framework: the latest stable JUnit release

- Status: ACCEPTED
- Date: 2026-11-24
- Decision makers: project owner ("Ok we can update doc to latest JUnit", 2026-11-24, answering `OQ-166`)
- Related requirements: `REQ-TEST-001`, `REQ-CORE-004`
- Open questions resolved: `OQ-166`
- Supersedes / superseded by: supersedes the test framework version of [ADR-0012](0012-platform-and-build-baseline.md) ("JUnit 5"); every other point of ADR-0012 is unchanged

## Context

ADR-0012 chose JUnit 5 as the test framework. The build skeleton inherits JUnit from `b3dgs-parent` 2.0.0, which provides JUnit Jupiter 6.0.3. JUnit 6 is the next major release of the same Jupiter programming model and needs Java 17 or later at test run time. The skeleton recorded the mismatch as `OQ-166`. On 2026-11-24 the latest stable release on Maven Central is 6.1.3; 6.2.0-M1 is a milestone.

## Decision

1. The engine tests use **JUnit 6** (the Jupiter API and the JUnit Platform suite engine), at its **latest stable release**: 6.1.3 on 2026-11-24.
2. The version is pinned in the aggregator `pom.xml` through the `junit.version` property of `b3dgs-parent`, which overrides the parent's version for every module.
3. Milestones and release candidates are never used. Moving to a newer stable release is an ordinary dependency bump: the pull request updates the property and the row of [`../dependencies.md`](../dependencies.md), and the build must stay green.

## Options considered

- **Latest stable JUnit 6, pinned in the aggregator (chosen):** the owner's choice; the build and the documentation state the same version.
- **The parent's version (6.0.3):** no override, but already one minor release behind, and it changes only when the parent is republished.
- **JUnit 5, as ADR-0012 stated:** would downgrade the parent's choice for no benefit.

## Consequences

- Positive consequences: one test framework version, stated in the build and the documentation; current fixes and features of JUnit.
- Costs / constraints:
  - the aggregator overrides one property of the parent;
  - every bump is checked by the full build.
- Android/DEX: JUnit is test-only and never shipped. Jupiter tests run on the JVM: the 6.x line needs a Java 17 runtime, and Jupiter instrumentation tests are not available on an API 21 device. The API 21 emulator tests of ADR-0044 therefore need their own instrumentation harness. The Android job batch chooses it, records it in [`../dependencies.md`](../dependencies.md), and keeps the shared logic testable from both sides.
- Impact on modules and APIs: none; test scope only.
- Impact on tests and documentation: `pom.xml`, [`../testing.md`](../testing.md), [`../dependencies.md`](../dependencies.md), [`../features/build-skeleton.md`](../features/build-skeleton.md), [`../roadmap.md`](../roadmap.md), [`../open-questions.md`](../open-questions.md), [`../agent-workflow.md`](../agent-workflow.md), [`../traceability.md`](../traceability.md), `README.md`, this index.

## Validation

Decided by the project owner on 2026-11-24; the build passes with JUnit 6.1.3 (`mvn -B clean verify`, 67 tests).
