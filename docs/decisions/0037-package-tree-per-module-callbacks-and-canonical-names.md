# ADR-0037 — Package tree per module, dependency-rule scope, callback types and canonical names

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: agent, under the project owner's explicit delegation of 2026-11-23 ("full power to make any changes needed in rewrite/v11 to make it as clear as possible"); reversible by a superseding ADR
- Related requirements: `REQ-CORE-005`, `REQ-TECH-006`, `REQ-TECH-007`, `REQ-RT-008`, `REQ-SCENE-004`, `REQ-RES-005`, `REQ-DEF-002`
- Open questions resolved: none; narrows `OQ-149` (package naming) and `OQ-147` (API choice for callbacks)
- Supersedes / superseded by: none; completes [ADR-0012](0012-platform-and-build-baseline.md), [ADR-0015](0015-single-version-and-package-dependency-rule.md), [ADR-0020](0020-no-reflection-factory-registry-and-capability-lookup.md) and [ADR-0036](0036-scene-flow-ids-triggers-conditions-transitions.md) (all unchanged)

## Context

ADR-0015 records the rule of `radial-encapsulation-maven-plugin`: a package may depend only on itself or a parent package. Reading the plugin source (version 1.0.2, Apache-2.0) confirms it: for every class of the analysed Maven module, a dependency whose name starts with the configured `basePackage` is accepted only when the class's package name starts with the dependency's package name (same package or ancestor, as a string prefix). Sibling packages are therefore violations. With one `basePackage` for all modules, `graphics` could not use `resources` or `render-api`, and the module graph of [`../architecture/modules.md`](../architecture/modules.md) could not be built.

Separately, `java.util.function`, `Optional` and streams need core library desugaring at API 21 (not verified, `OQ-147`) and allocate; several contracts need callbacks (scene factories, conditions, listeners, capability lookup).

## Decision

### 1. Package tree and rule scope

- Each module has one **root package**, given below. All its classes live in the root package or its subpackages.
- Each module configures the plugin with **`basePackage` = its own root package** (overriding the aggregator property `radial.base-package`). The plugin then enforces radial encapsulation inside the module (a subpackage may use its ancestors, never its descendants or siblings), and ignores dependencies on other modules.
- Rules between modules (allowed dependencies, no cycle, `core` and `*-api` independent of implementations) are enforced by the Maven dependency graph: a module can only reference what its POM declares, and Maven rejects cycles. The skeleton adds a check that each module's declared dependencies are within its row of `modules.md`.
- Package names inside one module must not be string prefixes of each other unless they are real ancestors (for example avoid `load` next to `loader`), because the plugin compares names as strings.

| Module | Root package (under `com.b3dgs.lionengine`) |
|---|---|
| `core` | `com.b3dgs.lionengine` (the root itself) |
| `runtime` | `.runtime` |
| `platform-api`, `platform-awt`, `platform-android` | `.platform`, `.platform.awt`, `.platform.android` |
| `render-api`, `render-awt`, `render-opengl` | `.render`, `.render.awt`, `.render.opengl` |
| `resources`, `resources-file`, `resources-archive`, `resources-memory`, `resources-android` | `.resources`, `.resources.file`, `.resources.archive`, `.resources.memory`, `.resources.android` |
| `audio-api`, `audio-javasound`, `audio-android`, `audio-codec-ogg` | `.audio`, `.audio.javasound`, `.audio.android`, `.audio.codec.ogg` |
| `graphics`, `graphics-imageio`, `graphics-android` | `.graphics`, `.graphics.imageio`, `.graphics.android` |
| `input-api`, `input-awt`, `input-android` | `.input`, `.input.awt`, `.input.android` |
| `tilemap`, `camera`, `ecs`, `physics`, `traits` | `.tilemap`, `.camera`, `.ecs`, `.physics`, `.traits` |
| `composition`, `multiplayer` | `.composition`, `.multiplayer` |
| `test-support`, `examples` | `.testsupport`, `.examples` |

An implementation module's root is a child of the API it implements, so the package tree also reads as the dependency direction. The `resources-*` providers are named `resources-file` (filesystem), `resources-archive` (ZIP archives through `java.util.zip`), `resources-memory` (memory and temporary storage) and `resources-android` (Android assets); the application-stream provider lives in `resources`.

### 2. Physical layout

- Each Maven module is a folder at the repository root named after its artifact, `lionengine-<module>` (for example `lionengine-core`), declared in the aggregator `pom.xml`.
- Modules that compile against the Android SDK (`platform-android`, `audio-android`, `input-android`, `graphics-android`, `resources-android`) are Android library modules of the separate Gradle build under `android/` (ADR-0012), which consumes the Maven artifacts; the same build holds the DEX verification module (`OQ-147`).
- A module is declared when its first batch starts; the skeleton declares the modules of Phases 1 to 3 (see [`../features/build-skeleton.md`](../features/build-skeleton.md)).

### 3. Callback types and absence

