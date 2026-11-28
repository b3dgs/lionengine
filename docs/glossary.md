# Glossary

**Status: VALIDATED — terms used by the requirements, ADRs and code; a new term is added with the decision that introduces it.**

| Term | Meaning |
|---|---|
| Action | A game-level input (for example "jump") bound to one or more physical inputs; game logic reads actions, not devices (ADR-0035). |
| ADR | Architecture Decision Record: a documented architecture decision. |
| Alpha (presentation factor) | `accumulator / step`, in `[0, 1)`: how far the presentation is between the last two simulation steps (ADR-0017). |
| Animated sprite | A sprite whose frame follows an animator (`AnimatedSprite`), advanced by the update step; rendering only reads it (ADR-0058). |
| Animator | Plays an animation (`Animator`): stopped, playing, paused or finished (`AnimatorState`), advanced by the update step with the time left over carried into the next frame (ADR-0058). |
| Animation | Ordered frames with durations in seconds and a loop mode (`once`, `loop`, `ping-pong`; `Animation` and `AnimationLoop`), built in code or read from a sheet definition; advanced from the simulation update step only (ADR-0003, ADR-0026). |
| Authority (network) | The server's exclusive right to create and remove replicated objects and decide outcomes; clients send only intentions (ADR-0038). |
| Auto-tiling | Rule-based update of a cell's tile from its neighborhood, applied to neighbors when a tile is destroyed; rules are ordered 8-neighbor bitmasks with a care mask (ADR-0028). |
| Backend | Concrete implementation of an abstract service (rendering, audio, resource...). |
| Baseline (snapshot) | The last snapshot a client acknowledged; the next snapshot to that client only carries what changed since it (ADR-0038). |
| Batch | A small, verifiable unit of work (one contract, its tests, its example), specified with the feature template before implementation. |
| Camera | Headless view onto the world: view rectangle in world units, zoom, translation, follow, smoothing and shake (ADR-0010). |
| Capability | A feature a platform may or may not provide (gamepad, shaders, audio...), found by a typed optional lookup rather than assumed (ADR-0020). |
| Color matrix | 4 by 5 matrix over red, green, blue and alpha plus offsets (`ColorMatrix`), as Android `ColorMatrix`: the grading part of a color transform, baked on the CPU (ADR-0056). |
| Color transform | Backend-independent description of a sprite's colors (`ColorTransform`): color mapping, grading, mask (silhouette fill), tint and alpha, in that order; tint and alpha per frame when the backend supports them, the rest baked once and cached (ADR-0003, ADR-0056, ADR-0057). |
| Command (network) | A game-defined reliable message from a client expressing an order (for example an RTS move order), validated and applied by the server (ADR-0038). |
| Command buffer | List of deferred structural changes (object and trait add/remove) applied at the flush of a simulation step (ADR-0011). Also, in rendering, the preallocated buffer of render commands (ADR-0026). |
| Component | In ECS, a plain data holder attached to an entity. In this engine a component is called a trait (ADR-0011). |
| Composition module | Optional module `composition`: ready-made scene content (world, views, tile map, music) and the camera follow adapter (ADR-0031). |
| Content hash | 32-bit hash of the replication schema and message registrations, compared when a client joins so that incompatible builds cannot play together (ADR-0038). |
| Contract skeleton | Phase 0 batch after the build skeleton that declares, with complete Javadoc and no behavior beyond value types, the cross-module contracts listed in `architecture/interfaces.md` (ADR-0049). |
| Core library desugaring | Android build feature that makes some newer Java library APIs available on older Android versions. |
| Custom filter | An output filter supplied by a game: `CustomFilter` of `render-api`, with its `OutputFilter` id and `FilterKind` (scaling or post), a fragment shader `ResourceKey` and/or a `CpuFilter`, registered explicitly on the render backend (ADR-0033, ADR-0047). |
| D8 / R8 | Android tools that convert Java bytecode to DEX (D8) and shrink/optimize it (R8). |
| Definition file | A JSON data file declaring `"format"` and `"version"` (sprite sheet, tileset, object definition), read by the strict reader of `core` (ADR-0028, ADR-0032). |
| DEX | Dalvik Executable: the bytecode format run by Android. |
| Display mode | A combination of width and height in pixels, refresh rate in Hz and color depth in bits supported by a display; `0` means unknown or multiple (ADR-0002, ADR-0022). |
| ECS | Entity-Component-System: model that stores entities as sets of components processed by systems; the single object model of the engine (ADR-0021). |
| Edge (input) | `pressed` or `released` transition of a digital action, seen by exactly one update step (ADR-0035). |
| Effective result | Immutable value reporting, per field, the requested and the actually applied value of a display or filter request, with a `degraded` flag and a reason (ADR-0006). |
| Engine change request | A need raised by a game that the engine does not cover; filed as a GitHub issue instead of being worked around inside the game (ADR-0023). |
| Entity | In ECS, an identifier grouping components. Called "object" in this engine. |
| Extrapolation | Presentation-only prediction of a visual state beyond the last known simulation state; opt-in, never fed back into the simulation (ADR-0017). |
| Flush | Single point at the end of each simulation step where the command buffer is applied and structural events are delivered (ADR-0011, ADR-0030). |
| Frame boundary | Point between two presentations, outside any simulation update; a display or filter change requested while presentations are skipped applies at the next presentation (ADR-0002). |
| Frameskipping | Dropping a presentation (render) when the machine cannot keep up, without dropping simulation computations. |
| Frontend | Adapter to a runtime environment (AWT, OpenGL, Android...). On desktop, `platform-awt` implements `platform-api` (ADR-0008). |
| Game loop | Mechanism that paces updates and rendering: fixed step, accumulator, catch-up, lag cap (ADR-0017). |
| Grounded | The `Jumpable` flag telling that an object stands on solid ground: set by the tile push-out of `CollisionSystem` when a step pushed the object up out of a floor, cleared when it did not; the game may set it too (ADR-0030, ADR-0083). |
| Headless | Execution without a window or real graphics device. |
| Host | A process running the server and one ordinary client (its own player) connected in memory; no dedicated machine needed (ADR-0038). |
| Inbox | Thread-safe bounded FIFO through which platform threads and workers hand events to the runtime thread, delivered at the start of an iteration; a `core` contract, so producers never depend on `runtime` (ADR-0018, ADR-0031, ADR-0040). |
| Input frame | The `input-api` action states of one player for one tick (3 bits per digital action, 16 bits per analog action), sent to the server every tick (ADR-0038). |
| Input lead | How many ticks before its use a client's input frame reaches the server; the client keeps it near 2 ticks (ADR-0038). |
| Interpolation | Presentation-only blend between the last two simulation states with `alpha`; the default presentation (ADR-0017). |
| Interpolation delay | How far in the past a client shows proxies (default two snapshot intervals, 0.1 s), so that it can blend between two received snapshots (ADR-0038). |
| Lag cap | Maximum real time added to the accumulator in one iteration (default 0.25 s); the excess is dropped and reported (ADR-0017, ADR-0031). |
| Layer and mask (collision) | Two objects collide only if each one's mask includes the other's layer (ADR-0030); both are `int` bit sets, one bit per layer, 32 layers (ADR-0082). |
| Logical resolution | Resolution in pixels of the frame the game renders, independent of the output display mode. |
| Long sound | Music or ambient sound, streamed by default, released when no longer needed (ADR-0004, ADR-0027). |
| Network id | `int` identifying a replicated object in a session, assigned by the server from 1 and never reused; clients map it to their local object ID (ADR-0038). |
| Object ID | Opaque ID of an ECS object: a packed `long`, generation in the high 32 bits, index in the low 32 bits, `0` invalid (ADR-0030). |
| Output filter | Presentation-time image filter (`NONE`, bilinear, `HQ2X`, CRT, custom) applied from the logical frame to the output surface; `NONE` samples nearest-neighbor when the frame is scaled. |
| Pending object | An object created during a step and not yet flushed: not alive, but commands may be queued with its ID (ADR-0030). |
| Pivot | Internal location (origin) of a sprite, in pixels from the frame's top-left corner, default the frame center; used for placement, rotation and scaling (ADR-0026). |
| Playlist | Predefined sequence of long sounds; the next starts when the current terminates (ADR-0004). |
| Prediction | A client simulating its own player's objects immediately, before the server confirms; checked against the server's state and corrected by replay (ADR-0038). |
| Presentation geometry | `PresentationGeometry` of `platform-api`: rectangle of the output surface, in device pixels, where the logical frame is presented, with the scale on each axis; computed by the platform from the effective scaling policy at a frame boundary, used by render backends and inverted by input adapters (ADR-0046). |
| Push-out (tile) | The blocking response of objects to solid tiles: each step, `CollisionSystem` moves a non-trigger object with `Movable` out of the overlapping solid boxes of its `ShapeQuery`, X then Y, against its displacement, leaving it exactly touching (ADR-0030, ADR-0083). |
| Provider | A resource source implementation (filesystem, archive, memory, Android assets, stream), searched in a configured order (ADR-0032). |
| Proxy | On a client, a replicated object whose state comes only from snapshots; carries `NetProxy` and is never simulated locally (ADR-0038). |
| Query | Cached view of the objects having all of a set of trait types and none of an exclusion set (ADR-0011). |
| Registry | Explicit instance mapping ids `namespace:name` to factories, used to create data-driven types without reflection (ADR-0020, ADR-0032). |
| Reliable-ordered channel | Messages delivered once and in order over UDP by resending until acknowledged (ADR-0039). |
| Replay (network) | Re-running a client's recent ticks with its recorded input frames after writing the server's state, to correct a misprediction (ADR-0038). |
| Replicated object | An ECS object of the server world mirrored on clients through snapshots; has a network id, a type and an owner (ADR-0038). |
| Resource | Data identified logically, independently of its location. |
| Resource key | Normalized logical path string identifying a resource for every origin (`/` separator, case-sensitive, no leading `/`, no `.` or `..`); used by every module cache (ADR-0007). |
| Round trip | Time for a packet to reach the other side and be acknowledged, in seconds; shown to players as ping (ADR-0039). |
| Runtime | Orchestration of the engine lifecycle and execution: loop, inbox, scenes. |
| Runtime thread | The single thread that owns the loop, the world, the caches and by default rendering (ADR-0018). |
| Scaling policy | How the logical frame is scaled to the output surface: `FIT` (aspect-preserving, default), `INTEGER` (integer factors only, opt-in) or `STRETCH` (ADR-0006). |
| Scene | Self-contained unit of the running game with a lifecycle (enter, update, render, exit, dispose), composing a world, cameras, a background and music (ADR-0021). |
| Scene flow | Validated state machine linking scene ids by named triggers, optional conditions and transitions `SWITCH`, `PUSH`, `POP`, `EXIT` (ADR-0036). |
| Screen mode | How the application occupies the screen: `WINDOWED`, `FULL_WINDOW` or `FULL_SCREEN`. |
| Seam (cross-module contract) | A contract defined in one module and implemented or used by another; listed per module in `architecture/interfaces.md` (ADR-0049). |
| Session | A network server or client connection set with its players, chat and messages (module `network`, ADR-0039). |
| Short sound | Low-latency sound effect of at most 10 s, cached for heavy reuse; played individually and stopped (ADR-0004, ADR-0033). |
| Simulation step | Fixed duration, in seconds, by which the simulation advances in one update (default 1/60 s, ADR-0017). |
| Slot (system) | `int` giving a system's place in the update order, lower first (ADR-0030, ADR-0034). |
| Snapshot | The absolute values of the replicated objects at one tick, sent by the server to a client, delta-compressed against its baseline (ADR-0038). |
| Sparse set | Storage giving O(1) add, remove and membership and dense iteration; one per trait type (ADR-0011). |
| Split-screen | Simultaneous presentation of several views/players on one screen. |
| Sprite | Lightweight instance (transform, color transform, pivot, alpha) over a shared immutable source (image + region) (ADR-0003). |
| Sprite sheet | Image grouping several sprite regions as a grid (cell size, columns, rows, optional margin/spacing), giving access to cells by index; sheets are grouped by unique id (ADR-0003). Also written "spritesheet". |
| Sheet group | Sprite sheets held under ids unique in the group (`SheetGroup`), acquired and released together; reference-counted through its handles, the last release frees every image (ADR-0026, REQ-SHEET-002). |
| System | In ECS, logic that processes the objects of a query during the update phase, in slot order. |
| Template color mapping | Static transform mapping exact key colors to target colors (`ColorMapping`), producing a cached color variant of one sprite (e.g. team colors); the first part of a color transform, read from the `templates` of a sheet definition (ADR-0026, ADR-0057). |
| Test JAR | The published test sources of a module (Maven classifier `tests`): the fakes and abstract contract suites of its contracts, used in test scope by the modules that depend on it (ADR-0045). |
| Texture store | `TextureStore` of `render-api`: creates textures from primitive ARGB data shared read-only, prepares and releases them by `int` handle (ADR-0047). |
| Tick | Number of a simulation step in a network session, counted as an `int` from 0 at session start (ADR-0038). |
| Tile | Cell or graphic element of a tileset. |
| Tilemap | Map organized in cells (single layer first, ADR-0009). |
| Tileset | Sprite sheet whose cells are the tiles of a tile map (`REQ-SHEET-001`). |
| Time scale | Multiplier of elapsed time before it enters the accumulator, in `[0.1, 4.0]`, for slow or fast motion; the step never changes (ADR-0017, ADR-0031). |
| Trait | Composable data attached to an ECS object; its logic lives in systems (ADR-0011). |
| Transition (scene) | A link of the scene flow from a scene (or any scene) to a target, selected by a trigger and a condition and applied at the next frame boundary (ADR-0036). |
| Trigger (collision) | A `Collidable` with its trigger flag set: an area that reports the objects entering and leaving it and never blocks (ADR-0030, ADR-0082). |
| Trigger (scene) | Name (for example `finished`, `pause`) passed to the scene flow to request a transition (ADR-0036). |
| Update and render callbacks | `UpdateCallback` (one fixed step, in seconds) and `RenderCallback` (one frame, with `alpha` and the unscaled delta) of `runtime`, wired to the game loop by the application or `composition` (ADR-0040). |
| Variant set | Group of short-sound files; playing it selects one variant or plays several at once (composed sound) (ADR-0027). |
| View | One camera paired with one viewport (ADR-0021); type provided by `composition`. |
| View transform | Per-frame immutable value (`scale`, `offsetX`, `offsetY`) converting world meters to logical-frame pixels (ADR-0029, ADR-0034). |
| Viewport | Rectangle of the logical frame, in pixels, in which a view is presented; it clips and offsets render commands (ADR-0021, ADR-0031). |
| Viewport culling | Drawing only the tiles that intersect the viewport rectangle (plus a margin). |
| Worker task | `WorkerTask` of `core`: one unit of read-and-decode work run by the platform's `WorkerPool`; it reports back through the inbox (ADR-0019, ADR-0040). |
| World | Container owning the objects, traits, queries, systems, flush and seeded random source; hosted by `ecs` (ADR-0021). |
