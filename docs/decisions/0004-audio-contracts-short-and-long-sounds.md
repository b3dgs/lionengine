# ADR-0004 — Audio contracts: short and long sounds, variant sets and playlists

- Status: PROPOSED
- Date: 2026-11-22
- Decision makers: project owner (decisions 1A, 2A, 3B of issue #853 given; this ADR stays `PROPOSED` until explicitly approved)
- Related requirements: `REQ-AUDIO-001` to `REQ-AUDIO-009`, `REQ-RES-003`, `REQ-RES-004`, `REQ-CORE-002`, `REQ-CORE-005`, `REQ-TEST-001`, `REQ-DOC-001`, `REQ-DOC-002`
- Open questions resolved: none (touches `OQ-053`, `OQ-032`; creates `OQ-110` to `OQ-118`)
- Supersedes / superseded by: none

## Context

The engine only has the backend-independent audio API requirement (`REQ-AUDIO-001`) and no contract for either audio usage. Short sounds (effects) and long sounds (music, ambient) have different lifecycles: the first are cached and reused heavily with low latency, the second are preloaded and released. The change adds public contracts in `audio-api` and makes `resources` a dependency of `audio-api` instead of "if needed". It also fixes a convention shared by every module that handles resources. No new module.

## Decision

Decisions taken by the owner (issue #853):

- **Two families (1A):** two separate contract families, short sounds and long sounds, with distinct handles and lifecycles.
- **Backend-independent logic (2A):** variant selection and playlist advance live in `audio-api` (or a shared helper); backends only expose play, stop and end-of-track.
- **Resource access and cache placement (3B):** `audio-*` depends on `resources`; decoded caches live only inside the audio modules; `resources` stays media-agnostic. Owner statement: this is a general rule for every module handling a resource (an item reachable from a path: audio, graphics, ...), which all follow one common convention for keys and release to limit divergence between implementations.

Proposal (not a decision): the common convention is documented in the Resources section of `architecture/contracts.md` and recorded by this ADR, tracked as `REQ-RES-004` and `OQ-118`. Its content (key form, release rules) is not fixed.

Not decided (stay `TO BE DEFINED`): cache policy (`OQ-110`); voice limit and overflow (`OQ-111`); variant selection and meaning of "composed" (`OQ-112`); long-sound strategy, decoded vs streamed (`OQ-113`); playlist end, failing track and callback thread (`OQ-114`); formats and decoders per platform (`OQ-115`); latency target (`OQ-116`); out-of-scope features pause, volume, looping, fades, spatial audio, mixer (`OQ-117`); location and content of the resource convention (`OQ-118`); Java signatures. `OQ-053` is not resolved.

## Options considered

### Modeling of the two families
- A (retained): two contract families with distinct handles and lifecycles. Clear lifecycles, more types.
- B: one generic `Sound` contract with a short/long mode. Fewer types, but lifecycle rules blur and misuse is possible.

### Variant selection and playlist advance
- A (retained): backend-independent. Testable headless with a seeded source and a fake backend; backends stay thin.
- B: in each backend. Divergent behavior risk, duplicated logic.

### Resource access and cache placement
- A: decoded cache in `resources`. Makes `resources` media-aware.
- B (retained): audio modules depend on `resources` and own decoded caches; `resources` stays media-agnostic. Each consuming module implements its cache, hence the need for a common convention.

## Consequences

- Positive consequences: clear lifecycles; selection and advance testable without a device; no platform type in `audio-api`; `resources` stays small and media-agnostic.
- Costs / constraints: two families to document and test; each media module owns a cache and must follow the common convention; completion callbacks need a thread rule so the audio thread never mutates simulation state (game-loop invariant unchanged).
- Impact on modules and APIs: new public contracts in `audio-api`; dependency `audio-api` to `resources` becomes firm (`audio-api` depends on `core` and `resources` only); no concrete audio library type in any API. Decoder dependencies need purpose, license and Android/DEX compatibility recorded first ([`../android-compatibility.md`](../android-compatibility.md), `OQ-115`); no `javax.sound` in `audio-api`.
- Impact on tests and documentation: unit tests (seeded variant selection, playlist advance, cache hit and miss); contract suites in `test-support` for every backend; headless integration with a fake backend and controllable clock; platform tests (latency, Android) later. Documents updated: requirements, traceability, open questions, modules, contracts, glossary, roadmap, testing, API index, game development guide, feature file.

## Validation

Not approved yet. Owner decisions 1A, 2A, 3B were given on issue #853; the ADR remains `PROPOSED` until the owner explicitly accepts it.