- Shared modules do not use `java.util.function`, `Optional`, `java.util.stream` or `java.time` in public APIs or in per-step and per-frame paths. Each callback is a small interface named by its role, with primitive parameters where it runs in hot paths: for example `SceneFactory<C>` (`create(C context)`), `TransitionCondition<C>` (`test(C context)`), `CloseListener`, `LagListener`. Such interfaces have one abstract method, so lambdas and method references work.
- Absence is expressed without `Optional`: the capability lookup is `<T> T getCapability(Class<T> type)` returning `null` when absent, plus `boolean hasCapability(Class<?> type)`; both documented as such. Comparing class literals is not reflection (ADR-0020).
- This removes the need for desugared APIs from every shared public API; `OQ-147` still verifies the language features and the remaining JDK APIs.

### 4. Canonical names

The batches use these names for the types the documents already describe, so that independent batches agree:

| Concept | Type name | Module |
|---|---|---|
| Logging facade, level | `Log`, `LogLevel`, `LogSink` | `core` |
| Definition error, JSON value, typed configuration, registry | `DefinitionException`, `JsonValue` (with `JsonObject`, `JsonArray`), `Configuration`, `Registry<T>` | `core` |
| Seeded random generator | `RandomSource` | `core` |
| Monotonic clock, loop, loop configuration | `Clock`, `GameLoop`, `LoopConfig` | `runtime` |
| Inbox, asynchronous handle and states, load group | `Inbox`, `AsyncHandle<T>`, `AsyncState`, `LoadGroup` | `runtime` |
| Scene, manager, flow | `Scene`, `SceneManager`, `SceneFlow<C>`, `SceneFlowBuilder<C>`, `TransitionKind`, `TransitionListener` | `runtime` |
| Platform object, capability lookup, lifecycle events | `Platform`, `PlatformEvent` | `platform-api` |
| Screen mode, display mode, display request and effective result | `ScreenMode`, `DisplayMode`, `DisplayRequest`, `EffectiveResult` | `platform-api` |
| Command buffer, viewport, texture handle, scaling policy, filter | `CommandBuffer`, `Viewport`, `TextureHandle` (an `int`), `ScalingPolicy`, `OutputFilter` | `render-api` |
| Resource key, provider, resources, resource error and reason | `ResourceKey`, `ResourceProvider`, `Resources`, `ResourceException`, `ResourceException.Reason` | `resources` |
| Decoding errors of consuming modules | `ImageDecodingException`, `AudioDecodingException`, `MapLoadingException` | `graphics`, `audio-api`, `tilemap` |
| Pixel buffer, image cache, sprite sheet, sprite, animation | `PixelBuffer`, `ImageCache`, `SpriteSheet`, `Sprite`, `Animation`, `Animator` | `graphics` |
| Audio entry point, sounds, variants, playlist | `Audio`, `ShortSound`, `LongSound`, `VariantSet`, `Playlist` | `audio-api` |
| World, system, query, trait type | `World`, `GameSystem`, `Query`, `TraitType<T>` | `ecs` |
| Box shape, broad phase, shape query | `Box`, `BroadPhase`, `ShapeQuery` | `physics` |
| Tile map, tileset reference, auto-tiling rules | `TileMap`, `Tileset`, `AutoTileRules` | `tilemap` |
| Camera, view transform, follow lookup | `Camera`, `ViewTransform`, `FollowLookup` | `camera` |
| Input actions, bindings, input state, player | `Action`, `Bindings`, `InputState`, `Player` | `input-api` |

Names not listed are chosen by the batch under [`../conventions.md`](../conventions.md) and reviewed in its pull request. A batch that finds a listed name unsuitable proposes the change in its pull request and updates this table through a new ADR.

## Options considered

- Per-module base package (chosen) vs one base package with a linear package nesting of all modules (the module graph is not a chain: `tilemap` needs both `graphics` and `physics`, `traits` both `ecs` and `physics`, so packages would nest absurdly) vs dropping the plugin (loses the intra-module rule the owner chose) vs `excludes` patterns for every allowed cross-module edge (long, fragile lists).
- Role-named callback interfaces (chosen) vs `java.util.function` (desugaring at API 21, generic names that document nothing).
- Canonical names now (chosen; independent batches stay consistent) vs names per batch (drift between batches written by different agents).

## Consequences

- Positive consequences: the module graph of `modules.md` can be built under the plugin; the package tree mirrors the API-to-implementation direction; no shared public API depends on desugaring; batches written separately agree on names.
- Costs / constraints: each module POM sets its `basePackage`; one more skeleton check (declared dependencies within `modules.md`); a few more small interfaces.
- Impact on modules and APIs: every module (package roots); `platform-api` capability lookup returns `null` when absent; `runtime` scene flow uses `SceneFactory<C>` and `TransitionCondition<C>`.
- Impact on tests and documentation: `modules.md`, `conventions.md`, `features/build-skeleton.md`, `features/scenes.md`, `android-compatibility.md`, `dependencies.md`, `open-questions.md` updated.

## Validation

Decided by the agent on 2026-11-23 under the owner's explicit delegation; reversible by a superseding ADR.
