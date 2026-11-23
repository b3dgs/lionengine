# Roadmap

**Status: VALIDATED — delivery order of the phases; dates are not committed. Each phase is split into batches specified with [`templates/FEATURE-TEMPLATE.md`](templates/FEATURE-TEMPLATE.md).**

## Phase 0 — structuring decisions and project skeleton

Done (ADR-0001 to ADR-0035):
- technical baseline, coordinates, units, angles, numeric types (ADR-0001, ADR-0012 to ADR-0016);
- game loop, threads, events, loading, memory, no reflection (ADR-0017 to ADR-0020, ADR-0031);
- scene, world and object model, modules and their dependencies (ADR-0021, ADR-0022, `architecture/modules.md`);
- game repository, change requests, assets, API index, dependency policy (ADR-0023, ADR-0032);
- determinism, budgets, supported desktop platforms (ADR-0024);
- every feature contract (ADR-0025 to ADR-0030, ADR-0033 to ADR-0035).

Remaining, in this order:
1. build skeleton (OQ-149): declare the modules of `architecture/modules.md` as Maven modules with empty `package-info.java`, package tree verified by `radial-encapsulation-maven-plugin`, JUnit 5, Javadoc with doclint failing on warnings (REQ-CORE-003), coverage, exact build and test commands recorded in `README.md` and `AGENTS.md`;
2. CI: Linux build and tests, Windows and macOS as non-blocking jobs (REQ-SUPPORT-001), documentation check, API index and asset provenance checks (REQ-DOC-005, REQ-DOC-006);
3. Android/DEX verification job and the record of verified Java features at API 21 (OQ-147).

**Expected output:** a compilable repository skeleton passing CI, including the Android/DEX job.

## Phase 1 — testable foundation

- `core`: logging facade, JSON reader, definition exception, registry and typed configuration, random generator (ADR-0031, ADR-0032);
- `test-support`: controllable clock, manually driven loop, headless frontend (REQ-TEST-001);
- `runtime`: loop with its calibration, inbox, scene contract and scene manager (one active scene) (ADR-0017, ADR-0018, ADR-0031);
- `resources` with memory and filesystem providers, provider order, resource convention suite (ADR-0007, ADR-0032);
- `platform-api` lifecycle events and capability lookup; minimal reference frontend `platform-awt` and `render-awt` (ADR-0008, ADR-0022);
- `examples` with a CI job that compiles and runs every example (REQ-DOC-001);
- first entries of the API index and of the game development guide (REQ-DOC-002, REQ-DOC-003);
- sources and Javadoc artifacts published by the build (REQ-DOC-004).

**Expected output:** a demo application starts, updates and stops cleanly, and a newcomer (human or agent) can reproduce it from the guide and the example alone.

## Phase 2 — 2D rendering and media

- `render-api` command buffer and viewport; `render-awt` executing it;
- `graphics` in three batches: sprite sheet (pixel buffer, PNG decoder, cache, sheet definition), sprite, animated sprite ([`features/sprite-sheet.md`](features/sprite-sheet.md), [`features/sprite.md`](features/sprite.md), [`features/animated-sprite.md`](features/animated-sprite.md));
- screen modes, display-mode selection and logical-resolution scaling, then output filtering ([`features/display-modes.md`](features/display-modes.md), [`features/output-filtering.md`](features/output-filtering.md));
- audio in batches: contracts + fake backend + contract suites; short sounds with cache, voices and variants; long sounds with streaming and playlists; `audio-javasound`; mixer groups and fades ([`features/audio-short-and-long-sounds.md`](features/audio-short-and-long-sounds.md));
- asynchronous loading and load groups (REQ-RT-004, REQ-RT-010);
- scene overlay stack and scene flow ([`features/scenes.md`](features/scenes.md), REQ-SCENE-003 to REQ-SCENE-008).

**Expected output:** a demonstrator displaying an animated scene and playing a sound.

## Phase 3 — game world

- `ecs` ([`features/ecs.md`](features/ecs.md)), `physics` and `traits` ([`features/standard-traits.md`](features/standard-traits.md));
- `tilemap` with the Tiled loader and tile collision ([`features/tile-map.md`](features/tile-map.md));
- `camera` ([`features/camera.md`](features/camera.md));
- `input-api` and `input-awt` (keyboard and mouse) (ADR-0035), needed by the demonstrator;
- `composition`: views, ready-made scene content and the camera follow adapter (ADR-0031).

**Expected output:** a simple 2D platformer demonstrator: a tile map, a player that runs, jumps and collides, a following camera, sounds.

## Phase 4 — platforms

- Android frontend: `platform-android`, `audio-android`, `input-android`, `graphics-android`, on top of the already verified Android/DEX pipeline;
- `render-opengl` with the GPU filters (CRT);
- desktop gamepad adapter, with its dependency recorded (ADR-0035);
- multi-monitor and HiDPI, if scheduled (OQ-096).

**Expected output:** the same game logic running on desktop and Android.

## Phase 5 — advanced extensibility

- local multiplayer demonstrator: two views and two players (REQ-MP-001);
- network architecture, only after the synchronization needs of a real game are defined (OQ-165).

**Expected output:** optional modules and demonstrators, without weighing down the minimal foundation.

## Phase 6 — first game

- create the game repository from [`templates/GAME-AGENTS-TEMPLATE.md`](templates/GAME-AGENTS-TEMPLATE.md) (ADR-0023);
- build the game in small batches with local and cloud agents ([`agent-workflow.md`](agent-workflow.md));
- every missing engine capability becomes an engine change request (GitHub issue template, ADR-0023), not a workaround buried in the game.

**Expected output:** a playable game built only on the public engine API, plus the list of API gaps and documentation fixes it revealed.

## Progression rule

Each phase is split into small, verifiable batches. A batch defines its scope, requirements, APIs, tests, dependencies and acceptance criteria before implementation (see [`templates/FEATURE-TEMPLATE.md`](templates/FEATURE-TEMPLATE.md)). The open questions needed by a phase are resolved before that phase starts (today: OQ-147 and OQ-149 for Phase 0). A batch that adds or changes a public API is not complete without its example and its API-index entry.
