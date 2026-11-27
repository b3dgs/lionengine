# ADR-0069 — Audio controls of batch 5: gain, looping, pause and resume, in the engine and in the backend SPI

- Status: ACCEPTED
- Date: 2026-11-27
- Decision makers: agent, under the owner's standing delegation of the batches ("Continue with the first next unfinished batch", automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-AUDIO-011`, `REQ-AUDIO-006`, `REQ-AUDIO-007`, `REQ-AUDIO-008`, `REQ-AUDIO-009`
- Open questions resolved: none (Java signatures and details left open by the feature page, needed by batch 5 of [`../features/audio-short-and-long-sounds.md`](../features/audio-short-and-long-sounds.md)); `OQ-175` stays open, the controls do not need it
- Supersedes / superseded by: none; completes [ADR-0027](0027-audio-policies-formats-latency-and-first-scope.md), [ADR-0031](0031-runtime-defaults-loop-calibration-inbox-scenes-platform-lifecycle.md), [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md), [ADR-0065](0065-audio-pcm-data-decoder-and-backend-contracts.md), [ADR-0066](0066-short-sounds-wav-decoder-cache-and-voices.md) and [ADR-0068](0068-long-sounds-streaming-backend-pull-and-playlists.md) (all unchanged)

## Context

Batch 5 of the feature page is "first-batch controls: volume, looping, pause, resume". The policies are fixed:
- the first audio batch provides play, stop, volume (master and per sound), looping, pause and resume; mixer groups and fades follow (ADR-0027, decision 8, `REQ-AUDIO-011`);
- a gain is a `double` in `[0, 1]`; the effective gain is master × group × sound; fades ramp the gain linearly per audio buffer in the backend (ADR-0033);
- backends expose play, stop, pause, resume, gain and end-of-track only; the playlist logic is backend-independent (ADR-0004);
- background maps to pause, and audio backends pause and resume their output with it (ADR-0031);
- the controls are called on the runtime thread; the backend threads only read what they are given and post completions to the inbox (ADR-0018, ADR-0055).

ADR-0065 and ADR-0068 left pause, resume, gain and looping of the SPI to this batch. ADR-0068 reported that a playlist of one track that loops starts again at the delivery of its end, after reopening the file, with a gap of up to one frame.

The documents leave open:
- whether the per-sound gain belongs to the handle, to the voice or both, and how a change reaches a playing voice;
- how a streamed sound loops, and how looping interacts with playlists;
- the interaction of a pause with stop, the voice-stealing policy, playlists and release, and what the master pause is;
- the SPI signatures, and whether the backend applies the gain per audio buffer.

What others do:
- **libGDX** `Sound.play(volume)` and `loop(volume)` give the gain and the looping at the play; `Music.setLooping`, `setVolume`, `pause`; `Audio` has no master pause, the backend pauses its output with the application.
- **Android** `SoundPool.play(id, left, right, priority, loop, rate)` takes gain and loop at the play; `autoPause()` and `autoResume()` hold every stream.
- **OpenAL** sources have `AL_GAIN`, `AL_LOOPING`, `alSourcePause`; the listener gain is the master.

## Decision

1. **Gain.** A gain is a `double` from `Audio.MIN_GAIN` (`0.0`) to `Audio.MAX_GAIN` (`1.0`), the default of every gain. NaN, infinite and out of range values are refused with an `IllegalArgumentException`, and nothing changes then.
   - Master: `Audio.setMasterGain(gain)` and `getMasterGain()`.
   - Per sound: on the handle, `ShortSound.setGain(gain)` and `LongSound.setGain(gain)` with `getGain()`. The gain belongs to the handle, not to the voice: ADR-0033 defines the effective gain as master × group × sound, with no voice factor. A `ShortSound` is one load, so two loads of one key have their own gains.
   - The effective gain of a voice is master × sound, computed in one private method of `Audio`, where the group factor of batch 7 joins the product. No group is added now.
   - A change reaches every voice it concerns at once: `Audio` keeps the sound of each short voice, and a handle keeps the `Audio` that last played it, so a change of the sound gain, or of the master, calls `AudioBackend.setGain` for each voice concerned, without allocating.
