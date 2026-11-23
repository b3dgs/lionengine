# ADR-0044 — Shared modules at Java 21 and the API 21 constraints observed on an emulator

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner (test result and constraints supplied on 2026-11-23); written by the agent
- Related requirements: `REQ-TECH-001`, `REQ-TECH-006`, `REQ-TECH-007`
- Open questions resolved: none (replaces the answer given to `OQ-147` by ADR-0041 with a tested one)
- Supersedes / superseded by: supersedes [ADR-0041](0041-android-bytecode-level-fallback.md) (its fallback chain 25 → 21 → 17); completes [ADR-0001](0001-technical-baseline-and-conventions.md) and [ADR-0012](0012-platform-and-build-baseline.md) (unchanged)

## Context

ADR-0041 planned to try Java 25 bytecode for shared modules, then 21, then 17, and to find the level at the build skeleton. The owner has since built a library at Java 21 source and target level and run it on an API 21 emulator with Android Gradle Plugin 9.x, D8 and `coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")`. The result is a tested list of what fails and what works. Only a debug build was tested (no R8 shrinking).

## Decision

1. **Shared level.** Shared modules (every module that is not desktop-only or Android-only) are compiled with `--release 21` (`sourceCompatibility` and `targetCompatibility` 21), built with JDK 25. Java 22 to 25 language features and class files are not used in shared code. Desktop-only modules (`platform-awt`, `render-awt`, `input-awt`, `audio-javasound`, `graphics-imageio`) may use Java 25, as before. Minimum API stays 21; desugaring library `desugar_jdk_libs` 2.1.5 or newer. A change of level needs a new ADR.
2. **Forbidden in shared code** (observed failures at API 21):

   | Do not use | Failure | Use instead |
   |---|---|---|
   | `case E.E1 ->` (qualified enum constant) in a pattern `switch` over a sealed interface | build fails (D8) | `case E e when e == E.E1`, or an `if` chain |
   | Virtual threads (`Thread.ofVirtual()`, ...) | build fails (not in `android.jar`) | a regular executor |
   | `String.lines()` | `NoSuchMethodError` at run time | `split("\\R")` |
   | `java.util.HexFormat` | `NoClassDefFoundError` at run time | `String.format("%02x", ...)` or a manual loop |
   | `List.getFirst()`, `getLast()`, `reversed()` (sequenced collections) | `NoSuchMethodError` at run time | `get(0)`, `get(size() - 1)`, `Collections.reverse` on a copy |

   Also avoided as a precaution (same family, not tested): `addFirst`, `addLast`, `removeFirst`, `removeLast` on `List`, `Deque` and `LinkedHashSet`, and the sequenced methods of `LinkedHashMap` and `SortedSet`.
3. **Verified at run time on API 21:** lambdas, `var`, nestmates, switch expressions, text blocks, records, sealed classes; pattern matching for `switch` on types, records, guards and `null`; `List.of`, `Map.of`, `Set.of`, `Stream.toList()`, `Math.clamp`. `String.isBlank()`, `strip()` and `repeat()` are probably fine but were not tested alone. The rules of ADR-0037 are unchanged (no `java.util.function`, `Optional`, streams or `java.time` in shared public APIs or hot paths), so verified conveniences such as `Stream.toList()` stay out of those places.
4. **Anything else newer than API 21 is not verified** (for example `Files.readString`, `String.formatted`, `Collectors.teeing`) and stays `not compatible yet` until checked (`android-compatibility.md`, procedure unchanged).
5. **Enforcement.** The Android build enables the lint check `NewApi` as an error (for example `lint { error += "NewApi" }`; the owner did not test this with AGP 9.5, so the skeleton batch verifies the setting is accepted) and fixes every violation instead of suppressing it. The Android job runs the shared modules' platform tests on an API 21 emulator, because a successful build does not prove run-time behavior: `NoSuchMethodError` appears only when the code runs.
6. **Not yet verified:** release builds with R8, and Java 25 and 17 bytecode (no longer needed).

## Options considered

- Java 21 fixed (chosen, tested on the owner's emulator): known to work, with a tested list of pitfalls.
- ADR-0041 chain starting at Java 25: unverified, and Java 22 to 25 features add D8 risk for little gain in shared code.
- Java 17: lower, but gives up records-with-patterns and `switch` pattern matching that are verified at 21.

## Consequences

- Positive consequences: the shared level is known before any engine code; the pitfalls are listed with their alternatives; the `Math.clamp` and records rows of the working list become verified.
- Costs / constraints: shared code uses no Java 22 to 25 feature; the five forbidden constructs are banned; an API 21 emulator job and a lint rule are added to the skeleton; release (R8) builds remain unverified.
- Impact on modules and APIs: none on contracts; compiler setting of shared modules.
- Impact on tests and documentation (including Android/DEX compatibility): `android-compatibility.md`, `conventions.md`, `features/build-skeleton.md`, `testing.md`, `roadmap.md`, `open-questions.md`, `traceability.md` and the pages that cite ADR-0041 updated.

## Validation

Owner test on an API 21 emulator with a Java 21 build, and owner instruction of 2026-11-23 to apply the resulting rules.
