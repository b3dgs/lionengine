# ADR-0005 — Common convention for modules handling resources

- Status: PROPOSED
- Date: 2026-11-22
- Decision makers: project owner (rule stated with decision 3B of issue #853); the placement of the convention was chosen by the agent under the owner's delegation of 2026-11-22 (joint review of packets #850, #852 and #853). This ADR stays `PROPOSED` until explicitly approved.
- Related requirements: `REQ-RES-003`, `REQ-RES-004`, `REQ-SPRITE-002`, `REQ-SPRITE-005`, `REQ-AUDIO-003`, `REQ-AUDIO-006`, `REQ-CORE-005`
- Open questions resolved: none (touches `OQ-032`, `OQ-094`, `OQ-101`, `OQ-106`, `OQ-110`, `OQ-118`)
- Supersedes / superseded by: none. It takes over the general rule first recorded inside [ADR-0004](0004-audio-contracts-short-and-long-sounds.md) (decision 3B), which now covers audio only.

## Context

Owner decision 3B of issue #853 keeps `resources` media-agnostic and puts the decoded caches inside the audio modules. The owner stated it as a general rule: every module handling a resource (an item reachable from a path: audio, graphics, ...) follows one common convention for keys and release, to limit divergence between implementations.

The rule was first recorded inside the audio ADR (ADR-0004), although it also governs `graphics` ([ADR-0003](0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md)) and any later consumer. ADR-0003 described the sprite source as "resource-owned", which contradicted the rule. Decisions must be atomic ([`README.md`](README.md)), so the cross-cutting rule gets its own ADR.

## Decision

1. `resources` resolves, opens and closes resources. It is media-agnostic and holds no decoded cache (owner, decision 3B of issue #853).
2. A module that loads or decodes resource content depends on `resources` and owns the decoded cache for that content (owner, decision 3B). Such content is never assumed to be a `File` (`REQ-RES-003`).
3. All such modules follow one common convention for cache keys and release (owner statement). The convention is recorded in this ADR and summarized in the Resources section of [`../architecture/contracts.md`](../architecture/contracts.md) (agent choice under delegation; this answers the "where" part of `OQ-118`).
4. Scope today: `graphics` (images, sprite sources, color variants, animation files: ADR-0003) and `audio-api` / `audio-*` (short and long sounds: ADR-0004). Candidates, to confirm when their own contracts are written: `tilemap` (map files) and rendering backends (shader assets, `OQ-094`).

Not decided (stays `TO BE DEFINED`, `OQ-118`): the key form, the release rule, the double-release behavior and thread safety. Module-specific cache policies (`OQ-101` for images, `OQ-110` for short sounds) and the sprite sheet id (`OQ-106`) must use the key form chosen in `OQ-118`.

## Options considered

### Where the convention lives

- A: `resources` documentation only. Close to the future code, but the rule binds modules that `resources` does not know about.
- B: inside ADR-0004, as first applied. Mixes an audio decision with a cross-cutting rule and breaks ADR atomicity.
- C (retained): its own ADR, plus a short summary in `architecture/contracts.md` (Resources). One place to supersede; every consuming module references it.

## Consequences

- Positive consequences: one key and release model for every media module; contract tests reusable across modules; `resources` stays small and media-agnostic.
- Costs / constraints: each consuming module implements its own cache; `OQ-118` must be answered before the first consuming module is implemented (sprite sheet batch or short-sound batch, Phase 2).
- Impact on modules and APIs: no new module. Each consuming module depends on `resources` (`graphics` already; `audio-api` and `audio-*` per ADR-0004); `tilemap` and rendering backends only once confirmed. No change to `core` and no frontend type in any API.
- Impact on tests and documentation (including Android/DEX compatibility): a reusable contract suite in `test-support` for key equality, release and double release, run by each consuming module (`REQ-RES-004`). No Android/DEX impact by itself; any cache implementation using a Java API not yet verified follows [`../android-compatibility.md`](../android-compatibility.md). The game-loop invariant is untouched.

## Validation

Not approved yet. The rule comes from the owner (issue #853, decision 3B); its placement was chosen by the agent under the owner's delegation. The ADR remains `PROPOSED` until the owner explicitly accepts it.
