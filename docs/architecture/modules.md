# Conceptual module breakdown

**Status: PROPOSED — this table does not prejudge the physical split into artifacts.**

| Domain | Responsibility | Allowed dependencies (conceptual direction) |
|---|---|---|
| `core` | Common types, lifecycle, errors, events, cross-cutting contracts | No frontend dependency |
| `runtime` | Orchestration, game loop, clock, scheduling | `core` |
| `platform-api` | Window/surface contracts, platform capabilities, frontend lifecycle, screen modes and display-mode selection (ADR-0002, PROPOSED) | `core` |
| `render-api` | Rendering contracts, textures, commands, viewport, output filtering (ADR-0002, PROPOSED) | `core` |
| `render-awt` | AWT implementation | `render-api`, `platform-api` |
| `render-opengl` | OpenGL implementation | `render-api`, `platform-api`; `resources` if shader assets are delivered through it (OQ-094, candidate under ADR-0005, PROPOSED) |
| `platform-android` | Android lifecycle, surface, resources and input adaptation | `platform-api` and the `*-api` modules it implements |
| `resources` | Resolution, opening, metadata and lifecycle of resources; media-agnostic, no decoded cache (ADR-0005, PROPOSED) | `core` |
| `resources-*` | Filesystem, archive, memory/temporary, Android and stream providers | `resources` |
| `audio-api` | Backend-independent audio contracts: short sounds, long sounds, variant sets, playlists (ADR-0004, PROPOSED) | `core`, `resources` (ADR-0004 decision 3B, PROPOSED; firm once ADR-0004 is accepted) |
| `audio-*` | Audio backends and decoders; own the decoded caches (ADR-0004, ADR-0005, PROPOSED) | `audio-api`, `resources` |
| `graphics` | Images, logical textures, sprites, spritesheets, animations (ADR-0003, PROPOSED); owns its decoded image cache (ADR-0005, PROPOSED); image decoders are platform-specific (OQ-100) | `core`, `resources`, `render-api` depending on the separation to be defined (OQ-051; ADR-0003 assumes sprites emit `render-api` commands) |
| `tilemap` | Tilesets, grids, layers, static/dynamic maps | `core`, graphics contracts; `resources` if map files are loaded through it (candidate under ADR-0005, PROPOSED) |
| `input-api` | Normalized actions, states and input events | `core` |
| `input-*` | Keyboard, mouse and gamepad adapters per platform | `input-api`, `platform-api` |
| `world` | Scenes, objects, lifecycle, queries | `core` |
| `traits` | Composable capabilities: mobility, gravity, collision, etc. | `world` and physics contracts (location: OQ-043) |
| `ecs` | Optional ECS storage/systems strategy (core model or optional module: OQ-041) | `core`; contracts shared with `world` to be defined |
| `multiplayer` | Player, view, session and network transport abstractions | `world`, `input-api`, network contracts to be defined |
| `test-support` | Controllable clock, manually driven loop, headless frontend, fake backends, reusable contract-test suites (REQ-TEST-001) | Contracts of the modules it tests; used in `test` scope only |
| `examples` | Minimal and demonstration applications, headless first | Public APIs only; never depended upon |

## Dependency direction (conceptual)

```text
examples ─────────────► public APIs of all modules
render-awt / render-opengl / platform-android / resources-* / audio-* / input-*
        └──────────────► *-api modules ──────────────► core
runtime, world, graphics, tilemap ───────────────────► core (+ resources / render-api where listed)
audio-api, audio-* ──────────────────────────────────► resources (ADR-0004, ADR-0005, PROPOSED)
traits ─► world        multiplayer ─► world, input-api       test-support ─► (test scope)
```

Arrows always point from the dependent module to its dependency. A cycle, or an arrow from `core` or an `*-api` module to an implementation module, is a violation (REQ-CORE-005).

## Splitting principles

- A module must have a coherent responsibility and a limited public API.
- Do not automatically create one module per class or per tiny feature.
- Separate API and implementation when several plausible implementations exist or when there is a real platform boundary.
- Do not artificially split a concept if it adds complexity without isolation or testing benefit.
- The names above are provisional identifiers; they may be renamed before validation.

## Open points

Module-related open points (OQ-041, OQ-043, OQ-044, OQ-050 to OQ-053, OQ-091 for the owner of the scaling policy, OQ-118 for the common resource convention and OQ-119 for the desktop implementer of `platform-api`) are tracked in [`../open-questions.md`](../open-questions.md).
