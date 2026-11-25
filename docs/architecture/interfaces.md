# Module interfaces

**Status: VALIDATED — per-module catalog of the public types and of every contract that crosses a module boundary (ADR-0049). It summarizes: type names belong to ADR-0037, ADR-0038, ADR-0040 and the feature pages, responsibilities to [`contracts.md`](contracts.md); every batch that adds or changes a cross-module contract updates this page.**

The base skeleton of the engine is built in two Phase 0 batches. The build skeleton declares the modules ([`../features/build-skeleton.md`](../features/build-skeleton.md)); the contract skeleton then declares the seams marked *contract skeleton* below ([`../features/contract-skeleton.md`](../features/contract-skeleton.md)). Both are done (2026-11-24): the seams marked *contract skeleton* exist as compiled, documented types. Every other type is written by the feature batch named in its row.

## 1. Rules

- **Placement.** A module's types live in its root package (ADR-0037) or its subpackages. The package rule lets a subpackage use only its ancestors, so a type used by several packages of one module lives in their common ancestor. Seams therefore sit in the module's root package: for `core`, `com.b3dgs.lionengine` itself. No module creates a subpackage named like another module's root (for example, `core` never uses `com.b3dgs.lionengine.render`).
- **Direction.** A module may use a type only from a module of its row in [`modules.md`](modules.md). The *Used by* column below has been checked against those rows.
- **Kinds.** *interface*: a contract with several implementations. *callback*: a role-named interface with one abstract method (ADR-0037). *enum*, *value* (immutable, validated at construction), *exception* (unchecked, [`../conventions.md`](../conventions.md)). *class*: a concrete engine class. A dash means the batch chooses.
- **Names.** Where no canonical name exists the concept is described in italics, and the declaring batch chooses the name under [`../conventions.md`](../conventions.md).
- **Test fixtures** of a contract live in the test JAR of its module; the cross-module headless kit is `test-support` (ADR-0045).
- **Definition files without `resources`.** `core` reads JSON from characters or bytes; `input-api` (bindings) and `ecs` (object definitions) take parsed `core` JSON values, never a resource key, because their rows have no `resources`. The caller opens the file through `resources`.

## 2. Cross-module contracts (seams)

