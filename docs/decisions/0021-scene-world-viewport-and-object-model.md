# ADR-0021 — Scene, world, camera and viewport; single ECS object model; physics contracts

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner (definitions of scene, world and viewport; OQ-041 to OQ-044 explicitly delegated to the agent: "I let you decide what is best")
- Related requirements: `REQ-OBJ-001`, `REQ-OBJ-002`, `REQ-TRAIT-001`, `REQ-TRAIT-002`, `REQ-ECS-001` to `REQ-ECS-010`, `REQ-STD-005`, `REQ-CAM-004`, `REQ-MAP-002`, `REQ-SCENE-001`, `REQ-SCENE-002`, `REQ-WORLD-001`, `REQ-PHYS-001`
- Open questions resolved: `OQ-040`, `OQ-041`, `OQ-042`, `OQ-043`, `OQ-044`, `OQ-146`
- Supersedes / superseded by: none; completes [ADR-0011](0011-ecs-dynamic-objects-traits-and-standard-traits.md) (which stays unchanged) and refines the module table of `architecture/modules.md`

Source: owner answers in the session of 2026-11-23, tracked in the dedicated issue (see `open-questions.md`, Resolution log).

## Context

The owner defined: *Scene* = the current environment (a world, a background, music, a camera, ...); *World* = handles objects; *Viewport* (or camera) = what can be seen on screen from a point of view; the agent may change these definitions to match standards. [ADR-0011](0011-ecs-dynamic-objects-traits-and-standard-traits.md) accepted `ecs` and `traits` but left open whether ECS is the core model (`OQ-041`), whether traits also apply to classic objects (`OQ-042`), the relation of `world` to `ecs` (`OQ-146`), the home of the physics contracts (`OQ-043`) and the independence of `world` from `tilemap` (`OQ-044`).

## Decision

1. **Vocabulary** (aligned with Unity, Godot and libGDX usage):
   - **Camera**: headless world-side view (rectangle in world units, zoom, follow, shake), already [ADR-0010](0010-camera-system-in-camera.md).
   - **Viewport**: surface-side region in which a view is presented, in pixels of the logical frame (`render-api`).
   - **View**: the pairing of one camera with one viewport. A scene may have several (split screen, minimap). The owner's "viewport, or maybe camera" is therefore two complementary terms, not synonyms; no new type is created for the pairing before the render batch (`OQ-131`, `OQ-108`).
   - **World**: the container that owns the objects (ECS entities), their traits, queries, systems and the structural-change flush. Headless; no knowledge of rendering, resources, audio, camera or tile maps.
   - **Scene**: a self-contained unit of the running game with a lifecycle (enter, update, render, exit, dispose). It is the composition of a world, cameras, a background, music and other content. The owner's definition is kept.
2. **One object model: the ECS** (`OQ-041`). `ecs` of ADR-0011 is the core of the object model; there is no second "simple object" strategy. The engine as a whole stays modular: a game that uses no objects (a menu, a viewer) links neither `ecs` nor `traits`, which reconciles `vision.md` principle 7.
3. **Traits belong to ECS objects only** (`OQ-042`): a trait is data attached to an object ID; there are no traits on a different "classic object" type.
4. **The world is hosted by `ecs`** (`OQ-146`): `World` is the top-level container type of the `ecs` module. There is no separate `world` module. The conceptual row `world` of `modules.md` is removed; `multiplayer` depends on `ecs` instead. Several worlds can coexist (ADR-0011 invariant).
5. **The scene is a lifecycle contract in `runtime`**: the contract and a scene manager (exactly one active scene, switched at a frame boundary) live in `runtime`, which depends on `core` only, and know nothing about world, camera, graphics or audio. A scene implementation composes its content in game code. A ready-made composition (world + camera + background + music) would need `ecs`, `camera`, `graphics` and `audio-api`; it is deferred to an optional module (`OQ-155`).
6. **Physics contracts live in a new headless module `physics`** (`OQ-043`), depending on `core` only: collision shapes (axis-aligned box first), overlap tests, and the uniform-grid broad phase as plain-data algorithms, with no ECS type. `traits` (the `Collision` system of ADR-0011) and, later, `tilemap` tile collision (`OQ-125`) depend on `physics`; `tilemap` does not depend on `ecs`. Rigid bodies, joints and a physics engine are out of scope (`OQ-142`).
7. **`ecs` is independent of `tilemap`** (`OQ-044`), and `tilemap` of `ecs`. The two are connected only through contracts of `physics` (tile shapes queried by traits systems) and by the scene that owns both.

## Options considered

### Object model (`OQ-041`, `OQ-042`)
- A (chosen): ECS only. One model, designed in ADR-0011 for add/remove at runtime and 1,000 objects on Android; traits and systems are written once.
- B: classic objects and ECS, with a common contract. Two models to maintain and test, a lowest-common-denominator contract, and the owner's performance needs are only met by the ECS one.
- C: classic objects first, ECS optional (the earlier status quo). Delays the capabilities asked for and duplicates the model.

Cost of A: a learning curve for simple games, to be mitigated by the guide and examples, not by a second model.

### World placement (`OQ-146`)
- A (chosen): `World` inside `ecs`. Fewer modules; ADR-0011 already puts the registry, command buffer, flush and system scheduling in `ecs`, so a separate `world` would be an empty wrapper.
- B: thin `world` module over `ecs`. Only justified by scene-level content, which is composition, not world logic (decision 5).
- C: replace `ecs` by `world`. Renames an accepted module for no benefit.

### Scene placement
- A (chosen): lifecycle contract in `runtime`, content composed by the game. Keeps `runtime` free of content dependencies.
- B: `Scene` owning world, camera, background and music in a content module. Forces every game through one composition and a wide dependency set; deferred as an optional module (`OQ-155`).
- C: inside `world`. The scene would depend on camera, graphics and audio, breaking `world` headlessness.

### Physics contracts (`OQ-043`)
- A (chosen): a new `physics` module (shapes, overlap, broad phase) on `core`. Both `traits` and `tilemap` need shapes and neither may depend on the other.
- B: inside `traits`. `tilemap` would depend on ECS-level traits.
- C: inside `core`. Grows the cross-cutting module with a domain.
- D: inside `tilemap`. Inverts the direction for dynamic objects.

## Consequences

- Positive consequences: one object model; clear vocabulary; headless world and physics; no hidden dependency between `ecs`, `tilemap` and `camera`; the scene contract is testable with a manual loop.
- Costs / constraints: a new module `physics` (declared at the build skeleton, `OQ-149`); an optional composition module may be requested later (`OQ-155`); `multiplayer` depends on `ecs`; the "simple" strategy of `REQ-OBJ-002` is dropped.
- Impact on modules and APIs: `modules.md`, `contracts.md`, `overview.md`, `glossary.md`, `roadmap.md`, `vision.md`, `api-index.md`, `features/*`. ADR-0011 stays unchanged; its collision broad phase is hosted by `physics` and driven by the `Collision` system of `traits`.
- Impact on tests and documentation: new requirements `REQ-SCENE-001`, `REQ-SCENE-002`, `REQ-WORLD-001`, `REQ-PHYS-001`; `REQ-OBJ-002` reworded.

## Validation

Definitions provided by the project owner on 2026-11-23; decisions 2 to 7 taken by the agent under the owner's explicit delegation ("I let you decide what is best") and reversible by a superseding ADR. Follow-ups: `OQ-155`.
