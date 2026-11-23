# ADR-0028 — Tile maps: collision, JSON definition files, auto-tiling, tileset ownership, storage

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner ("Confirmed with JSON, write the ADR"; the owner stated "I don't need to keep XML if better exists")
- Related requirements: `REQ-MAP-001` to `REQ-MAP-008`, `REQ-STD-005`, `REQ-DEF-001`, `REQ-ANIM-001`, `REQ-RT-007`
- Open questions resolved: `OQ-125` (first batch scope; further shapes `OQ-142`), `OQ-126`, `OQ-127`, `OQ-128`, `OQ-129`; the format part of `OQ-154`
- Supersedes / superseded by: none; completes [ADR-0009](0009-tile-map-contracts-in-tilemap.md), [ADR-0020](0020-no-reflection-factory-registry-and-capability-lookup.md) and [ADR-0026](0026-graphics-pixels-cache-transforms-animation-and-commands.md) (all unchanged)

## Context

ADR-0009 left tile collision, the map format, the auto-tiling rules, tileset ownership and large-map storage open. ADR-0013 left the tile row order open. ADR-0020 and ADR-0026 left the definition file format open (XML or other). ADR-0021 created `physics`.

## Decision

1. **Tile collision (`OQ-125`).** Each tile has a collision property read from its tileset definition. First set: empty or solid (full tile box); one-way platforms and slopes later (`OQ-142`). `tilemap` implements a shape-query contract of `physics`; the `Collision` system of `traits` asks which tile boxes overlap a box. `tilemap` never depends on `ecs`. A map has a world placement used only by collision: an origin and a tile size in meters, default 1 m per tile (ADR-0014). Rendering stays in pixels. Setting or destroying a tile changes its collision immediately, in the update phase, with the existing tile-changed notification (`REQ-MAP-007`).
2. **Tile coordinates and row order.** Tile coordinates have their origin at the bottom-left with Y up, like the world (ADR-0013). Loaders flip rows coming from top-down formats (Tiled, image files).
3. **Definition format (`OQ-126`).** JSON for every definition file of the engine: sprite sheets, animations (ADR-0026), maps, tilesets, object definitions (ADR-0020). Standard JSON only, no extensions; documentation goes in an optional `"comment"` field. The reader is a small strict reader written by the project, in `core` (pure Java, no dependency, deterministic on every platform, headless-testable). The first real map format is Tiled JSON (`.tmj`, `.tsj`), loaded through the abstract loader contract of ADR-0009; the loader is one implementation of it. XML is not used. A packaged binary form may be added later (`OQ-162`).
4. **Auto-tiling (`OQ-127`).** A rule is a bitmask of the 8 neighbors plus a care mask saying which neighbors matter. 4-neighbor tiling is a rule ignoring the corners. Rules are tested in order, the first match wins, and are defined in the tileset definition.
5. **Tileset ownership (`OQ-128`).** A map refers to its tileset by the sprite sheet string id of ADR-0026 (ADR-0007 key form). The map holds a reference to the sheet group through reference counting and releases it when the map closes; it never owns image data. The scene that loads a map owns it and closes it on exit (ADR-0021).
6. **Storage and render cache (`OQ-129`).** First batch: a flat row-major `int[]` cell array and viewport culling only (about 4 MB for 1000 × 1000 cells; the render cost does not depend on map size, `REQ-MAP-005`). Chunked storage and pre-rendered chunk caches are added only when a benchmark shows a need (budgets: ADR-0024).

## Options considered

- Definition format: JSON with an own strict reader (chosen) vs XML (string-only values, DOM allocations, platform parser to verify, TMX CSV and base64 tile data) vs YAML or TOML (heavier parsers) vs a third-party JSON library (dependency review, `OQ-148`) vs binary only (not editable by hand or by agents).
- Row order: bottom-left Y up (chosen, consistent with the world) vs top-left (an extra flip in every consumer).
- Collision: `physics` shape-query contract implemented by `tilemap` (chosen) vs `tilemap` depending on `ecs` or `traits`.
- Storage: flat `int[]` first (chosen; wasteful for very large sparse maps) vs chunked now (premature).

## Consequences

- Positive consequences: one typed definition format, with no dependency, matching Tiled's native export; no map dependency on the object model; consistent coordinates.
- Costs / constraints: a JSON reader to write and test; standard JSON has no comments; old XML files of the previous engine need a one-off converter outside the engine; very large sparse maps waste memory until chunking exists.
- Impact on modules and APIs: `core` (JSON reader), `physics` (shape-query contract), `tilemap` (loader, collision provider, auto-tiling).
- Impact on tests and documentation: `REQ-MAP-008` and `REQ-DEF-001` added; `features/tile-map.md`, `contracts.md`, `glossary.md`, `api-index.md`, `modules.md` updated.

## Validation

Decisions confirmed by the project owner on 2026-11-23. Follow-ups: `OQ-162`.
