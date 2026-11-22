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
| Frame boundary | Point between two presentations, outside any simulation update; a display or filter change requested while presentations are skipped applies at the next presentation (ADR-0002). |
| Display mode | A combination of width and height in pixels, refresh rate in Hz and color depth in bits supported by a display (ADR-0002). |
| Frontend | Adapter to a runtime environment (AWT, OpenGL, Android...). On desktop, `platform-awt` implements `platform-api` (ADR-0008). |
| Logical resolution | Resolution in pixels of the frame the game renders, independent of the output display mode. |
| Scaling policy | How the logical frame is scaled to the output surface: `FIT` (aspect-preserving, default), `INTEGER` (integer factors only, opt-in) or `STRETCH` (ADR-0006). |
| Effective result | Immutable value reporting, per field, the requested and the actually applied value of a display or filter request, with a `degraded` flag and a reason (ADR-0006). |
| Game loop | Mechanism that paces updates and/or rendering. |
| Headless | Execution without a window or real graphics device. |
| Interpolation | Presentation-only blend between two known simulation states (distinct from extrapolation). |
| Output filter | Presentation-time image filter (`NONE`, bilinear, `HQ2X`, CRT...) applied from the logical frame to the output surface; `NONE` samples nearest-neighbor when the frame is scaled. |
| Provider | A resource source implementation (filesystem, archive, memory, Android assets, stream). |
| Resource | Data identified logically, independently of its location. |
| Resource key | Normalized logical path string identifying a resource for every origin (`/` separator, case-sensitive, no leading `/`, no `.` or `..`); used by every module cache (ADR-0007). |
| Runtime | Orchestration of the engine lifecycle and execution. |
| Screen mode | How the application occupies the screen: `WINDOWED`, `FULL_WINDOW` or `FULL_SCREEN`. |
| Simulation step | Fixed duration, in seconds, by which the simulation advances in one update (value `TO BE DEFINED`, OQ-020). |
| Split-screen | Simultaneous presentation of several views/players on one screen. |
| Sprite | Displayable graphic element, possibly tied to an image region. In ADR-0003: a lightweight instance (transform, color transform, location, alpha) over a shared immutable source (image + region). |
| Animation | Ordered frames with durations in seconds and a loop mode; advanced from the simulation update step only (ADR-0003). |
| Short sound | Low-latency sound effect, cached for heavy reuse; can be played individually and stopped (ADR-0004). |
| Long sound | Music or ambient sound, preloaded for direct use and released when no longer needed (ADR-0004). |
| Variant set | Group of short-sound files; playing it selects one variant or plays several at once (composed sound); policies `TO BE DEFINED` (OQ-112). |
| Playlist | Predefined sequence of long sounds; the next starts when the current terminates (ADR-0004). |
| Pivot | Internal location (origin) of a sprite, used for placement, rotation and scaling. |
| Sprite sheet | Image grouping several sprite regions as a grid (cell size, columns, rows, optional margin/spacing), giving access to cells by index; sheets are grouped by unique id (ADR-0003). |
| Template color mapping | Static transform mapping source colors (or an indexed channel) to target colors, producing a cached color variant of one sprite (e.g. team colors). |
| Spritesheet | Same as sprite sheet. |
| System | In ECS, logic that processes entities having a given set of components. |
| Tile | Cell or graphic element of a tileset. |
| Tilemap | Map organized in cells and layers (single layer first, ADR-0009). |
| Tileset | Sprite sheet whose cells are the tiles of a tile map (`REQ-SHEET-001`). |
| Auto-tiling | Rule-based update of a cell's tile from its neighborhood, applied to neighbors when a tile is destroyed; rule format `TO BE DEFINED` (OQ-127). |
| Viewport culling | Drawing only the tiles that intersect the viewport rectangle (plus a margin). |
| Trait | Composable capability or behavior attached to an object/entity. |
| Viewport | Region where a view of the world is presented on a surface. |
