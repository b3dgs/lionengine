# ADR-0008 — AWT desktop implementer of platform-api

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner (decisions of issue #861, 2026-11-22); module definition proposed by the agent and validated by the owner's acceptance on 2026-11-22.
- Related requirements: `REQ-PLATFORM-001`, `REQ-FE-002`, `REQ-FE-003`, `REQ-DISPLAY-001` to `REQ-DISPLAY-008`, `REQ-CORE-002`, `REQ-CORE-005`, `REQ-TECH-006`, `REQ-TECH-007`
- Open questions resolved: `OQ-119` (touches `OQ-055`, `OQ-096`, `OQ-030`; creates `OQ-123`, `OQ-124`)
- Supersedes / superseded by: none

## Context

[ADR-0002](0002-display-modes-resolution-and-output-filtering.md) places screen modes and display-mode selection in `platform-api`, and [ADR-0006](0006-display-scaling-effective-result-and-fallback-orders.md) defines the effective-result report and the fallback orders. No module implements `platform-api` on desktop: `render-awt` and `render-opengl` only depend on it (`OQ-119`).

## Decision

Decisions taken by the owner (issue #861):

1. The desktop reference implementer of `platform-api` is AWT, using the JDK only, in its own module.
2. Reason: AWT `DisplayMode` exposes size, refresh rate and bit depth, which matches the display-mode value of ADR-0002.
3. An LWJGL/GLFW implementer is not decided now. It is revisited as a `render-api` question if GPU filters (`HQ2X`, CRT) require it.

Module definition (proposed by the agent, validated by acceptance):

- **Name:** `platform-awt`.
- **Responsibility:** implement `platform-api` on desktop with the JDK AWT: window and surface lifecycle, screen modes, display-mode enumeration and selection, and the effective-result report of ADR-0006.
- **Does not do:** rendering (render side: `OQ-123`), input (a future `input-*` adapter), audio, resources.
- **Dependencies:** `platform-api` and `core`; JDK module `java.desktop` only; no third-party library. No `core` or `*-api` module depends on it.
- **Missing capabilities:** a display-mode change needs full-screen exclusive mode and is refused on many systems; the refresh rate may be unknown and the bit depth may be "multi". All are reported as degraded through the effective result, never thrown. How the neutral value types represent "unknown" is `OQ-124`.
- **Threads:** AWT work runs on the AWT event thread; requests return without blocking the update step (ADR-0006, `OQ-030`).

Not decided (stay `TO BE DEFINED`): the render-side implementer and how the AWT surface reaches it without crossing an `*-api` (`OQ-123`); the representation of unknown refresh rate and bit depth (`OQ-124`); packaging and Java names.

## Options considered

- A (retained): a separate `platform-awt` module. Keeps the platform boundary explicit, as `platform-android` does; `render-awt` stays a rendering module.
- B: let `render-awt` implement `platform-api`. Fewer modules, but mixes window management with rendering and blocks a second renderer (for example OpenGL) on the same window code.
- C: no desktop implementer before an OpenGL or GLFW backend exists. Delays the display contracts and the reference frontend.

## Consequences

- Positive consequences: display contracts get a real desktop implementation with no third-party dependency; the module boundary mirrors `platform-android`.
- Costs / constraints: one more module; mode changes are often refused and must be reported; the surface hand-off to a renderer is still open (`OQ-123`). AWT applies HiDPI scaling by default on some systems, so pixel sizes may be scaled (`OQ-096`).
- Impact on modules and APIs: new implementation module `platform-awt`; no change to `core` or any `*-api`; no AWT type outside the module (`REQ-DISPLAY-006`, `REQ-CORE-002`).
- Impact on tests and documentation (including Android/DEX compatibility): the `test-support` contract suites for `platform-api` run against it; tests needing a real display are platform tests, kept apart from headless tests. Android/DEX: platform module only, excluded from every Android build (`java.desktop` does not exist on Android). The game-loop invariant is untouched.

## Validation

Accepted by the project owner on 2026-11-22 (explicit request in the review session that followed PR #862). The decisions were given by the owner on issue #861; acceptance also validates the module definition, including the name `platform-awt`. Acceptance resolves `OQ-119`; `OQ-123` and `OQ-124` stay open.
