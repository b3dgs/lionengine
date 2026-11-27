# ADR-0002 — Display modes, resolution selection and output filtering contracts

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner (decisions 1B, 2A, 3A, 4A of issue #850; accepted on 2026-11-22)
- Related requirements: `REQ-DISPLAY-001` to `REQ-DISPLAY-006`, `REQ-FILTER-001` to `REQ-FILTER-003`, `REQ-FE-003`, `REQ-LOOP-006`, `REQ-TECH-006`
- Open questions resolved: `OQ-050`, `OQ-090`, `OQ-092` (touches `OQ-020`; creates `OQ-090` to `OQ-096`; `OQ-119` raised by the review, `OQ-120` at acceptance)
- Supersedes / superseded by: none

> Revision note (joint review of packets #850, #852 and #853, under the owner's delegation of 2026-11-22): `OQ-051` removed from the touched questions (it concerns `graphics`, not display); the choices below were added. The ADR was still `PROPOSED` then.

## Context

Games need to choose how they occupy the screen (windowed, full window, full screen), which display mode is used (size, refresh rate, color depth) and how the frame is filtered when scaled to the output (nearest, bilinear, `HQ2X`, CRT). Platforms differ widely (AWT, OpenGL, Android) and not all offer every capability. Game code must stay identical everywhere, without frontend types in core APIs (`REQ-CORE-002`, `REQ-FE-003`).

## Decision

Principles (part of the decision):

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

Choices made during the joint review of packets #850, #852 and #853, under the owner's delegation of 2026-11-22:

- **`NONE` and nearest:** the standard identifiers are those of `REQ-FILTER-001` (`NONE`, bilinear, `HQ2X`, CRT). `NONE` means no output filter: when the logical frame is scaled, pixels are sampled nearest-neighbor (pixel-exact). There is no separate "nearest" identifier.
- **Frame boundary:** the point between two presentations, outside any simulation update. A change requested while presentations are skipped (frameskipping, `REQ-LOOP-005`) applies at the next presentation.
- **Fallback order:** a proposed order is recorded in `OQ-120`, to be confirmed with the first platform implementations.
- **Desktop implementer of `platform-api`:** not defined by `modules.md`; tracked as `OQ-119`.

Not decided (stay `TO BE DEFINED`): scaling policy set, default and owner (`OQ-091`); `HQ2X`/CRT sources and licenses (`OQ-093`); shader portability and assets (`OQ-094`); persistence of user settings (`OQ-095`); Java signatures; the form of the extension point; the form of the effective-result report and the fallback orders (`OQ-120`).

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

Accepted by the project owner on 2026-11-22 (explicit request in the review session that followed PR #856). Owner decisions 1B, 2A, 3A, 4A were given on issue #850; the choices added by the joint review under the owner's delegation are part of the accepted decision. Acceptance resolves `OQ-050` (separate `platform-api` and `render-api`), `OQ-090` and `OQ-092`. `OQ-091` (scaling policy) was still open at acceptance and stays `TO BE DEFINED`.
