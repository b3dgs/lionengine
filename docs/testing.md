# Test strategy

**Status: PROPOSED**

## Levels

1. **Unit**: isolated, deterministic logic, no platform.
2. **Contract**: the same suite executed on every implementation of an abstraction.
3. **Integration**: interactions between modules and providers.
4. **Platform**: tests with a real frontend, OS, codec or hardware.
5. **Performance/stability**: reproducible measurements, stress, leaks and duration.
6. **Android/DEX compatibility**: DEX transformation of the relevant artifacts, verification of available APIs and tests on the targeted Android versions (see [`android-compatibility.md`](android-compatibility.md)).

## Rules

- Every `REQ-*` requirement must be linked to one or more tests, or explicitly declared as not automatically testable (see [`traceability.md`](traceability.md)).
- A fixed bug receives a regression test.
- Prefer a simulated clock to real waiting when testing the game loop (REQ-TEST-001).
- Use temporary/fake resources for resource tests.
- Test errors and closing, not only the nominal path.
- Headless tests are the reference for engine logic.
- A successful Java build does not validate Android compatibility: the Android pipeline must run D8/R8 and produce the expected DEX artifacts.
- APIs requiring *core library desugaring* must be covered by a verification build and tests on the targeted Android versions.
- Tests depending on a device or graphical environment are identified as such and must not make unit tests fragile.
- Tests must be deterministic: no dependency on wall-clock time, real sleeping, execution order or random seeds that are not fixed.

## Envisaged test matrix

| Domain | Unit tests | Contract | Integration / platform |
|---|---|---|---|
| Game loop | clock, time step, pause | each implementation | frontend lifecycle |
| Resources | resolution, errors, streams | each provider | archive/Android/filesystem |
| Audio | playback state, commands | each backend | codec and device |
| Sprites | slicing, animation, mutation | data API | upload and rendering |
| Tilemap | coordinates, layers, editing | map model | map rendering |
| Objects | lifecycle, order | management strategy | complete scene |
| Traits | isolated behavior, interactions | trait contract if defined | object composition |
| Input | mapping, states, events | normalized adapter | keyboard/mouse/gamepad |
| Multiplayer | player/session logic | to be defined | split-screen then network |
| Java/Android compatibility | check of the APIs used | — | D8/R8 compilation, DEX and device/emulator tests |
| Architecture rules | — | — | forbidden-dependency and cycle check (REQ-CORE-005) |

## Feature acceptance criteria

- requirements covered;
- relevant automated tests added;
- existing tests still green;
- no forbidden coupling introduced;
- public API documented;
- errors and limits described;
- minimal example or demonstrator where it helps validation;
- traceability matrix updated.

## Test tooling

Framework, version and build configuration: **TO BE DEFINED** (OQ-004). JUnit 5 is a candidate option, to be confirmed with the choice of build and Java version.
