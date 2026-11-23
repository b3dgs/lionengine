# ADR-0025 — Filter implementation and shaders, display settings persistence, HiDPI constraint

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner ("Confirmed as proposed, write the ADR")
- Related requirements: `REQ-FILTER-001`, `REQ-FILTER-002`, `REQ-FILTER-003`, `REQ-FILTER-004`, `REQ-DISPLAY-001`, `REQ-DISPLAY-003`, `REQ-DISPLAY-009`
- Open questions resolved: `OQ-093`, `OQ-094`, `OQ-095`; `OQ-096` stays open (constraint recorded)
- Supersedes / superseded by: none; completes [ADR-0002](0002-display-modes-resolution-and-output-filtering.md) and [ADR-0006](0006-display-scaling-effective-result-and-fallback-orders.md) (both unchanged)

## Context

ADR-0002 fixed the filter identifiers (`NONE`, bilinear, `HQ2X`, CRT) but left the source and license of the algorithms, shader portability, settings persistence and HiDPI open.

## Decision

1. **Own implementations (`OQ-093`).** Each filter is implemented by the project, with no third-party filter library. CRT effects (scanlines, mask) are written from scratch. `HQ2X` ships only after the license of the algorithm and of any reference implementation is checked and recorded under `OQ-148`; until then a neighbour-based scaler such as `Scale2x` or `EPX` stands in. No dependency is added, so there is no new DEX or license risk (`AGENTS.md`).
2. **Shaders (`OQ-094`).** Shaders use the common subset of GLSL and GLSL ES (desktop OpenGL 2.1 and later, OpenGL ES 2.0 and later) and are delivered as resources through `resources`, following the convention of [ADR-0005](0005-common-convention-for-modules-handling-resources.md). A backend without a GPU (`render-awt`) implements the same filter on the CPU, under the fallback chain of ADR-0006.
3. **Display settings persistence (`OQ-095`).** Responsibility of the game. The engine exposes the settings as the immutable requested-versus-effective value of ADR-0006; it defines no settings file format and no storage location.
4. **Multi-monitor and HiDPI (`OQ-096`).** Stays deferred (ADR-0002, decision 4A) and open until Phase 4 schedules it. Constraint recorded now: display contracts must not assume a single monitor or a pixel scale of 1.0, and `platform-awt` may report scaled sizes (ADR-0008).

## Options considered

- Filters: own implementation (chosen) vs a third-party library (license and DEX checks, dependency) vs `HQ2X` immediately (unverified license).
- Shaders: common GLSL/GLSL ES subset (chosen; one shader per effect, fewer effects possible) vs separate desktop and Android shaders (richer, doubled maintenance).
- Settings: game responsibility (chosen) vs engine-managed file (a format and location per platform).
- HiDPI: record the constraint only (chosen) vs design now (premature).

## Consequences

- Positive consequences: no dependency or license risk for filters; one shader source; no storage code in the engine.
- Costs / constraints: more own code; `HQ2X` delayed behind a license check; each game saves its own display settings; a limited effect palette.
- Impact on modules and APIs: `render-opengl` loads shader resources through `resources`; `render-awt` carries CPU filters.
- Impact on tests and documentation: `REQ-FILTER-004` and `REQ-DISPLAY-009` added; `features/output-filtering.md`, `features/display-modes.md`, `android-compatibility.md`, `roadmap.md` updated. `REQ-FILTER-001` lists `HQ2X` as a standard identifier: its availability follows decision 1.

## Validation

Decisions confirmed by the project owner on 2026-11-23. Follow-ups: `OQ-159`.
