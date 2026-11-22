# Android/DEX compatibility checklist

**Status: PROPOSED — operational procedure applying the VALIDATED requirements REQ-TECH-006 and REQ-TECH-007 (ADR-0001). Versions are `TO BE DEFINED` (OQ-001, OQ-002); do not assume them.**

Java compilation alone is not proof of DEX compatibility. Verification is continuous, not a final step.

## Before using a Java language feature, a Java API or a dependency in shared code

1. Identify where it will be used: shared code (must be Android-compatible) or a platform-specific module (isolated, may use platform APIs).
2. Check that the targeted toolchain supports it: language level accepted by the Android build, D8/R8 handling of the construct, availability of the API at the minimum Android API level (OQ-001).
3. If the API is only available through *core library desugaring*: confirm it is supported, record the configuration, and cover it with a verification build and tests on the targeted Android versions.
4. For a third-party dependency: confirm it contains no unsupported APIs or native code, and record its license (OQ-006).
5. Record the outcome in the pull request (or feature file): `compatible`, `compatible with desugaring`, `platform module only`, or `rejected`. If unable to verify, the answer is **not compatible yet**, never "probably fine".

## Pipeline requirements

- A CI job builds the Android-targeted modules through D8/R8 and produces the expected DEX artifacts (REQ-TECH-006, REQ-TECH-007).
- A verification build covers APIs requiring desugaring.
- Tests run on the targeted Android versions (devices or emulators); these are platform tests and are kept separate from the headless unit tests.
- Pipeline design with a Maven-based build: **TO BE DEFINED** (OQ-002).

## What to record once decided

Minimum Android API level, Android Gradle Plugin version (if used), D8/R8 version, desugaring library and its version, list of Java language features and APIs verified. Put them in an ADR and reference it here.
