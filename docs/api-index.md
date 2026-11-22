# API index

**Status: PROPOSED — template. Empty until the first public contracts exist.**

Purpose: let a developer or an AI agent find, by **task**, which public types to use and which example to copy, without reading the whole code base.

## Rules

- Every public contract (REQ-CORE-004) appears here with its module, entry-point types and the example that uses it.
- Only list types that exist. An agent must never use a type that is not in this index, the code or the Javadoc.
- Keep one line per task. Details belong in the Javadoc.
- Keeping this index in sync with the code (manual review vs generated, CI check): **TO BE DEFINED** (OQ-083).

## Index by task

| Task | Module | Entry-point types | Example to copy | Since | Notes (units, lifecycle, capability) |
|---|---|---|---|---|---|
| Start, update and stop an application | `runtime` | *(to be filled)* | *(to be created)* | — | Loop model: ADR-0001 |
| Load a resource | `resources` | *(to be filled)* | *(to be created)* | — | Closing rules: REQ-RES-003; keys and idempotent release: REQ-RES-005 (ADR-0007) |
| Display and animate a sprite | `graphics` | *(to be filled; ADR-0003 ACCEPTED)* | *(to be created: headless example loading a sprite sheet, animating a sprite and rendering two color variants of one sprite)* | — | Pixels for images and cells, seconds for animation; world in meters, pixels at presentation only (REQ-SPRITE-*, REQ-ANIM-*, REQ-SHEET-*) |
| Read player input | `input-api` | *(to be filled)* | *(to be created)* | — | Capability may be absent (OQ-034) |
| Create objects and attach behaviors | `world` / `traits` / `ecs` | *(to be filled)* | *(to be created)* | — | Model: OQ-041 |
| Load a tile map | `tilemap` | *(to be filled)* | *(to be created)* | — | |
| Query and request screen mode, display mode and output filter | `platform-api` / `render-api` (desktop implementer: `platform-awt`, ADR-0008) | *(to be filled; ADR-0002 ACCEPTED)* | *(to be created: headless example listing capabilities, requesting a mode and a filter, printing the effective result)* | — | Pixels, Hz, bits; scaling policy `FIT` by default; scaling never changes world units; the effective result arrives at the frame boundary, never as the return value of apply (REQ-DISPLAY-*, REQ-FILTER-*, ADR-0006) |
| Play a short sound, a variant set, a long sound or a playlist | `audio-api` | *(to be filled; ADR-0004 ACCEPTED)* | *(to be created: headless example with a fake backend playing a short sound, a variant set and a two-track playlist)* | — | Seconds and Hz; formats depend on backend (OQ-115); completion callbacks never mutate simulation state (REQ-AUDIO-003 to REQ-AUDIO-009) |

## Removed or renamed APIs

| Old name (earlier LionEngine versions) | Replacement in this version | Since |
|---|---|---|
| *(to be filled when the v11 API exists)* | | |
