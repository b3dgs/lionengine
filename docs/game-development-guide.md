# Guide: building a game with LionEngine

**Status: VALIDATED — outline. Each section is filled only when the matching engine feature and its compiled example exist; until then a section has no code. Do not fill it from memory of other engines or earlier LionEngine versions.**

Audience: a developer or an AI agent writing a game on top of the engine, who has never seen this API.

## Rules for this guide

- Every code snippet is **included from a compiled example** in the `examples` module (REQ-DOC-001). No snippet is written by hand here: a hand-written snippet drifts and misleads agents.
- Keep it short: a path from "nothing" to "a small game", not a reference. The reference is the Javadoc; the lookup table is [`api-index.md`](api-index.md).
- Each section names the example it comes from and the requirements it illustrates.

## Outline

1. **Prerequisites** — Java/Maven versions (ADR-0001), how to depend on the engine: pinned Maven version with sources and Javadoc JARs (ADR-0023).
2. **Hello engine** — the smallest headless application: start, update, stop. *Example: `GameLoopExample` (runtime), `HeadlessGameTest` (a game without a window); see [`api-index.md`](api-index.md).*
3. **Concepts, in order of use** — start with [`how-the-engine-works.md`](how-the-engine-works.md); then runtime and game loop, scenes, resources and definition files, graphics (sprite, animation), world and objects (ECS, ADR-0021), tilemap, camera, input, audio.
4. **Recipes** — one short, tested recipe per common task:
   - load images once and share them between scenes (example `ImageCacheExample`); cut an image into a sprite sheet and group sheets (example `SpriteSheetExample`); load a sheet from its definition file (example `SheetLoaderExample`); load in the background behind a loading screen (example `LoadingScreenExample`);
   - display a sprite, an image or a frame of a sheet, moved, scaled and rotated about its pivot (example `SpriteExample`); animate it, and show two color variants of one sprite (ADR-0003; examples to be created);
   - load a map and scroll the camera (camera contract: [`features/camera.md`](features/camera.md), ADR-0010);
   - read player input;
   - detect a collision;
   - play a sound: short sound, variant set, long sound, playlist (ADR-0004; example to be created);
   - run the game headless in a test.
5. **Pitfalls**
   - World units are meters and seconds; pixels exist only at presentation (ADR-0001).
   - An extrapolated state is never reused as simulation input.
   - Closing and ownership of resources (REQ-RES-003); modules handling resources follow one convention for keys and release: normalized logical path keys, idempotent release (REQ-RES-004, REQ-RES-005, ADR-0007).
   - Caches, sheet groups and their handles belong to the runtime thread, the thread of their first operation: use them from scenes and inbox events, never from a loading worker or a platform callback, which get an `IllegalStateException` (ADR-0018, ADR-0055).
   - Audio completion callbacks (playlist advance) must not mutate simulation state from the audio thread (ADR-0004).
   - Not every capability exists on every platform; look it up on the platform object before using it (ADR-0020).
   - Objects created or removed during a step exist or vanish only at the end of the step (the flush, ADR-0030).
   - Read input through actions; a press is seen by exactly one step (ADR-0035).
   - Every data file is strict JSON with `format` and `version`; an unknown field is an error (ADR-0032).
   - Simulation randomness comes from the world's seeded generator, never `Math.random` or `java.util.Random` (ADR-0031).
   - Animation time advances in the update step only; a sprite emits render commands and never draws directly (ADR-0003).
   - Screen mode, display mode and filter requests are best-effort: read the effective result, delivered at the frame boundary, instead of assuming it (ADR-0002, ADR-0006).
   - Android compatibility of any added dependency or Java API ([`android-compatibility.md`](android-compatibility.md)).
6. **Differences from earlier LionEngine versions** — a table of renamed/removed concepts, so that agents do not reuse the old API from memory. *Filled when the v11 API exists.*
7. **Where to look next** — [`api-index.md`](api-index.md), Javadoc, `examples`.
