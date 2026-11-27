# ADR-0070 — `audio-javasound`, the first desktop audio backend: software mixing into one line, output format, latency, voices and format conversion

- Status: ACCEPTED
- Date: 2026-11-27
- Decision makers: project owner for `OQ-175` (answered on 2026-11-27: the backend has more voices than the short-voice limit); agent for the rest, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-AUDIO-002`, `REQ-AUDIO-001`, `REQ-AUDIO-007`, `REQ-AUDIO-009`, `REQ-AUDIO-011`, `REQ-CORE-005`
- Open questions resolved: `OQ-175` (backend voices kept for the long sounds)
- Supersedes / superseded by: none; completes [ADR-0022](0022-optional-multiplayer-audio-backends-reference-frontend-and-surface-hand-off.md), [ADR-0027](0027-audio-policies-formats-latency-and-first-scope.md), [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md), [ADR-0044](0044-java-21-shared-level-and-api-21-constraints.md), [ADR-0065](0065-audio-pcm-data-decoder-and-backend-contracts.md), [ADR-0068](0068-long-sounds-streaming-backend-pull-and-playlists.md) and [ADR-0069](0069-audio-controls-gain-looping-pause.md) (all unchanged)

## Context

Batch 6 of the feature page is "`audio-javasound` (`REQ-AUDIO-002`)": the first backend that makes sound. The policies are fixed:
- one backend per output library or platform; `audio-javasound` on desktop, desktop only, Java 25 class files allowed (ADR-0022, ADR-0033, ADR-0044);
- no `javax.sound` type in `audio-api` (`REQ-CORE-005`, feature page);
- latency targets: 50 ms on desktop, measured by platform tests outside CI (ADR-0027);
- the backend SPI of ADR-0065, ADR-0068 and ADR-0069: buffers and streams on `int` voices, the effective gain read once per audio buffer, a buffer looping in the backend without gap, a stream pulled from the backend's thread and never read once `stop`, `pause`, `pauseOutput` or `close` returned, ends posted to the inbox only (ADR-0018);
- PCM WAV on every backend (`REQ-AUDIO-002`): the WAV decoder keeps the rate and channel count of the file, so a backend receives any rate, mono or stereo.

The documents leave open:
- the output format, the buffer size and the voice count, and `OQ-175`: does `Audio` keep backend voices for the long sounds, or has the backend more voices than the 16 short voices;
- how sounds whose format differs from the line are played;
- how the device tests are kept out of CI.

What others do:
- **libGDX** (LWJGL3 backend) mixes nothing itself: OpenAL sources, 16 to 32 of them by default, a fixed output rate, the library resampling.
- **SDL_mixer** mixes in software into one device stream, converts every chunk to the device format, 8 channels by default plus music.
- **Java Sound** offers a `SourceDataLine` per stream; a `Clip` per sound does not scale, and opening several lines is not portable (some mixers offer one).

## Decision

1. **Module.** `lionengine-audio-javasound`, root package `com.b3dgs.lionengine.audio.javasound`, depends on `audio-api` only (its `core` types come through it); its test scope uses the `audio-api` and `core` test JARs. It is the only module that uses `javax.sound.sampled`, desktop only, compiled with `--release 25`, never in Android builds; nothing of it appears in `audio-api`.
2. **Public API.** One type, `JavaSoundBackend implements AudioBackend`:
   - `JavaSoundBackend(Inbox inbox)` and `JavaSoundBackend(Inbox inbox, int maxVoices)` check their arguments, open and start the default `SourceDataLine`, then start the mixing thread; no line for the format is an `IllegalStateException` with the cause, so that a game may go on without sound.
   - Constants: `SAMPLE_RATE` (`44_100`), `FORMAT` (44,100 Hz stereo), `BUFFER_FRAMES` (`512`), `LINE_BUFFERS` (`3`), `DEFAULT_MAX_VOICES` (`32`).
   - The SPI methods, with the semantics of ADR-0065, ADR-0068 and ADR-0069. No other public type: the mixer, the voices, the output and the end events are package-private.
3. **Software mixing into one line.** A package-private `SoftwareMixer` implements the SPI without thread or line: its `mix(byte[])` mixes the next buffer of `BUFFER_FRAMES` frames. Each voice not paused, while the output is not paused, is added to a `double` accumulator times its gain, read once per buffer; the sum is clipped to 16 bits and written signed little-endian. One daemon platform thread of maximum priority, the mixing thread, loops: mix a buffer under the lock of the mixer, then write it to the line outside the lock; the line blocks it at the pace of the device.
   - The controls take the same lock, so a control waits at most for the mixing of one buffer, the stream pulls included, and once `stop`, `pause`, `pauseOutput` or `close` returns, the streams concerned are no longer read.
   - A buffer that loops wraps inside the mixing, without gap; a stream is pulled from the mixing thread through the window of its voice; an empty buffer ends at its first buffer even when looping.
   - While the output is paused, the thread mixes and writes silence, so the line keeps running and the latency after the resume stays the same.
   - `close()` stops every voice, closes the line (a blocked write returns), and joins the thread (at most 1 s); it is idempotent.
4. **Ends and allocation.** A voice that ends inside a buffer is freed at once and its end posted to the inbox from the mixing thread, one event per end, taken from a pool of events owned by the mixer: the delivery gives the event back, on the runtime thread, before calling the listener. The pool starts with one event per voice and doubles, on the runtime thread at a play, when fewer events are free than voices play (ends not delivered while sounds keep being played). An end rejected by a full inbox is lost and counted by the inbox, as for every producer. Mixing a buffer allocates nothing.
5. **Output format, buffer and latency.** 44,100 Hz, stereo, 16-bit signed little-endian, the most widely supported format of the desktop mixers. A sound played now is mixed into the next buffer; its first frame reaches the device after at most the 3 buffers queued in the line and the one being mixed: 4 × 512 frames = 46.4 ms, within the 50 ms target, plus what the driver and the device add. A buffer of 512 frames is also the step of a gain change and, later, of a fade (11.6 ms). The latency from the request to the first sample is measured by a device test, outside CI (decision 8).
6. **Voices (`OQ-175`).** `Audio` keeps no backend voice for the long sounds: the backend has more voices than the short-voice limit. `DEFAULT_MAX_VOICES` is 32, the 16 short voices of `Audio` and 16 voices for the long sounds and the playlists; mixing 32 voices costs a few percent of one core. A game choosing another limit keeps it above the short-voice limit of its `Audio`, as `FakeAudioBackend` (32 voices) already does. `Audio` and its defaults are unchanged.
7. **Format conversion.** The line has one format; every voice is converted as it is mixed, never ahead, so that a sound is decoded once and costs no extra memory:
   - mono is played on both channels at the gain of the voice (no pan, spatial audio is deferred);
   - another sample rate is resampled by linear interpolation between two frames, the position advancing by a fixed-point step of 16 fractional bits per output frame (rates up to about 45 MHz); a sound at the output rate is copied as it is; the last frame of a looping buffer is interpolated toward its first frame, the last frame of another sound is held;
   - each voice reads its sound through a window of 1,024 frames, copied from the buffer or pulled from the stream, so that resampling allocates nothing and a stream is read in pieces of up to 1,024 frames.
   Linear interpolation was chosen over a windowed-sinc filter for its cost per voice; its high-frequency loss on upsampling is accepted for game effects, and a better resampler stays possible inside the module.
8. **Tests.**
   - Headless, in every build: `SoftwareMixerTest` (gain per buffer and its change, sums and clipping, looping wrap, end inside a buffer and its posting, pause and output pause, stream pulls, paused and stopped streams not pulled, resampling up and down and across the window, a resampled loop, the growing event pool, a full inbox, the allocation test of the mixing path); `AudioBackendContract` bound on the mixer alone (`SoftwareMixerContractTest`) and on `JavaSoundBackend` with its mixing thread on a manual output that replaces the line (`JavaSoundBackendThreadContractTest`); `JavaSoundBackendTest` (the thread mixes and posts, the close stops it, no stream read after a stop, a pause, the output pause or the close while the thread runs free, the defaults against the latency target and `OQ-175`, arguments checked before any line opens).
   - With a sound device, outside CI: the JUnit tag `audio-device`, which every build leaves out, the `platform-tests` profile included (no CI runner has a sound device), and which the new Maven profile `audio-device-tests` runs (`mvn -B verify -Paudio-device-tests`, on a desktop with a display and a sound device). They skip themselves when no line opens. `JavaSoundBackendDeviceContractTest` binds the suite on the default line; `JavaSoundLatencyTest` measures the median of 11 plays from the request until the line reports the first frame played, and checks it within 50 ms; `JavaSoundExampleDeviceTest` runs the example.
9. **Example.** `JavaSoundExample` plays a `Jingle` of four mono notes at 22,050 Hz, each started at the delivery of the end of the previous one; its game logic only knows `AudioBackend` and is tested on `FakeAudioBackend`, its `main()` opens the backend on the inbox of a `GameLoop` and closes it at the end.

Reported, not added:
- A control waits for the mixing of one buffer, and a looping stream reopens its resource on the mixing thread (ADR-0069), so a control may wait for that opening. A finer lock per stream stays possible inside the module if a game measures a stall.
- The line keeps running while the output is paused; stopping it on background, to let the device sleep, belongs to the platform wiring of the master pause (ADR-0069).
- A failure of the line while writing ends the mixing thread; the voices then never end. No hook is added: an error policy of the backend belongs to a later batch if a desktop shows the need.
- Mixer groups and fades (batch 7): no hook is needed; the group factor joins the gain product in `Audio`, and a fade can ramp the gain per buffer inside `MixVoice`, the 512-frame buffer being its step. The OGG codec, `audio-android` and spatial audio need nothing from this module.

## Options considered

- Mixing:
  - In software into one `SourceDataLine` (chosen): one line is portable, the voice count is ours, the gain and the loop are exact per buffer, and the streams are pulled on one thread.
  - One `Clip` per sound (rejected: a `Clip` holds its own copy, cannot be fed by a stream, and many clips exhaust some mixers).
  - One line per voice (rejected: several lines are not offered by every mixer, and each has its own latency).
- Voices (`OQ-175`, owner's answer):
  - More backend voices than the short-voice limit (chosen): nothing changes in `Audio`, and the fake already works that way.
  - Voices kept by `Audio` for the long sounds (rejected by the owner: a new default and a change of `Audio` without need).
- Format conversion:
  - In the mixer, per voice, as it is mixed (chosen): one decode, no extra memory, any WAV plays.
  - At the load, to the line format (rejected: the cache would hold a converted copy, the decoder is backend-independent, and the streams would need a converting stage).
  - Refusing other formats (rejected: the SPI defines no such refusal, and `REQ-AUDIO-002` asks WAV to play on every backend).
- Device tests:
  - A separate tag left out of every CI build (chosen): CI never depends on a device.
  - The `platform` tag with a skip without device (rejected: a runner with a dummy device would make CI depend on its timing).
  - A manual output replacing the line (also chosen, for the headless binding of the suite on the real thread).

## Consequences

- Positive consequences:
  - Desktop games make sound with one line of setup, `new JavaSoundBackend(loop.getInbox())`, and every audio feature of batches 1 to 5 plays on it.
  - The suite runs on the real mixing thread in every build, headless.
- Costs / constraints:
  - The latency is bounded by the line buffer; a slow machine underrunning a 34.8 ms line would need a larger `LINE_BUFFERS`, recorded as a constant.
  - Linear resampling loses some treble on upsampled sounds.
- Impact on modules and APIs:
  - New module content: `JavaSoundBackend` (public); `SoftwareMixer`, `MixVoice`, `MixLoop`, `MixOutput`, `LineOutput`, `VoiceEnd` (package-private).
  - Build: the tag `audio-device` is excluded by default and by `platform-tests`; the profile `audio-device-tests` runs it. `lionengine-examples` depends on `audio-javasound`.
  - `audio-api`: unchanged.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: decision 8; `JavaSoundExampleTest` on the fake.
  - Documentation: the feature page, `testing.md`, `contracts.md`, `interfaces.md`, `modules.md`, `api-index.md`, `engine-features.md`, the roadmap, the agent workflow, the traceability, `OQ-175` and the feature batch status.
  - Android/DEX: none; the module is desktop only and is not in the Android verification.

## Validation

`OQ-175` answered by the owner on 2026-11-27 (backend voices above the short-voice limit); the rest decided by the agent on 2026-11-27 under the owner's delegation of the batches; reviewed in the pull request of audio batch 6.
