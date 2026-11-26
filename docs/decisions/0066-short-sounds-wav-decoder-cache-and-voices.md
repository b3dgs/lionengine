# ADR-0066 — Short sounds of batch 2: the WAV decoder, the short-sound cache with its length limit, and the voices of the audio system

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-AUDIO-002`, `REQ-AUDIO-003`, `REQ-AUDIO-004`, `REQ-AUDIO-010`, `REQ-AUDIO-013`, `REQ-RES-004`, `REQ-RES-005`, `REQ-RT-003`, `REQ-RT-004`
- Open questions resolved: none (Java signatures and details left open by the feature page, needed by batch 2 of [`../features/audio-short-and-long-sounds.md`](../features/audio-short-and-long-sounds.md))
- Supersedes / superseded by: none; completes [ADR-0027](0027-audio-policies-formats-latency-and-first-scope.md), [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md), [ADR-0055](0055-cache-thread-check-opt-in-limit-and-development-reload.md) and [ADR-0065](0065-audio-pcm-data-decoder-and-backend-contracts.md) (all unchanged)

## Context

Batch 2 of the feature page is "short sounds: WAV parser, cache, voices, length limit". The policies are fixed:
- WAV is parsed by project code on every backend (ADR-0027);
- short sounds are preloaded explicitly, lazily on first play as an option, reference-counted, never evicted, runtime thread only, with an opt-in limit in bytes (ADR-0027, ADR-0033, ADR-0055);
- a short sound lasts at most 10 s; a longer one is refused with a defined error (ADR-0033);
- 16 voices by default; on overflow the oldest voice of the lowest priority is stolen, drop-new and refuse being selectable; priorities from `0` to `100`, default `50` (ADR-0027, ADR-0033);
- the engine keeps its own voice limit and policy over the backend SPI (ADR-0065).

The documents leave open:
- which WAV files are accepted, and the decoder's default frame limit;
- the types and signatures of the cache, the sound handle, the audio system and the policy;
- what "the defined error" of a long file is;
- whether a sound of lower priority may steal a voice of higher priority;
- the difference between drop-new and refuse.

The image side gives the model to follow: `PngDecoder`, `ImageCache` and `Image` (ADR-0054, ADR-0055).

## Decision

1. **WAV decoder.** `WavDecoder` implements `AudioDecoder`.
   - It accepts a RIFF `WAVE` file with a `fmt ` chunk before its `data` chunk, integer PCM (format tag `1`, or `WAVE_FORMAT_EXTENSIBLE` with the PCM sub-format), mono or stereo, 8, 16, 24 or 32 bits, any positive rate. Other chunks are skipped with their pad byte; nothing after `data` is read.
   - It refuses with `AudioDecodingException`: other encodings (floating point, ADPCM...), more than two channels, an inconsistent frame size, a `data` size that is not a whole number of frames, missing or truncated chunks, and a sound above its frame limit, checked from the header before allocating.
   - 8-bit samples are centered and shifted up; 24-bit and 32-bit samples keep their 16 most significant bits.
   - The default frame limit is `DEFAULT_MAX_FRAMES` = 1,920,000: 10 s at 192 kHz, the longest short sound at the highest common rate (7.68 MB in stereo). `WavDecoder(maxFrames)` sets another one. The long sounds of batch 4 stream, so this limit does not bound them.
2. **Short-sound cache.** `ShortSoundCache(resources)` and `ShortSoundCache(resources, maxBytes)` follow `ImageCache`.
   - Keys are resource keys; the decoder is chosen by extension, `WavDecoder` for `wav`, others with `setDecoder`.
   - `load(key)` preloads and returns a new `ShortSound` handle on shared, immutable samples. `loadLazily(key)` returns a handle decoded at its first play; its errors come at that play, and a later play retries. `loadAsync(key, workers, inbox)` decodes on a loading worker and caches at inbox delivery.
   - `has`, `size`, `reload` (development), `getUsedBytes` (2 bytes per sample), `getMaxBytes`; the thread of the first operation owns the cache (ADR-0055).
   - A sound above the limit in bytes is refused with `IllegalStateException`, as in the image cache.
3. **Length limit.** `ShortSoundCache.MAX_SECONDS` = 10.0. A sound that lasts longer is refused with an `AudioDecodingException` whose message starts with the key and tells to play it as a long sound. Exactly 10 s is accepted. The check runs on the decoded duration, so it applies to every decoder; the decoder's frame limit bounds the memory before it.
4. **Sound handle.** `ShortSound` gives its key, `isLoaded`, `isReleased`, `getFormat` and `getDuration`, and an idempotent `release()`, checked on the owner thread. A voice already playing a released sound plays to its end: the backend reads immutable samples.
5. **Audio system.** `Audio(backend)` (16 voices, or fewer if the backend has fewer, policy `STEAL`) and `Audio(backend, maxVoices, policy)`.
   - It becomes the backend's single `VoiceListener` (ADR-0065).
   - `play(sound)` at priority `DEFAULT_PRIORITY` (`50`) and `play(sound, priority)` (`MIN_PRIORITY` `0` to `MAX_PRIORITY` `100`) start the sound at once and return the backend voice, or `NO_VOICE` when it is not played.
   - `stop(voice)` stops a voice; no effect on an ended, stopped or unknown voice. `stopAll()`, `isPlaying`, `getPlayingCount`, `getMaxVoices` and `getPolicy` complete it.
   - A voice is free when it is stopped, and when the end the backend posted to the inbox is delivered.
   - Play, stop and end allocate nothing (fixed arrays in start order), except the first play of a lazy sound.
6. **Overflow policy.** `VoicePolicy`:
   - `STEAL` (default): the oldest voice of the lowest priority is stopped for the new sound, **only if that priority is not above the new sound's**; otherwise the new sound is not played (`NO_VOICE`). A priority would mean nothing if any sound could cut a more important one.
   - `DROP_NEW`: the new sound is not played, `NO_VOICE` is returned, silently.
   - `REFUSE`: the new sound is refused with `IllegalStateException`, so that a game that must never lose a sound learns of it.
7. **Test fixtures** (`audio-api` test JAR, ADR-0045): `WavWriter` writes WAV files of every accepted depth, the extensible tag, extra chunks and malformed variants. The cache convention suite of the `resources` test JAR is bound on `ShortSoundCache`; `AudioDecoderContract` is bound on `WavDecoder`.

## Options considered

- Where the 10 s check runs:
  - On the decoded duration in the cache (chosen): one rule for every decoder, OGG included.
  - In each decoder, from its header (rejected: every decoder repeats it, and the long sounds use the same decoders).
- Error of a sound too long:
  - `AudioDecodingException` (chosen): the content is unsuitable for its family, as a sound above a decoder's limit already is.
  - A new exception type (rejected: one more public type for one case).
- Stealing across priorities:
  - Only a voice whose priority is not above the new sound's (chosen).
  - Any voice, the lowest priority first (rejected: a footstep would cut an alarm).
- Drop-new and refuse:
  - Silent `NO_VOICE` and an exception (chosen): two different needs, a best-effort effect and a sound that must be heard.
  - Both returning `NO_VOICE` (rejected: no difference between them).
- Types:
  - A cache, a handle and an audio system, as for images (chosen).
  - Everything in `Audio` (rejected: too many responsibilities, and the cache convention suite binds a cache).

## Consequences

- Positive consequences:
  - Games load WAV sounds and play them on every backend; the voice logic is tested headless on the fake.
  - The cache follows the convention of the image cache, checked by the same suite.
  - No allocation per play.
- Costs / constraints:
  - Floating point WAV files are refused: they are exported as integer PCM.
  - The first play of a lazy sound decodes on the runtime thread, which costs a frame.
  - Gain, looping, pause and resume come with batch 5; variant sets with batch 3.
- Impact on modules and APIs: `audio-api`: `WavDecoder`, `ShortSoundCache`, `ShortSound`, `Audio`, `VoicePolicy`; its test JAR: `WavWriter`; test-scope dependency on the `resources` test JAR.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: `WavDecoderTest`, `WavDecoderContractTest`, `ShortSoundCacheTest`, `ShortSoundCacheConventionTest`, `AudioTest` (with an allocation test); `ShortSoundExampleTest`; device test `AudioTest` extended.
  - Documentation: the feature page, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md` and the traceability.
  - Android/DEX: plain Java (`java.io`, arrays, `HashMap`), run by the device test of the module.

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the batches; reviewed in the pull request of audio batch 2.
