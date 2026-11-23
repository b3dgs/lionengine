# Engine vision

**Status: PROPOSED**

## Purpose

LionEngine is a 2D game engine written in Java. It must provide a reusable foundation for building games without imposing a frontend, a rendering mode or a single entity-management model.

## Proposed guiding principles

1. **Independent core**: engine rules do not depend on the graphics, audio or input APIs of a particular system.
2. **Interchangeable frontends**: AWT, OpenGL and Android are possible integrations, not dependencies of the core.
3. **Testable abstractions**: essential behaviors can be tested without a window or hardware.
4. **Controlled extensibility**: new features are integrated through documented contracts.
5. **Composition**: object behaviors are composable, notably through traits/capabilities.
6. **Implementation choice**: several strategies may coexist when they address different constraints (game loop, object management, rendering).
7. **No premature over-architecture**: do not impose ECS, networking or GPU rendering on every project. *(Reconciled by ADR-0021: ECS is the single object model, but a game using no objects links neither `ecs` nor `traits`.)*

## Envisaged functional scope

- lifecycle and game loop abstraction;
- resource access independent of origin;
- audio;
- images, sprites, animations and spritesheets;
- tilemaps, tilesets and static/dynamic maps;
- game object management;
- reusable traits/capabilities (collision, gravity, mobility, etc.);
- keyboard, mouse and gamepad input;
- multiple views/players and a network mode.

## Initial non-goals (proposed)

- providing a complete game editor;
- imposing a scripting language;
- imposing a full physics engine;
- imposing a network protocol or a game server;
- guaranteeing that every platform has every feature in the first version.

These non-goals must be confirmed or amended.

## Success criteria

- a frontend can be replaced without rewriting game logic;
- resources can be loaded from several origins through a consistent API;
- essential systems are testable in a headless environment;
- adding a module follows a documented process;
- every public behavior has relevant contracts and tests.
