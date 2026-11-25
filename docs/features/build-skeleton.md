# Feature: build skeleton

**Status: VALIDATED — content of the skeleton; its exact commands and versions are recorded by the batch itself (ADR-0042).**

- Owner: project owner
- Roadmap phase: Phase 0, first batch of the project; done by the owner or a cloud agent, not a local agent (it fixes the commands every later batch uses)
- Affected modules: all modules of [`../architecture/modules.md`](../architecture/modules.md) needed by Phases 1 to 3, `android/` Gradle build
- Requirements: `REQ-TECH-001`, `REQ-TECH-002`, `REQ-TECH-006`, `REQ-TECH-007`, `REQ-CORE-003`, `REQ-CORE-005`, `REQ-DOC-001`, `REQ-DOC-004` to `REQ-DOC-007`, `REQ-SUPPORT-001`
- Applicable ADRs: [`ADR-0012`](../decisions/0012-platform-and-build-baseline.md), [`ADR-0015`](../decisions/0015-single-version-and-package-dependency-rule.md), [`ADR-0023`](../decisions/0023-game-repository-change-requests-assets-and-api-index-check.md), [`ADR-0024`](../decisions/0024-determinism-performance-budgets-and-desktop-support.md), [`ADR-0032`](../decisions/0032-definition-files-registry-ids-resource-providers-dependency-policy.md), [`ADR-0037`](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md), [`ADR-0045`](../decisions/0045-test-fixtures-with-their-contracts-and-test-support-kit.md), [`ADR-0048`](../decisions/0048-composition-dependencies-and-skeleton-declaration.md), [`ADR-0049`](../decisions/0049-interface-catalog-and-contract-skeleton.md)
- Open questions involved: none open; `OQ-166` and `OQ-167`, raised by this batch, are resolved by [`ADR-0050`](../decisions/0050-test-framework-latest-stable-junit.md) and [`ADR-0051`](../decisions/0051-parent-pom-b3dgs-parent-3-snapshot.md); `OQ-147` resolved by [`ADR-0041`](../decisions/0041-android-bytecode-level-fallback.md) and [`ADR-0044`](../decisions/0044-java-21-shared-level-and-api-21-constraints.md), `OQ-149` by [`ADR-0042`](../decisions/0042-build-skeleton-closure-and-maven-central.md); this batch does the work they describe)

## Objective

A compilable, testable, empty multi-module project whose CI enforces every structural rule, so that each later batch only adds code.

## Out of scope

Any engine behavior; the first release itself (artifacts go to Maven Central, snapshots to the Sonatype Central snapshot repository, ADR-0042).

## Expected content

1. **Maven reactor.** The aggregator `pom.xml` (group `com.b3dgs.lionengine`, version `11.0.0-SNAPSHOT`, one version for all modules) declares one folder per module, `lionengine-<module>`, for: `core`, `runtime`, `platform-api`, `render-api`, `resources`, `resources-file`, `resources-archive`, `resources-memory`, `audio-api`, `graphics`, `tilemap`, `camera`, `input-api`, `ecs`, `physics`, `traits`, `composition` (ADR-0048), `test-support`, `examples`, and the desktop modules `platform-awt`, `render-awt`, `input-awt`, `audio-javasound`, `graphics-imageio`. Other modules are declared by their first batch: `audio-codec-ogg` with its dependency entry (ADR-0033), the Phase 4 and 5 modules.
2. **Each module:** a POM declaring exactly the dependencies of its row in `modules.md` (no more), its root package from ADR-0037 with a `package-info.java` describing its responsibility, the property `radial.base-package` set to that root package, and an empty test source folder published as the module's test JAR (ADR-0045).
3. **Build configuration:** JUnit 6, latest stable release (ADR-0050); JaCoCo; Javadoc with doclint `all` failing on warnings; sources and Javadoc JARs; compiler release for shared modules `--release 21` (ADR-0044); desktop-only modules use 25.
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

- [x] Every module listed above builds, with its root package and `package-info.java`.
- [x] Every check of item 5 runs in CI and fails on a probe violation: the Maven build, the package rule, checkstyle, doclint, the module-dependency check, the API index and the provenance checks, and the Android/DEX job (each probe shown once, then removed; the `java.desktop` probe in pull request #909, commit `60fd53c`, refused by the compilation of `android/api-lint`).
- [x] Commands recorded in `README.md` and `AGENTS.md`; Android job versions, lint `NewApi` result and first R8 release-build result recorded in `android-compatibility.md` (ADR-0044).

## State (2026-11-24)

Done: items 1 to 6; item 4 and the Android/DEX job of item 5 by the Android/DEX batch of 2026-11-25 (below).

Decisions of the batch:
- parent `b3dgs-parent` 3.0.0-SNAPSHOT, with the snapshot repository declared in the aggregator to resolve it (ADR-0051, resolving `OQ-167`; checkstyle 14.1.0);
- JUnit 6.1.3, the latest stable release, pinned in the aggregator (ADR-0050, resolving `OQ-166`);
- the parent's compile-scope `slf4j-api` restricted to tests (REQ-CORE-006) and `b3dgs-buildtools` to `provided`, so that neither ships;
- `-Xlint:all -Werror`; enforcer rule JDK 25 or later;
- a test JAR in every module (ADR-0045);
- `maven.javadoc.skip` in each module without a public type, removed by the batch that adds its first one (javadoc refuses an empty module).

Android/DEX batch (2026-11-25, pull request #909): the `android/` Gradle build and the `android` job of `build.yml`, described with their commands, versions and results in [`../android-compatibility.md`](../android-compatibility.md), section "Android verification job". Decisions of the batch:
- the shared modules are read from the aggregator `pom.xml`: every module without a `jdk.version` override, except `test-support`;
- lint `NewApi` runs on a library module (`api-lint`) compiling the shared sources, because lint does not analyze the bytecode of dependencies; D8 and R8 run on the Maven artifacts in an application module (`verification`), which checks the DEX content of its APKs;
- the instrumentation harness of the API 21 emulator tests is AndroidX Test with JUnit 4 (ADR-0050 leaves the choice to this batch; recorded in [`../dependencies.md`](../dependencies.md)); the device tests take the cases and expected values of the JVM tests;
- the Android Gradle Plugin stays at the 9.2.1 baseline of ADR-0012;
- the first lint run found `CharSequence.isEmpty()` (API 35) in `DisplayState`, replaced by a separator field, with a JVM regression test and a device test;
- the probes moved the unverified items of [`../implementation-guide.md`](../implementation-guide.md), section 6, to the verified or forbidden lists.

Next batch: [`contract-skeleton.md`](contract-skeleton.md) declares the cross-module contracts in these modules (ADR-0049).
