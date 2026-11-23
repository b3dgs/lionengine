# Architecture overview

**Status: VALIDATED — principles and boundaries (ADR-0001 to ADR-0043). Sections 6 and 7 restate ADR-0001 as refined by later ADRs. A plain-language version with diagrams: [`../how-the-engine-works.md`](../how-the-engine-works.md).**

## 1. General direction

Layered architecture with dependency inversion: the core defines the contracts; adapters provide the platform- and library-specific implementations.

```text
                 Game / client application
                           |
                    Public engine API
                           |
        +------------------+------------------+
        |                  |                  |
     Runtime            World              Media
  loop, scenes      ecs, traits,      graphics, audio,
  inbox, threads    physics, camera,  tilemap rendering
                    tile collision
        |                  |                  |
        +------------------+------------------+
                           |
                 core + *-api contracts
                           |
       +-------------------+-------------------+
       |                   |                   |
  AWT frontend        OpenGL renderer     Android frontend
  platform-awt,       render-opengl       platform-android,
  render-awt,                             audio-android,
  input-awt                               input-android
       |                   |                   |
       +------------ Backends -----------------+
                           |
                 OS / native libraries
```

Modules are named `lionengine-XXX` under group `com.b3dgs.lionengine` (ADR-0012); the module list and allowed dependencies are in [`modules.md`](modules.md); their physical declaration is done at the build skeleton (ADR-0042). DEX is produced by a separate Gradle module consuming the Maven artifacts (ADR-0012).

## 2. Boundaries

### Core

Contains the contracts and the platform-independent logic: time, lifecycle, objects, scenes, abstract resources, animation, coordinates, events, definition files and orchestration.

### Adapters

Implement the contracts for a given environment: window, rendering surface, keyboard/mouse/gamepad events, audio output and decoding, Android asset access.

### Game application

Configures the engine, selects and registers the implementations explicitly (ADR-0020), declares the resources and defines the game-specific rules. It never needs to know the internal details of the backends.

## 3. Dependency rules

- `core` depends on no frontend.
- An adapter depends on the core contracts, never the reverse.
- Business modules do not depend directly on each other without contractual need (`ecs` and `tilemap` meet only through `physics` and the scene, ADR-0021).
- Optional modules (`composition`, `network`, `multiplayer`) are never required to use the foundation.
- Transitive dependencies of a frontend never leak into the public APIs of the core.
- Dependency cycles between modules are forbidden; a package never depends on a descendant package (ADR-0015).
- These rules are verified automatically by the build (REQ-CORE-005, ADR-0012).

## 4. Several implementations

An abstraction does not imply a universal implementation. For example:
- a real-time loop for an interactive application;
- a manually driven loop for tests;
- an AWT rendering backend;
- an OpenGL backend;
- an Android adapter.

Each implementation respects the same contract and passes the same contract suite of `test-support`, or explicitly declares its capabilities and limits.

## 5. Capabilities and compatibility

- Platforms announce their capabilities (gamepad, audio, shaders, per-frame color effects, screen modes, ...). The engine never assumes that a capability exists everywhere.
- Lookup: a typed optional lookup on an explicit platform object, without class-name lookup or reflection (ADR-0020).
- Screen modes, display-mode selection and output filtering are best-effort requests with the effective result reported (ADR-0002, ADR-0006). They are presentation concerns: the logical resolution is decoupled from the output resolution and nothing here changes world units or simulation state.

## 6. Physical and temporal conventions

- The world is two-dimensional, with two coordinates: `X` and `Y`.
- World coordinates and distances are expressed in **meters**, as `double` (ADR-0014).
- Time, durations and time deltas are expressed in **seconds**.
- Speeds are expressed in **meters per second (`m/s`)**; accelerations in `m/s²`.
- The world origin is `(0, 0)` with Y up; the screen (logical frame) has its origin at the top left with Y down (ADR-0013).
- Angles are in degrees, counter-clockwise positive, `0°` along `+X`; absolute angles in `[0, 360)`, signed differences in `(-180, 180]` (ADR-0013, ADR-0014, ADR-0016).
- Conversion to pixels is done by the camera view transform only; there is no engine-wide pixels-per-meter constant; `float` appears only at the render boundary (ADR-0014, ADR-0029).

APIs make units explicit in their Javadoc. Never mix meters and pixels, or seconds and milliseconds, implicitly. Parameter and method names carry the unit where the type does not (`deltaSeconds`, `speedMetersPerSecond`).

## 7. Default game loop model

The default loop is **independent of machine performance** for the simulation (ADR-0001). It combines:
- **frameskipping**: rendering may be skipped when the machine cannot keep up, never a simulation step below the lag cap;
- **presentation between states**: interpolation by default, extrapolation opt-in.

Simulation and presentation are separated. A presented state is never fed back into the simulation, so an "extrapolation greater than 1" cannot degrade accuracy or cause drift.

Made precise by [ADR-0017](../decisions/0017-fixed-step-game-loop.md) and [ADR-0031](../decisions/0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md): fixed step (default 60 Hz) with a `long` nanosecond accumulator and catch-up; `alpha = accumulator / step` in `[0, 1)`; lag capped at 0.25 s by default, excess dropped and reported; vsync snapping; pause without catch-up; time scale in `[0.1, 4.0]`; fail-fast exceptions; stop at a frame boundary.

**Historical note (ADR-0001):** "extrapolation greater than 1" meant an extrapolation factor greater than the normalized interval between two reference states. With ADR-0017 that case no longer reaches the renderer.

## 8. Threads

One runtime thread owns the loop, the world and the caches; platform threads and workers only post to the inbox (ADR-0018, ADR-0031). The inbox, asynchronous handles, worker pool and loop-control contracts are in `core`, so producers never depend on `runtime` (ADR-0040). Network sockets are non-blocking and polled by the runtime thread during the session update, so networking starts no thread either (ADR-0039). Details: [`contracts.md`](contracts.md#threads-and-events-adr-0018-adr-0031).

## 9. Open architectural questions

None remain open (ADR-0041 to ADR-0043); new gaps go to [`../open-questions.md`](../open-questions.md).
