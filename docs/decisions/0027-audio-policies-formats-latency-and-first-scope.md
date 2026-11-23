# ADR-0027 — Audio policies: cache, voices, variants, streaming, playlists, formats, latency and first scope

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner ("Confirmed as proposed, write the ADR")
- Related requirements: `REQ-AUDIO-002` to `REQ-AUDIO-008`, `REQ-AUDIO-010`, `REQ-AUDIO-011`, `REQ-DET-001`, `REQ-RT-005`
- Open questions resolved: `OQ-110`, `OQ-111`, `OQ-112`, `OQ-113`, `OQ-114`, `OQ-115`, `OQ-116`, `OQ-117`
- Supersedes / superseded by: none; completes [ADR-0004](0004-audio-contracts-short-and-long-sounds.md) (unchanged)

## Context

ADR-0004 fixed two sound families, backend-independent selection and playlist logic, and decoded caches inside audio modules. Policies, formats, latency and scope were open. ADR-0018, ADR-0019, ADR-0022 and ADR-0024 settled the threading, loading, backend split and determinism constraints.

## Decision

1. **Short-sound cache (`OQ-110`).** Sounds are preloaded explicitly, normally by the scene that needs them; lazy loading on first play is an option. Reference-counted, no automatic eviction (ADR-0019), runtime thread only (ADR-0018). A size limit is opt-in. A short sound has a documented maximum length; longer sounds belong to the long family.
2. **Voices (`OQ-111`).** The number of simultaneous short voices is configurable, default 16 (a starting value, to calibrate by measurement on Android, `OQ-070`). Default overflow policy: steal the oldest voice of the lowest priority; drop-new and refuse are selectable.
3. **Variants (`OQ-112`).** Default selection: random with no immediate repeat, with optional weights. "Composed" means several sounds started together as simultaneous layers; offsets are a later extension. Variant randomness uses its own seeded generator, separate from the world's, so cosmetic audio choices cannot alter the deterministic simulation (`REQ-DET-001`).
4. **Long sounds (`OQ-113`).** Streamed by default: decoding in chunks into a small bounded buffer, so memory stays bounded on Android (`OQ-033`). Fully decoded preload is an option for short music loops. Streaming decode may use a worker that only decodes (ADR-0019).
5. **Playlists (`OQ-114`).** At the end of a playlist: stop by default, loop opt-in. A failing track is skipped and reported through an event; abort is an option. The audio side posts end-of-track to the inbox (ADR-0018); the advance logic runs on the runtime thread, so the audio thread never mutates simulation state. The next track is prepared ahead to avoid a gap.
6. **Formats (`OQ-115`).** Baseline on every backend: PCM WAV, parsed by project code. First compressed format: OGG Vorbis, decoded by the platform on Android and, on desktop, by a decoder library to be chosen after its license and Android check are recorded (`OQ-148`). MP3 is not in the baseline. This narrows `REQ-AUDIO-002`.
7. **Latency (`OQ-116`).** Targets, to be measured and not guaranteed: "low latency" for short sounds is playback starting within 50 ms of the request on desktop and within 100 ms best effort on Android API 21 and later; devices with a native low-latency path may do better.
8. **Scope (`OQ-117`).** First batch: play, stop, volume (master and per sound), looping, pause and resume. Second batch: mixer groups (for example music, effects, UI) and fades. Spatial audio (2D pan and attenuation with the camera as listener) is deferred.

## Options considered

- Long sounds: streamed by default (chosen; needs a decoding worker) vs fully decoded (large on Android).
- Overflow: steal oldest lowest-priority (chosen) vs drop-new (notable sounds lost) vs refuse (silent failures).
- Formats: WAV baseline plus OGG Vorbis (chosen) vs MP3 first (decoder and license questions) vs a bundled decoder library now (unverified license and DEX).
- Variant randomness: separate seeded generator (chosen) vs the world's (couples cosmetics to determinism).

## Consequences

- Positive consequences: bounded memory; a deterministic simulation unaffected by audio; a small dependency-free baseline.
- Costs / constraints: a WAV parser and a streaming decoder to write; desktop OGG delayed behind a license check; latency targets only measured later.
- Impact on modules and APIs: `audio-api` (voice policy, variants, playlist events), audio backends.
- Impact on tests and documentation: `REQ-AUDIO-002` narrowed, `REQ-AUDIO-010` and `REQ-AUDIO-011` added; feature file, contracts, glossary, android-compatibility, roadmap updated.

## Validation

Decisions confirmed by the project owner on 2026-11-23. Follow-ups: `OQ-161`.
