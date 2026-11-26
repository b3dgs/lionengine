# Feature: audio, short sounds and long sounds

**Status: VALIDATED**

- Owner: project owner
- Roadmap phase: Phase 2, see `roadmap.md` (batches: contracts + fake backend + contract tests; short sounds with cache, voices and variants; long sounds with streaming and playlists; first desktop backend `audio-javasound`; mixer groups and fades; Android backend `audio-android`)
- Affected modules: `audio-api`, `audio-javasound`, `audio-android`, `audio-codec-ogg`, `resources`, `test-support`, `examples`
- Requirements: `REQ-AUDIO-001` to `REQ-AUDIO-013`, `REQ-RES-004`, `REQ-RES-005`
- Applicable ADRs: [`ADR-0004`](../decisions/0004-audio-contracts-short-and-long-sounds.md), [`ADR-0005`](../decisions/0005-common-convention-for-modules-handling-resources.md), [`ADR-0007`](../decisions/0007-resource-convention-keys-release-and-errors.md), [`ADR-0018`](../decisions/0018-runtime-thread-events-and-cache-thread-safety.md), [`ADR-0022`](../decisions/0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md), [`ADR-0027`](../decisions/0027-audio-policies-formats-latency-and-first-scope.md), [`ADR-0033`](../decisions/0033-media-defaults-module-names-filters-graphics-data-audio.md), [`ADR-0065`](../decisions/0065-audio-pcm-data-decoder-and-backend-contracts.md), [`ADR-0066`](../decisions/0066-short-sounds-wav-decoder-cache-and-voices.md), [`ADR-0067`](../decisions/0067-variant-sets-selection-references-and-composed-play.md), [`ADR-0068`](../decisions/0068-long-sounds-streaming-backend-pull-and-playlists.md)
- Open questions involved: `OQ-175` (backend voices kept for the long sounds, raised by batch 4); Android latency measurement waits for the DEX job (ADR-0044)

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

## State (2026-11-26)

Batch 1 (2026-11-26), the `audio-api` contracts, fake backend and contract suites, with the example `BeepExample` and its API-index row. The signatures this page leaves to the batch follow ADR-0065 (owner delegation), within ADR-0004, ADR-0018 and ADR-0027:
- PCM data: `PcmFormat(sampleRate, channels)` (positive Hz, mono or stereo) and `PcmBuffer`, a fully decoded sound of 16-bit signed samples interleaved by frame. The buffer takes over its array without copying, reads frames by copy with `readFrames` (no allocation, the array is never exposed), and gives `getFrames()` and `getDuration()` in seconds. It is immutable, so a backend thread may read it.
- Decoder contract: `AudioDecoder.decode(InputStream)` returns a new `PcmBuffer`. It reads the stream without closing it, refuses invalid, truncated or unsupported data and sounds above its frame limit with an `AudioDecodingException` before allocating, and is thread-safe. Streaming decoding comes with batch 4, the WAV decoder with batch 2.
- Backend SPI: `AudioBackend` plays fully decoded buffers on voices, each a positive `int` never reused.
  - `play(buffer)` returns `NO_VOICE` (`0`) when every voice is busy.
  - `stop(voice)` posts no end, and has no effect on an ended or unknown voice. `isPlaying`, `getMaxVoices` and an idempotent `close` complete it.
  - A voice that ends by itself posts one inbox event, which calls the single `VoiceListener` on the runtime thread.
  - The engine keeps its own voice limit and overflow policy (batch 2). Pause, resume, gain and looping come with batch 5.
- Test JAR (ADR-0045): `FakeAudioBackend` (plays nothing, counts frames, `advance(frames)` as the clock, records what it played, configurable voice limit) and `RawAudioDecoder` (a raw test format with `encode`); the suites `AudioBackendContract` (voices, the end through the inbox, stop, the voice limit, the listener, close) and `AudioDecoderContract` (decode, frame limit, new buffer, stream left open, read failure, empty, text and truncated data, `null`, two threads).
- Tests: `PcmBufferTest`; `FakeAudioBackendTest` and `RawAudioDecoderTest`, which bind the suites; `BeepExampleTest` on the fake; device test `AudioTest` on API 21.

