# Engine features

**Status: VALIDATED — what the engine lets a game do, area by area. Kept up to date by every batch that changes a capability (see "Keeping this page current" below).**

One line per capability, no implementation detail. Each area links to its feature page, which holds the rules and values. The *State* column says where each area stands:

- **Specified**: decided and documented; no code yet.
- **In progress**: some batches merged; the page lists what works.
- **Available**: implemented, tested, with an example.
- **Planned**: in the roadmap, not yet specified in detail.

Today the build skeleton, the cross-module contracts ([`features/contract-skeleton.md`](features/contract-skeleton.md)), the core foundation ([`features/core-foundation.md`](features/core-foundation.md)), the game loop with asynchronous loading ([`features/game-loop.md`](features/game-loop.md)), the scenes with their overlay stack and declared flow ([`features/scenes.md`](features/scenes.md)), the file, memory and application-stream resource providers ([`features/resources.md`](features/resources.md)), the platform with rendering, headless and in a desktop window ([`features/platform-and-rendering.md`](features/platform-and-rendering.md)), the image decoding, image cache and sprite sheets with their definition files ([`features/sprite-sheet.md`](features/sprite-sheet.md)), the sprites drawn with render commands ([`features/sprite.md`](features/sprite.md)), and the animated sprites ([`features/animated-sprite.md`](features/animated-sprite.md)) exist: scenes and navigation, and images, sprites and animations are *Available*; resources and data files, the game loop, window and display, and platforms are *In progress*; every other area is *Specified*.

