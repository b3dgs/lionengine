# API index

**Status: VALIDATED — structure of the index. Rows name tasks and modules now; types and examples are filled by the batch that creates them.**

Purpose: let a developer or an AI agent find, by **task**, which public types to use and which example to copy, without reading the whole code base.

## Rules

- Every public contract (REQ-CORE-004) appears here with its module, entry-point types and the example that uses it.
- Only list types that exist. An agent must never use a type that is not in this index, the code or the Javadoc.
- Keep one line per task. Details belong in the Javadoc.
- The index stays hand-written, by task; a CI check verifies its types and examples once the build skeleton exists (ADR-0023, `REQ-DOC-005`).

## Index by task

| Task | Module | Entry-point types | Example to copy | Since | Notes (units, lifecycle, capability) |
|---|---|---|---|---|---|
| Start, update and stop an application | `runtime` | *(to be filled)* | *(to be created)* | — | Fixed step 60 Hz, interpolation with `alpha`, fail-fast (ADR-0017, ADR-0031) |
| Switch scenes and show a pause overlay | `runtime` | *(to be filled)* | *(to be created)* | — | Switch at a frame boundary; overlay stack in a second batch (REQ-SCENE-001 to REQ-SCENE-003) |
| Read a definition file and register a data-driven type | `core` | *(to be filled)* | *(to be created)* | — | Strict JSON, `format` and `version`, ids `namespace:name` (REQ-DEF-001 to REQ-DEF-003, REQ-RT-007) |
| Load a resource | `resources` | *(to be filled)* | *(to be created)* | — | Closing rules: REQ-RES-003; keys and idempotent release: REQ-RES-005 (ADR-0007) |
| Display and animate a sprite | `graphics` | *(to be filled; ADR-0003 ACCEPTED)* | *(to be created: headless example loading a sprite sheet, animating a sprite and rendering two color variants of one sprite)* | — | Pixels for images and cells, seconds for animation; world in meters, pixels at presentation only (REQ-SPRITE-*, REQ-ANIM-*, REQ-SHEET-*) |
| Read player input | `input-api` | *(to be filled)* | *(to be created: headless example binding keys to actions and reading per-step edges)* | — | Actions, not devices; one step sees each edge; capability may be absent (REQ-IN-001 to REQ-IN-005, ADR-0035) |
| Create objects and attach behaviors | `ecs` / `traits` | *(to be filled)* | *(to be created)* | — | Single ECS model (ADR-0021) |
| Create and remove objects by ID, add and remove traits at runtime, query and run systems | `ecs` | *(to be filled; ADR-0011 ACCEPTED)* | *(to be created: headless example creating objects, attaching and detaching traits, iterating a query and adding a game-defined trait and system)* | — | `long` IDs with stale detection; structural changes applied at the flush; no allocation per step; seconds (REQ-ECS-001 to REQ-ECS-013, [`features/ecs.md`](features/ecs.md)) |
| Move, apply gravity, jump, collide and expire objects with the standard traits | `traits` | *(to be filled; ADR-0011 ACCEPTED)* | *(to be created: headless example of a falling, jumping and colliding object)* | — | Meters, m/s, m/s², seconds; fixed system order (REQ-STD-001 to REQ-STD-010, [`features/standard-traits.md`](features/standard-traits.md)) |
| Load a tile map | `tilemap` | *(to be filled; ADR-0009 ACCEPTED)* | *(to be created: headless example loading a map)* | — | Loader contract over `resources`; format: Tiled JSON (ADR-0028) (REQ-MAP-004) |
| Create and edit a tile map manually | `tilemap` | *(to be filled)* | *(to be created)* | — | Tile indices `int`, tile size in pixels of the logical frame (REQ-MAP-003) |
| Destroy a tile and render a tile map | `tilemap` | *(to be filled)* | *(to be created: build, load, destroy a tile, render)* | — | Update phase only for changes; rendering read-only, viewport in pixels (REQ-MAP-005 to REQ-MAP-007) |
| Move, zoom, follow and shake a camera | `camera` | *(to be filled; ADR-0010 ACCEPTED)* | *(to be created: headless example following an object, zooming and shaking)* | — | Meters, seconds, dimensionless zoom; update phase only; rendering reads the view transform (REQ-CAM-001 to REQ-CAM-010) |
| Query and request screen mode, display mode and output filter | `platform-api` / `render-api` (desktop implementer: `platform-awt`, ADR-0008) | *(to be filled; ADR-0002 ACCEPTED)* | *(to be created: headless example listing capabilities, requesting a mode and a filter, printing the effective result)* | — | Pixels, Hz, bits; scaling policy `FIT` by default; scaling never changes world units; the effective result arrives at the frame boundary, never as the return value of apply (REQ-DISPLAY-*, REQ-FILTER-*, ADR-0006) |
| Play a short sound, a variant set, a long sound or a playlist | `audio-api` | *(to be filled; ADR-0004 ACCEPTED)* | *(to be created: headless example with a fake backend playing a short sound, a variant set and a two-track playlist)* | — | Seconds and Hz; WAV everywhere, OGG Vorbis per backend; completion goes through the inbox (REQ-AUDIO-001 to REQ-AUDIO-013, ADR-0027, ADR-0033) |

## Removed or renamed APIs

| Old name (earlier LionEngine versions) | Replacement in this version | Since |
|---|---|---|
| *(to be filled when the v11 API exists)* | | |
