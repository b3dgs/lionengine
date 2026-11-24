# ADR-0051 — Parent POM: `b3dgs-parent` 3.0.0-SNAPSHOT

- Status: ACCEPTED
- Date: 2026-11-24
- Decision makers: project owner ("You can use 3.0.0-SNAPSHOT", 2026-11-24, answering `OQ-167` after publishing that version)
- Related requirements: `REQ-TECH-002`, `REQ-CORE-003`, `REQ-CORE-005`
- Open questions resolved: `OQ-167`
- Supersedes / superseded by: none; completes [ADR-0012](0012-platform-and-build-baseline.md) and [ADR-0042](0042-build-skeleton-closure-and-maven-central.md) (both unchanged)

## Context

The aggregator `pom.xml` referenced `b3dgs-parent` 2.1.0-SNAPSHOT, which was never published, so the build skeleton used the released 2.0.0 and recorded `OQ-167`. The owner then published `b3dgs-parent` and `b3dgs-buildtools` 3.0.0-SNAPSHOT to the Sonatype Central snapshot repository. Compared with 2.0.0, the new parent changes these points:
- checkstyle 14.1.0 with the rules of `b3dgs-buildtools` 3.0.0-SNAPSHOT:
  - `JavadocStyle` is removed and `SummaryJavadoc` is enforced;
  - new Javadoc layout checks: leading asterisks, content location, block-tag location, an empty line before the tag group, no misplaced Javadoc;
  - the header and the suppressions are unchanged;
- JUnit 6.1.3;
- JaCoCo 0.8.16;
- SLF4J 2.0.20;
- newer Maven plugins;
- Maven 3.9.0 or later required;
- a default Java level of 21.

## Decision

1. The aggregator `pom.xml` uses `b3dgs-parent` **3.0.0-SNAPSHOT**.
2. The aggregator declares the Sonatype Central snapshot repository (id `snapshots`, as in the parent), because Maven must find the snapshot parent before it can read the repositories the parent declares. The declaration is removed when the parent is a release.
3. The other settings of the build skeleton stay:
   - the JUnit pin of ADR-0050, equal to the parent's version today;
   - `jdk.version` 21 for shared modules and 25 for desktop-only modules (ADR-0044);
   - the test and `provided` scopes given to the inherited `slf4j-api` and `b3dgs-buildtools`.

## Options considered

- 3.0.0-SNAPSHOT (chosen): the owner's current build configuration, with the checkstyle rules the project wants enforced.
- Stay on the released 2.0.0: reproducible, but older checkstyle rules and plugins than the owner's projects use.

## Consequences

- Positive consequences: the engine follows the owner's current checkstyle rules and tool versions; `OQ-167` is closed.
- Costs / constraints:
  - A snapshot parent changes under the build: the parent updates snapshots on every build, so the same commit can meet new rules later. A build that starts failing after a parent update is fixed in the engine code, or reported to the owner.
  - A release cannot use a snapshot parent (Maven Central publishing refuses snapshot dependencies). Before the first engine release, the parent moves to a released version and the repository declaration of decision 2 is removed (ADR-0042).
- Impact on modules and APIs: none; the existing code passes the new rules unchanged.
- Impact on tests and documentation (including Android/DEX compatibility):
  - `pom.xml` updated;
  - documents updated: [`../dependencies.md`](../dependencies.md), [`../implementation-guide.md`](../implementation-guide.md) (checkstyle section), [`../testing.md`](../testing.md), [`../features/build-skeleton.md`](../features/build-skeleton.md), [`../open-questions.md`](../open-questions.md), [`../agent-workflow.md`](../agent-workflow.md), [`../roadmap.md`](../roadmap.md), [`../traceability.md`](../traceability.md), `README.md`;
  - no Android/DEX impact: build tools only.

## Validation

Decided by the project owner on 2026-11-24. With this parent, `mvn -B -U clean verify` passes: checkstyle 14.1.0, compile, the package rule, Javadoc and 67 tests. A probe of the newly enforced `SummaryJavadoc` rule fails as expected.