| Area | State | Phase |
|---|---|---|
| [Game loop and timing](#game-loop-and-timing) | In progress | 1–2 |
| [Scenes and navigation](#scenes-and-navigation) | Available | 1–2 |
| [Resources and data files](#resources-and-data-files) | In progress | 1 |
| [Window, display and rendering](#window-display-and-rendering) | In progress | 1–2 |
| [Images, sprites and animations](#images-sprites-and-animations) | Available | 2 |
| [Audio](#audio) | Specified | 2 |
| [Objects and behaviors](#objects-and-behaviors) | Specified | 3 |
| [Tile maps](#tile-maps) | Specified | 3 |
| [Camera](#camera) | Specified | 3 |
| [Input](#input) | Specified | 3–4 |
| [Platforms](#platforms) | In progress | 1–4 |
| [Multiplayer and network](#multiplayer-and-network) | Specified | 5 |

## Game loop and timing

[`features/game-loop.md`](features/game-loop.md)

- The game runs at a fixed simulation rate (60 updates per second by default, configurable), identical on fast and slow machines.
- Motion is drawn smoothly between updates, whatever the display rate.
- Pause and resume without jumps; slow motion and fast motion from 0.1× to 4×, for example for bullet time.
- Under heavy load the game slows down briefly instead of freezing; the lost time is reported.
- Menus and other presentation-only effects keep animating during pause and slow motion.
- Long loads can run in the background, with a progress value for a loading screen.
- Every behavior can be tested without a window, with a clock that the test controls.

Works now: the fixed-step loop with interpolation, catch-up, lag cap and report, pause, time scale, snapping, fail-fast stop, and the inbox for events from other threads (example `GameLoopExample`); background loading on worker threads with progress and load groups for a loading screen, images first (example `LoadingScreenExample`).

## Scenes and navigation

[`features/scenes.md`](features/scenes.md)

- A game is split into scenes: introduction, menu, level, pause, credits.
- The links between scenes are declared in one place: "on *finished*, go to the menu", "on *start*, if a slot is selected, go to the level".
- A pause menu or dialog can be shown over a scene, which stays visible but frozen.
- Mistakes in the links (unknown scene, missing start) are reported at startup.
- Data shared between scenes (save slot, level, score) lives in one game-owned object.

Works now: one active scene, switched by the game at a frame boundary, with every scene exited and disposed before the next is entered (example `SceneExample`); a pause menu or dialog shown over a scene, which stays visible but frozen and continues where it was once the menu closes (example `PauseOverlayExample`); the links between scenes declared in one place, as scene ids and named triggers with conditions on the game's data, checked at startup, replacing a scene, opening and closing an overlay, or ending the game from any scene (example `SceneFlowExample`).

## Resources and data files

[`features/resources.md`](features/resources.md), [`features/core-foundation.md`](features/core-foundation.md)

- Files are named by a logical key (`sprites/hero.png`) wherever they are: folder, ZIP archive, memory, Android assets, or a stream given by the game.
- Several sources can be stacked; the first one that has a file wins, so a mod folder can override the base data.
- Loaded data is shared and freed when nobody uses it any more.
- Game data (sprite sheets, animations, maps, object definitions, input bindings) is written in JSON; a mistake is reported with the file, line and field.
- Object types used by data files are registered by name (`game:enemy`), so data can create them without code changes.
- A seeded random generator gives the same results on every platform.

Works now: reading JSON data files with exact error positions, typed parameters with strict fields, the registry of data-driven types, logging and the seeded random generator (examples `DefinitionExample` and `RandomExample`). Files are opened by key from a folder, from memory or from streams registered by the game, with providers stacked so that the first one having a key wins (example `ResourcesExample`). Decoded images are cached and shared by the image cache, which follows the cache convention every module that caches decoded data must pass. ZIP archives and Android assets come with the next resources batch.

## Window, display and rendering

[`features/platform-and-rendering.md`](features/platform-and-rendering.md), [`features/display-modes.md`](features/display-modes.md), [`features/output-filtering.md`](features/output-filtering.md)

- The game draws at a fixed logical resolution (for example 320 × 180) and the engine scales it to the window: keep the aspect ratio, whole multiples only (crisp pixel art), or stretch.
- Windowed, borderless full-window and full-screen modes, with resolution and refresh-rate selection where the platform allows it; the game reads what was actually applied.
- Output filters on the final image: none (sharp pixels), smooth, `HQ2X`-style upscaling, CRT effect; custom filters can be added.
- Drawing is done in layers; several views can share the screen (split screen, minimap).
- The window reports resize, focus, background and close; closing can be confirmed by the game.
- Version 11 opens on the default monitor; on high-density (HiDPI) screens the scaling works on real device pixels, so whole-multiple scaling stays crisp. Choosing another monitor is not offered yet (ADR-0043).

Works now: the scaling of the logical frame to any surface size with the three policies; drawing with render commands (textures, positions, scale, rotation, tint, opacity, layers, views); a desktop window that shows the game, scales it when resized, pauses when iconified and stops when closed unless the game vetoes it (example `MovingRectangleDesktop`); and a headless platform with a recording renderer on which a game runs, draws and reacts to resize, background, foreground and close, for tests without a window (examples `HeadlessGameTest`, `MovingRectangle`). The desktop window is windowed only: full screen, display modes and output filters come with their own batches.

## Images, sprites and animations

[`features/sprite-sheet.md`](features/sprite-sheet.md), [`features/sprite.md`](features/sprite.md), [`features/animated-sprite.md`](features/animated-sprite.md)

- PNG images are loaded on every platform with the same result; sprite sheets cut an image into a grid of frames.
- Many sprites can share one image; each can be moved, scaled, rotated around its pivot, tinted, faded, flashed to a silhouette or color-graded.
- Color variants of one sprite (team colors) from a color mapping, without extra image files.
- Animations from frames and durations: play once, loop or ping-pong; pause, resume, stop.
- Animations advance with the simulation, so they stay in sync with gameplay.

Works now: decoding PNG images of every kind but interlaced and animated ones, with the same result on every platform and size limits against oversized files; an image cache that decodes each image once, shares it and its texture between scenes, keeps modified copies as their own images, frees each image with its last user, reports its memory with an optional limit, reloads a changed image during development, and refuses use from another thread than the game's (examples `ImageDecodingExample`, `ImageCacheExample`); sprite sheets that cut an image into a grid of cells, with margin and spacing, grouped by id and released together (example `SpriteSheetExample`), read from JSON definition files that also declare animations and color templates, with every mistake reported by file, line and field (example `SheetLoaderExample`); sprites showing an image or a frame of a sheet, moved, scaled, mirrored and rotated about their pivot, drawn with render commands in layers (example `SpriteExample`), any number of them sharing one image and its texture, beside modified copies drawn as images of their own (example `SpriteSharingExample`); sprites tinted and faded every frame at no cost, flashed to a silhouette or color-graded (gray, sepia, brightness) once and shared, with the same colors on a backend that cannot tint (example `ColorTransformExample`); color variants of one sprite such as team colors, from the color templates of its definition file, each computed once and shared, combinable with the other color effects (example `TeamColorExample`); animations defined in code or read from the sheet definition, with frame durations in seconds and their loop mode (example `AnimationDefinitionExample`), played from the update step (play, pause, resume, stop; loop, ping-pong or once), at the same speed whatever the step (example `AnimatorExample`); animated sprites drawn in the game loop, their frame advanced by the update step only and drawn at interpolated positions (example `AnimatedSpriteExample`). Optional platform image decoders (other formats than PNG) are not written yet.

## Audio

[`features/audio-short-and-long-sounds.md`](features/audio-short-and-long-sounds.md)

- Sound effects that start quickly and can be played many times at once (16 voices by default, the least important ones cut first).
- Random variants of an effect without immediate repetition, or several layered together (an explosion).
- Music and ambient sounds streamed with little memory; playlists that chain tracks and can loop.
- Volume per sound, and per group (music, effects, interface) with fades.
- WAV everywhere, OGG Vorbis on desktop and Android.

## Objects and behaviors

[`features/ecs.md`](features/ecs.md), [`features/standard-traits.md`](features/standard-traits.md)

- Objects are created and destroyed at any time, and receive or lose behaviors ("traits") while the game runs.
- Ready-made behaviors: position and size, movement with a speed limit, gravity, jump (with the forgiving "coyote time" and jump buffering), collision with layers and triggers, limited lifetime.
- Objects stand on and bump into solid tiles of the map.
- Games add their own behaviors and logic in the same way as the ready-made ones, in a fixed, documented order.
- Designed for at least 1,000 moving objects per frame, including on mid-range Android phones.
- Objects can be described in JSON and created from the description.

## Tile maps

[`features/tile-map.md`](features/tile-map.md)

- Maps made with the Tiled editor (JSON export) are loaded directly; maps can also be built in code.
- Tiles can be placed, changed and destroyed during the game; neighbors update their look automatically (auto-tiling).
- Solid tiles block objects; only the visible part of a map is drawn, so map size does not slow rendering.

## Camera

[`features/camera.md`](features/camera.md)

- Follow an object with optional smoothing, a dead zone and an offset; stay inside the level bounds.
- Zoom in and out, instantly or with an eased animation, around the center or a chosen point.
- Screen shake, identical on every platform for the same seed.
- Convert between screen and world positions (for mouse or touch picking).

## Input

[`features/input.md`](features/input.md)

- Games react to actions ("jump", "move") rather than keys; bindings can be changed and loaded from a file.
- Keyboard, mouse and touch on desktop and Android; gamepads on Android first.
- A quick tap is never missed and never counted twice.
- Several local players, each with their own devices and bindings.
- Input can be recorded and replayed.

## Platforms

[`features/platform-and-rendering.md`](features/platform-and-rendering.md), [`features/build-skeleton.md`](features/build-skeleton.md), [`features/contract-skeleton.md`](features/contract-skeleton.md)

- Desktop (Windows, macOS, Linux) through Java AWT: the first reference platform.
- Android from API level 21, with the same game code (Phase 4).
- OpenGL rendering on desktop and Android, needed for the CRT filter (Phase 4).
- A headless mode for automated tests and servers.

Works now: desktop through Java AWT (a window, the loop on its own thread, events, rendering) and the headless platform for tests (examples `MovingRectangleDesktop`, `HeadlessGameTest`). The Android frontend and OpenGL are not started; the shared modules already dex and run on an Android API 21 emulator in CI (Android/DEX job, [`android-compatibility.md`](android-compatibility.md)).

## Multiplayer and network

[`features/network.md`](features/network.md), [`features/multiplayer.md`](features/multiplayer.md)

- Local multiplayer and split screen are covered by views and input players above.
- Host a game on one machine and let others join by IP address and port with a player name; the host plays too, no dedicated server needed. A dedicated server can also run without a window.
- Every machine sees the same list of players with their ping and game-defined details (team, color, ready); the host can kick a player or close the game to newcomers.
- A built-in chat, to everyone or to one player, with a filter and flood protection.
- The server is the reference: it sends the real values of the objects about 20 times per second, so a client can never stay out of sync, even when packets are lost.
- Your own character reacts at once (prediction, corrected by the server if needed); other objects move smoothly (interpolation, about 0.1 s behind).
- Fits platformers and shoot'em ups (each player's input sent every step) and strategy games (orders sent as commands, only changed units sent).
- Games can send their own messages; servers on the local network can be found automatically.
- Designed for up to 16 players; a test mode simulates lag and packet loss on one machine.
- Not provided yet: encryption and passwords (the protocol is ready for them), internet matchmaking and NAT traversal, taking over when the host leaves.

## Not provided

A game editor (maps come from Tiled), a scripting language, a full physics engine (rigid bodies, joints), online services (matchmaking, server lists, relays), 3D.

## Keeping this page current

This page is part of the definition of done of every batch ([`../AGENTS.md`](../AGENTS.md) section 7, [`change-process.md`](change-process.md), [`templates/FEATURE-TEMPLATE.md`](templates/FEATURE-TEMPLATE.md)):

- a batch that adds, removes or changes what a game can do updates the matching bullet in the same pull request, in plain words, without implementation detail;
- the *State* column changes from *Specified* to *In progress* with the first merged batch of an area, and to *Available* when every batch of its feature page is merged with its example;
- a new feature page is added to the table and gets its own section; `scripts/check-docs.py` fails if a feature page is not linked from this page.
