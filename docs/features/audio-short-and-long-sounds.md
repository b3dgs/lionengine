# Feature: audio, short sounds and long sounds

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2, see `roadmap.md` (batches: contracts + fake backend + contract tests; short sounds with cache, voices and variants; long sounds with streaming and playlists; first desktop backend `audio-javasound`; mixer groups and fades; Android backend `audio-android`)
- Affected modules: `audio-api`, `audio-javasound`, `audio-android`, `audio-codec-ogg`, `resources`, `test-support`, `examples`
- Requirements: `REQ-AUDIO-001` to `REQ-AUDIO-013`, `REQ-RES-004`, `REQ-RES-005`
- Applicable ADRs: [`ADR-0004`](../decisions/0004-audio-contracts-short-and-long-sounds.md), [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md), [`ADR-0007`](../decisions/0007-resource-convention-keys-release-and-errors.md), [`ADR-0018`](../decisions/0018-runtime-thread-events-and-cache-thread-safety.md), [`ADR-0022`](../decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md), [`ADR-0027`](../decisions/0027-audio-policies-formats-latency-and-first-scope.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md)
- Open questions involved: none (Android latency measurement waits for the DEX job, ADR-0041)

## Objective

Provide two audio families with different lifecycles: short sounds (low-latency effects, cached, variant sets) and long sounds (music, ambient, streamed, playlists).

## Out of scope

Spatial audio (deferred: camera center as listener, meters, pan and attenuation when scheduled); MP3; sound offsets inside composed sounds.

## Expected behavior

- **Short sound:** at most 10 s (a longer file is refused with a defined error); preloaded explicitly, normally by the scene that needs it, or lazily on first play as an option; cached by reference count with no automatic eviction; an opt-in cache limit in bytes; played individually and stopped; stopping a finished sound does nothing.
- **Voices:** 16 simultaneous short voices by default; on overflow the oldest voice of the lowest priority (`0` to `100`, default `50`) is stolen; drop-new and refuse are selectable.
- **Variant set:** random selection with no immediate repeat (when at least two variants have a positive weight), optional weights (non-negative `double`, default `1.0`, `0` excludes); or composed: all layers started together. Its own seeded generator, separate from the world's.
- **Long sound:** streamed by default through a bounded buffer (4 chunks of 4,096 frames); fully decoded preparation on request; released when no longer needed; played and stopped.
- **Playlist:** predefined long sounds; the next track is prepared ahead and starts when the current one ends; at the end it stops (default) or loops; a failing track is skipped and reported to a listener (playlist, track index, exception) on the runtime thread, or aborts the playlist on request; end-of-track is posted to the inbox and the advance runs on the runtime thread.
- **First batch controls:** play, stop, volume (gain in `[0, 1]`, master and per sound), looping, pause, resume.
- **Second batch:** mixer groups `music`, `effects`, `ui` plus game-defined groups; each sound in one group (`effects` for short sounds, `music` for long sounds by default); gain = master × group × sound. Fades: target gain and duration in seconds, linear ramp applied by the backend per audio buffer, completion posted to the inbox.
- **Formats:** PCM WAV parsed by project code on every backend; OGG Vorbis by the Android platform and by `audio-codec-ogg` (JOrbis candidate, after its register entry) on desktop.
- Variant selection and playlist advance are backend-independent; backends expose play, stop, pause, resume, gain and end-of-track only.

## Envisaged APIs / contracts

Responsibilities in [`../architecture/contracts.md`](../architecture/contracts.md) (Audio); canonical type names in [ADR-0037](../decisions/0037-package-tree-per-module-callbacks-and-canonical-names.md); signatures written by the batch: audio system, short sound and long sound handles, variant set, playlist, voice policy, mixer group, backend SPI.

## Batches (order)

1. `audio-api` contracts, fake backend and contract suites (`REQ-AUDIO-001`, `REQ-AUDIO-009`).
2. Short sounds: WAV parser, cache, voices, length limit (`REQ-AUDIO-003`, `REQ-AUDIO-004`, `REQ-AUDIO-010`, `REQ-AUDIO-013`).
3. Variant sets (`REQ-AUDIO-005`).
4. Long sounds and streaming, playlists (`REQ-AUDIO-006` to `REQ-AUDIO-008`).
5. First-batch controls: volume, looping, pause, resume (`REQ-AUDIO-011`).
6. `audio-javasound` (`REQ-AUDIO-002`).
7. Mixer groups and fades (`REQ-AUDIO-012`).
8. `audio-codec-ogg` after its register entry; `audio-android` in Phase 4.

## Lifecycle and concurrency

Distinct handles and lifecycles per family. Decoded caches live in the audio modules, on the runtime thread only. Backend threads and streaming decoders never touch simulation state; they post completions to the inbox (ADR-0018). Backgrounding pauses output (ADR-0031).

## Units and Android/DEX compatibility

Seconds for durations and positions, Hz for sample rate, gain in `[0, 1]`. No `javax.sound` in `audio-api`; `audio-javasound` is desktop only; `audio-android` uses platform APIs. Latency targets (50 ms desktop, 100 ms best effort Android) are measured by platform tests outside CI.

## Dependencies

`audio-api` on `core`, `resources`; backends on `audio-api`. Forbidden: any concrete audio library or platform type in `audio-api`. A codec library only in an `audio-codec-*` module with its row in [`../dependencies.md`](../dependencies.md).

## Tests

- Unit tests: seeded variant selection (no repeat, weights, composition), playlist advance (end, loop, failing track), voice stealing per policy, cache hit and miss, short-sound length limit, WAV parsing.
- Contract tests (`test-support`): reusable suites run on every backend, with a fake backend first.
- Integration tests: headless fake backend with a controllable clock, including fades and gain products.
- Platform tests (later, separate): latency, Android.

## Acceptance criteria

- [ ] Requirements covered.
- [ ] API reviewed in the pull request.
- [ ] Automated tests added and passing.
- [ ] Complete Javadoc.
- [ ] Headless example, API-index entry, traceability updated (REQ-DOC-001, REQ-DOC-002).
- [ ] No forbidden coupling (REQ-CORE-005).
- [ ] Android/DEX compatibility verified.
