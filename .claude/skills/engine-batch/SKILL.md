---
name: engine-batch
description: Implement, build and verify a change in the LionEngine v11 repository - a feature batch of docs/features/, a Java change in a lionengine-* module, an engine example, or the pull request that carries it. Gives the fast build loop, the quality-gate pitfalls met in earlier batches and the pre-push sequence. Not for games written with the engine (they follow docs/templates/GAME-AGENTS-TEMPLATE.md).
---

# Engine batch: fast loop and pre-push sequence

This skill only orders the work and records what saved time in earlier batches. The rules live in
[`AGENTS.md`](../../../AGENTS.md) and the documents it links; this file never restates them. If this file and
a document disagree, the document wins: fix this file in the same pull request.

Read first, always: [`AGENTS.md`](../../../AGENTS.md), the batch's page in `docs/features/`, the ADRs it names,
[`docs/implementation-guide.md`](../../../docs/implementation-guide.md) (commands, checkstyle limits, Sonar rules,
Android constraints, definition of done) and [`docs/testing.md`](../../../docs/testing.md).

## 1. Session setup (cloud container)

1. `export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64`.
2. If `~/.m2/settings.xml` has no proxy, write one from `HTTPS_PROXY` (its port changes between sessions, even
   after a restart in one session):

   ```bash
   p=${HTTPS_PROXY##*:}; p=${p%%/*}; mkdir -p ~/.m2; cat > ~/.m2/settings.xml <<EOF
   <settings><proxies>
   <proxy><id>https</id><active>true</active><protocol>https</protocol><host>127.0.0.1</host><port>$p</port></proxy>
   <proxy><id>http</id><active>true</active><protocol>http</protocol><host>127.0.0.1</host><port>$p</port></proxy>
   </proxies></settings>
   EOF
   ```

   A 429 from Maven Central is transient: retry after a minute.
3. Install the reactor once without tests, so that `-pl` builds of one module resolve their siblings:
   `mvn -B -q -T 1C install -DskipTests`. This is a cache warm-up only, never a check (see section 3).

## 2. Before writing code

- List the points the documents leave open (AGENTS.md section 3) and ask them in **one** question round, each
  with the recommended option first. Record the answers and every detail left to the batch in the batch's new ADR.
- Read the closest existing classes and copy their shape (constants, constructors, Javadoc layout, tests).
- Design for the checkstyle limits of implementation-guide section 3 from the start; the ones hit most often:
  an anonymous class over 20 lines (move its body to a private method of the outer class), cyclomatic
  complexity over 10 (split into private helpers), more than 7 instantiated types in a class (data abstraction
  coupling: drop a convenience type such as a config object), more than 3 boolean operators in one expression
  (a small private predicate).

## 3. Inner loop

Measured in a 4-processor cloud container (2026-11-28):

| Goal | Command | Time |
|---|---|---|
| Install everything, no tests (warm-up) | `mvn -B -q -T 1C install -DskipTests` | 14 s (22 s without `-T 1C`) |
| One module, one test class, offline | `mvn -B -q -o -pl lionengine-<module> verify -Dtest='<Class>*' -Dsurefire.failIfNoSpecifiedTests=false` | 16 s |
| Full gate, parallel modules (while iterating) | `mvn -B -T 1C verify` | 47 s |
| Full gate, as documented (before the push) | `mvn -B verify` | 74 s |

- `-T 1C` passed the whole reactor once, but the CI and the documents run the build serially: use it to
  iterate, and run the documented serial `mvn -B verify` before the push. A failure seen only with `-T 1C` is
  investigated, not ignored.
- `-o` and `-Dtest` are for the loop only. The final check is the full gate of section 4, with no skip flag
  (implementation-guide section 2). Drop `-o` when a plugin artifact is missing.
- A module that depends on a changed sibling needs it installed first: `mvn -B -q install -DskipTests -pl
  lionengine-<sibling>`.
- Read failures from files, not from the console: checkstyle in `target/checkstyle-result.xml`
  (`grep "<error" lionengine-<module>/target/checkstyle-result.xml`), tests in
  `target/surefire-reports/TEST-*.xml`. With `-q`, a successful run prints nothing.
- To find exact step numbers or values for an example or a test, write a temporary probe test that dumps the
  state per step into the scratchpad, read it, then delete the probe before committing.
- Device tests under `android/` cannot run without an Android SDK. Check their logic on the JVM with a temporary
  JUnit 5 copy in the module's test sources, run it, delete it, and say so in the pull request.

## 4. Pre-push sequence

Run in this order; each step must pass before the next. Push once, when everything passes.

1. `mvn -B verify` on the whole reactor.
2. `python3 -I scripts/check-modules.py`, `check-api-index.py`, `check-provenance.py`, `check-docs.py`.
3. Coverage of the new classes (at least 80 %): the build writes only `jacoco.xml`; for the CSV run
   `mvn -B -q org.jacoco:jacoco-maven-plugin:report -pl lionengine-<module>` after the tests ran, then read
   `lionengine-<module>/target/site/jacoco/jacoco.csv`.
4. `python3 -I scripts/check-sonar.py`: it takes several minutes, so run it in the background and keep working.
   Stage the changes first (`git add -A`). It may report issues in files the batch did not touch: fix them too,
   then run it again until it reports `No issue beyond those of rewrite/v11`.
5. Commit with the attribution lines of the session; reference the `REQ-*` and `ADR-NNNN` identifiers.

Sonar rules missed most often in earlier batches, beyond the table of implementation-guide section 5:
floating-point equality (S1244: `Double.compare(a, b) == 0`), a lambda with more than one throwing call inside
`assertThrows` (S5778), `throws` clauses a method cannot throw (S1130).

## 5. Documentation

AGENTS.md section 7 and implementation-guide section 9 list what a batch updates. To find the stale mentions,
grep before editing, for example `grep -rn -i "batch <n>\|next batch\|<old value>" docs` and the placeholder
row of `docs/api-index.md`. Run `python3 -I scripts/check-docs.py` after the last edit.

## 6. Pull request and merge

- Branch from `origin/rewrite/v11`, open the pull request against `rewrite/v11`, ready for review, with the
  headings of `PULL_REQUEST_TEMPLATE.md`; then subscribe to its activity.
- The auto-merge workflow merges it once its checks, the `linux` job (build, tests and Sonar quality gate) and
  the `android` job pass (`.github/workflows/auto-merge-agent-prs.yml`); merge by hand only if it cannot. A
  pull request of documentation only (`docs/`, root Markdown files) runs no `build.yml`.
- After the merge, check that the head branch is gone and that the `build` run of `rewrite/v11` for the merge
  commit is green with no cancelled job (it runs `linux` and `benchmark` only; `desktop` and `android` ran on the
  pull request). A Maven Central 429 in a setup step is not a code failure: re-run the failed jobs once. A branch ruleset can refuse the deletion (HTTP 422 "Cannot delete this
  branch" in the merge job log, a warning only): report it, do not work around it.
