# ADR-0062 — Output filter chain: one scaling filter before the post filters, fallback order on the filter, resolution in the display state

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-FILTER-001`, `REQ-FILTER-002`, `REQ-DISPLAY-008`
- Open questions resolved: none (details left open by ADR-0002, ADR-0006 and ADR-0033, needed by batch 1 of [`../features/output-filtering.md`](../features/output-filtering.md))
- Supersedes / superseded by: none; completes [ADR-0006](0006-display-scaling-effective-result-and-fallback-orders.md) (decisions 3 and 5), [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md) and [ADR-0046](0046-display-state-owned-by-the-platform-and-presentation-geometry.md) (all unchanged)

## Context

ADR-0002 makes the output filters an ordered chain from the logical frame to the output surface. ADR-0006 splits them into kinds:
- scaling filters (`NONE`, bilinear, `HQ2X`) resample the logical frame to the output size;
- post filters (CRT) run on the scaled output.

ADR-0006 also gives the fallback per filter: CRT is skipped, `HQ2X` falls back to bilinear, bilinear to `NONE`, and each fallback is reported. ADR-0033 adds custom filters, declared as scaling or post filters, which a backend lacking them skips. ADR-0046 makes the platform resolve the chain against the filters of the attached render backend, in the shared `DisplayState`.

The platform batches resolve a chain by keeping the filters the backend supports and skipping the others: a missing `HQ2X` is dropped instead of falling back to bilinear. `DisplayRequest` accepts any list. No document says:
- what a chain holding two scaling filters, or a post filter before a scaling filter, means;
- whether a filter may appear twice;
- where the fallback order is written, so the platform and the backends read the same one;
- how a custom scaling filter falls back;
- the wording of a fallback in the reason.

What other engines do:
- **RetroArch** shader presets are passes in order. Each pass declares its own scale, so several scaling passes are allowed, and a missing shader fails the whole preset.
- **libretro cores and most pixel-art engines** offer one sampling choice (nearest or linear) plus optional post effects.
- **Godot** has one stretch mode plus a texture filter, and screen-space effects after it.

## Decision

1. **Chain shape.** A chain holds at most one scaling filter, placed before every post filter, and no filter twice. `DisplayRequest` refuses any other chain with an `IllegalArgumentException`: the shape is a programming error, not a missing capability. A chain without a scaling filter scales with `NONE`; an empty chain is `NONE` alone.
2. **Fallback order on the filter.** `OutputFilter.fallback()` gives the filter used in place of one the backend does not implement:
   - `HQ2X` → bilinear; bilinear → `NONE`;
   - any other scaling filter, a custom one included → `NONE`;
   - `NONE` and every post filter → `null`. `NONE` is always implemented; a missing post filter is skipped.

   The order lives in `render-api`, so the platform and every backend read the same one.
3. **Resolution in the display state.** `DisplayState` walks each requested filter along its fallbacks to the first one the attached backend implements. `NONE` stays out of the effective chain, as before.
   - A replaced scaling filter is reported as `"Output filter <requested id> not implemented by the render backend, <effective id> used"`.
   - A skipped post filter keeps the reason `"Output filter not implemented by the render backend, skipped: <id>"`.
   - The reasons follow the chain order.
   - Without a backend, no filter is applied and this is reported, as before.
   - A request is never refused for a missing capability.

## Options considered

- Several scaling filters:
  - Refused (chosen): the logical frame is resampled once to the output size, so a second resampling has no defined input size (ADR-0006, decision 3).
  - Allowed as successive passes, as in RetroArch (rejected: each pass would need its own scale, which no contract defines; this can come later through a custom post filter).
- A post filter before a scaling filter:
  - Refused (chosen): post filters run on the scaled output by definition.
  - Reordered silently (rejected: it hides a mistake of the game).
- Where the fallback order lives:
  - On `OutputFilter` in `render-api` (chosen): one definition next to the identifiers, read by the platform and by the backends.
  - Only in `DisplayState` (rejected: a backend presenting a chain could not tell the order).
- A custom scaling filter the backend lacks:
  - Falls back to `NONE` (chosen): the frame must still be scaled, and `NONE` is the default sampling; ADR-0033's "skips it" means its sampling is not applied.
  - Dropped with no scaling (rejected: impossible, every presentation samples).

## Consequences

- Positive consequences:
  - A game asks for `HQ2X` and gets bilinear on a backend without it, with the reason, instead of losing the filter.
  - An invalid chain fails where it is written.
  - Every platform and backend reads one fallback order.
- Costs / constraints:
  - A chain built from user settings must respect the shape before the request is created.
  - Several scaling passes need a later ADR.
- Impact on modules and APIs:
  - `render-api`: `OutputFilter.fallback()`.
  - `platform-api`: the chain shape checked by `DisplayRequest`, and the fallback in `DisplayState`. The contract suite checks the fallback reported by each platform with a fake backend.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: unit tests of the fallback order and of the chain shape; display-state tests with fake backends; contract tests on both platforms.
  - Documentation: `features/output-filtering.md`, `contracts.md`, `api-index.md`, `engine-features.md` and the traceability.
  - Android/DEX: plain Java in `render-api` and `platform-api`, run by the device tests.

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the batches; reviewed in the pull request of output-filtering batch 1.
