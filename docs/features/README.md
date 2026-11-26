# Feature pages

**Status: VALIDATED — index of the feature pages, in implementation order. A batch starts from its feature page.**

A feature page is the entry point of an implementation batch: it restates every requirement, default value and rule the batch needs, and links the ADRs that justify them. An agent implements from the page; it reads the ADRs only to understand why. If the page and an ADR disagree, the ADR wins and the page is fixed in the same pull request (see [`../documentation.md`](../documentation.md), "Which document wins").

| Phase | Page | Modules | Requirements |
|---|---|---|---|
| 0 | [`build-skeleton.md`](build-skeleton.md) | all (empty), `android/` | REQ-TECH-*, REQ-CORE-003, REQ-CORE-005, REQ-DOC-* |
| 0 | [`contract-skeleton.md`](contract-skeleton.md) | `core`, `runtime`, `resources`, `render-api`, `platform-api` (cross-module contracts of [`../architecture/interfaces.md`](../architecture/interfaces.md)) | REQ-CORE-003, REQ-CORE-004, REQ-CORE-005, REQ-TECH-006, REQ-TECH-008 |
| 1 | [`core-foundation.md`](core-foundation.md) | `core` | REQ-CORE-006, REQ-DEF-*, REQ-RT-007, REQ-DET-003 |
| 1–2 | [`game-loop.md`](game-loop.md) | `runtime` | REQ-LOOP-*, REQ-RT-001 to REQ-RT-004, REQ-RT-009, REQ-RT-010 |
| 1–2 | [`scenes.md`](scenes.md) | `runtime` | REQ-SCENE-* |
| 1 | [`resources.md`](resources.md) | `resources`, `resources-*` | REQ-RES-* |
| 1–2 | [`platform-and-rendering.md`](platform-and-rendering.md) | `platform-api`, `render-api`, `platform-awt`, `render-awt`, `test-support` | REQ-FE-*, REQ-PLATFORM-*, REQ-RT-008, REQ-SPRITE-010 |
| 2 | [`sprite-sheet.md`](sprite-sheet.md) | `graphics` | REQ-SHEET-*, REQ-SPRITE-009 |
| 2 | [`sprite.md`](sprite.md) | `graphics` | REQ-SPRITE-* |
| 2 | [`animated-sprite.md`](animated-sprite.md) | `graphics` | REQ-ANIM-* |
| 2 | [`display-modes.md`](display-modes.md) | `platform-api`, `platform-awt`, `render-api` | REQ-DISPLAY-* |
| 2 | [`output-filtering.md`](output-filtering.md) | `render-api`, `render-awt`, `render-opengl` | REQ-FILTER-* |
| 2 | [`audio-short-and-long-sounds.md`](audio-short-and-long-sounds.md) | `audio-api`, `audio-*` | REQ-AUDIO-* |
| 3 | [`ecs.md`](ecs.md) | `ecs` | REQ-ECS-*, REQ-WORLD-001 |
| 3 | [`standard-traits.md`](standard-traits.md) | `traits`, `physics` | REQ-STD-*, REQ-PHYS-001 |
| 3 | [`tile-map.md`](tile-map.md) | `tilemap` | REQ-MAP-* |
| 3 | [`camera.md`](camera.md) | `camera` | REQ-CAM-* |
| 3–4 | [`input.md`](input.md) | `input-api`, `input-awt`, `input-android` | REQ-IN-* |
| 5 | [`network.md`](network.md) | `network` | REQ-NET-* |
| 5 | [`multiplayer.md`](multiplayer.md) | `multiplayer`, `traits` | REQ-MP-004 to REQ-MP-015, REQ-STD-011 |

## Batch status

Where the implementation stands, batch by batch: the numbers are those of the "Batches" section of each page. Every batch updates its row in the pull request that implements it, so the merged table always tells the next agent where to start. Details of a finished batch are in the "State" section of its page.