| Seam | Kind | Defined in | Implemented or provided by | Used by | Declared by |
|---|---|---|---|---|---|
| `LogLevel`, `LogSink` | enum, interface | `core` | default `System.err` sink in `core`; platform sinks (`platform-android`) | every module | contract skeleton |
| `DefinitionException` | exception | `core` | thrown by the `core` JSON reader, `Configuration` and `Registry<T>` | `graphics`, `tilemap`, `input-api`, `ecs`, `traits` (factories reading a `Configuration`) | contract skeleton |
| `Inbox`, `InboxEvent` | interface, callback | `core` | `runtime` (`GameLoop` owns the inbox); events by every producer; inbox spy in the `core` test JAR | `platform-*`, `input-*`, `audio-*`, `graphics`, `audio-api`, `tilemap`, `composition` | contract skeleton |
| `AsyncHandle<T>`, `AsyncState` | interface, enum | `core` | completed through an `InboxEvent` by the loaders of `graphics`, `audio-api`, `tilemap` | those loaders, scenes, games | contract skeleton |
| `WorkerPool` and its unit of work `WorkerTask` | interface, callback | `core` | `platform-awt`, `platform-android`, `test-support`; fake in the `core` test JAR | `graphics`, `audio-api`, `tilemap`, `network` (later host-name batch) | contract skeleton |
| `LoopControl`, `LoopDriver` | interface | `core` | `runtime` (`GameLoop`); manual driver in the `core` test JAR | `platform-api` (started with a `LoopDriver`), `platform-*`, `composition` | contract skeleton |
| `ThreadConfinement` | final class | `core` | `core` (the thread of the first check owns the object, ADR-0055) | the caches of `graphics`, `audio-api`, `tilemap` | ADR-0055, with the image cache |
| `Clock` | interface | `runtime` | `runtime` (`System.nanoTime`); controllable clock in the `runtime` test JAR | `runtime`, `test-support` | contract skeleton |
| `UpdateCallback`, `RenderCallback` | callback | `runtime` | the application, `composition`, `runtime` (`SceneManager`, which a simple game gives to the loop as both callbacks) | `runtime` (`GameLoop`) | contract skeleton |
| `Scene` | interface | `runtime` | games, `composition` | `runtime` (`SceneManager`) | contract skeleton |
| `Platform` | interface | `platform-api` | `platform-awt`, `platform-android`, `test-support` (`HeadlessPlatform`); contract suite `PlatformContract` in the `platform-api` test JAR | applications, `composition`, `render-*`, `input-*` | contract skeleton; render-backend attachment added with the render-backend contract |
| `PlatformEvent`, `CloseListener` | event type (an `InboxEvent`), callback | `platform-api` | platform implementations; games (`CloseListener`) | applications, `composition` | contract skeleton |
| `ScreenMode`, `DisplayMode`, `DisplayRequest`, `EffectiveResult` | enum, value, value, value | `platform-api` | platform implementations | applications, `composition`, `platform-*` | contract skeleton |
| `DisplayState` (ADR-0046) | class | `platform-api` | — | `platform-awt` (`AwtDisplay`), `test-support` (`HeadlessDisplay`), later platforms | [`platform-and-rendering.md`](../features/platform-and-rendering.md) |
| `PresentationGeometry` (ADR-0046) | value | `platform-api` | platform implementations | `render-*`, `input-*`, games | contract skeleton |
| `ScalingPolicy`, `OutputFilter`, `FrameScaling` (the scaling function) | enum, —, value | `render-api` | — | `platform-api` (ADR-0046), `platform-*`, `test-support`, `render-*`, applications | contract skeleton; `FrameScaling` with [`platform-and-rendering.md`](../features/platform-and-rendering.md) batch 1 |
| `Viewport` | value | `render-api` | — | `graphics`, `tilemap`, `composition`, `render-*` | contract skeleton |
| `TextureStore` (ADR-0047) | interface | `render-api` | `render-awt`, `render-opengl`, `test-support` (`RecordingBackend`) | `graphics` (sheets used by `tilemap` come from `graphics`) | contract skeleton |
| `CustomFilter`, `FilterKind`, `CpuFilter` (ADR-0033, ADR-0047) | interface, enum, callback | `render-api` | games | `render-*` (registered on the backend) | contract skeleton |
| `CommandBuffer` | class | `render-api` | — (written by the render code, read by backends) | `graphics`, `tilemap`, `composition`, `render-*`, `test-support` | [`platform-and-rendering.md`](../features/platform-and-rendering.md) batch 2 |
| `RenderBackend`, the render backend contract (present a `CommandBuffer`, report filters, provide the `TextureStore`) | interface | `render-api` | `render-awt`, `render-opengl`, `test-support` (`RecordingBackend`); contract suite `RenderBackendContract` in the `render-api` test JAR | applications, `composition`, `platform-api` (`Platform.attach`, ADR-0046) | [`platform-and-rendering.md`](../features/platform-and-rendering.md) batch 2 |
| *color-transform capability* (tint, alpha) | interface | `render-api` | render backends | `graphics` | [`sprite.md`](../features/sprite.md) batch 3 |
| `AwtSurface` | interface | `platform-awt` | `platform-awt` (`AwtPlatform`) | `render-awt` (`AwtRenderBackend`), `input-awt` | [`platform-and-rendering.md`](../features/platform-and-rendering.md) batch 3 |
| `ResourceKey`, `ResourceException` with `Reason` | value, exception with enum | `resources` | — | every consuming module, `render-api` (shader keys, ADR-0047) | contract skeleton |
| `ResourceProvider` | interface | `resources` | `resources-file` (`FileProvider`), `resources-archive`, `resources-memory` (`MemoryProvider`), `resources-android`, `resources` (`StreamProvider`, the application-stream provider); fake provider and contract suite in the `resources` test JAR | `resources` | contract skeleton |
| `ImageDecoder` (the image decoder contract) | interface | `graphics` | `graphics` (`PngDecoder`), `graphics-imageio`, `graphics-android` (registered explicitly); contract suite `ImageDecoderContract` in the `graphics` test JAR | `graphics`, applications | [`sprite-sheet.md`](../features/sprite-sheet.md) batch 1 |
| *audio backend contract*, *audio decoder contract* | interface | `audio-api` | `audio-javasound`, `audio-android`; `audio-codec-ogg` | `audio-api` | [`audio-short-and-long-sounds.md`](../features/audio-short-and-long-sounds.md) batch 1 |
| *input adapter contract* (devices, raw events) | interface | `input-api` | `input-awt`, `input-android`; fake devices in the `input-api` test JAR | `input-api` | [`input.md`](../features/input.md) batch 1 |
| `InputState` | class | `input-api` | — | `multiplayer`, `composition` | [`input.md`](../features/input.md) batch 1 |
| `ViewTransform` | value | `camera` | — | `composition`, games | [`camera.md`](../features/camera.md) batch 1 |
| `FollowLookup` | interface | `camera` | `composition` (adapter over a `World`), examples; fake in the `camera` test JAR | `camera` | [`camera.md`](../features/camera.md) batch 2 |
| `Box`, `ShapeQuery` | value, interface | `physics` | `tilemap` (`ShapeQuery`) | `traits`, `composition` | [`standard-traits.md`](../features/standard-traits.md) batch 3 (hot path: no allocation per query) |
| *map loader contract* | interface | `tilemap` | Tiled loader in `tilemap`, game formats | `tilemap` | [`tile-map.md`](../features/tile-map.md) batch 3 |
| `World`, `Query` | class, — | `ecs` | — | `traits`, `multiplayer`, `composition` | [`ecs.md`](../features/ecs.md) batches 1 to 3 |
| `GameSystem`, `TraitType<T>` | interface, — | `ecs` | `traits`, `multiplayer`, games | `ecs` | [`ecs.md`](../features/ecs.md) batches 2 and 4 |
| Standard traits (`Transformable`, ...) | class | `traits` | — | `multiplayer` (codecs), `composition` (follow adapter) | [`standard-traits.md`](../features/standard-traits.md) |
| Sessions and wire encoding (`NetworkServer`, `NetworkClient`, `MessageType<T>`, `MessageCodec<T>`, `NetWriter`, `NetReader`, ...) | — | `network` | `network` | `multiplayer` | [`network.md`](../features/network.md) |

