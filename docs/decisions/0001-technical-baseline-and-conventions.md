# ADR-0001 — Technical baseline and world conventions

- Status: ACCEPTED
- Date: 2026-11-21
- Decision makers: project owner
- Related requirements: `REQ-TECH-001` to `REQ-TECH-008`, `REQ-LOOP-004` to `REQ-LOOP-006`

> Editorial note: translated to English and related requirements completed (`REQ-TECH-006` to `REQ-TECH-008` were missing). The decision text itself is unchanged.

## Context

The engine needs a stable technical baseline and common conventions so that modules, AI agents and games built on the engine use the same assumptions.

## Decision

### Java and build

- The engine targets **Java 25 or higher**.
- The build system is **Maven 3 or higher**.
- The code must remain compatible with an Android compilation chain producing **DEX**.
- Java 25 defines the development version, but does not guarantee that every language feature, every Java API or every library is usable on Android.
- Every Java feature, standard API or dependency must be checked against the targeted Android compatibility. D8/R8 mechanisms and *core library desugaring* may only be used when their support is established.
- Code shared between platforms should favor the Java subset and the APIs actually available on the selected Android targets. Platform-specific features must remain isolated in the modules concerned.

### Coordinates and units

- The world is 2D and uses `X` and `Y` coordinates.
- World distances and positions are expressed in **meters**.
- Time and durations are expressed in **seconds**.
- Speeds are expressed in **meters per second (`m/s`)**; accelerations in `m/s²`.
- Conversion to pixels belongs to the presentation layer.

The origin of the coordinate system, the positive direction of Y, angles and the details of screen conversion remain to be defined in later decisions.

### Default game loop

- The default game loop must be independent of machine performance for simulation calculations.
- It combines frameskipping and extrapolation to adapt presentation to the available capabilities.
- Frameskipping may drop a presentation, but must not arbitrarily drop simulation calculations.
- Extrapolation is intended for presentation only.
- An extrapolation greater than `1` must not reduce the accuracy of simulation calculations.
- The extrapolated state must never be reused as the input state of the next simulation step.

The formula, the precise mathematical meaning of the extrapolation factor, the simulation step and the catch-up bounds remain to be specified.

## Consequences

- World modules must use the physical units defined here.
- APIs must document units and avoid implicit conversions.
- Simulation tests must be able to control time and verify states without depending on the real cadence of a machine.
- Rendering may present a predicted state without altering the gameplay state.
- Android compatibility must be verified continuously, not postponed to the end of development. The minimum Android version, the Android Gradle Plugin version, the D8/R8 version and the *desugaring* options remain to be defined. Java compilation alone is not proof of DEX compatibility.

## Validation

Decision explicitly provided by the project owner on 2026-11-21.
