# ADR-0029 — Camera details: view transform, follow lookup, delay, shake, zoom and candidate features

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner ("Confirmed as proposed, write the ADR")
- Related requirements: `REQ-CAM-001` to `REQ-CAM-009`, `REQ-LOOP-004`
- Open questions resolved: `OQ-130`, `OQ-131`, `OQ-132`, `OQ-133`, `OQ-134`, `OQ-135`, `OQ-136`
- Supersedes / superseded by: none; completes [ADR-0010](0010-camera-system-in-camera.md) (unchanged)

## Context

ADR-0010 fixed the camera placement, world units, smoothing, seeded shake and the plain view output, and left seven details open. Since then ADR-0014 (scale factor), ADR-0017 (fixed step, `alpha`), ADR-0021 (views, scene composition) and ADR-0011 (object IDs) constrain them.

## Decision

1. **Shake (`OQ-130`).** Smoothed hashed value noise built on integer operations only (no `java.util.Random`), bit-exact on the JVM and on Android. Each camera has an explicit seed and a reseed call; the default seed is a fixed constant. When a shake starts while another is active, the one with the larger remaining amplitude wins; shakes never add up.
2. **View transform (`OQ-131`).** Per frame the camera produces a plain immutable view transform: scale (pixels per meter, ADR-0014), offset and the Y flip, computed from the state interpolated with `alpha` (ADR-0017). The scene's render code converts world positions to pixels of the logical frame with it. `tilemap` receives a pixel rectangle (ADR-0009) and sprites pixel positions (ADR-0003). No `Camera` type reaches rendering.
3. **Follow lookup (`OQ-132`).** The lookup contract identifies the target by a primitive `long` key: no allocation, no `ecs` dependency. ECS object IDs (index plus generation) fit in a `long`; the exact encoding is `OQ-137`. The adapter over the world lives in the optional scene composition module (`OQ-155`) and in examples until then; no `camera-ecs` module.
4. **Follow delay (`OQ-133`).** Default time constant 0 (rigid). On a target switch the camera moves smoothly to the new target; an explicit snap call serves teleports; an optional snap-distance threshold is off by default. The target is sampled at the end of the update step, after the systems, and rendering interpolates between steps.
5. **Shake units and picking (`OQ-134`).** Amplitude in meters, frequency in Hz, linear decay. World-to-screen includes the shake. Screen-to-world for picking ignores it by default; an `includeShake` option is available. This refines `REQ-CAM-007`.
6. **Zoom (`OQ-135`).** A dimensionless multiplier of the scale (2 means twice as large). Default 1.0, starting range 0.25 to 4.0, configurable. Animation by duration in seconds and a curve, default smoothstep. The pivot is the camera center by default, with an optional world-point anchor for zooming towards a point.
7. **Candidate features (`OQ-136`).** Bounds clamp is the first addition (tile maps need it), then dead zone and follow offset. Split screen and multi-camera need no camera code: several instances, no singleton, views per ADR-0021. Parallax is a background-layer feature reading the camera position, not a camera feature. Rotation is deferred.

## Options considered

- Shake noise: integer hashed value noise (chosen) vs `java.util.Random` (not guaranteed identical across platforms and shared state) vs sinusoids (visibly periodic).
- Overlapping shakes: strongest wins (chosen, bounded) vs additive (can exceed the intended amplitude).
- Follow key: primitive `long` (chosen) vs an ECS ID type in `camera` (forces a dependency) vs an object reference (allocation, no stale detection).
- Picking: ignore shake by default (chosen) vs include (the aim jitters with the effect).
- Shake amplitude: meters (chosen, consistent with world units; the visual size changes with zoom) vs fraction of the view (zoom independent).

## Consequences

- Positive consequences: deterministic, platform-identical camera effects; rendering consumers need no camera type; no dependency between `camera` and `ecs`.
- Costs / constraints: the adapter is outside `camera` until the composition module exists; shake amplitude follows zoom; rotation unavailable.
- Impact on modules and APIs: `camera` only; render code uses the view transform.
- Impact on tests and documentation: `REQ-CAM-007` refined, `REQ-CAM-009` added; `features/camera.md`, `contracts.md`, traceability updated.

## Validation

Decisions confirmed by the project owner on 2026-11-23. Follow-ups: `OQ-163`.