2. **Backend SPI.** The SPI gives the effective gain of a voice only; master, sound and groups stay in the engine.
   - `play(PcmBuffer buffer, double gain, boolean looping)` and `play(PcmStream stream, double gain)` replace `play(PcmBuffer)` and `play(PcmStream)`. The gain and the looping are given at the play, so that the first audio buffer already has them: a gain set after the play would let a first buffer sound at full gain, and a loop set after it could miss the end of a sound shorter than one buffer.
   - `setGain(voice, gain)` changes the gain of a playing voice.
   - `pause(voice)` and `resume(voice)` hold and continue one voice.
   - `pauseOutput()` and `resumeOutput()` hold and continue every voice, as ADR-0031 asks of the backends.
   - The backend reads the gain of a voice once per audio buffer it fills: a change reaches a playing voice at its next buffer, as a step. The fades of batch 7 will ramp it per buffer the same way.
   - Every control has no effect on an ended, stopped or unknown voice, and the gain is checked whatever the voice. Once `pause` or `pauseOutput` returns, a stream is not read until it is resumed, as after `stop`.
3. **Looping.**
   - On the handle: `ShortSound.setLooping(boolean)` and `LongSound.setLooping(boolean)` with `isLooping()`, off by default, read at the play; a playing voice keeps the looping it started with.
   - A looping voice never ends by itself and posts no end; it plays until it is stopped, stolen, or its long sound is released. A buffer of no frame ends at once even when looping, so that nothing spins.
   - A buffer voice loops in the backend, which starts again from the first frame after the last one without gap.
   - A stream loops in the engine. When the source of a looping `StreamBuffer` ends, the buffer opens the resource again (`Resources.open` and the decoder are thread-safe), checks that the format did not change, and goes on decoding in the same chunk: the loop has no gap, and the stream never returns fewer frames than asked, so the backend needs no loop of its own for streams.
     - Reopening was chosen over rewinding the `PcmSource`, which would change the decoder contract and need a seekable stream.
     - Each pass opens a new resource stream and a new source, on the backend's thread: one allocation per pass (the WAV source has an 8 KB read buffer), never per chunk.
     - The previous stream is closed at the wrap; a failure to close it is dropped, as after a stop, since nothing reads it any more.
     - A failure to open a pass, or a pass of another format, ends the stream as a failure of the source (ADR-0068). A pass of no frame ends the stream.
   - Releasing a `ShortSound` stops its looping voices, which would never end; its other voices still play to their end (ADR-0066).
4. **Pause.**
   - Per voice: `Audio.pause(voice)`, `resume(voice)` and `isPaused(voice)`, for short and long voices. Pausing or resuming twice, an ended or an unknown voice has no effect.
   - A paused voice keeps its voice: it counts in the limit, is still playing for `isPlaying`, and may be stolen as any voice (no new stealing rule is stated). `stop` and `stopAll` stop it without posting an end.
   - A paused stream is not pulled.
   - Master: `Audio.setMasterPaused(boolean)` and `isMasterPaused()` pause the output of the backend, for backgrounding (ADR-0031). The master pause is independent of the pause of each voice: a voice paused on its own stays paused when the output resumes, and a sound played during the master pause starts when it ends. The platform wiring that calls it on background and foreground stays with the platform batches; a game calls it from its lifecycle listener meanwhile.
5. **Playlists.**
   - `Playlist.pause()`, `resume()` and `isPaused()` act on the voice of the track playing. Pausing that voice with `Audio.pause` pauses the playlist the same way, since a paused track never ends. A stopped playlist is not paused, and `play()` starts unpaused.
   - A track whose sound loops never ends, so the playlist stays on it until it is stopped, as an intro followed by a looping theme; the track after it is not prepared ahead. A looping track is therefore allowed, not refused.
   - A playlist of one track that loops plays its track on a looping voice from the play, so the gap reported in ADR-0068 is gone. A change of `setLooping` reaches that voice at the next `play()` only.
6. **Fake and suite** (`audio-api` test JAR, ADR-0045).
   - `FakeAudioBackend` records, for each playing voice, its gain (`getGain`), its looping (`isLooping`), its pause (`isPaused`) and the frames it played across its loops (`getPlayedFrames`), plus `isOutputPaused()`.
   - Its clock wraps looping buffer voices, does not move paused voices, and moves nothing while the output is paused. It does not mix, so it applies no gain to samples.
   - `AudioBackendContract` checks the gain refusals, looping without end, the pause of a buffer and of a stream, a paused voice in the voice limit, and the output pause, on every backend.
