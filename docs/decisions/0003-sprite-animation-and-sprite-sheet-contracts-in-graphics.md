# ADR-0003 — Sprite, animation and sprite sheet contracts in graphics

- Status: PROPOSED
- Date: 2026-11-22
- Decision makers: project owner (decisions 1C, 2, 3B, 4 of issue #852 given; this ADR stays `PROPOSED` until explicitly approved)
- Related requirements: `REQ-SPRITE-001` to `REQ-SPRITE-008`, `REQ-ANIM-001` to `REQ-ANIM-003`, `REQ-SHEET-001`, `REQ-SHEET-002`, `REQ-SPR-001`, `REQ-SPR-002`, `REQ-LOOP-006`, `REQ-CORE-005`, `REQ-DOC-001`, `REQ-DOC-002`, `REQ-RES-004`
- Open questions resolved: none (touches `OQ-051`, `OQ-054`, `OQ-011`, `OQ-012`, `OQ-118`; creates `OQ-100` to `OQ-108`)
- Supersedes / superseded by: none

> Revision note (joint review of packets #850, #852 and #853, under the owner's delegation of 2026-11-22): decision 4 no longer calls the source "resource-owned" (it contradicted decision 3B of issue #853, now [ADR-0005](0005-common-convention-for-modules-handling-resources.md)); `OQ-054` is listed as touched; the choices below were added. The ADR stays `PROPOSED`.

## Context

Sprites, animated sprites and sprite sheets are the base visual building blocks of games and of `tilemap`, which depends on graphics contracts. They need to be loaded through `resources`, cached, transformed (scale, translation, rotation, color), animated and rendered, without any frontend type in the API (`REQ-CORE-002`, `REQ-FE-003`) and with animation time driven only by the simulation update step (`REQ-LOOP-006`). The change adds public contracts inside the existing `graphics` module; no new module.

## Decision

Decisions taken by the owner (issue #852):

- **Color transformation (1C):** hybrid. A backend-independent color-transform *description* lives in `graphics`. Per-frame effects (fade, tint, alpha) go through `render-api` draw parameters. Static transforms (palette swap, grading, mask bake) are applied on the CPU and cached. The description is independent of the execution path, so a backend can always fall back to the CPU.
- **Multi-color templates (2, recommendation retained):** a template palette mapping (source color to target color, or indexed channel to target) applied as a static transform, producing a variant cached by (source image, mapping).
- **Batch rendering (3B):** sprite rendering goes through a render *command* abstraction; sprites emit commands and never draw directly. Batching is a later optimization behind it (`Could`, `REQ-SPRITE-008`). This assumes the abstract-command style for `OQ-054` (rendering API style), which stays open until this ADR is `ACCEPTED`.
- **Sprite model (4, recommendation retained):** a shared immutable **source** (image + region, loaded through `resources`, held by the `graphics` cache under the common convention of ADR-0005) is separate from a lightweight **sprite instance** (transform, color transform, location, alpha). Animated sprites and sheet cells produce instances or regions from the same source.

Choices made during the joint review of packets #850, #852 and #853, under the owner's delegation of 2026-11-22:

- **Units:** sprite location, translation, pivot and size are in pixels of the logical frame (ADR-0002). `graphics` performs no meter conversion; the conversion from world meters to pixels belongs to the camera/viewport (`OQ-012`), as ADR-0001 requires.
- **Rotation:** the unit and direction follow the engine-wide angle convention (`OQ-011`); `OQ-104` keeps pivot semantics, composition order and instance mutability.
- **Modifiable pixels (`REQ-SPR-001`):** the cached shared source is never mutated. Modifiable pixel data is a separately owned buffer; turning it into a source creates a new cache entry (`OQ-100`).
- **Image decoding:** platform-specific (no `java.awt` or `javax.imageio` on Android). Decoders sit in platform-specific code behind a `graphics` abstraction, as `audio-*` does for audio; their module placement is part of `OQ-100`.

Not decided (stay `TO BE DEFINED`): the backend-neutral image / pixel-buffer abstraction (`OQ-100`); cache key, scope, eviction, ownership and thread safety (`OQ-101`); capability model and CPU fallback rules (`OQ-102`); template authoring convention, key colors vs mask layer (`OQ-103`); rotation unit, pivot semantics, composition order and instance mutability (`OQ-104`); animation format and loader (`OQ-105`); sprite sheet id type and group lifecycle (`OQ-106`); loading thread and GPU upload (`OQ-107`); the render command contract in `render-api` (`OQ-108`); Java signatures. `OQ-051` is not resolved: decision 3B implies a `graphics` to `render-api` dependency, which is recorded as an assumption only while this ADR is `PROPOSED`. The owner comment also listed decisions 5 to 8, which the packet does not define; they were not applied.

## Options considered

### Where color transformation runs

- A: CPU only, results cached as variants. Backend-independent and easy to test, but costly for per-frame fades and memory-heavy.
- B: backend-side only. Cheap per frame, but capability varies per backend and Android, and `render-api` grows.
- C (retained): hybrid. Each effect uses the cheapest path; two execution paths to test and a capability query with CPU fallback needed.

### Batch rendering

- A: define a batching contract now. Speculative.
- B (retained): commands now, batching later behind them. Requires the command contract to exist (`OQ-108`).

## Consequences

- Positive consequences: many sprites share one image; effects use the cheapest path; no frontend type in `graphics`; batching can be added without breaking the contract; testable headless.
- Costs / constraints: two color-transform paths to keep equivalent; `render-api` gains draw parameters and a command contract; variant memory for baked transforms; cache keys and release follow the common convention (ADR-0005, `REQ-RES-004`).
- Impact on modules and APIs: new public contracts in `graphics` and additions in `render-api`; no new module; a `graphics` to `render-api` dependency is assumed (`OQ-051`) and must be confirmed before acceptance. Image decoding and pixel buffers must avoid `java.awt` in shared code; any decoding API or dependency needs its purpose, license and Android/DEX compatibility recorded first ([`../android-compatibility.md`](../android-compatibility.md), `OQ-100`).
- Impact on tests and documentation: unit tests (transforms, palette mapping, animation state machine, grid index math); contract suites in `test-support` (sprite render and cache); headless integration (load, cache, render); platform color-transform parity and performance tests. Game-loop invariant untouched: animation advances in the update step only. Documents updated: requirements, traceability, open questions, modules, contracts, glossary, roadmap, testing, API index, game development guide, three feature files.

## Validation

Not approved yet. Owner decisions 1C, 2, 3B, 4 were given on issue #852; the ADR remains `PROPOSED` until the owner explicitly accepts it.
