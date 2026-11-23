# ADR-0046 — Display state owned by the platform: `platform-api` on `render-api`, presentation geometry

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, at the project owner's request of 2026-11-23 to review the architecture with the dedicated skills and apply the changes the review suggests, so that the base skeleton of each module and its interfaces can be defined; reversible by a superseding ADR
- Related requirements: `REQ-CORE-005`, `REQ-DISPLAY-004`, `REQ-DISPLAY-007`, `REQ-DISPLAY-008`, `REQ-FILTER-002`, `REQ-IN-004`, `REQ-PLATFORM-002`
- Open questions resolved: none (defect found by the skeleton-readiness review of 2026-11-23)
- Supersedes / superseded by: changes the `platform-api` row of [`../architecture/modules.md`](../architecture/modules.md) (`core` → `core`, `render-api`); completes [ADR-0002](0002-display-modes-resolution-and-output-filtering.md), [ADR-0006](0006-display-scaling-effective-result-and-fallback-orders.md), [ADR-0031](0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md), [ADR-0035](0035-input-actions-devices-and-step-edges.md) and [ADR-0040](0040-thread-hand-off-contracts-in-core-and-loop-wiring.md) (all unchanged)

## Context

The accepted decisions describe one display state, but split its types across two modules that cannot see each other:

- ADR-0031 (section 4): the application configures the platform with the logical frame size, the initial screen-mode request, the **scaling policy** and the **filter chain**.
- ADR-0006 (decision 2) and ADR-0037: `ScalingPolicy` and `OutputFilter` are `render-api` types; `DisplayRequest` and `EffectiveResult` are `platform-api` types.
- `REQ-DISPLAY-008`: applying a display or filter request returns the accepted request, and the effective result (requested vs effective per field, `degraded`, reason) is one immutable value delivered through the inbox. `REQ-FILTER-002`: filter fallbacks are reported there, and they depend on what the render backend implements (`render-awt`: `NONE`, bilinear, `Scale2x`).
- ADR-0035 (decision 6): pointer positions are in logical-frame pixels after the inverse of the scaling policy. Input adapters depend on `input-api` and `platform-api`, not on `render-api`.

`modules.md` lets `platform-api` depend on `core` only. As written, the platform configuration, `DisplayRequest` and `EffectiveResult` cannot name the scaling policy or the filters. The platform also cannot learn which filters the backend implements, and an input adapter cannot learn where the logical frame is presented. The contract skeleton could not compile.

## Decision

1. **`platform-api` depends on `render-api`** (row: `core`, `render-api`). `render-api` never depends on `platform-api`. The edge links two API modules, adds no cycle, and lets no implementation type cross.
2. **The platform owns the display state.** It holds the logical frame size, screen mode, display mode, scaling policy and filter chain. Its configuration and `DisplayRequest` carry the scaling policy and the filter chain as `render-api` types. `EffectiveResult` reports every field, requested vs effective, with `degraded` and a reason, as one immutable value (`REQ-DISPLAY-008`).
3. **The render backend is attached to the platform** by the application, as its `render-api` contract, before the platform starts (ADR-0020: nothing is discovered). The platform asks the attached backend which filters it implements, resolves the fallback chain of ADR-0006 and reports the result.
4. **Presentation geometry.** Each time the effective display state changes, the platform computes the presentation geometry at the frame boundary where the change applies (resize, screen or display mode, scaling policy). It is the rectangle of the output surface, in device pixels (ADR-0043), where the logical frame is presented, plus the scale from logical-frame pixels to device pixels on each axis. It comes from the effective scaling policy, the logical frame size and the surface size, through the one scaling function of `render-api`, so the platform and every backend compute the same values. The platform exposes it as readable current state on the runtime thread:
   - the render backend presents with it;
   - input adapters post device-pixel pointer positions, and their inbox events convert them at delivery, on the runtime thread, with the inverse of the geometry current at that frame boundary, flagging positions outside the rectangle (ADR-0035);
   - the game may read it, like the effective result.
5. The loop wiring of ADR-0040 is unchanged: the render callback renders the scenes, then presents the command buffer through the backend.

## Options considered

- `platform-api` on `render-api`, platform owning the display state (chosen): one request, one effective result and one geometry shared by the renderer and the input adapters. ADR-0006 and ADR-0037 placements are unchanged, and the only cost is one edge between two API modules.
- Split ownership: the platform keeps screen and display modes, and the render backend owns scaling and filters with its own request and effective result in `render-api` (rejected). It needs two result values for one display change, changes the platform configuration of ADR-0031, and still needs an `input-*` → `render-api` edge or application wiring for the pointer geometry.
- Move `ScalingPolicy` and the filter identifiers into `platform-api` (rejected): supersedes ADR-0006 and the ADR-0037 table, and makes `render-api` depend on `platform-api` for the filter kinds.
- A new `display-api` module for these value types (rejected): one more module for a handful of types, with the same dependents.

## Consequences

- Positive consequences: the display contracts compile as specified; the renderer and the input adapters share one geometry; the effective result stays one value.
- Costs / constraints: every platform implementation compiles against `render-api` (and, through it, `resources`, ADR-0047). Attaching the backend to the platform is one more wiring line. Pointer conversion happens at inbox delivery, not on the producing thread.
- Impact on modules and APIs: `platform-api` row and diagram; `Platform` configuration, `DisplayRequest` and `EffectiveResult` name `render-api` types; a presentation-geometry value type in `platform-api` (its name is chosen by the contract skeleton, ADR-0049); the scaling function in `render-api`.
- Impact on tests and documentation (including Android/DEX compatibility): `modules.md`, `overview.md`, `contracts.md`, `interfaces.md`, `glossary.md`, `features/platform-and-rendering.md`, `features/display-modes.md`, `features/output-filtering.md`, `features/input.md`. Contract suites check that the platform, the recording backend and an input adapter agree on the geometry for each scaling policy. No new JDK API.

## Validation

Decided by the agent on 2026-11-23 at the owner's request to apply the review's changes; reversible by a superseding ADR.