7. **Allocation.** The control paths (gains, pause, resume, master pause, a looping play) and the reads of a looping stream between two passes allocate nothing; an allocation test covers them.

Reported, not added:
- A gain change is a step at the next audio buffer, so a large change may click; the linear ramps of batch 7 are the remedy.
- The fake checks gains, not their effect on samples; real backends check what they sound like in their platform tests (ADR-0065).
- No group or fade hook was needed: the group factor will join the product in `Audio`, and fades will ramp the backend gain.

## Options considered

- Per-sound gain:
  - On the handle (chosen): matches master × group × sound of ADR-0033, and a looping ambient sound changes its gain while it plays.
  - On the voice (rejected: a fourth factor outside ADR-0033, and every play would need a gain argument).
  - Both (rejected: two places to change one volume, and the product would no longer be the documented one).
- Gain and looping in the SPI:
  - Given at the play, changed later for the gain (chosen): no first buffer at the wrong gain, no missed loop of a very short sound.
  - Separate setters after the play (rejected: the race above, on a real backend thread).
- Looping of a stream:
  - In the engine, by reopening the resource (chosen): no SPI method for streams, no gap, and the memory stays the ring.
  - Rewinding the `PcmSource` (rejected: changes the decoder contract and needs a seekable resource stream).
  - In the backend (rejected: the backend cannot rewind a `PcmStream`).
- Master pause:
  - A pause of the backend output (chosen): ADR-0031 asks the backends to pause their output, and a sound played in the background stays silent without a race.
  - A pause of every voice from the engine (rejected: a voice started during the pause could sound before it is paused).
- A looping track in a playlist:
  - Allowed, the playlist stays on it (chosen): an intro then a looping theme is a common music pattern.
  - Refused as a failing track (rejected: it forbids that pattern without need).

## Consequences

- Positive consequences:
  - Volume, looping and pause work the same on every backend, and the backend only learns one gain per voice.
  - A looping ambient sound, short, streamed or fully decoded, loops without gap and keeps a bounded memory.
  - Backgrounding holds every sound with one call.
- Costs / constraints:
  - Every `AudioBackend` implementation changes: two `play` signatures and five new methods.
  - A streamed loop opens its resource on the backend's thread at each pass, and allocates a stream and a source each time.
  - `Audio` keeps three more arrays per short voice and one per long voice.
- Impact on modules and APIs:
  - `audio-api`: `AudioBackend` (`play(PcmBuffer, double, boolean)`, `play(PcmStream, double)`, `setGain`, `pause`, `resume`, `pauseOutput`, `resumeOutput`); `Audio` (`MIN_GAIN`, `MAX_GAIN`, `setMasterGain`, `getMasterGain`, `pause`, `resume`, `isPaused`, `setMasterPaused`, `isMasterPaused`); `ShortSound` and `LongSound` (`setGain`, `getGain`, `setLooping`, `isLooping`); `Playlist` (`pause`, `resume`, `isPaused`); `StreamBuffer` loops (package-private).
  - Its test JAR: `FakeAudioBackend` (gain, looping, pause, output pause, `getPlayedFrames`), `AudioBackendContract` (controls), `SilentAudioBackend`.
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: `AudioControlsTest` (gain products and refusals, looping, pause and master pause, stealing, stop, release, the allocation test); `LongSoundControlsTest` (gain, streamed and fully decoded loops, pause, playlists, bounded memory); `StreamBufferLoopTest` (no gap, short and empty sounds, failures of a pass, bounded memory, the allocation test); `PlaylistTest.testSingleTrackLoop` (now gapless); the extended `AudioBackendContract` on the fake; `AudioControlsExampleTest`; device test `AudioTest.testControls`.
  - Documentation: the feature page, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md`, the roadmap, the agent workflow, the Android compatibility page and the traceability.
  - Android/DEX: plain Java (arrays, `volatile`, `InputStream`), run by the device test of the module.

## Validation

Decided by the agent on 2026-11-27 under the owner's delegation of the batches; reviewed in the pull request of audio batch 5.
