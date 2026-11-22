# Architecture overview

**Status: PROPOSED** (sections 6 and 7 restate VALIDATED decisions from ADR-0001)

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
   game loop        objects / traits    sprites / audio
        |                  |                  |
        +------------------+------------------+
                           |
                    Core contracts
                           |
       +-------------------+-------------------+
       |                   |                   |
  AWT frontend        OpenGL frontend     Android frontend
  Input / Display     Render / Input      View / Input / Audio
       |                   |                   |
       +------------ Backends -----------------+
                           |
                 OS / native libraries
```

The diagram is conceptual: modules are named `lionengine-XXX` under group `com.b3dgs.lionengine` (ADR-0012); the exact module list and the allowed dependencies are set at the build skeleton (OQ-149). The Android toolchain is not Maven-native: DEX is produced by a separate Gradle module consuming the Maven artifacts (ADR-0012).

## 2. Boundaries

### Core

Contains the contracts and the platform-independent logic: time, lifecycle, objects, scenes, abstract resources, animation, coordinates, events and orchestration.

### Adapters

Implement the contracts for a given environment: window, rendering surface, keyboard/mouse/gamepad events, audio decoding or Android asset access.

### Game application

Configures the engine, selects the implementations, declares the resources and defines the game-specific rules. It must not need to know the internal details of the backends.

## 3. Proposed dependency rules

- `core` depends on no frontend.
- An adapter depends on the core contracts, never the reverse.
- Business modules do not depend directly on each other without contractual need.
- Optional integrations must not be required to use the foundation.
- Transitive dependencies of a frontend must not leak into the public APIs of the core.
- Dependency cycles between modules are forbidden.
- These rules are verified automatically by the build (REQ-CORE-005, OQ-005).

## 4. Several implementations

An abstraction does not imply a universal implementation. For example:
- a real-time loop for an interactive application;
- a manually driven loop for tests;
- an AWT rendering backend;
- an OpenGL backend;
- an Android adapter.

Each implementation must respect the same contract or explicitly declare its capabilities/limits.

## 5. Capabilities and compatibility

**PROPOSED:** platforms may announce their capabilities (gamepad, audio, acceleration, shaders, etc.). The engine must not assume that a capability exists everywhere.

The form of this mechanism (interface, registry, configuration) is **TO BE DEFINED** (OQ-034).

**ACCEPTED (ADR-0002):** screen modes, display-mode selection and output filtering follow this capability model: best-effort requests, with the effective result reported. They are presentation concerns: the logical resolution is decoupled from the output resolution and nothing here changes world units or simulation state.

## 6. Physical and temporal conventions — VALIDATED

- The world is two-dimensional, with two coordinates: `X` and `Y`.
- World coordinates and distances are expressed in **meters**.
- Time, durations and time deltas are expressed in **seconds**.
- Speeds are expressed in **meters per second (`m/s`)**; accelerations in `m/s²`.
- Conversions to pixels are a responsibility of the presentation layer (camera/viewport/rendering, see [ADR-0010](../decisions/0010-camera-system-in-camera.md)), never a change to the world units.
- The world origin is `(0, 0)` with Y up; the screen has its origin at the top left with Y down; angles are in degrees (ADR-0013). The meters-to-pixels conversion (OQ-012), the numeric type (OQ-013) and the rotation direction (OQ-150) remain to be decided.

APIs must make units explicit in their Javadoc. Do not implicitly mix meters and pixels, or seconds and milliseconds. Parameter and method names should avoid ambiguous units (`deltaSeconds`, `speedMetersPerSecond`, etc.) where this improves readability.

## 7. Default game loop model — VALIDATED in principle

The default loop must be **independent of machine performance** for the simulation. It combines:
- **frameskipping**: rendering may be skipped when the machine cannot keep up with the presentation cadence;
- **extrapolation**: the display may anticipate the visual state between two known simulation states.

Simulation and presentation must be separated. Extrapolation is a presentation operation: it must never feed its result back into the simulation state. An extrapolation greater than `1` therefore must not degrade calculation accuracy or cause cumulative drift. Gameplay calculations must remain based on the reference simulation time.

**Important:** "extrapolation greater than 1" here means an extrapolation factor greater than the normalized interval between two reference states. The exact mathematical definition of this factor, its bounds and the behavior under extreme lag must be specified before implementation (OQ-020 to OQ-022).

## 8. Open architectural questions

All open architectural questions (origin of the coordinate system, angles, meters/pixels, simulation step, extrapolation, threading, loading, resource ownership, scene/world/viewport, ECS compatibility, network determinism, Android build strategy, module layout, rendering API style) are tracked in [`../open-questions.md`](../open-questions.md).
