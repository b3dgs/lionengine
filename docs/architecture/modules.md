# Conceptual module breakdown

**Status: PROPOSED — this table does not prejudge the physical split into artifacts.**

| Domain | Responsibility | Allowed dependencies (conceptual direction) |
|---|---|---|
| `core` | Common types, lifecycle, errors, events, cross-cutting contracts; the strict JSON reader for definition files (ADR-0028) | No frontend dependency |
| `runtime` | Orchestration, game loop, clock, scheduling; scene lifecycle contract and scene manager (ADR-0021) | `core` |
| `platform-api` | Window/surface contracts, platform capabilities, frontend lifecycle, screen modes and display-mode selection (ADR-0002) | `core` |
| `render-api` | Rendering contracts, textures, commands, viewport, output filtering (ADR-0002), scaling policy type (ADR-0006) | `core` |
| `render-awt` | AWT implementation; reaches the AWT surface through a capability of `platform-awt` (ADR-0022) | `render-api`, `platform-api`, `platform-awt` |
| `render-opengl` | OpenGL implementation | `render-api`, `platform-api`; `resources` if shader assets are delivered through it (ADR-0025, ADR-0005) |
| `platform-awt` | Desktop implementation of `platform-api` with the JDK AWT: window and surface lifecycle, screen modes, display modes, effective-result report; no rendering, input, audio or resources (ADR-0008) | `platform-api`, `core`; JDK `java.desktop` only; excluded from Android builds |
| `platform-android` | Android lifecycle, surface, resources and input adaptation | `platform-api` and the `*-api` modules it implements |
| `resources` | Resolution, opening, metadata and lifecycle of resources; media-agnostic, no decoded cache (ADR-0005); resource key and missing-resource exception types (ADR-0007) | `core` |
| `resources-*` | Filesystem, archive, memory/temporary, Android and stream providers | `resources` |
| `audio-api` | Backend-independent audio contracts: short sounds, long sounds, variant sets, playlists (ADR-0004) | `core`, `resources` (ADR-0004, decision 3B) |
| `audio-*` | Audio backends, one per output library or platform, and decoder modules per codec only when a third-party library is needed (ADR-0022);  own the decoded caches (ADR-0004, ADR-0005) | `audio-api`, `resources` |
| `graphics` | Images, logical textures, sprites, spritesheets, animations (ADR-0003); owns its decoded image cache (ADR-0005); built-in pure-Java PNG decoder, optional platform decoders (ADR-0026) | `core`, `resources`, `render-api` (ADR-0003: sprites emit `render-api` commands; OQ-051 resolved) |
| `tilemap` | Tilesets, grids, layers, static/dynamic maps (single layer first, collision deferred: ADR-0009, ACCEPTED) | `core`, graphics contracts; `resources` if map files are loaded through it (candidate under ADR-0005) |
| `camera` | Camera: view rectangle in world units, zoom, translation, follow through a lookup contract, smoothing, shake; single module, headless, no ECS or platform dependency (ADR-0010) | `core` only; the ECS adapter lives outside (OQ-132) |
| `input-api` | Normalized actions, states and input events | `core` |
| `input-*` | Keyboard, mouse and gamepad adapters per platform | `input-api`, `platform-api` |
| `traits` | Composable capabilities as ECS traits and systems (ADR-0011, ADR-0021): `Transformable`, `Movable`, `Gravity`, `Jumpable`, `Collidable`, `Lifetime` | `ecs`, `physics`, `core` |
| `ecs` | The object model (ADR-0011, ADR-0021): `World` container, object IDs, trait registry and sparse-set storage, cached queries, command buffer, events, system ordering; headless, independent of `tilemap` | `core` |
| `physics` | Headless collision contracts (ADR-0021): shapes (axis-aligned box first), overlap tests, uniform-grid broad phase over plain data; no rigid bodies | `core` |
| `multiplayer` | Optional extension (ADR-0022, no module depends on it): session and network transport abstractions; local split-screen is several views in one scene (ADR-0021) | `ecs`, `input-api`, network contracts to be defined |
| `test-support` | Controllable clock, manually driven loop, headless frontend, fake backends, reusable contract-test suites (REQ-TEST-001) | Contracts of the modules it tests; used in `test` scope only |
| `examples` | Minimal and demonstration applications, headless first | Public APIs only; never depended upon |

## Dependency direction (conceptual)

```text
examples ─────────────► public APIs of all modules
render-awt / render-opengl / platform-awt / platform-android / resources-* / audio-* / input-*
        └──────────────► *-api modules ──────────────► core
runtime, ecs, physics, graphics, tilemap, camera ───────────────► core (+ resources / render-api where listed)
audio-api, audio-* ──────────────────────────────────► resources (ADR-0004, ADR-0005)
traits ─► ecs, physics ─► core        tilemap ─► physics (tile collision, OQ-125)        multiplayer ─► ecs, input-api       test-support ─► (test scope)
```

Arrows always point from the dependent module to its dependency. A cycle, or an arrow from `core` or an `*-api` module to an implementation module, is a violation (REQ-CORE-005).

## Splitting principles

- A module must have a coherent responsibility and a limited public API.
- Do not automatically create one module per class or per tiny feature.
- Separate API and implementation when several plausible implementations exist or when there is a real platform boundary.
- Do not artificially split a concept if it adds complexity without isolation or testing benefit.
- The names above are provisional identifiers; they may be renamed before validation.

## Open points

Module-related open points (OQ-156 for the follow-ups of ADR-0022, OQ-121 for the thread safety of module caches) are tracked in [`../open-questions.md`](../open-questions.md).
