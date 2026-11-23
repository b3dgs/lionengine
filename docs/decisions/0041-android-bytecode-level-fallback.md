# ADR-0041 — Bytecode level of shared modules: Java 25, then 21, then 17

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner (answer of 2026-11-23 in the architecture-review session); written by the agent
- Related requirements: `REQ-TECH-001`, `REQ-TECH-006`, `REQ-TECH-007`
- Open questions resolved: `OQ-147`
- Supersedes / superseded by: none; completes [ADR-0001](0001-technical-baseline-and-conventions.md) and [ADR-0012](0012-platform-and-build-baseline.md) (both unchanged)

## Context

ADR-0001 sets Java 25 as the development baseline under Android/DEX constraints, and ADR-0012 sets minimum API 21 with D8 through a separate Gradle module and core library desugaring. Whether D8 accepts Java 25 class files at API 21 is not verified (`OQ-147`). The proven path in `master` is Java 21 bytecode; the owner also reports a working Android build with JDK 17.

The open question mixed a choice (which level to use if Java 25 fails) with work (running the verification). Only the choice needs a decision; the work belongs to the build-skeleton batch.

## Decision

1. **Target and fallback order.** Shared modules (every module that is not desktop-only or Android-only) target Java 25 bytecode (`--release 25`). If the Android verification job rejects it, they use `--release 21`; if that is also rejected, `--release 17`. The first level in that order that passes the job is **the shared level**. The build is always run with JDK 25.
2. **When it is fixed.** The build-skeleton batch runs the job in this order, sets the shared level in the build, and records it, with the D8/R8 version, the Android Gradle Plugin version and the JDK of the job, in [`../android-compatibility.md`](../android-compatibility.md). The skeleton comes before any engine code, so no shared code is written before the level is known.
3. **What shared code may use.** Only language features and JDK APIs of the shared level, further limited by API 21 and desugaring as listed in `android-compatibility.md` (ADR-0037 rules unchanged). Desktop-only modules (`platform-awt`, `render-awt`, `input-awt`, `audio-javasound`, `graphics-imageio`) may use Java 25.
4. **Minimum API.** API 21 stays. Raising it is not part of the fallback; it would need a new ADR.
5. **Re-check.** The Android job runs on every build, so a later change of toolchain that accepts a higher level can raise the shared level through a pull request that updates `android-compatibility.md`.

## Options considered

- Fallback chain 25 → 21 → 17 (chosen by the owner): uses the newest level that works, with a known working floor.
- Fixed Java 21 (proven in `master`): certain, but gives up Java 25 bytecode even if D8 accepts it.
- Keep `OQ-147` open until verified: blocks the specification freeze for work, not for a choice.

## Consequences

- Positive consequences: no open question for Phase 0; the skeleton batch has a mechanical rule.
- Costs / constraints: until the skeleton runs, the exact shared level is unknown; feature pages and examples must not rely on language features newer than Java 17 being available in shared code; each per-item Android status in `android-compatibility.md` stays `not compatible yet` until the job checks it.
- Impact on modules and APIs: none on contracts; compiler setting of shared modules.
- Impact on tests and documentation (including Android/DEX compatibility): `android-compatibility.md`, `features/build-skeleton.md`, `open-questions.md`, `traceability.md` and the pages that referred to `OQ-147` updated.

## Validation

Owner answer of 2026-11-23 ("Java 25, fall back to 21, then 17 if still fail; I already made it work with JDK 17").
