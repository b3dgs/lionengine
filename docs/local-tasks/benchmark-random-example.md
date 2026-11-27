# Task: benchmark, recreate RandomExample and its test

**Status: VALIDATED (owner, 2026-11-25) — benchmark task of `OQ-170` ([`../local-agents.md`](../local-agents.md), section 8). Its branch is never pushed or merged: it measures a model, it does not change the engine.**

- Slug: bench-random-example
- Model profile: PC1
- Requirements: REQ-DOC-001, REQ-DOC-002, REQ-DET-003
- Decisions: ADR-0053
- Remove before start: lionengine-examples/src/main/java/com/b3dgs/lionengine/examples/core/RandomExample.java, lionengine-examples/src/test/java/com/b3dgs/lionengine/examples/core/RandomExampleTest.java

Change the `Model profile` line to the machine that runs the benchmark (PC1 or PC2; PC3 runs the single-shot variant of section 8 of `local-agents.md`).

## Batch file
The row "Draw reproducible random numbers" of `docs/api-index.md`, the Javadoc of `RandomSource` in `lionengine-core`, and the structure of `DefinitionExample` and `DefinitionExampleTest` in `lionengine-examples` (same package).

## Files in scope
- lionengine-examples/src/main/java/com/b3dgs/lionengine/examples/core/RandomExample.java
- lionengine-examples/src/test/java/com/b3dgs/lionengine/examples/core/RandomExampleTest.java
Anything else is out of scope.

## Steps
1. Create `RandomExample`, a final class with a private constructor that throws `UnsupportedOperationException`, in package `com.b3dgs.lionengine.examples.core`, with the license header of the other examples and `@since 11.0.0`.
2. Give it the public constants `SEED = 2026L` and `DIE_FACES = 6`, and `public static int[] rollDice(long seed, int count)`: a `RandomSource` created with `seed`, `count` rolls each in `[1, DIE_FACES]` drawn with `nextInt(DIE_FACES)`.
3. Give it `main(String[] args)` that logs five rolls at level `INFO` with a `Log` instance (copy how `DefinitionExample` logs), with the checkstyle comment `// CHECKSTYLE IGNORE LINE: UncommentedMain` above it.
4. Create `RandomExampleTest` (JUnit Jupiter, package-private final class): one test that 100 rolls of the same seed are equal twice and each in `[1, 6]`, one test that `main` runs without exception.
5. Complete Javadoc on every member (description, `@param`, `@return`). Commit with a message that names REQ-DOC-001 and REQ-DET-003.

## Acceptance commands
- mvn -B -pl lionengine-examples -am verify
- python3 -I scripts/check-api-index.py

## Stop conditions
- a TO BE DEFINED point, a missing default value or a missing contract;
- a file outside the scope would have to change;
- 3 failed gate runs (ADR-0053; counted by `scripts/local-task.sh`);
- a method of `RandomSource` or `Log` that the code and the Javadoc do not show.
