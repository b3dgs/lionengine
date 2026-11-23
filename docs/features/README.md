# Feature pages

**Status: VALIDATED — index of the feature pages, in implementation order. A batch starts from its feature page.**

A feature page is the entry point of an implementation batch: it restates every requirement, default value and rule the batch needs, and links the ADRs that justify them. An agent implements from the page; it reads the ADRs only to understand why. If the page and an ADR disagree, the ADR wins and the page is fixed in the same pull request (see [`../documentation.md`](../documentation.md), "Which document wins").

| Phase | Page | Modules | Requirements |
|---|---|---|---|
| 0 | [`build-skeleton.md`](build-skeleton.md) | all (empty), `android/` | REQ-TECH-*, REQ-CORE-003, REQ-CORE-005, REQ-DOC-* |
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

Every page here is summarized for readers in [`../engine-features.md`](../engine-features.md); a batch that changes a capability updates that summary too (checked by `scripts/check-docs.py` for links).

Not yet paged (written when scheduled): `composition` (Phase 3, ADR-0031), `render-opengl` and the Android frontend (Phase 4). New pages start from [`../templates/FEATURE-TEMPLATE.md`](../templates/FEATURE-TEMPLATE.md).
