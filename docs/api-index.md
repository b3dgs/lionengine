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
| Load a resource | `resources` | *(to be filled)* | *(to be created)* | — | Closing rules: REQ-RES-003 |
| Display and animate a sprite | `graphics` | *(to be filled)* | *(to be created)* | — | World in meters; pixels at presentation only |
| Read player input | `input-api` | *(to be filled)* | *(to be created)* | — | Capability may be absent (OQ-034) |
| Create objects and attach behaviors | `world` / `traits` / `ecs` | *(to be filled)* | *(to be created)* | — | Model: OQ-041 |
| Load a tile map | `tilemap` | *(to be filled)* | *(to be created)* | — | |
| Query and request screen mode, display mode and output filter | `platform-api` / `render-api` | *(to be filled; ADR-0002 PROPOSED)* | *(to be created: headless example listing capabilities, requesting a mode and a filter, printing the effective result)* | — | Pixels, Hz, bits; scaling never changes world units; effective result reported (REQ-DISPLAY-*, REQ-FILTER-*) |
| Play a sound | `audio-api` | *(to be filled)* | *(to be created)* | — | Formats depend on backend |

## Removed or renamed APIs

| Old name (earlier LionEngine versions) | Replacement in this version | Since |
|---|---|---|
| *(to be filled when the v11 API exists)* | | |