Batch 2 (2026-11-26), short sounds: the WAV decoder, the short-sound cache with its length limit, and the voices of the audio system, with the example `ShortSoundExample` and its API-index row. The signatures follow ADR-0066 (owner delegation), within ADR-0027, ADR-0033, ADR-0055 and ADR-0065:
- WAV: `WavDecoder` decodes integer PCM (format tag `1`, or `WAVE_FORMAT_EXTENSIBLE` with the PCM sub-format), mono or stereo, 8, 16, 24 or 32 bits, to 16 bits (8-bit centered and shifted, 24 and 32-bit keep their 16 high bits). It skips other chunks with their pad byte, refuses other encodings, more channels, inconsistent sizes and truncated files, and checks its frame limit from the header before allocating: `DEFAULT_MAX_FRAMES` = 1,920,000, 10 s at 192 kHz.
- Cache: `ShortSoundCache(resources[, maxBytes])` mirrors the image cache. `load(key)` preloads and shares, `loadLazily(key)` decodes at the first play, `loadAsync(key, workers, inbox)` decodes on a worker and caches at delivery; `has`, `size`, `reload`, `getUsedBytes` (2 bytes per sample), `getMaxBytes`; the WAV decoder is registered for `wav`, `setDecoder` adds others. An opt-in limit in bytes refuses a sound with `IllegalStateException`. The thread of the first operation owns it.
- Length: `MAX_SECONDS` = 10.0; a longer sound is refused with an `AudioDecodingException` starting with the key and telling to play it as a long sound.
- Handle: `ShortSound` gives its key, `isLoaded`, `isReleased`, format and duration, and an idempotent `release()`; voices already playing a released sound play to their end.
- Voices: `Audio(backend)` has 16 voices (fewer if the backend has fewer) and the `STEAL` policy; `Audio(backend, maxVoices, policy)` chooses. It is the backend's voice listener. `play(sound[, priority])` (`0` to `100`, default `50`) returns the voice or `NO_VOICE`; `stop`, `stopAll`, `isPlaying`, `getPlayingCount`. `VoicePolicy.STEAL` stops the oldest voice of the lowest priority, only if that priority is not above the new sound's; `DROP_NEW` returns `NO_VOICE`; `REFUSE` throws `IllegalStateException`. Playing, stopping and ending allocate nothing.
- Test JAR: `WavWriter` writes WAV files of every accepted depth and malformed variants.
- Tests: `WavDecoderTest`, `WavDecoderContractTest` (the decoder suite), `ShortSoundCacheTest` (hit and miss, length and byte limits, lazy and asynchronous loads, decoders), `ShortSoundCacheConventionTest` (the cache convention suite), `AudioTest` (voices, priorities, each policy, an allocation test); `ShortSoundExampleTest`; device test `AudioTest` extended on API 21.

Batch 3 (2026-11-26), variant sets, with the example `VariantSetExample` and its API-index row. The signatures follow ADR-0067 (owner delegation), within ADR-0027, ADR-0033 and ADR-0066:
- Set: `VariantSet.random(seed, variants...)`, `random(seed, variants, weights)` and `composed(layers...)`; `isComposed`, `size`, `getSound`, `getWeight`, `getVoicesPerPlay` (`1`, or the number of layers) and `getLastIndex` (`NO_INDEX` before the first play and for a composed set).
- Randomness: each random set owns a core `RandomSource` created from its seed, separate from the world's generator. Its choices depend only on its seed and its own plays, and are the same on every platform (reference vector checked on the JVM and on API 21).
- Selection: one `nextDouble()` per play, times the weights of the eligible variants (positive weight, not the last one when at least two have a positive weight); a single eligible variant is chosen without drawing. Weights are finite and non-negative, `DEFAULT_WEIGHT` = `1.0`, `0` excludes, at least one positive.
- Sounds: the set refers to the `ShortSound` handles it is given and releases none; their owner releases them, normally the scene on exit. A released sound is refused at play with `IllegalStateException`; a lazy one is decoded at the first play of its set.
- Play: `Audio.play(set[, priority])` plays the chosen variant of a random set and returns its voice. `Audio.play(set, priority, voices)` takes either kind, writes one voice per layer (`NO_VOICE` for a layer not played) to the caller's array and returns the number started.
  - The layers of a composed set start in the same call, after every layer is checked.
  - Under `REFUSE`, the set is refused before any layer starts unless a voice is free for each layer.
  - Under `STEAL` and `DROP_NEW`, each layer takes a voice as a single play would, never the voice of another layer of the same play.
  - Selection and play allocate nothing.
- Tests: `VariantSetTest` (no repeat, weights, exclusion, a single variant, seeds, independence of sets, reference vector, arguments); `AudioVariantSetTest` (random and composed plays on `FakeAudioBackend`, each policy, released and lazy sounds, arguments, an allocation test); `VariantSetExampleTest`; device test `AudioTest.testVariantSets` on API 21. `SilentAudioBackend` is extracted to the test JAR from `AudioTest`.