A seam declared by a feature batch rather than by the contract skeleton is either built on a concrete class of that batch, or has a hot-path shape that the batch fixes (`ShapeQuery`, `FollowLookup`).

## 3. Public types per module

Names already fixed by ADR-0037, ADR-0038, ADR-0040 and the feature pages. *Bold* types are declared by the contract skeleton; the others come with the feature page given.

| Module | Root package | Public types named so far | Feature page |
|---|---|---|---|
| `core` | `com.b3dgs.lionengine` | **`LogLevel`**, **`LogSink`**, `Log`, `JsonReader`, `JsonValue`, `JsonObject`, `JsonArray`, **`DefinitionException`**, `Configuration`, `Registry<T>`, `RegistryFactory<T>`, `RandomSource`, **`Inbox`**, **`InboxEvent`**, **`AsyncHandle<T>`**, **`AsyncState`**, `LoadGroup`, **`WorkerPool`**, **`WorkerTask`**, **`LoopControl`**, **`LoopDriver`**, `ThreadConfinement` | [`core-foundation.md`](../features/core-foundation.md), [`game-loop.md`](../features/game-loop.md) |
| `runtime` | `.runtime` | **`Clock`**, `GameLoop`, `LoopConfig`, `LoopState`, **`UpdateCallback`**, **`RenderCallback`**, `LagListener`, **`Scene`**, `SceneManager`, `SceneFlow<C>`, `SceneFlowBuilder<C>`, `SceneFactory<C>`, `TransitionCondition<C>`, `TransitionKind`, `TransitionListener` | [`game-loop.md`](../features/game-loop.md), [`scenes.md`](../features/scenes.md) |
| `platform-api` | `.platform` | **`Platform`**, **`PlatformEvent`**, **`CloseListener`**, **`ScreenMode`**, **`DisplayMode`**, **`DisplayRequest`**, **`EffectiveResult`**, **`PresentationGeometry`**, `DisplayState` | [`platform-and-rendering.md`](../features/platform-and-rendering.md), [`display-modes.md`](../features/display-modes.md) |
| `render-api` | `.render` | `CommandBuffer`, **`Viewport`**, `TextureHandle` (an `int`, no wrapper type), **`ScalingPolicy`**, `FrameScaling`, **`OutputFilter`**, **`FilterKind`**, **`TextureStore`**, **`CustomFilter`**, **`CpuFilter`**, `RenderBackend`, *color-transform capability* | [`platform-and-rendering.md`](../features/platform-and-rendering.md), [`output-filtering.md`](../features/output-filtering.md) |
| `platform-awt`, `render-awt` | `.platform.awt`, `.render.awt` | `AwtSurface`; implementations named by what distinguishes them (`AwtPlatform`, `AwtRenderBackend`) | [`platform-and-rendering.md`](../features/platform-and-rendering.md) |
| `resources` | `.resources` | **`ResourceKey`**, **`ResourceProvider`**, `Resources`, **`ResourceException`** with **`Reason`**, `StreamProvider`, `StreamSupplier` (application streams) | [`resources.md`](../features/resources.md) |
| `resources-*` | `.resources.file`, `.resources.archive`, `.resources.memory`, `.resources.android` | one provider each (`FileProvider`, `MemoryProvider`, ...) | [`resources.md`](../features/resources.md) |
| `audio-api` | `.audio` | `Audio`, `ShortSound`, `LongSound`, `VariantSet`, `Playlist`, `AudioDecodingException`, *audio backend contract*, *audio decoder contract* | [`audio-short-and-long-sounds.md`](../features/audio-short-and-long-sounds.md) |
| `graphics` | `.graphics` | `PixelBuffer`, `ImageDecoder`, `PngDecoder`, `ImageDecodingException`, `ImageCache`, `Image`, `SpriteSheet`, `Sprite`, `Animation`, `Animator` | [`sprite-sheet.md`](../features/sprite-sheet.md), [`sprite.md`](../features/sprite.md), [`animated-sprite.md`](../features/animated-sprite.md) |
| `tilemap` | `.tilemap` | `TileMap`, `Tileset`, `AutoTileRules`, `MapLoadingException`, *map loader contract*, *tile-changed listener* | [`tile-map.md`](../features/tile-map.md) |
| `camera` | `.camera` | `Camera`, `ViewTransform`, `FollowLookup`, *zoom curve enum* | [`camera.md`](../features/camera.md) |
| `input-api` | `.input` | `Action`, `Bindings`, `InputState`, `Player`, *input adapter contract* | [`input.md`](../features/input.md) |
| `ecs` | `.ecs` | `World`, `GameSystem`, `Query`, `TraitType<T>`, *ECS listeners* | [`ecs.md`](../features/ecs.md) |
| `physics` | `.physics` | `Box`, `BroadPhase`, `ShapeQuery` | [`standard-traits.md`](../features/standard-traits.md) |
| `traits` | `.traits` | traits `Transformable`, `Movable`, `Gravity`, `Jumpable`, `Collidable`, `Lifetime`; systems `Jump`, `Gravity`, `Movement`, `Collision`, `Lifetime` (two systems share a trait's name: the batch gives the system classes distinct names) | [`standard-traits.md`](../features/standard-traits.md) |
| `composition` | `.composition` | `View`, *camera follow adapter* | not yet paged (Phase 3) |
| `network` | `.network` | `NetworkServer`, `NetworkClient`, `ServerConfig`, `ClientConfig`, `Transport`, `UdpTransport`, `LoopbackNetwork`, `ConditionedTransport`, `NetworkConditions`, `NetworkTime`, `PlayerInfo`, `PlayerList`, `PlayerListener`, `ConnectionListener`, `ConnectionState`, `DisconnectReason`, `RejectReason`, `ChatMessage`, `ChatListener`, `ChatFilter`, `MessageType<T>`, `MessageCodec<T>`, `MessageListener<T>`, `Delivery`, `Authenticator`, `PropertyValidator`, `NetWriter`, `NetReader`, `ConnectionStats`, `ServerInfo`, `Discovery`, `PacketProtection` | [`network.md`](../features/network.md) |
| `multiplayer` | `.multiplayer` | `ReplicationSchema`, `TraitCodec<T>`, `ReplicatedTypeFactory`, `RelevanceFilter`, `StandardCodecs`, `ServerWorld`, `ClientWorld`, `Host`, `Replicated`, `NetProxy`, `InputFrame` | [`multiplayer.md`](../features/multiplayer.md) |
| `test-support` | `.testsupport` | `HeadlessPlatform`, `RecordingBackend`, `RecordedCommand`; the manually driven loop is a `GameLoop` on the controllable clock (ADR-0045) | [`platform-and-rendering.md`](../features/platform-and-rendering.md), [`game-loop.md`](../features/game-loop.md) |
| `examples` | `.examples` | none (applications) | every page |

Implementation modules (`platform-*`, `render-*`, `resources-*`, `audio-*`, `graphics-*`, `input-*`) implement the seams above. The only types they add for other modules are capability types: `AwtSurface` (`platform-awt`) and the Wi-Fi multicast lock capability of `platform-android` (ADR-0039).

## 4. Startup wiring

Nothing is discovered (ADR-0020); the application, or `composition`, wires the engine in this order (ADR-0040, ADR-0046, ADR-0047):

1. Build `Resources` with its ordered providers; create the registries and register the factories.
2. Create the `GameLoop` with its `LoopConfig` and its update and render callbacks.
3. Create the platform with its configuration (logical frame size, initial `DisplayRequest` with scaling policy and filter chain, log sink); attach the render backend; hand it the loop's inbox.
4. Create the input adapters with the platform and the inbox; the audio backend with the inbox; the `graphics`, audio and `tilemap` loaders with `Resources`, the platform's `WorkerPool` and the inbox; `graphics` also gets the backend's texture contract.
5. Update callback: advance every `InputState`, then update the scene manager. Render callback: render the scene manager into the `CommandBuffer`, then present it through the backend, which uses the platform's presentation geometry.
6. Start the platform with the loop as its `LoopDriver`.
