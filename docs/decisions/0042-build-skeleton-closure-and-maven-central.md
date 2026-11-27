# ADR-0042 — Build skeleton closure and artifact hosting on Maven Central

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner (answer of 2026-11-23 in the architecture-review session); written by the agent
- Related requirements: `REQ-CORE-002`, `REQ-CORE-003`, `REQ-CORE-005`, `REQ-DOC-001`
- Open questions resolved: `OQ-149`
- Supersedes / superseded by: none; completes [ADR-0012](0012-platform-and-build-baseline.md), [ADR-0015](0015-single-version-and-package-dependency-rule.md), [ADR-0032](0032-definition-files-registry-ids-resource-providers-dependency-policy.md) and [ADR-0037](0037-package-tree-per-module-callbacks-and-canonical-names.md) (all unchanged)

## Context

`OQ-149` listed the build-skeleton details: module declaration, package naming verified against the dependency plugin, Maven profiles, exact build and test commands, the adaptation of `ci-cd.yml`, and artifact hosting. ADR-0012, ADR-0015, ADR-0032 and ADR-0037 have since decided the module list, the package tree and plugin scope, the Javadoc checks and the CI platform; [`../features/build-skeleton.md`](../features/build-skeleton.md) describes the batch. What remained was one choice, artifact hosting, and work that the batch itself performs.

## Decision

1. **Artifact hosting.** Released artifacts are published to **Maven Central** through the Sonatype Central portal, with the publishing configuration inherited from `com.b3dgs:b3dgs-parent`. Snapshots are published to the Sonatype Central snapshot repository already declared in the aggregator `pom.xml`. Game repositories depend on these artifacts (ADR-0023).
2. **The rest is batch content, not an open question.** The build-skeleton batch declares the modules, profiles and CI jobs described in `features/build-skeleton.md`, then writes the exact build, test and single-module commands in `README.md` and `AGENTS.md`, and the tool versions in `dependencies.md`. Until that batch is merged, no build or test command exists and agents must not invent one (`AGENTS.md` section 7).
3. The `editor` job of `master` `ci-cd.yml` is not carried over (as already stated in `OQ-149`).

## Options considered

- Maven Central (chosen by the owner): no credentials needed by game repositories; matches the parent POM and the declared snapshot repository.
- GitHub Packages: game repositories would need a token to download the engine.
- Decide at the first release: leaves the specification open.

## Consequences

- Positive consequences: no open question for Phase 0; the skeleton batch has a closed scope.
- Costs / constraints: release signing and Central credentials are configured in the parent POM and CI secrets, outside this repository's documents; the skeleton batch records the publishing entries in `dependencies.md`.
- Impact on modules and APIs: none.
- Impact on tests and documentation (including Android/DEX compatibility): `features/build-skeleton.md`, `open-questions.md`, `traceability.md`, `roadmap.md` and the pages that referred to `OQ-149` updated.

## Validation

Owner answer of 2026-11-23 ("Maven Central").
