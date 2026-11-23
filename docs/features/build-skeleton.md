# Feature: build skeleton

**Status: VALIDATED — content of the skeleton; its exact commands and versions are recorded by the batch itself (OQ-149).**

- Owner: project owner
- Roadmap phase: Phase 0, first batch of the project; done by the owner or a cloud agent, not a local agent (it fixes the commands every later batch uses)
- Affected modules: all modules of [`../architecture/modules.md`](../architecture/modules.md) needed by Phases 1 to 3, `android/` Gradle build
- Requirements: `REQ-TECH-001`, `REQ-TECH-002`, `REQ-TECH-006`, `REQ-TECH-007`, `REQ-CORE-003`, `REQ-CORE-005`, `REQ-DOC-001`, `REQ-DOC-004` to `REQ-DOC-007`, `REQ-SUPPORT-001`
- Applicable ADRs: [`ADR-0012`](../decisions/0012-platform-and-build-baseline.md), [`ADR-0015`](../decisions/0015-single-version-and-package-dependency-rule.md), [`ADR-0023`](../decisions/0023-game-repository-change-requests-assets-and-api-index-check.md), [`ADR-0024`](../decisions/0024-determinism-performance-budgets-and-desktop-support.md), [`ADR-0032`](../decisions/0032-definition-files-registry-ids-resource-providers-dependency-policy.md), [`ADR-0037`](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md)
- Open questions involved: `OQ-147` (Android verification, done in this batch), `OQ-149` (closed by this batch)

## Objective

A compilable, testable, empty multi-module project whose CI enforces every structural rule, so that each later batch only adds code.

## Out of scope

Any engine behavior; release publishing and artifact hosting (decided at the first release, recorded in OQ-149 until then).

## Expected content

1. **Maven reactor.** The aggregator `pom.xml` (group `com.b3dgs.lionengine`, version `11.0.0-SNAPSHOT`, one version for all modules) declares one folder per module, `lionengine-<module>`, for: `core`, `runtime`, `platform-api`, `render-api`, `resources`, `resources-file`, `resources-archive`, `resources-memory`, `audio-api`, `graphics`, `tilemap`, `camera`, `input-api`, `ecs`, `physics`, `traits`, `test-support`, `examples`, and the desktop modules `platform-awt`, `render-awt`, `input-awt`, `audio-javasound`, `graphics-imageio`. Other modules are declared by their first batch.
2. **Each module:** a POM declaring exactly the dependencies of its row in `modules.md` (no more), its root package from ADR-0037 with a `package-info.java` describing its responsibility, the property `radial.base-package` set to that root package, and an empty test source folder.
3. **Build configuration:** JUnit 5; JaCoCo; Javadoc with doclint `all` failing on warnings; sources and Javadoc JARs; compiler release for shared modules as verified by `OQ-147` (Java 21 bytecode is the proven Android path; desktop-only modules may use 25).
4. **Android build** (`android/`, Gradle, ADR-0012): consumes the Maven artifacts of the shared modules, a verification module that really runs D8 on them, core library desugaring enabled. The batch records the results in [`../android-compatibility.md`](../android-compatibility.md) and closes `OQ-147`.
5. **Checks run in CI** (GitHub Actions, adapted from `master` `ci-cd.yml`):
   - Maven build and tests on JDK 25, Linux (blocking); Windows and macOS (non-blocking) (`REQ-SUPPORT-001`);
   - `radial-encapsulation-maven-plugin` with `maxViolations` 0 in every module;
   - a module-dependency check: each module's declared dependencies are within its row of `modules.md`;
   - `python3 -I scripts/check-docs.py` (already present);
   - the API index check and the asset provenance check (ADR-0032), able to run on an empty index;
   - the Android/DEX job.
6. **Documentation updated by the batch:** the exact build, test and single-module commands in `README.md` (Getting started) and `AGENTS.md` section 7; the versions used in [`../dependencies.md`](../dependencies.md); `OQ-147` and `OQ-149` marked resolved with links.

## Lifecycle and concurrency

Not applicable.

## Units and Android/DEX compatibility

The Android job is part of the batch: the skeleton is not done until shared modules are proven to dex.

## Dependencies

Only build and test tools listed in `dependencies.md`; each new one is recorded there first.

## Tests

- The full CI passes on the empty modules.
- A deliberately wrong dependency (a sibling package inside a module, an undeclared module dependency, a `java.desktop` use in a shared module seen by the Android job) fails the corresponding check; the batch shows each failure once in its pull request and then removes the probe.

## Acceptance criteria

- [ ] Every module listed above builds, with its root package and `package-info.java`.
- [ ] Every check of item 5 runs in CI and fails on a probe violation.
- [ ] Commands recorded in `README.md` and `AGENTS.md`; `OQ-147` and `OQ-149` resolved.