Next batch: [`audio-short-and-long-sounds.md`](audio-short-and-long-sounds.md) batch 5 (first-batch controls: volume, looping, pause, resume; [`output-filtering.md`](output-filtering.md) batch 4 waits for the license record of `HQ2X` and for `render-opengl` in Phase 4), the first unfinished batch in the order of the table; [`resources.md`](resources.md) batch 4 is reserved for the local agents ([`../local-agents.md`](../local-agents.md)).

| Phase | Page | Batches | Done | Remaining |
|---|---|---|---|---|
| 0 | [`build-skeleton.md`](build-skeleton.md) | items 1 to 6 | all: Maven skeleton (2026-11-24), Android/DEX job (2026-11-25, #909) | none |
| 0 | [`contract-skeleton.md`](contract-skeleton.md) | 3 | 1 to 3 (2026-11-24) | none |
| 1 | [`core-foundation.md`](core-foundation.md) | 4 | 1 to 4 (2026-11-24) | none |
| 1–2 | [`game-loop.md`](game-loop.md) | 4 | 1 to 3 (2026-11-24), 4 (2026-11-26) | none |
| 1–2 | [`scenes.md`](scenes.md) | 4 | 1 (2026-11-24), 2 (2026-11-26, #935), 3 (2026-11-26, #936), 4 (2026-11-26, #937) | none |
| 1 | [`resources.md`](resources.md) | 4 | 1 and 2 (2026-11-24), 3 (2026-11-25, with sprite sheet batch 2) | 4 (`resources-archive`; `resources-android` later) |
| 1–2 | [`platform-and-rendering.md`](platform-and-rendering.md) | 3 | 1 and 2 (2026-11-24), 3 (2026-11-25) | none |
| 2 | [`sprite-sheet.md`](sprite-sheet.md) | 4 | 1 to 4 (2026-11-25) | none |
| 2 | [`sprite.md`](sprite.md) | 4 | 1 to 3 (2026-11-26), 4 (2026-11-26, #931) | none; `REQ-SPRITE-004` parity waits for the OpenGL and Android backends |
| 2 | [`animated-sprite.md`](animated-sprite.md) | 3 | 1 (2026-11-26, #932), 2 (2026-11-26, #933), 3 (2026-11-26, #934) | none |
| 2 | [`display-modes.md`](display-modes.md) | 4 | 1 (2026-11-26, #938), 2 (2026-11-26, #939), 3 (2026-11-26, #940, delivered by earlier batches and recorded), 4 (2026-11-26, #941, review; `OQ-174` raised) | none |
| 2 | [`output-filtering.md`](output-filtering.md) | 4 | 1 (2026-11-26, #942), 2 (2026-11-26, #943), 3 (2026-11-26, #944) | 4 (`HQ2X` after its license record; GPU filters and CRT with `render-opengl`, Phase 4) |
| 2 | [`audio-short-and-long-sounds.md`](audio-short-and-long-sounds.md) | 8 | 1 (2026-11-26, #945), 2 (2026-11-26, #948), 3 (2026-11-26, #949), 4 (2026-11-26) | 5 to 8 (`audio-android` in Phase 4) |
| 3 | [`ecs.md`](ecs.md) | 6 | none | 1 to 6 |
| 3 | [`standard-traits.md`](standard-traits.md) | 7 | none | 1 to 7 |
| 3 | [`tile-map.md`](tile-map.md) | 5 | none | 1 to 5 |
| 3 | [`camera.md`](camera.md) | 5 | none | 1 to 5 |
| 3–4 | [`input.md`](input.md) | 5 | none | 1 to 5 (`input-android` in Phase 4) |
| 5 | [`network.md`](network.md) | 7 | none | 1 to 7 |
| 5 | [`multiplayer.md`](multiplayer.md) | 7 | none | 1 to 7 |

Every page here is summarized for readers in [`../engine-features.md`](../engine-features.md); a batch that changes a capability updates that summary too (checked by `scripts/check-docs.py` for links).

Not yet paged (written when scheduled): `composition` (Phase 3, ADR-0031, ADR-0048; declared empty by the build skeleton), `render-opengl` and the Android frontend (Phase 4). New pages start from [`../templates/FEATURE-TEMPLATE.md`](../templates/FEATURE-TEMPLATE.md).
