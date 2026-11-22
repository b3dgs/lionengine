# Feature: audio, short sounds and long sounds

**Status: PROPOSED**

- Owner: to be filled in
- Roadmap phase: Phase 2, see `roadmap.md` (batches: contracts + fake backend + contract tests; short sounds with cache and variants; long sounds with preload/release and playlist; first desktop backend; Android backend)
- Affected modules: `audio-api`, `audio-*`, `resources`, `test-support`, `examples`
- Requirements: `REQ-AUDIO-003` to `REQ-AUDIO-009`, `REQ-RES-004` (refines `REQ-AUDIO-001`, `REQ-AUDIO-002`)
- Applicable ADRs: [`ADR-0004`](../decisions/0004-audio-contracts-short-and-long-sounds.md), [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md) (PROPOSED)
- Open questions involved: `OQ-110` to `OQ-118` (`OQ-053`, `OQ-032` touched)

## Objective

Provide two audio families with different lifecycles: short sounds (low-latency effects, cached, variant sets) and long sounds (music, ambient, preload and release, playlists).

## Out of scope

Pause/resume, volume, looping, fades, spatial audio, mixer channels (`OQ-117`).

## Expected behavior

- Short sound: cached for heavy reuse without repeated loading or decoding; played individually; stopped.
- Variant set: groups several short-sound files; playing it selects one variant randomly or plays several simultaneously; policies `OQ-112`.
- Long sound: preloaded for direct use, released when no longer needed; played individually; stopped.
- Playlist: predefined long sounds; next track starts when the current terminates; can be stopped; end, failure and thread: `OQ-114`.
- Variant selection and playlist advance are backend-independent; backends expose play, stop and end-of-track only (decision 2A).

## Envisaged APIs / contracts

Responsibilities only; signatures are not fixed. See `architecture/contracts.md`.

## Lifecycle and concurrency

Distinct handles and lifecycles per family. Decoded caches live inside the audio modules and follow the common resource convention (ADR-0005, `REQ-RES-004`, `OQ-118`, `OQ-110`). Completion callbacks must not mutate simulation state from the audio thread (`OQ-114`, `OQ-030`).

## Units and Android/DEX compatibility

Seconds for durations and positions, Hz for sample rate. No `javax.sound` in `audio-api`; real backends are platform-specific; decoders, licenses and desugaring to be verified first (`OQ-115`, `docs/android-compatibility.md`).

## Dependencies

Allowed: `core`, `resources`. Forbidden: any concrete audio library or platform type in `audio-api`.

## Tests

- Unit tests: seeded variant selection, playlist advance, cache hit and miss.
- Contract tests (`test-support`): reusable suites for every backend.
- Integration tests: headless fake backend with controllable clock.
- Platform tests (later, separate): latency, Android.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API validated.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
