# ADR-0068 — Long sounds of batch 4: the streaming decoder contract, the streamed voices pulled by the backend, long-sound handles and playlists

- Status: ACCEPTED
- Date: 2026-11-26
- Decision makers: agent, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-AUDIO-006`, `REQ-AUDIO-007`, `REQ-AUDIO-008`, `REQ-AUDIO-009`, `REQ-RES-004`, `REQ-RES-005`
- Open questions resolved: none (Java signatures and details left open by the feature page, needed by batch 4 of [`../features/audio-short-and-long-sounds.md`](../features/audio-short-and-long-sounds.md)); raises `OQ-175`
- Supersedes / superseded by: none; completes [ADR-0027](0027-audio-policies-formats-latency-and-first-scope.md), [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md), [ADR-0065](0065-audio-pcm-data-decoder-and-backend-contracts.md) and [ADR-0066](0066-short-sounds-wav-decoder-cache-and-voices.md) (all unchanged)

## Context

Batch 4 of the feature page is "long sounds and streaming, playlists". The policies are fixed:
- long sounds are streamed by default, by decoding chunks into a small bounded buffer, and fully decoded on request for short music loops; streaming may use a worker that only decodes (ADR-0027, decision 4);
- the default buffer is 4 chunks of 4,096 frames, configurable (ADR-0033);
- a playlist prepares its next track ahead, stops at its end by default and loops on request, skips a failing track and reports it through an event, or aborts on request; end-of-track goes through the inbox and the advance runs on the runtime thread (ADR-0027, decision 5);
- the failure event is a listener called on the runtime thread with the playlist, the track index and the exception (ADR-0033);
- backends expose play, stop and end-of-track only; the playlist logic is backend-independent (ADR-0004);
- the canonical type names are `LongSound` and `Playlist` (ADR-0037); long sounds follow the resource convention (ADR-0005, ADR-0007) and the cache thread rule (ADR-0055).

Batch 1 left streaming to this batch: `AudioDecoder` decodes a whole sound, and the backend SPI plays only fully decoded `PcmBuffer`s (ADR-0065).

The documents leave open:
- the streaming decoder contract;
- how a backend gets the frames of a streamed sound, and on which thread they are decoded;
- how a long sound is created, prepared, played and released, and its limit when fully decoded;
- how a playlist hears the end of its track, and what counts as a failing track;
- the Java signatures.

What others do:
- **libGDX** `Music` streams on its audio thread, which decodes a buffer each time one is free; the end is a callback on that thread.
- **OpenAL** streams by queueing buffers on a source; the application refills the processed buffers.
- **Android** `AudioTrack` in streaming mode is fed by `write()` from a thread of the application.

## Decision

1. **Streaming decoder.** `AudioStreamDecoder` extends `AudioDecoder` with `PcmSource open(InputStream)`.
   - `open` reads and checks the header only; a `PcmSource` gives the format and decodes the next frames into an array of the caller with `read(target, offset, frames)`, which returns fewer frames only at the end, then `0`. A truncated or corrupted stream is an `AudioDecodingException` at the read that reaches it, a read failure an `IOException`.
   - Streaming has no frame limit, so a long sound is not limited to the 10 s of the short sounds. `decode` keeps the frame limit of ADR-0065.
   - Ownership as in ADR-0065: the caller opens and closes the stream; the decoder and its sources never close it. The decoder is thread-safe; a source is read by one thread at a time and allocates nothing.
   - `WavDecoder` implements both; its full decoding now opens the header the same way. The OGG Vorbis decoder of `audio-codec-ogg` will implement the same interface.
2. **Streamed voices: the backend pulls.** The SPI gains one method, `int play(PcmStream stream)`.
   - `PcmStream` gives its format and `read(target, offset, frames)`, which copies the next frames and returns fewer than asked only at the end of the sound. It never throws for a failure of its data.
   - The backend calls `read` from its own thread, in pieces of the size it needs, until a read returns fewer frames than asked: the voice then ends by itself and posts its end to the inbox as a buffer voice does.
   - Both kinds of voices share the numbering, the voice limit, `stop`, `isPlaying`, `close` and the `VoiceListener`.
   - The backend reads a stream only from the call to `play` until its voice ends, or until `stop` or `close` returns, so that the engine may then close the resource.
3. **The bounded buffer, decoded on the backend's thread.** The engine's `PcmStream` of a long sound is a ring of `chunks × chunkFrames` frames.
   - Preparing the sound fills the ring on the runtime thread. Then, each time the backend reads a chunk to its end, that chunk is decoded again at once from the source, on the backend's thread, so the ring stays full ahead of the voice.
   - No worker is used: the read path decodes one chunk at a time and allocates nothing, so it can run on the backend's feeding thread, as in libGDX and on `AudioTrack`. A worker stays possible later behind the same `PcmStream`, without an SPI change.
   - A failure of the source ends the stream after the frames already decoded. It is kept and reported on the runtime thread at the delivery of the end, as a `ResourceException` (read failure) or an `AudioDecodingException` starting with the key.
   - The backend thread and the decoder touch only the ring, the source and the resource stream, never simulation state, and post nothing themselves.
4. **Long sounds.** `LongSoundLoader(resources)` creates them, with `WavDecoder` registered for `wav` and `setDecoder(extension, AudioStreamDecoder)` for others.
   - `stream(key)` gives a sound streamed through `DEFAULT_CHUNKS` (`4`) chunks of `DEFAULT_CHUNK_FRAMES` (`4,096`) frames; `stream(key, chunks, chunkFrames)` chooses the buffer; `decode(key)` gives a sound fully decoded at its preparation, within the frame limit of its decoder (`WavDecoder.DEFAULT_MAX_FRAMES` for WAV unless a game registers another `WavDecoder`). No new limit is introduced.
   - Long sounds are not cached or shared: each `LongSound` has its own stream. A new handle reads nothing. `prepare()` opens the resource, reads the header and fills the buffer (or decodes everything), and raises the resource and decoding errors on the runtime thread.
   - `Audio.play(LongSound)` prepares it if needed and plays it on a backend voice outside the short-sound limit and its policy. It returns the voice, or `NO_VOICE` when the backend has no free voice (the sound stays prepared). `Audio.stop(voice)` and `isPlaying(voice)` work for both families. A long sound plays on one voice at a time: playing it again while it plays is an `IllegalStateException`.
   - A streamed sound closes its resource when its voice ends or is stopped, and is prepared again by its next play; a fully decoded sound keeps its samples until its release.
   - `release()` stops a playing sound, closes its stream and frees its samples. It is idempotent; preparing or playing a released sound is an `IllegalStateException`.
   - The thread of the loader's first operation owns the loader and its sounds (ADR-0055). Long sounds belong to the `music` group by default when mixer groups come (batch 7).
5. **Playlists.** `Playlist(audio, tracks...)` refers to the `LongSound` handles it is given, as a variant set does, and releases none.
   - `play()` starts the first track (restarting a playing playlist) and prepares the next one ahead; `stop()` stops the track and closes the track the playlist prepared ahead.
   - The end of a track reaches the playlist through `Audio`, the backend's voice listener, at the inbox delivery. The playlist then starts the next track and prepares the one after it, on the runtime thread, independently of the backend.
   - After the last track it stops, or starts again from the first with `setLooping(true)`.
   - A track that fails to prepare or to start, or whose stream failed, is reported to the `PlaylistListener` (`onTrackFailed(playlist, track, exception)`) on the runtime thread, then skipped. With `setAbortOnFailure(true)` the playlist stops instead. Failing to start covers a missing or unreadable resource, an invalid content, a released track and no free backend voice.
   - When every track fails in a row within one advance, the playlist stops, even when looping, so that it never spins.
   - Stopping the voice of its track with `Audio.stop`, or releasing its track, stops the playlist.
   - A failure of a track prepared ahead is left to its turn, where the preparation is tried again and reported.
   - A track that follows itself, as in a playlist of one track that loops, is prepared at its turn only, since a long sound plays on one voice at a time.
   - Getters: `isPlaying`, `getTrack` (`NO_TRACK` = `-1` when stopped), `getVoice`, `size`, `getSound`, `isLooping`, `isAbortOnFailure`.
6. **Fake and suite** (`audio-api` test JAR, ADR-0045): `FakeAudioBackend.advance(frames)` pulls each streamed voice in pieces of at most `PULL_FRAMES` (`1,024`) frames and ends it at the first short read; `getStreams()` and `getPulledFrames(voice)` record what it pulled. `AudioBackendContract` checks that a stream is read to its end and never after, that a stopped or closed stream is no longer read and posts no end, that both kinds share the voice limit, and that a refused stream is never read.
7. **Allocation.** The per-chunk path (the backend's read, the refill of a chunk and the WAV source) allocates nothing; an allocation test covers it. Preparing a sound and advancing a playlist allocate (the resource stream, the source and the ring), once per track rather than per audio buffer.

Reported, not added: single-sound looping and gain stay with batch 5 (`REQ-AUDIO-011`). A playlist of one track that loops plays again at the delivery of its end, after reopening the file, so there is a gap of up to one frame until then. A loop on the voice will remove it.

## Options considered

- Feeding a streamed voice:
  - The backend pulls a `PcmStream` from its own thread (chosen): one SPI method, and the backend calls it when its output needs data, as `AudioTrack` and Java Sound lines are fed. The engine keeps the buffer and the decoder, so the fake drives it with its clock.
  - The engine pushes chunks to the backend from the runtime thread (rejected: a slow frame would starve the output, and the runtime thread would poll every voice).
  - The backend decodes with the codec itself (rejected: each backend would duplicate the decoding and the buffer, and the memory bound would not be the engine's).
- Thread of the decoding:
  - The backend's thread, a chunk at a time (chosen): no extra thread, no allocation per chunk, deterministic on the fake.
  - A decoding worker of the loading pool (rejected for now: a task per chunk allocates, a long-lived task holds a worker for the length of a track; it stays possible behind the same `PcmStream`).
- Streaming decoder:
  - `AudioStreamDecoder` extends `AudioDecoder` (chosen): one registration per format serves the streamed sounds, the fully decoded long sounds and the short-sound cache, and the frame limit of full decoding stays.
  - A separate interface that cannot decode fully (rejected: fully decoded long sounds would need a second registration).
- Failure of a stream while it plays:
  - Kept by the stream, reported at the delivery of the end (chosen): the backend contract stays play, stop and end only.
  - A failure callback in the SPI (rejected: a second event type for every backend).
- Creating long sounds:
  - A loader that creates handles, not a cache (chosen): a streamed sound has its own stream position, so it cannot be shared.
  - A reference-counted cache like the short sounds (rejected: sharing a stream is impossible, and sharing fully decoded music is not a stated need).
- No free voice for a track:
  - Reported as a failing track and skipped (chosen): the playlist never waits silently.
  - Retried later (rejected: no retry rule is stated, and it would need a timer).

## Consequences

- Positive consequences:
  - Music of any length plays with a fixed memory of 16,384 frames per sound by default, 64 KB in stereo.
  - Every backend streams the same way through one SPI method, and every backend passes the same suite.
  - Playlists advance and report failures on the runtime thread, the same on every backend.
- Costs / constraints:
  - Real backends must stop reading a stream before `stop` and `close` return.
  - A long sound takes a backend voice outside the 16 short voices, so a backend with as many voices as the short-sound limit can refuse it while every short voice plays (`OQ-175`).
  - The end of a track is heard at the next inbox delivery, so the next track starts up to one frame later; preparing ahead avoids reading its header then.
  - Preparing a fully decoded long sound decodes it on the runtime thread at once.
  - `AudioBackend.play(null)` now needs a cast, since `play(PcmBuffer)` and `play(PcmStream)` both match.
- Impact on modules and APIs:
  - `audio-api`: `AudioStreamDecoder`, `PcmSource`, `PcmStream`, `AudioBackend.play(PcmStream)`, `LongSoundLoader`, `LongSound`, `Playlist`, `PlaylistListener`; `Audio.play(LongSound)`, `getLongPlayingCount()`, and `stop` and `isPlaying` for long voices; `WavDecoder` implements `AudioStreamDecoder`.
  - Its test JAR: `FakeAudioBackend` (streamed voices), `AudioBackendContract` (streamed voices, `CountingStream`), `GeneratedWav`.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: `StreamBufferTest` (order, end, failures, bounded memory of ten minutes of stereo, the per-chunk allocation test); `LongSoundTest` (preparation, use, release and double release, streamed and fully decoded, stop, errors, threads, bounded memory through the fake); `PlaylistTest` (end, loop, failing tracks skipped and reported on the runtime thread, abort, every track failing, stop); `WavDecoderTest` (streaming); `AudioBackendContract` on the fake; `LongSoundExampleTest`; device test `AudioTest.testLongSounds`.
  - Documentation: the feature page, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md`, the roadmap, the agent workflow, the Android compatibility page, the traceability and `OQ-175`.
  - Android/DEX: plain Java (arrays, `volatile`, `InputStream`), run by the device test of the module.

## Validation

Decided by the agent on 2026-11-26 under the owner's delegation of the batches; reviewed in the pull request of audio batch 4.
