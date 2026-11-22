# Proposed roadmap

**Status: PROPOSED — indicative order, not a delivery commitment.**

## Phase 0 — structuring decisions and project skeleton

- apply the validated choices: Java 25+, Maven 3+, X/Y coordinates in meters and time in seconds (ADR-0001);
- specify the coordinate system (origin, direction of Y), meters/pixels conversions and angle conventions (OQ-010 to OQ-013);
- formalize the game loop model mathematically: simulation step, frameskipping, extrapolation and factor greater than 1 (OQ-020, OQ-021);
- decide the place of ECS: core model or optional module (OQ-041);
- set package conventions, versioning, thread rules and physical module layout (OQ-003, OQ-030);
- define the Android build strategy with Maven (OQ-002) and the project license (OQ-006);
- define the core/frontend boundaries;
- choose the first reference frontend (a headless test frontend is mandatory, REQ-TEST-001);
- define the minimal game loop and resource contracts;
- start from the existing Maven baseline (aggregator `pom.xml`, no modules yet);
- set up the build skeleton and continuous integration: Maven build, test framework, Javadoc with doclint, automatic dependency-rule check (REQ-CORE-005) and an **Android/DEX verification job** on the skeleton, as required by ADR-0001 (verification is continuous, not postponed).

**Expected output:** recorded decisions, compilable repository skeleton passing CI (including the Android/DEX job), applicable agent rules.

## Phase 1 — testable foundation

- minimal core;
- abstract clock and test-driven loop (`test-support`);
- runtime lifecycle;
- resources with memory and filesystem providers;
- minimal reference frontend;
- contract tests and a headless example;
- the `examples` module with a CI job that compiles and runs every example (REQ-DOC-001);
- first entries of the API index and of the game development guide (REQ-DOC-002, REQ-DOC-003);
- sources and Javadoc artifacts published by the build (REQ-DOC-004).

**Expected output:** a demo application starts, updates and stops cleanly, and a newcomer (human or agent) can reproduce it from the guide and the example alone.

## Phase 2 — 2D rendering and media

- rendering contract;
- images and sprites;
- spritesheet and animation; sprite, animated sprite and sprite sheet contracts in `graphics` (ADR-0003, REQ-SPRITE-*, REQ-ANIM-*, REQ-SHEET-*), in three batches: sprite sheet (foundation), sprite, animated sprite; batching stays a note inside the sprite feature;
- first rendering backend;
- audio API and first backend if prioritized;
- screen modes, display-mode selection and logical-resolution scaling, then output filtering (ADR-0002, REQ-DISPLAY-*, REQ-FILTER-*), after the first backend exists; `HQ2X`/CRT sources and licenses (OQ-093) and shaders (OQ-094) before filter implementation; persistence of user display settings (OQ-095) to be placed.

**Expected output:** a demonstrator displaying an animated scene and playing a sound.

## Phase 3 — game world

- objects/scenes;
- basic traits;
- tilemap and tilesets;
- collisions according to the retained scope.

**Expected output:** a simple 2D game demonstrator.

## Phase 4 — input and platforms

- keyboard, mouse, gamepad;
- additional frontend(s);
- Android frontend (`platform-android`) on top of the already-verified Android/DEX pipeline.

**Expected output:** the same game logic usable through several frontends.

## Phase 5 — advanced extensibility

- optional ECS strategy if justified (see OQ-041);
- local multiplayer and split-screen;
- network architecture, only after the synchronization needs are defined (OQ-060).

**Expected output:** optional modules and demonstrators, without weighing down the minimal foundation.

## Phase 6 — first game

- create the game repository from [`templates/GAME-AGENTS-TEMPLATE.md`](templates/GAME-AGENTS-TEMPLATE.md) (OQ-080);
- build the game in small batches with local and cloud agents ([`agent-workflow.md`](agent-workflow.md));
- every missing engine capability becomes an engine change request (OQ-081), not a workaround buried in the game.

**Expected output:** a playable game built only on the public engine API, plus the list of API gaps and documentation fixes it revealed.

## Progression rule

Each phase is split into small, verifiable batches. A batch must define its scope, requirements, APIs, tests, dependencies and acceptance criteria before implementation (see [`templates/FEATURE-TEMPLATE.md`](templates/FEATURE-TEMPLATE.md)). Open questions needed by a phase must be resolved before that phase starts. A batch that adds or changes a public API is not complete without its example and its API-index entry.
