# ADR-0009 — Tile map contracts in `tilemap`

- Status: ACCEPTED
- Date: 2026-11-22
- Decision makers: project owner (decisions 1 to 6 of issue #865, comment `@claude apply 1A 2A 3A 4A 5A 6A`; accepted on 2026-11-22).
- Related requirements: `REQ-MAP-001`, `REQ-MAP-003`, `REQ-MAP-004`, `REQ-MAP-005`, `REQ-MAP-006`, `REQ-MAP-007`, `REQ-SHEET-001`, `REQ-SPRITE-007`, `REQ-RES-004`, `REQ-RES-005`
- Open questions resolved: none. Touched: `OQ-012`, `OQ-040`, `OQ-043`, `OQ-044`, `OQ-122`, `OQ-101`. Created: `OQ-125` to `OQ-129`.
- Supersedes / superseded by: none. Builds on [ADR-0003](0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md), [ADR-0005](0005-common-convention-for-modules-handling-resources.md) and [ADR-0007](0007-resource-convention-keys-release-and-errors.md) (all ACCEPTED, not modified).

## Context

`tilemap` is a row of `architecture/modules.md` (dependencies: `core`, graphics contracts, `resources` candidate) and `REQ-MAP-001` / `REQ-MAP-002` exist, but the module contract was unspecified. Tile maps need manual editing, loading from a resource, viewport rendering and destroyable tiles with consistent rendering. The camera and viewport contract (`OQ-012`, `OQ-040`) and the `core` event contract (`OQ-122`) are open.

## Decision

Decisions taken by the owner (issue #865):

1. **Viewport (1A):** `tilemap` renders into a viewport rectangle in pixels of the logical frame passed by the caller as a plain value; no camera type. The camera/viewport contract stays in `OQ-012`, `OQ-040` and `render-api`.
2. **Auto-tiling (2A):** rule-based transitions: a rule set maps a cell neighborhood to a tileset index. The rule format and the 4 or 8 neighborhood are `OQ-127`.
3. **Notification (3A):** a synchronous listener called from the update phase, with the changed cell and the old and new tile; aligned later with `OQ-122`.
4. **Collision (4A):** out of scope; the tile model keeps a per-tile properties hook, no collision type (`OQ-125`).
5. **File format (5A):** left `TO BE DEFINED` (`OQ-126`); the first batch loads through an abstract loader contract tested with an in-memory test format.
6. **Layers (6A):** a single layer in the first batch; layers stay a candidate (mentioned in `contracts.md`).

Invariants: tile coordinates are `int` tile indices; tile size, viewport and render positions are in pixels of the logical frame (ADR-0002, ADR-0003); `tilemap` does no meter conversion (`OQ-012`). Set, destroy and neighbor updates run in the update phase only; rendering reads state and never mutates it (`REQ-LOOP-003`, `REQ-LOOP-006`). Rendering emits `render-api` commands only. No frontend type appears in a `tilemap` API; no dependency of `core` or an `*-api` module on `tilemap`. The `tilemap` dependency on `graphics` (sprite sheet) and `render-api` is consistent with `modules.md`.

Not decided: Java types and signatures, collision, the map format, the rule format, tileset ownership, chunking.

## Options considered

### Viewport
- A (retained): plain pixel rectangle from the caller. Decoupled from the camera contract.
- B: wait for the camera contract. Blocks the module.

### Auto-tiling
- A (retained): rule-based. Flexible; needs a rule format (`OQ-127`).
- B: hard-wired 4-bit or 8-bit bitmask with a fixed index table. Simple but rigid.
- C: no engine rule; the game updates neighbors from change events. Pushes the work to every game.

### Notification
- A (retained): synchronous listener. Simple; to be aligned with `OQ-122`.
- B: dirty-cell set polled by the renderer. No listener in the first batch.

### Collision
- A (retained): out of scope, properties hook only.
- B: minimal static collision flag now. Risks pre-empting `OQ-043`.

### Map file format
- A (retained): `TO BE DEFINED`, abstract loader.
- B: own text or binary format now.
- C: existing format (Tiled TMX/JSON); needs license and Android/DEX review.

### Layers
- A (retained): single layer first.
- B: layers from the start.

## Consequences

- Positive consequences: the module can start without the camera, event or physics contracts; rendering cost is independent of map size.
- Costs / constraints: rule format, loader format, tileset ownership and chunking remain open (`OQ-126` to `OQ-129`); the notification contract may change with `OQ-122`; adding layers later changes the contract.
- Impact on modules and APIs: `tilemap` depends on `core`, `graphics`, `render-api` and `resources`; no new module.
- Impact on tests and documentation: unit, loader contract (`test-support`) and headless integration tests; feature file `features/tile-map.md`; headless example and `api-index.md` entries; no new dependency, no `java.awt` (Android/DEX unaffected, `android-compatibility.md`).

## Validation

Accepted by the project owner on 2026-11-22 (explicit request to accept ADR-0009 in the repository, after issue #865 was accepted and closed). Owner decisions 1A to 6A were given on issue #865. Acceptance does not resolve any open question: `OQ-125` to `OQ-129` stay `TO BE DEFINED`, and `OQ-012`, `OQ-040` and `OQ-122` stay open. The requirements `REQ-MAP-003` to `REQ-MAP-007` stay PROPOSED in the requirements register, as for ADR-0002 and ADR-0003.
