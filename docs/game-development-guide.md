# Guide: building a game with LionEngine

**Status: PROPOSED — skeleton. Every section is `TO BE DEFINED` until the matching engine feature and its compiled example exist. Do not fill it from memory of other engines or earlier LionEngine versions.**

Audience: a developer or an AI agent writing a game on top of the engine, who has never seen this API.

## Rules for this guide

- Every code snippet is **included from a compiled example** in the `examples` module (REQ-DOC-001). No snippet is written by hand here: a hand-written snippet drifts and misleads agents.
- Keep it short: a path from "nothing" to "a small game", not a reference. The reference is the Javadoc; the lookup table is [`api-index.md`](api-index.md).
- Each section names the example it comes from and the requirements it illustrates.

## Outline

1. **Prerequisites** — Java/Maven versions (ADR-0001), how to depend on the engine (OQ-080).
2. **Hello engine** — the smallest headless application: start, update, stop. *Example: to be created in Phase 1.*
3. **Concepts, in order of use** — runtime and game loop, resources, graphics (sprite, animation), world (objects, traits or ECS: OQ-041), tilemap, input, audio.
4. **Recipes** — one short, tested recipe per common task:
   - display and animate a sprite;
   - load a map and scroll the camera;
   - read player input;
   - detect a collision;
   - play a sound;
   - run the game headless in a test.
5. **Pitfalls**
   - World units are meters and seconds; pixels exist only at presentation (ADR-0001).
   - An extrapolated state is never reused as simulation input.
   - Closing and ownership of resources (REQ-RES-003).
   - Not every capability exists on every platform (OQ-034); check before using.
   - Screen mode, display mode and filter requests are best-effort: read the effective result instead of assuming it (ADR-0002, PROPOSED).
   - Android compatibility of any added dependency or Java API ([`android-compatibility.md`](android-compatibility.md)).
6. **Differences from earlier LionEngine versions** — a table of renamed/removed concepts, so that agents do not reuse the old API from memory. *Filled when the v11 API exists.*
7. **Where to look next** — [`api-index.md`](api-index.md), Javadoc, `examples`.
