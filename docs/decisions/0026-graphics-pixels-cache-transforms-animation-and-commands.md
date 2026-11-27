# ADR-0026 — Graphics: pixel buffer and decoders, image cache, color transforms, pivot, animation, sheet ids, loading and render commands

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner ("Confirmed as proposed, write the ADR")
- Related requirements: `REQ-SPR-001`, `REQ-SPR-002`, `REQ-SPRITE-001` to `REQ-SPRITE-008`, `REQ-ANIM-001` to `REQ-ANIM-003`, `REQ-SHEET-001`, `REQ-SHEET-002`, `REQ-RT-005`
- Open questions resolved: `OQ-100`, `OQ-101`, `OQ-102`, `OQ-103`, `OQ-104`, `OQ-105`, `OQ-106`, `OQ-107`, `OQ-108`
- Supersedes / superseded by: none; completes [ADR-0003](0003-sprite-animation-and-sprite-sheet-contracts-in-graphics.md) (unchanged)

## Context

ADR-0003 fixed the sprite model (shared immutable source, lightweight instance), the hybrid color transform, template mapping, command-based rendering and platform-specific decoding, and left nine points open. Since then ADR-0007 (keys), ADR-0014 (rotation), ADR-0018 and ADR-0019 (threads, loading, release), ADR-0020 (registry and string ids) and ADR-0021 (scene lifecycle) settled most of their constraints.

## Decision

1. **Pixels and decoders (`OQ-100`).** `graphics` defines its own backend-neutral `int[]` ARGB pixel buffer; no `java.awt` type in any API. `graphics` includes a pure-Java PNG decoder built on `java.util.zip.Inflater` (available on Android), so PNG behaves identically everywhere and in headless tests. Other formats come from optional platform decoder modules (desktop ImageIO, Android `BitmapFactory`), registered through the explicit registry of ADR-0020. Modifiable pixel data is a separate buffer owned by the caller; turning it into a source copies it into a new cache entry. The cached source is never mutated.
2. **Image cache (`OQ-101`).** Owned by a `graphics` instance (no static state). Key: the resource key (ADR-0007) plus a discriminator (for example the palette mapping). Explicit reference counting and release, no automatic eviction, a size limit only when configured (ADR-0019). Touched on the runtime thread only (ADR-0018).
3. **Color transforms (`OQ-102`).** `render-api` exposes a typed capability query for per-frame effects; the first set is tint (multiplied color) and alpha. An effect the backend cannot apply per frame (palette swap, grading) is baked once on the CPU and cached. The transform description stays backend-independent (ADR-0003).
4. **Templates (`OQ-103`).** Authoring by exact key colors: one image plus a mapping table in the sheet definition mapping each key color to its target. A mask layer is a later extension for anti-aliased art.
5. **Pivot and transforms (`OQ-104`).** The pivot is in pixels from the frame's top-left corner, default the frame center. Composition order: scale, then rotation, then translation, all about the pivot. Rotation is counter-clockwise as seen on screen, in degrees (ADR-0014); the sprite handles the screen Y flip. A sprite instance is mutable and lightweight, changed in the update phase only, allocating nothing (ADR-0019).
6. **Animation (`OQ-105`).** An animation is a list of frame indices, a frame duration in seconds and a loop mode (once, loop, ping-pong). It is defined in the sprite sheet definition data or built in code; the file format follows the definition-format decision (`OQ-154`, `OQ-126`), with no separate format. Time advances only with the simulation step (`REQ-LOOP-006`). Per-frame duration overrides are a later extension.
7. **Sheet ids and groups (`OQ-106`).** The id is a string in the key form of ADR-0007, consistent with ADR-0020. A group is owned by the `graphics` cache through reference counting; whoever acquires a group releases it, normally the scene on exit (ADR-0021).
8. **Loading and upload (`OQ-107`).** Synchronous by default; asynchronous loading is opt-in, with decoding only on workers (ADR-0019). GPU upload happens on the render thread at first use, or at an explicit "prepare" call on the runtime thread, never on a worker.
9. **Render commands (`OQ-108`).** Commands are plain value entries in a preallocated buffer (no allocation per frame). Each carries a texture handle, a source rectangle, a destination transform with pivot, a color or alpha, and an explicit layer for ordering. Sprites emit commands only; batching stays a later optimization behind the buffer. Exact field types are left to the batch design (`OQ-160`).

## Options considered

- Decoding: own PNG decoder plus optional platform decoders (chosen) vs platform decoders only (no headless determinism, platform code in every build) vs a third-party decoder library (license and DEX checks).
- Template authoring: exact key colors (chosen; cannot express smooth gradients) vs mask layer (two files per sprite).
- Pivot default: center (chosen) vs top-left (surprising for rotation).
- Cache: reference-counted, no eviction (chosen) vs automatic eviction (non-deterministic, forbidden by ADR-0019).

## Consequences

- Positive consequences: no platform code or dependency in the graphics base; deterministic headless tests; one lifecycle rule from cache to scene.
- Costs / constraints: a PNG decoder to write and test; few per-frame effects at first; key colors only.
- Impact on modules and APIs: `graphics` (pixel buffer, decoder contract, PNG decoder), `render-api` (capability query, command buffer), optional decoder modules.
- Impact on tests and documentation: `features/sprite*.md`, `sprite-sheet.md`, `animated-sprite.md`, `contracts.md`, `modules.md`, `android-compatibility.md`, traceability updated; no new requirement.

## Validation

Decisions confirmed by the project owner on 2026-11-23. Follow-ups: `OQ-160`.
