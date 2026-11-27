# ADR-0017 — Fixed-step game loop with accumulator, interpolation, pause and time scale

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner
- Related requirements: `REQ-LOOP-004`, `REQ-LOOP-005`, `REQ-LOOP-006`, `REQ-LOOP-007` to `REQ-LOOP-010`, `REQ-TEST-001`, `REQ-STD-001`, `REQ-ANIM-003`
- Open questions resolved: `OQ-020`, `OQ-021`, `OQ-022`, `OQ-023` (follow-up: `OQ-151`)
- Supersedes / superseded by: none; completes [ADR-0001](0001-technical-baseline-and-conventions.md) (which stays unchanged)

Source: owner confirmation of the proposal on issue #876.

## Context

[ADR-0001](0001-technical-baseline-and-conventions.md) fixed the principles of the default loop (independence of machine performance, frameskipping, extrapolation, extrapolation greater than 1 never fed back) and left the step, the clock, the factor, the lag policy, pause, slow motion, exceptions and shutdown open. The previous LionEngine loop kept updates at a constant speed, skipped only rendering and used an extrapolation value below 1.0 to render more frames than the native rate, without supporting lag.

## Decision

### Step, clock and catch-up (OQ-020)

- The simulation advances by a **fixed step** of constant duration, the same for every update. Default **60 Hz** (`1/60 s`), configurable per loop. No variable step in the simulation.
- Time comes from an **abstract monotonic clock** (nanoseconds). On the JVM and Android the implementation uses `System.nanoTime`; wall-clock time is never used. A controllable clock is the test implementation (`REQ-TEST-001`).
- Each iteration adds the measured real time (scaled, see below) to an **accumulator**. While the accumulator holds at least one step, one update runs and one step is subtracted (**catch-up**). The accumulator may be kept in integer units to avoid rounding drift (details at implementation).
- **Maximum tolerated lag:** the real time added in one iteration is capped. Proposed cap: **0.25 s** (15 steps at 60 Hz); the value is calibrated on first measurement (`OQ-151`). The excess is **dropped**: the game slows down for that moment instead of freezing, and the loop reports the drop (counter or event, form left to the implementation batch).
- **Frameskipping:** when several updates run in one iteration, rendering is called once, so intermediate frames are skipped. Updates are never skipped below the cap.
- Rendering is paced by the display (vsync on desktop, the platform frame callback on Android); this is a platform concern.

### Factor (OQ-021)

- The presentation factor is `alpha = accumulator / step`, always in **`[0, 1)`** by construction. A factor greater than 1 never reaches the renderer: lateness is absorbed by catch-up and, beyond the cap, by dropping time. The principle of `REQ-LOOP-006` (a presented state is never fed back into the simulation) holds unchanged.

### Interpolation and extrapolation (OQ-022)

- **Interpolation is the default** presentation: `previous + (current - previous) * alpha`. It shows the state about one step in the past, is exact and never overshoots.
- **Extrapolation is opt-in**: `current + velocity * alpha * step`. It adds no latency but is wrong when the velocity changes inside the step; its range is `alpha` in `[0, 1)` only.
- Both are presentation-only and never feed back into the simulation. `Transformable` keeps the previous position (`REQ-STD-001`).

### Pause, slow motion, exceptions, shutdown (OQ-023)

- **Pause:** the simulation time stops advancing. On resume the clock reference is reset and the accumulator cleared: no catch-up and no time shift. Rendering may continue (for menus) with `alpha` frozen. A platform going to the background maps to pause (platform side).
- **Slow motion:** a **time scale** multiplies the elapsed time before it enters the accumulator. The step duration never changes, so the simulation stays deterministic, and rendering keeps its full rate through interpolation (bullet time). The scale has a documented bounded range; changes apply at a frame boundary. Pause is distinct from a scale of 0.
- **Exceptions:** fail-fast. An exception thrown by an update or a render is not swallowed: the loop stops in a defined *failed* state, no render is made from a half-updated state, and the exception reaches the application that owns the loop. A platform contract may define a recoverable render failure (for example a lost surface).
- **Shutdown:** a stop request is honored at the next frame boundary (the current step and render complete, never a partial step), is idempotent, and resources are closed by their owners ([ADR-0007](0007-resource-convention-keys-release-and-errors.md)). Which thread may request it is `OQ-030`.

## Options considered

- Variable step: simpler, but physics and collisions depend on the frame rate. Rejected for the simulation.
- Extrapolation by default: no added latency, but visible jitter on collisions and direction changes. Kept as opt-in.
- Never dropping lag: exact simulation time but a risk of the spiral of death on slow devices. Rejected; time is dropped beyond the cap.

## Consequences

- Positive consequences: deterministic, machine-independent simulation; smooth motion at any display rate; bullet time and pause without glitches; testable with a controllable clock.
- Costs / constraints: interpolation adds up to one step of latency; objects need a previous state; a lag cap makes the game slow down under extreme load; the exact cap and the time domains remain to settle (`OQ-151`).
- Impact on modules and APIs: `runtime` loop contract; `traits`/`ecs` keep the previous state; `graphics` and `camera` read states with `alpha`.
- Impact on tests and documentation: tests with a controllable clock (exact update counts for a given elapsed time, `alpha` bounds, pause and resume without catch-up, time scale, lag cap, failure and shutdown); `overview.md` section 7, `contracts.md`, `glossary.md` and `requirements.md` updated (`REQ-LOOP-007` to `REQ-LOOP-010`). The text of `REQ-LOOP-004` to `REQ-LOOP-006` is unchanged.

## Validation

Proposal confirmed by the project owner on 2026-11-22 (issue #876).
