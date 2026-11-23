# ADR-0022 — Optional multiplayer, audio backend split, reference frontend, AWT surface hand-off, unknown display values

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner ("Confirmed as proposed, write the ADR")
- Related requirements: `REQ-MP-001`, `REQ-MP-002`, `REQ-AUDIO-001`, `REQ-AUDIO-002`, `REQ-TEST-001`, `REQ-PLATFORM-001`, `REQ-TECH-006`, `REQ-RT-008`, `REQ-DISPLAY-006`, `REQ-CORE-005`
- Open questions resolved: `OQ-052`, `OQ-053`, `OQ-055`, `OQ-123`, `OQ-124`
- Supersedes / superseded by: none; completes [ADR-0004](0004-audio-contracts-short-and-long-sounds.md) and [ADR-0008](0008-awt-desktop-implementer-of-platform-api.md) (both stay unchanged)

## Context

Five questions of the "Modules and APIs" section remained open: the status of `multiplayer`, the split of the audio backends, the first reference frontend, how the AWT surface reaches the renderer without crossing an `*-api`, and how unknown display-mode values are represented.

## Decision

1. **`multiplayer` is an optional extension** (`OQ-052`). No other module depends on it. Local split-screen needs no `multiplayer` module: it is several views in one scene (ADR-0021). The module covers sessions and network transport only (Phase 5, `OQ-060`). The design constraints that keep a later network mode possible stay on the other modules (`REQ-MP-002`, `OQ-145`).
2. **Audio backends are split by output library or platform** (`OQ-053`): one backend module per library and platform (for example a desktop Java Sound backend, an Android backend, later an OpenAL backend). Names are provisional. A decoder gets its own module per codec only when a third-party library is needed, so that its license and Android compatibility are recorded once (`OQ-148`, `OQ-115`). Formats supported by the JDK or the platform stay inside the backend.
3. **The first reference frontend is desktop AWT** (`OQ-055`): `platform-awt` plus `render-awt`, together with the headless test frontend (`REQ-TEST-001`). `platform-android` follows; the DEX verification job (`OQ-147`) starts at the build skeleton so that Android compatibility is checked from the beginning.
4. **`render-awt` depends on `platform-awt`** (`OQ-123`). Both are AWT-only, desktop-only implementation modules, so this is an implementation-to-implementation dependency, not an `*-api` crossing, and `REQ-CORE-005` allows it. The surface is reached through the typed capability lookup of [ADR-0020](0020-no-reflection-factory-registry-and-capability-lookup.md) on the platform object; the AWT surface capability type lives in `platform-awt`. No AWT type appears in `platform-api` or `render-api` (`REQ-DISPLAY-006`). A later `render-opengl` brings its own surface capability; it is not changed by this decision.
5. **Unknown display-mode values are `0`** (`OQ-124`): the refresh rate (Hz) and the bit depth are `int` values where `0` means unknown or multiple, with a documented "is known" check in the contract. AWT's `0` and `-1` are normalized to `0` by `platform-awt`. Nothing is allocated and no desugared Java API is needed.

## Options considered

### `multiplayer` status
- A (chosen): optional extension. Keeps the base small; local views are already covered.
- B: base module. Imposes network concepts on every game (contradicts `vision.md` principle 7).

### Audio backend split
- A (chosen): per library or platform, decoders per codec only when needed. One license and Android record per third-party library.
- B: per codec only. Every backend would depend on every decoder.
- C: per platform only. Hides library choices inside one module.

### Reference frontend
- A (chosen): AWT desktop plus headless first. No native dependency, runs in CI. Cost: Android-specific issues appear a little later; mitigated by `OQ-147` at the skeleton.
- B: Android first. Delays a runnable demonstrator and needs the DEX pipeline before any code.

### Surface hand-off
- A (chosen): `render-awt` ─► `platform-awt` with the AWT surface type in `platform-awt`. Smallest design.
- B: a shared `awt-common` module for the type. Cleaner isolation but a module for one type; can be introduced later if a second AWT consumer appears.
- C: an opaque surface object in `platform-api`. Needs casting inside the renderer and weakens type safety.

### Unknown values
- A (chosen): `int` with `0` for unknown or multiple. Cost: "unknown" and "multiple" are not distinguished; a richer type can be added by a later ADR if needed.
- B: `OptionalInt` or nullable wrappers. More expressive, allocates and depends on desugaring.

## Consequences

- Positive consequences: small base; explicit and reviewable dependencies; a runnable reference frontend early.
- Costs / constraints: more audio modules over time; `render-awt` cannot be used with another desktop platform implementer; Android reaches parity after the desktop reference.
- Impact on modules and APIs: `modules.md` (`render-awt` depends on `platform-awt`; `multiplayer` optional; audio backend naming), `contracts.md`, `roadmap.md`, `api-index.md`.
- Impact on tests and documentation: traceability updated; no new requirement.

## Validation

Decisions confirmed by the project owner on 2026-11-23 ("Confirmed as proposed"). Follow-ups: `OQ-156`.
