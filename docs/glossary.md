# Glossary

**Status: PROPOSED — definitions will be refined with the architecture decisions.**

| Term | Provisional meaning |
|---|---|
| ADR | Architecture Decision Record: a documented architecture decision. |
| Backend | Concrete implementation of an abstract service (rendering, audio, resource...). |
| Batch | A small, verifiable unit of work (one contract, its tests, its example), specified with the feature template before implementation. |
| Capability | A feature a platform may or may not provide (gamepad, shaders, audio...), announced rather than assumed. |
| Component | In ECS, a plain data holder attached to an entity. |
| Core library desugaring | Android build feature that makes some newer Java library APIs available on older Android versions. |
| D8 / R8 | Android tools that convert Java bytecode to DEX (D8) and shrink/optimize it (R8). |
| DEX | Dalvik Executable: the bytecode format run by Android. |
| ECS | Entity-Component-System: model that stores entities as sets of components processed by systems. |
| Engine change request | A need raised by a game that the engine does not cover; tracked instead of being worked around inside the game. |
| Entity | In ECS, an identifier grouping components. |
| Extrapolation | Presentation-only prediction of a visual state beyond the last known simulation state; never fed back into the simulation. |
| Frameskipping | Dropping a presentation (render) when the machine cannot keep up, without dropping simulation computations. |
| Display mode | A combination of width and height in pixels, refresh rate in Hz and color depth in bits supported by a display (ADR-0002, PROPOSED). |
| Frontend | Adapter to a runtime environment (AWT, OpenGL, Android...). |
| Logical resolution | Resolution in pixels of the frame the game renders, independent of the output display mode. |
| Game loop | Mechanism that paces updates and/or rendering. |
| Headless | Execution without a window or real graphics device. |
| Interpolation | Presentation-only blend between two known simulation states (distinct from extrapolation). |
| Output filter | Presentation-time image filter (nearest, bilinear, `HQ2X`, CRT...) applied from the logical frame to the output surface. |
| Provider | A resource source implementation (filesystem, archive, memory, Android assets, stream). |
| Resource | Data identified logically, independently of its location. |
| Runtime | Orchestration of the engine lifecycle and execution. |
| Screen mode | How the application occupies the screen: `WINDOWED`, `FULL_WINDOW` or `FULL_SCREEN`. |
| Simulation step | Fixed duration, in seconds, by which the simulation advances in one update (value `TO BE DEFINED`, OQ-020). |
| Split-screen | Simultaneous presentation of several views/players on one screen. |
| Sprite | Displayable graphic element, possibly tied to an image region. |
| Spritesheet | Image grouping several sprite regions. |
| System | In ECS, logic that processes entities having a given set of components. |
| Tile | Cell or graphic element of a tileset. |
| Tilemap | Map organized in cells and layers. |
| Trait | Composable capability or behavior attached to an object/entity. |
| Viewport | Region where a view of the world is presented on a surface. |
