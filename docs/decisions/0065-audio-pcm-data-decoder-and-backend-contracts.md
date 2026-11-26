# ADR-0065 — Audio contracts of batch 1: 16-bit PCM data, the decoder contract, the backend SPI with voices and end events through the inbox

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-AUDIO-001`, `REQ-AUDIO-009`, `REQ-AUDIO-010`
- Open questions resolved: none (Java signatures left open by ADR-0004 and the feature page, needed by batch 1 of [`../features/audio-short-and-long-sounds.md`](../features/audio-short-and-long-sounds.md))
- Supersedes / superseded by: none; completes [ADR-0004](0004-audio-contracts-short-and-long-sounds.md), [ADR-0018](0018-runtime-thread-events-and-cache-thread-safety.md), [ADR-0027](0027-audio-policies-formats-latency-and-first-scope.md) and [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md) (all unchanged)

## Context

ADR-0004 splits audio into short and long sounds, and keeps the variant selection and the playlist advance in `audio-api`: backends only expose play, stop and end-of-track, then pause, resume and gain (ADR-0027). ADR-0018 has backends post their completions to the inbox, so the runtime thread acts on them. ADR-0027 and ADR-0033 give the policies:
- 16 voices with a priority-based overflow, managed by the engine;
- WAV parsed by project code; OGG later through `audio-codec-ogg`;
- streaming in chunks of 4,096 frames.

The interface catalog names `Audio`, `ShortSound`, `LongSound`, `VariantSet`, `Playlist`, `AudioDecodingException`, an *audio backend contract* and an *audio decoder contract*. ADR-0045 asks for a fake backend and contract suites in the `audio-api` test JAR.

Batch 1 of the feature page is "`audio-api` contracts, fake backend and contract suites". No document gives:
- the sample format;
- the signatures of the backend and decoder contracts;
- how a voice is named;
- how its end reaches the runtime thread;
- what the fake does.

What others do:
- **libGDX** has `Sound` (fully loaded, `play()` returns a `long` id, at most a fixed number of instances) and `Music` (streamed); PCM is 16-bit signed.
- **OpenAL** names sources and buffers by integers; 16-bit PCM is the common format.
- **Android** `SoundPool` returns an `int` stream id from `play`, and `AudioTrack` takes `short[]` 16-bit PCM.

## Decision

1. **PCM data.**
   - `PcmFormat(sampleRate, channels)` is a record: the sample rate in Hz is positive, and there are 1 or 2 channels.
   - Samples are 16-bit signed, interleaved by frame. Decoders convert other depths (batch 2 for WAV).
   - `PcmBuffer` holds a format, a frame count and the samples of a fully decoded sound. It is created from a `short[]` that the buffer takes over, never copied, so the caller must not keep or modify it.
   - It is read with `readFrames(fromFrame, target, offset, frames)`, which copies frames without allocating; the array is not exposed. Its duration in seconds is `frames / sampleRate`.
   - Immutable once created, so it is safe to read from the backend's thread.
2. **Decoder contract.** `AudioDecoder.decode(InputStream)` returns a new `PcmBuffer`, as `ImageDecoder` does for images.
   - The caller opens and closes the stream; the decoder reads it and never closes it.
   - Invalid, truncated or unsupported data, or a sound above the frame limit given when the decoder is created, is an `AudioDecodingException` (a `RuntimeException`), raised before allocating the samples.
   - Implementations are thread-safe and hold no engine state, so loading workers can decode in parallel (ADR-0019).
   - Streaming decoding comes with batch 4.
3. **Backend SPI.** `AudioBackend` plays fully decoded buffers. Every method is called on the runtime thread.
   - `int play(PcmBuffer)` starts a sound at once and returns its voice: a positive `int`, never reused while the backend lives. With no free voice it returns `0` and plays nothing. The engine keeps its own voice limit and overflow policy (batch 2), within `getMaxVoices()`.
   - `stop(voice)` stops a voice; it has no effect on an ended or unknown voice.
   - `isPlaying(voice)` tells whether the voice still plays.
   - `close()` stops every voice and releases the output. It is idempotent; after it, `play` is an `IllegalStateException`.
   - When a voice ends by itself, the backend posts one inbox event. At delivery, on the runtime thread, the `VoiceListener` given with `setVoiceListener` is called with the voice. A stopped voice posts nothing. The listener is single: the engine's audio system is the only user of the backend.
   - Pause, resume, gain and looping come with batch 5, streaming with batch 4, and output pause in the background with the platform wiring.
4. **Fake and suites** (`audio-api` test JAR, ADR-0045):
   - `FakeAudioBackend` plays nothing. It counts the frames of each voice, has a configurable voice limit, and records what was played. `advance(frames)` moves every voice forward and posts the end of each voice that runs out.
   - `AudioBackendContract` checks the voices, the end event through the inbox, stop, the voice limit and close. A real backend binds it with an `advance` that waits for real time.
   - `AudioDecoderContract` checks null input, the stream left open, a valid sound, and invalid, truncated and over-limit data. It is bound on a raw test decoder until the WAV decoder of batch 2.
5. **No platform type** in `audio-api` (`REQ-AUDIO-009`): no `javax.sound`, no Android type; `check-modules.py` enforces it.

## Options considered

- Sample format:
  - 16-bit signed in `short[]` (chosen): what every target outputs natively (`AudioTrack`, Java Sound, OpenAL), half the memory of `float`, and lossless for WAV and OGG output.
  - `float` samples (rejected: twice the memory for cached short sounds on Android, and every backend converts back).
- Naming a voice:
  - An `int` (chosen): no allocation per play, as `SoundPool` and OpenAL do.
  - A voice object (rejected: an allocation per play on the hot path of effects).
- End of a voice:
  - An inbox event delivered to one listener (chosen): the rule of ADR-0018, and the engine's audio system is the only client.
  - Polling `isPlaying` (kept as well; rejected as the only way: a frame of delay and a scan of every voice).
- Batch 1 scope:
  - The backend and decoder contracts only (chosen): the engine-side `Audio`, sounds, variants and playlists come with their logic in batches 2 to 5, each adding what it needs to the SPI.
  - The whole API at once (rejected: signatures without behavior would be guesses).

## Consequences

- Positive consequences:
  - The engine's audio logic of the next batches is written and tested headless against the fake, with a controllable clock.
  - Every backend passes the same suite.
  - No allocation per play.
- Costs / constraints:
  - 24-bit and float sources lose precision to 16 bits.
  - The SPI grows in batches 4 and 5.
  - The fake does not mix, so gain and timing precision are checked by real backends' platform tests.
- Impact on modules and APIs: `audio-api`: `PcmFormat`, `PcmBuffer`, `AudioDecoder`, `AudioDecodingException`, `AudioBackend`, `VoiceListener`; its test JAR: `FakeAudioBackend`, `AudioBackendContract`, `AudioDecoderContract`.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: unit tests of the PCM types; the suites bound on the fake backend and a test decoder; a headless example playing a generated sound on the fake.
  - Documentation: the feature page, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md` and the traceability.
  - Android/DEX: plain Java in `audio-api`, run by a new device test of the module.

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the batches; reviewed in the pull request of audio batch 1.
