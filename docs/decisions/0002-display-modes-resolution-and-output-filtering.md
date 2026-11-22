# ADR-0002 — Display modes, resolution selection and output filtering contracts

- Status: PROPOSED
- Date: 2026-11-22
- Decision makers: project owner (decisions 1B, 2A, 3A, 4A of issue #850 given; this ADR stays `PROPOSED` until explicitly approved)
- Related requirements: `REQ-DISPLAY-001` to `REQ-DISPLAY-006`, `REQ-FILTER-001` to `REQ-FILTER-003`, `REQ-FE-003`, `REQ-LOOP-006`, `REQ-TECH-006`
- Open questions resolved: none (touches `OQ-050`, `OQ-051`; creates `OQ-090` to `OQ-096`)
- Supersedes / superseded by: none

## Context

Games need to choose how they occupy the screen (windowed, full window, full screen), which display mode is used (size, refresh rate, color depth) and how the frame is filtered when scaled to the output (nearest, bilinear, `HQ2X`, CRT). Platforms differ widely (AWT, OpenGL, Android) and not all offer every capability. Game code must stay identical everywhere, without frontend types in core APIs (`REQ-CORE-002`, `REQ-FE-003`).

## Decision

Principles (proposals, recorded here; the ADR is not accepted yet):

1. **Capability-based best-effort negotiation** with a documented fallback: a request is applied as far as the platform can, and the effective result (and reason) is reported. A missing capability never throws.
2. **Own neutral value types** for screen mode, display mode and filter identifiers; no AWT, OpenGL or Android type in `core`, `platform-api` or `render-api` contracts.
3. **Logical resolution decoupled from output resolution**, with a scaling policy (stretch, aspect-preserving, integer scale). Scaling never changes world units (m, m/s, m/s²).
4. **Filters as an ordered chain** with an extension point (form not fixed).
5. **Changes applied at a frame boundary and signaled by an event**; they never alter simulation state.

Decisions taken by the owner (issue #850):

- **Placement (1B):** screen modes and display-mode selection live in `platform-api`; output filtering lives in `render-api`. No new module.
- **Request exactness (2A):** pure best-effort, the effective result is reported; no "required" flag that fails when unsatisfiable.
- **Filter scope (3A):** filtering applies from the logical frame to the output surface only, not per layer.
- **Multi-monitor and HiDPI (4A):** outside the first batch (`OQ-096`); the contracts must not preclude them.

Not decided (stay `TO BE DEFINED`): scaling policy set, default and owner (`OQ-091`); `HQ2X`/CRT sources and licenses (`OQ-093`); shader portability and assets (`OQ-094`); persistence of user settings (`OQ-095`); Java signatures; the form of the extension point and of the effective-result report.

## Options considered

### Placement

- A: everything in `platform-api`. Simple, but puts image-processing in the platform boundary.
- B (retained): screen and display modes in `platform-api`, filtering in `render-api`. Matches the responsibilities in `modules.md`; relies on `platform-api` and `render-api` staying separate (`OQ-050`).
- C: new `display` module. Rejected for now: the splitting principles of `modules.md` do not justify a module for this size of API.

### Request exactness

- A (retained): best-effort with a reported result.
- B: optional "required" flag failing when unsatisfiable. More complex; can be added later if a need appears.

### Filter scope

- A (retained): logical frame to output surface only.
- B: also per layer. Larger cost and contract surface; can be reconsidered.

### Multi-monitor and HiDPI

- A (retained): out of the first batch.
- B: included. Larger first batch.

## Consequences

- Positive consequences: identical game code on every platform; no frontend coupling; testable with fake capabilities.
- Costs / constraints: every implementation must report its effective result; fallback orders must be documented and tested; filters beyond `NONE` need per-backend work (CRT needs shaders).
- Impact on modules and APIs: new contracts in `platform-api` and `render-api`; no new module and no new dependency between layers. Android specifics stay in `platform-android`.
- Impact on tests and documentation: unit tests (value types, fallback, chain); contract suites in `test-support`; headless integration tests with fake capabilities; golden-image platform tests. Android/DEX: own value types only; any third-party filter library needs its license and DEX compatibility recorded first ([`../android-compatibility.md`](../android-compatibility.md), `OQ-093`). Game-loop invariant untouched (`REQ-LOOP-006`). Documents updated: requirements, traceability, open questions, modules, contracts, overview, glossary, roadmap, testing, API index, game development guide.

## Validation

Not approved yet. Owner decisions 1B, 2A, 3A, 4A were given on issue #850; the ADR remains `PROPOSED` until the owner explicitly accepts it.
