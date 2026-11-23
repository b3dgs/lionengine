# ADR-0016 — Angle range and normalization

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner
- Related requirements: `REQ-SPRITE-003`, `REQ-CAM-007`
- Open questions resolved: `OQ-150`
- Supersedes / superseded by: none; completes [ADR-0014](0014-meters-to-pixels-numeric-type-and-rotation.md) (which stays unchanged)

Source: owner confirmation on issue #875.

## Context

[ADR-0013](0013-world-coordinates-and-angles.md) and [ADR-0014](0014-meters-to-pixels-numeric-type-and-rotation.md) fixed degrees, counter-clockwise rotation and `0°` along `+X`, and left the angle range open.

## Decision

- **Absolute angles (orientation)** are normalized to the half-open range `[0, 360)`: `360` is never a valid normalized value and is represented as `0`.
- **Signed differences** (shortest rotation from one angle to another) are in `(-180, 180]`; a positive value is a counter-clockwise turn.
- **Raw accumulated values** (for example an angular speed in degrees per second) are not normalized.
- Normalization is explicit, done by documented helpers, never implicit in setters. Conversion from `Math.atan2` (range `(-180, 180]` in degrees) shifts negative results by `360`.

## Options considered

- A single `(-180, 180]` range for everything: negative values for the left half and harder orientation tables. Rejected.
- Closed `[0, 360]`: two representations of the same direction. Rejected; the owner's `[0, 360]` is read as the half-open `[0, 360)`.

## Consequences

- Positive consequences: one representation per direction; direct use for orientation tables and indexes; the turn direction is readable from the sign of a difference.
- Costs / constraints: two documented ranges; helpers needed.
- Impact on modules and APIs: `graphics` (sprite rotation), `camera`, `traits`.
- Impact on tests and documentation: boundary tests at `0`, `360`, `-0`, `-180`, `180`; `conventions.md` updated.

## Validation

Recommendation confirmed by the project owner on 2026-11-22 (issue #875).
