# ADR-0013 — World coordinate system and angles

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner
- Related requirements: `REQ-TECH-003`, `REQ-CAM-001`, `REQ-CAM-003`, `REQ-CAM-007`, `REQ-SPRITE-003`, `REQ-STD-003`
- Open questions resolved: `OQ-010`, `OQ-011` (rotation direction: `OQ-150`)
- Supersedes / superseded by: none; completes [ADR-0001](0001-technical-baseline-and-conventions.md) (which stays unchanged)

Source: owner answers on issue #875.

## Context

[ADR-0001](0001-technical-baseline-and-conventions.md) fixed meters and seconds but left the origin, the direction of Y and the angle unit open. Several accepted decisions depend on them (camera, sprites, standard traits).

## Decision

- The world origin is `(0, 0)`.
- In the **world**, the positive Y direction is **up**: Y increases upward.
- The **screen** is a different space: origin at the top left, Y increasing downward, in pixels of the logical frame (ADR-0002). The conversion between the two, including the Y flip, is done in one place, the camera (`REQ-CAM-007`); the meters-to-pixels factor stays `OQ-012`.
- Angles are expressed in **degrees** in every public API and data file. Radians may be used only inside internal math helpers, never in a public signature.
- The rotation direction and the reference axis stay open (`OQ-150`).

## Options considered

### Y down in the world (as the screen)

No flip, but opposite to mathematics, physics and OpenGL; formulas and gravity signs are counter-intuitive.

### Y up in the world (chosen)

The mathematics, physics, OpenGL, Box2D and libGDX convention. The flip is isolated in the camera.

### Radians

The convention of `java.lang.Math` and most math libraries; less readable in data files. Degrees chosen for readability; the cost is one conversion at trigonometric calls.

## Consequences

- Positive consequences: familiar world formulas; gravity is a negative Y acceleration and an upward jump impulse is positive (inputs to `OQ-143`); a single place for the screen flip.
- Costs / constraints: any screen or editor source with Y down (tile editors, image files) needs a conversion at its boundary; degree-to-radian conversions in math code.
- Impact on modules and APIs: `camera`, `graphics` (sprite rotation, `OQ-104`), `tilemap` (row ordering to be settled in the tilemap batches) and `traits` follow this decision.
- Impact on tests and documentation: unit tests state the convention; `architecture/overview.md` updated.

## Validation

Decisions explicitly provided by the project owner on 2026-11-22 (issue #875).