Batch 4 (2026-11-26), long sounds with streaming, and playlists, with the example `LongSoundExample` and its API-index row. The signatures follow ADR-0068 (owner delegation), within ADR-0004, ADR-0005, ADR-0007, ADR-0018, ADR-0027, ADR-0033, ADR-0055 and ADR-0065:
- Streaming decoder: `AudioStreamDecoder` extends `AudioDecoder` with `open(InputStream)`, which reads the header and returns a `PcmSource` (`getFormat`, `read(target, offset, frames)`, fewer frames only at the end). Streaming has no frame limit; `decode` keeps it. `WavDecoder` implements both, and the OGG decoder of `audio-codec-ogg` will implement the same interface.
- Backend SPI: one method more, `play(PcmStream)`. The backend pulls `read(target, offset, frames)` from its own thread until a read returns fewer frames than asked, then the voice ends and posts its end as a buffer voice. Both kinds share the numbering, the limit, `stop`, `isPlaying`, `close` and the listener. The backend reads a stream only until its voice ends or `stop` or `close` returns.
- Bounded buffer: a ring of 4 chunks of 4,096 frames by default, filled at the preparation on the runtime thread, then refilled a chunk at a time on the backend's thread, with no allocation. A failure of the source ends the stream after the frames decoded, and is reported on the runtime thread at the end: a `ResourceException` (read failure) or an `AudioDecodingException` starting with the key.
- Long sounds: `LongSoundLoader(resources)` (`wav` registered, `setDecoder(extension, AudioStreamDecoder)`): `stream(key)`, `stream(key, chunks, chunkFrames)` and `decode(key)`, fully decoded within the frame limit of its decoder. Handles are not shared. `LongSound` reads nothing until `prepare()`, which opens, checks and fills the buffer and raises the errors; `isStreamed`, `getChunks`, `getChunkFrames`, `isPrepared`, `isPlaying`, `isReleased`, `getFormat`; an idempotent `release()` stops it first. The loader and its sounds belong to the thread of its first operation.
- Play: `Audio.play(LongSound)` prepares it if needed and plays it on a backend voice outside the short-voice limit, or returns `NO_VOICE` (the sound stays prepared). `Audio.stop(voice)` and `isPlaying(voice)` serve both families; `getLongPlayingCount()`. A long sound plays on one voice at a time. A streamed sound closes its resource at its end or stop and is prepared again by its next play; a fully decoded one keeps its samples until its release.
- Playlist: `Playlist(audio, tracks...)` refers to handles released by their owner. `play()` starts the first track and prepares the next ahead; `stop()`. The end of a track reaches it at the inbox delivery and the advance runs on the runtime thread. At the end it stops, or loops with `setLooping(true)`. A track that fails to prepare or start (missing, unreadable, invalid, released, no voice) or whose stream failed is reported to `PlaylistListener.onTrackFailed(playlist, track, exception)` on the runtime thread, then skipped, or aborts with `setAbortOnFailure(true)`. When every track fails in a row it stops, even when looping. `getTrack` (`NO_TRACK`), `getVoice`, `size`, `getSound`.
- Test JAR: `FakeAudioBackend` pulls streamed voices in pieces of at most `PULL_FRAMES` (1,024) frames from its clock (`getStreams`, `getPulledFrames`); `AudioBackendContract` checks streamed voices with its `CountingStream`; `GeneratedWav` streams WAV files of any length without holding them.
- Tests: `StreamBufferTest` (order across chunks, end, truncated and unreadable data, ten minutes of stereo through the default buffer with bounded memory, the per-chunk allocation test); `LongSoundTest` (preparation, use, release and double release, streamed and fully decoded, stop, one voice at a time, no voice, errors, threads, bounded memory through the fake); `PlaylistTest` (end, loop, single-track loop, failing track skipped and reported on the runtime thread, failure while playing, abort, every track failing, released track and no voice, stop); `WavDecoderTest` (streaming); the extended `AudioBackendContract` on `FakeAudioBackend`; `LongSoundExampleTest`; device test `AudioTest.testLongSounds` on API 21.
- Reported, not added: single-sound looping and gain stay with batch 5. A playlist of one track that loops starts again at the delivery of its end, after reopening the file, so up to one frame later; the playlist advance runs per track, not per audio buffer, so it has no allocation test.

## Lifecycle and concurrency

Distinct handles and lifecycles per family. Decoded caches live in the audio modules, on the runtime thread only. Backend threads and streaming decoders never touch simulation state; they post completions to the inbox (ADR-0018). Backgrounding pauses output (ADR-0031).

## Units and Android/DEX compatibility

Seconds for durations and positions, Hz for sample rate, gain in `[0, 1]`. No `javax.sound` in `audio-api`; `audio-javasound` is desktop only; `audio-android` uses platform APIs. Latency targets (50 ms desktop, 100 ms best effort Android) are measured by platform tests outside CI.

## Dependencies

`audio-api` on `core`, `resources`; backends on `audio-api`. Forbidden: any concrete audio library or platform type in `audio-api`. A codec library only in an `audio-codec-*` module with its row in [`../dependencies.md`](../dependencies.md).

## Tests

- Unit tests: seeded variant selection (no repeat, weights, composition), playlist advance (end, loop, failing track), voice stealing per policy, cache hit and miss, short-sound length limit, WAV parsing.
- Contract tests (`audio-api` test JAR, ADR-0045): reusable suites run on every backend, with a fake backend first.
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
