# Engine vision

**Status: VALIDATED**

## Purpose

LionEngine is a 2D game engine written in Java. It provides a reusable foundation for building games without imposing a frontend or a rendering mode. Its object model is a single ECS (ADR-0021), used only by games that need objects.

## Guiding principles

1. **Independent core**: engine rules do not depend on the graphics, audio or input APIs of a particular system.
2. **Interchangeable frontends**: desktop AWT, OpenGL and Android are integrations, not dependencies of the core.
3. **Testable abstractions**: essential behaviors can be tested without a window or hardware.
4. **Controlled extensibility**: new features are integrated through documented contracts.
5. **Composition**: object behaviors are composable through traits and systems.
6. **Implementation choice where constraints differ**: several implementations of one contract may coexist (game loops, renderers, audio backends, resource providers); the object model is deliberately one (ADR-0021).
7. **No premature over-architecture**: ECS, networking and GPU rendering are not imposed on every project: a game that uses no objects links neither `ecs` nor `traits`, `network` and `multiplayer` are optional, and the AWT renderer needs no GPU.
8. **Predictability**: fixed-step deterministic simulation, explicit lifetimes, no reflection, no hidden threads (ADR-0017 to ADR-0020).

## Functional scope

- lifecycle, game loop and scenes;
- resource access independent of origin, JSON definition files;
- audio: short sounds, long sounds, variant sets, playlists;
- images, sprites, animations and sprite sheets;
- display modes, scaling and output filters;
- tile maps and tilesets, with destroyable tiles and tile collision;
- game objects (ECS) and reusable standard traits (movement, gravity, jump, collision, lifetime);
- camera: zoom, follow, smoothing, shake;
- keyboard, mouse, touch and gamepad input through actions;
- several local views and players; network play with a server as the reference, client prediction, player list and chat (optional modules `network` and `multiplayer`, Phase 5).

## Non-goals

- providing a complete game editor (maps are made with Tiled);
- imposing a scripting language;
- providing a full physics engine (rigid bodies, joints): collision is detection plus tile push-out;
- imposing networking on games that do not use it, or providing online services (matchmaking, server lists, relays, NAT traversal);
- guaranteeing that every platform has every feature in the first version (capabilities are reported instead).

## Success criteria

- a frontend can be replaced without rewriting game logic;
- resources can be loaded from several origins through a consistent API;
- essential systems are testable in a headless environment;
- adding a module follows a documented process;
- every public behavior has relevant contracts and tests;
- a developer or an agent can write a small game from the repository alone (REQ-DOC-001 to REQ-DOC-003).
