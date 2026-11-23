# Feature: build skeleton

**Status: VALIDATED — content of the skeleton; its exact commands and versions are recorded by the batch itself (ADR-0042).**

- Owner: project owner
- Roadmap phase: Phase 0, first batch of the project; done by the owner or a cloud agent, not a local agent (it fixes the commands every later batch uses)
- Affected modules: all modules of [`../architecture/modules.md`](../architecture/modules.md) needed by Phases 1 to 3, `android/` Gradle build
- Requirements: `REQ-TECH-001`, `REQ-TECH-002`, `REQ-TECH-006`, `REQ-TECH-007`, `REQ-CORE-003`, `REQ-CORE-005`, `REQ-DOC-001`, `REQ-DOC-004` to `REQ-DOC-007`, `REQ-SUPPORT-001`
- Applicable ADRs: [`ADR-0012`](../decisions/0012-platform-and-build-baseline.md), [`ADR-0015`](../decisions/0015-single-version-and-package-dependency-rule.md), [`ADR-0023`](../decisions/0023-game-repository-change-requests-assets-and-api-index-check.md), [`ADR-0024`](../decisions/0024-determinism-performance-budgets-and-desktop-support.md), [`ADR-0032`](../decisions/0032-definition-files-registry-ids-resource-providers-dependency-policy.md), [`ADR-0037`](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md), [`ADR-0045`](../decisions/0045-test-fixtures-with-their-contracts-and-test-support-kit.md), [`ADR-0048`](../decisions/0048-composition-dependencies-and-skeleton-declaration.md), [`ADR-0049`](../decisions/0049-interface-catalog-and-contract-skeleton.md)
- Open questions involved: none (`OQ-147` resolved by [`ADR-0041`](../decisions/0041-android-bytecode-level-fallback.md) and [`ADR-0044`](../decisions/0044-java-21-shared-level-and-api-21-constraints.md), `OQ-149` by [`ADR-0042`](../decisions/0042-build-skeleton-closure-and-maven-central.md); this batch does the work they describe)

## Objective

A compilable, testable, empty multi-module project whose CI enforces every structural rule, so that each later batch only adds code.

## Out of scope

Any engine behavior; the first release itself (artifacts go to Maven Central, snapshots to the Sonatype Central snapshot repository, ADR-0042).

## Expected content

1. **Maven reactor.** The aggregator `pom.xml` (group `com.b3dgs.lionengine`, version `11.0.0-SNAPSHOT`, one version for all modules) declares one folder per module, `lionengine-<module>`, for: `core`, `runtime`, `platform-api`, `render-api`, `resources`, `resources-file`, `resources-archive`, `resources-memory`, `audio-api`, `graphics`, `tilemap`, `camera`, `input-api`, `ecs`, `physics`, `traits`, `composition` (ADR-0048), `test-support`, `examples`, and the desktop modules `platform-awt`, `render-awt`, `input-awt`, `audio-javasound`, `graphics-imageio`. Other modules are declared by their first batch: `audio-codec-ogg` with its dependency entry (ADR-0033), the Phase 4 and 5 modules.
2. **Each module:** a POM declaring exactly the dependencies of its row in `modules.md` (no more), its root package from ADR-0037 with a `package-info.java` describing its responsibility, the property `radial.base-package` set to that root package, and an empty test source folder published as the module's test JAR (ADR-0045).
3. **Build configuration:** JUnit 5; JaCoCo; Javadoc with doclint `all` failing on warnings; sources and Javadoc JARs; compiler release for shared modules `--release 21` (ADR-0044); desktop-only modules use 25.
4. **Android build** (`android/`, Gradle, ADR-0012): consumes the Maven artifacts of the shared modules, a verification module that really runs D8 on them, core library desugaring enabled. Lint `NewApi` is an error (the batch verifies the setting is accepted by the Android Gradle Plugin version used), and the shared modules' platform tests run on an API 21 emulator (ADR-0044). The batch records the D8/R8, Android Gradle Plugin, JDK and emulator image versions and the first R8 release-build result in [`../android-compatibility.md`](../android-compatibility.md).
5. **Checks run in CI** (GitHub Actions, adapted from `master` `ci-cd.yml`):
   - Maven build and tests on JDK 25, Linux (blocking); Windows and macOS (non-blocking) (`REQ-SUPPORT-001`);
   - `radial-encapsulation-maven-plugin` with `maxViolations` 0 in every module;
   - a module-dependency check: each module's declared dependencies are within its row of `modules.md`; test-scope dependencies only on the test JARs of modules it already depends on, and on `test-support` when `test-support` does not depend on it (ADR-0045);
   - `python3 -I scripts/check-docs.py` (already present);
   - the API index check and the asset provenance check (ADR-0032), able to run on an empty index;
   - the Android/DEX job.
6. **Documentation updated by the batch:** the exact build, test and single-module commands in `README.md` (Getting started) and `AGENTS.md` section 7; the versions used in [`../dependencies.md`](../dependencies.md); the versions of the Android job in `android-compatibility.md`.

## Lifecycle and concurrency

Not applicable.

## Units and Android/DEX compatibility

The Android job is part of the batch: the skeleton is not done until shared modules are proven to dex.

## Dependencies

Only build and test tools listed in `dependencies.md`; each new one is recorded there first.

## Tests

- The full CI passes on the empty modules.
- A deliberately wrong dependency (a sibling package inside a module, an undeclared module dependency, a test-scope dependency against the direction of ADR-0045, a `java.desktop` use in a shared module seen by the Android job) fails the corresponding check; the batch shows each failure once in its pull request and then removes the probe.
- A probe test using a fixture from another module's test JAR passes with the recorded build and test commands (ADR-0045).

## Acceptance criteria

- [ ] Every module listed above builds, with its root package and `package-info.java`.
- [ ] Every check of item 5 runs in CI and fails on a probe violation.
- [ ] Commands recorded in `README.md` and `AGENTS.md`; Android job versions and lint `NewApi` result recorded in `android-compatibility.md` (ADR-0044).

Next batch: [`contract-skeleton.md`](contract-skeleton.md) declares the cross-module contracts in these modules (ADR-0049).
