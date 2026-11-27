# ADR-0014 — Meters-to-pixels conversion, numeric type and rotation direction

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner
- Related requirements: `REQ-TECH-003`, `REQ-CAM-001`, `REQ-CAM-007`, `REQ-DISPLAY-004`, `REQ-SPRITE-003`, `REQ-SPRITE-006`, `REQ-STD-001`
- Open questions resolved: `OQ-012`, `OQ-013`, `OQ-150`
- Supersedes / superseded by: none; completes [ADR-0013](0013-world-coordinates-and-angles.md) (which stays unchanged)

Source: owner confirmation of the recommendations on issue #875.

## Context

[ADR-0013](0013-world-coordinates-and-angles.md) fixed the origin, the Y direction and the angle unit, and left the meters-to-pixels conversion, the numeric type and the rotation direction open.

## Decision

- **Conversion:** the world stays in meters. The conversion to pixels is done in the camera only. The camera derives `pixelsPerMeter` from the logical frame width in pixels (ADR-0002) divided by the view width in meters, multiplied by the zoom. There is no engine-wide pixels-per-meter constant. The game chooses the scale; for pixel art the recommended practice is 1 tile = 1 m with `pixelsPerMeter` equal to the tile size in pixels.
- **Screen space:** origin at the top left, Y increasing downward, in pixels of the logical frame. The Y flip happens in the camera conversion only. Sprites stay in pixels of the logical frame (ADR-0003).
- **Numeric type:** `double` for world quantities (positions, velocities, accelerations, time accumulators). `float` is allowed only at the render boundary, after the camera conversion (GPU buffers, pixel coordinates).
- **Rotation:** counter-clockwise positive, `0°` along `+X`, consistent with Y up. The angle range (`[0, 360)` or `(-180, 180]`) is not decided here and is recorded in `OQ-150`.

## Options considered

- Global pixels-per-meter constant: simple, but forbids zoom and several resolutions. Rejected.
- `float` for all world quantities: half the memory, visible precision loss far from the origin and over long runs. Rejected for world quantities.

## Consequences

- Positive consequences: zoom and any resolution work through one factor; no precision loss on large maps; deterministic simulation is easier.
- Costs / constraints: twice the memory per value (negligible at the ECS targets); explicit `double` to `float` conversion at the render boundary; 64-bit ARM runs `double` at full speed, 32-bit devices to be measured (`OQ-139`).
- Impact on modules and APIs: `camera`, `graphics`, `tilemap`, `traits`, `ecs` follow it.
- Impact on tests and documentation: unit tests state the conventions; `conventions.md` and `architecture/overview.md` updated; Android needs no special handling for `double`.

## Validation

Recommendations confirmed by the project owner on 2026-11-22 (issue #875).
