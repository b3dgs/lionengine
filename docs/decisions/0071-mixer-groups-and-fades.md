# ADR-0071 — Mixer groups and fades of batch 7: groups owned by `Audio`, fades of a sound or a group ramped per voice in the backend, the `fade` SPI and its completion

- Status: ACCEPTED
- Date: 2026-11-27
- Decision makers: agent, under the owner's delegation of the batch ("Decide and record in a new ADR (ADR-0071), under the batch delegation", with automerge after the pull request), reversible by a superseding ADR
- Related requirements: `REQ-AUDIO-012`, `REQ-AUDIO-009`, `REQ-AUDIO-011`, `REQ-AUDIO-002`
- Open questions resolved: none (Java signatures and details left open by the feature page and by ADR-0033, needed by batch 7 of [`../features/audio-short-and-long-sounds.md`](../features/audio-short-and-long-sounds.md)); none opened
- Supersedes / superseded by: none; completes [ADR-0018](0018-runtime-thread-events-and-cache-thread-safety.md), [ADR-0027](0027-audio-policies-formats-latency-and-first-scope.md), [ADR-0033](0033-media-defaults-module-names-filters-graphics-data-audio.md), [ADR-0065](0065-audio-pcm-data-decoder-and-backend-contracts.md), [ADR-0069](0069-audio-controls-gain-looping-pause.md) and [ADR-0070](0070-audio-javasound-desktop-backend.md) (all unchanged)

## Context

Batch 7 of the feature page is "mixer groups and fades (`REQ-AUDIO-012`)". The policies are fixed by ADR-0033:
- the groups `music`, `effects` and `ui` exist by default, and a game may add groups by id;
- each sound is in exactly one group, `effects` by default for short sounds, `music` for long sounds;
- the effective gain is master × group × sound;
- a fade has a target gain and a duration in seconds, the backend ramps the gain linearly per audio buffer, and its completion is posted to the inbox.

ADR-0069 computes the effective gain in one private method of `Audio`, where the group factor joins the product, and gives the backend the effective gain of each voice only; the backend reads it once per audio buffer. ADR-0070 mixes 512-frame buffers in `JavaSoundBackend` and reported that a fade can ramp the gain per buffer in `MixVoice`.

The owner's delegation asks this ADR to decide:
- what a fade applies to (voice, sound, group, master);
- the SPI addition the ramp needs, and how a fade interacts with `setGain`, pause, the output pause, stop, voice stealing and a new fade;
- whether a fade to `0` stops the voice;
- the completion listener and its thread.

The documents also leave open the Java type of a group, how a sound names its group, and when a group change reaches a voice.

What others do:
- **FMOD** has channel groups with a volume each, the product of the hierarchy; fades are fade points on a channel or a group, multiplied with the volume; a stop can wait for a fade-out.
- **Unity** `AudioMixer` groups have a volume each; fades are snapshot transitions or scripted volume changes.
- **libGDX** has no groups nor fades: games multiply volumes themselves.
- **Godot** has buses with a volume; fades are tweens of a property, frame by frame on the main thread.

## Decision

1. **Groups.** A public final class `MixerGroup` in `audio-api`, created by `Audio` only:
   - ids `MixerGroup.MUSIC` (`"music"`), `MixerGroup.EFFECTS` (`"effects"`) and `MixerGroup.UI` (`"ui"`), created by every `Audio` at full gain;
   - `Audio.addGroup(id)` adds a group of the game at full gain; a `null`, empty or already used id is refused with `IllegalArgumentException`; `Audio.getGroup(id)` (same exception for an unknown id) and `hasGroup(id)`; groups are never removed;
   - `getId()`, `setGain(gain)`, `getGain()` and `fade(gain, seconds)`; a gain is checked as every gain (ADR-0069).
   A group belongs to its `Audio`, since the voices it changes are the voices of that system.
2. **The group of a sound.** The handle names its group by id: `ShortSound.setGroup(id)` (`effects` by default) and `LongSound.setGroup(id)` (`music` by default), with `getGroup()`; `null` or empty refused with `IllegalArgumentException`. The handles are created by caches and loaders that know no `Audio`, so an id, not a `MixerGroup`, is kept on the handle.
   - The group is read at the play, as the looping (ADR-0069): a voice keeps the group it started with. `Audio` keeps the group of each voice, short and long.
   - A sound whose id is not a group of the `Audio` that plays it is refused at the play with `IllegalStateException`, before any voice is taken or stolen; every layer of a composed set is checked before any starts (ADR-0067).
3. **Gain product.** The private method of `Audio` returns master × group × sound. `setMasterGain`, a sound's `setGain` and a group's `setGain` call `AudioBackend.setGain` for every voice concerned at once, without allocating; the group lookup at a play compares ids among the groups, without allocating.
4. **What a fade applies to: a sound or a group.**
   - `ShortSound.fade(gain, seconds)`, `LongSound.fade(gain, seconds)` and `MixerGroup.fade(gain, seconds)`. A fade sets the gain of the sound or of the group to the target at once (its getter returns the target, and a sound played from then on starts at it), then ramps each voice it concerns, in the backend, from the gain the voice has to its new effective gain, over the duration.
   - A sound or a group playing on no voice takes the gain and reports nothing.
   - No master fade: the master is the player's setting, and backgrounding uses the master pause (ADR-0069); fading every voice is a group fade per group. No voice fade either: ADR-0033 defines the gain as master × group × sound, with no voice factor, as ADR-0069 recalled.
   - The duration is in seconds, finite and not negative (`IllegalArgumentException` otherwise, nothing changes); `0` reaches the target at the next audio buffer.
5. **The SPI addition.** Two methods on `AudioBackend`:
   - `fade(int voice, double gain, double seconds)`: ramps the gain of the voice linearly from its current gain to the target, over the duration rounded up to whole audio buffers of the backend, at least one. The gain is constant within a buffer and moves by the same step from one buffer to the next, the last buffer of the ramp being at the target. Gain and duration are checked whatever the voice; no effect on an ended, stopped or unknown voice.
   - `setFadeListener(FadeListener)`: the listener of the ends of the fades, as `setVoiceListener`.
   - The new public interface `FadeListener` has one method, `onFadeEnded(int voice)`.
6. **Interactions**, the same on every backend:
   - a voice has at most one ramp: a new fade replaces it, from the gain reached, and the replaced one posts nothing;
   - `setGain` during a ramp changes its target and keeps its remaining time, so a change of the master, of a group or of the sound (a volume slider moved during a fade-out) does not cut the fade short nor make it jump; the ramp ends at the new effective gain, at its time;
   - a ramp holds while its voice is paused and while the output is paused, as the frames of the voice do;
   - a stop, a voice stolen (a stop in the backend) and the close drop the ramp without completion;
   - a voice that ends by itself during its ramp, its last buffer included, posts its end only.
7. **A fade to `0` does not stop the voice.** The voice plays on, silent, at its position: a fade-in, a ducked ambient sound or a crossfade back needs it. A fade-out that should end the sound stops its voice in the fade listener, one call; the example does so.
8. **Completion.** Once the last buffer of a ramp is played, the backend posts the completion to the inbox from its own thread, one event per voice whose ramp ended; at the delivery, on the runtime thread, the backend calls its `FadeListener`, which is the `Audio` (ADR-0018). `Audio.setFadeListener(FadeListener)` gives the game a listener of the same type, called on the runtime thread for a voice that still plays in `Audio`, so a completion posted just before a stop is not reported. A fade of a short sound with several voices, or of a group, reports once per voice.
9. **Backends.**
   - `FakeAudioBackend` ramps per frame of its clock: a fade of `s` seconds lasts `ceil(s × rate)` frames of the voice (at least one), the rate being the sample rate of its sound, its "audio buffer" being one frame; it adds `isFading(voice)` and `getFadeTarget(voice)`.
   - `JavaSoundBackend` ramps per 512-frame buffer in `MixVoice`, before mixing the buffer: `ceil(s × 44,100 / 512)` buffers, at least one. The completion is an event of the pool of the mixer (`VoiceEnd`, now carrying its kind); the pool keeps a free event for each voice playing and each fade running, and grows on the runtime thread at a play or a fade. Mixing with fades running allocates nothing.
   - `SilentAudioBackend` (test JAR) accepts fades and calls the fade listener on request, for the allocation tests.
10. **Tests.**
    - `AudioBackendContract` gains the fade checks: completion once after the duration and not before, a voice still playing at `0`, no duration, a stream, refusals, a new fade, `setGain` during a fade, pause and output pause, stop, end of the voice and close. The checks keep a margin of four buffers of 512 frames, so a backend that rounds up and mixes one buffer ahead passes. It is bound on `FakeAudioBackend`, on `SoftwareMixer` (`SoftwareMixerContractTest`), on `JavaSoundBackend` with its thread (`JavaSoundBackendThreadContractTest`) and, with a sound device, by `JavaSoundBackendDeviceContractTest`.
    - Unit tests: `AudioMixerGroupTest`, `AudioFadeTest` (with the allocation test of the group and fade paths), `SoftwareMixerFadeTest` (the ramp per buffer, the rounding, the retarget and the replacement, the pause, the end, the pool, the allocation test of the mixing path with fades running).
11. **Example.** `MixerGroupExample`: a volume slider sets the `ui` group, and the exit of the scene fades its music out over 1.5 s, then stops it and releases the sounds in the fade listener; tested on `FakeAudioBackend`.

Reported, not added:
- A voice played during the fade of its sound or group starts at the target, not on the ramp.
- Playlists have no crossfade between tracks; a game crossfades two long sounds with two fades.
- A ramp is linear in gain, not in decibels, as ADR-0033 states; a curved fade would be a later decision.
- `audio-android` (Phase 4) implements `fade` and `setFadeListener` with the same semantics; if it mixes in software, its ramp is per mixed buffer as here; if it uses platform players, it steps their volume once per audio buffer period, and the completion goes through the inbox. `audio-codec-ogg` and spatial audio need no hook.

## Options considered

- What a fade applies to:
  - The sound and the group, ramped per voice in the backend (chosen): the common cases (music fade-out, fade-in, crossfade, ducking a group) are one call, the product stays master × group × sound, and the backend still knows one gain per voice.
  - A voice factor (rejected: a fourth factor outside ADR-0033, rejected for the same reason by ADR-0069).
  - The master as well (rejected: no need stated, the master pause covers backgrounding, and a group fade per group gives it).
- A gain change during a ramp:
  - Retarget, keeping the remaining time (chosen): a slider or a master change does not cut a fade, and the ramp still ends at the right gain and time.
  - Cancel the ramp, a step to the new gain (rejected: a click, and a fade-out would stop at a volume change).
- A fade to `0`:
  - Keeps the voice (chosen): fades back in, ducking and crossfades keep the position.
  - Stops the voice (rejected: hides a stop in a gain change, and forbids those uses).
- The completion:
  - Per voice, through the inbox, to one listener on `Audio` (chosen): the backend knows voices only, the listener is called on the runtime thread like the voice ends, and nothing allocates.
  - One completion per fade call (rejected: `Audio` would count voices ending, stolen or played during the fade, and a fade with no voice would need a completion that no backend posts).
- The group of a sound:
  - An id on the handle, read at the play (chosen): the handles do not know the `Audio`, and the voice keeps a stable group like its looping.
  - A `MixerGroup` object on the handle (rejected: a handle could be given the group of another `Audio`).

## Consequences

- Positive consequences:
  - Volume settings per category and smooth fades with one call, on every backend, with no allocation and no click.
  - The backend SPI grows by two methods only and still knows one gain per voice.
- Costs / constraints:
  - Every `AudioBackend` implementation changes: `fade` and `setFadeListener`.
  - `Audio` keeps the group of each voice; the group lookup at a play is a linear search among the groups.
  - A fade of many voices reports many completions.
- Impact on modules and APIs:
  - `audio-api`: `MixerGroup` and `FadeListener` (new); `AudioBackend` (`fade`, `setFadeListener`); `Audio` (`addGroup`, `getGroup`, `hasGroup`, `setFadeListener`); `ShortSound` and `LongSound` (`fade`, `setGroup`, `getGroup`).
  - Its test JAR: `FakeAudioBackend` (ramp, `isFading`, `getFadeTarget`), `SilentAudioBackend`, `AudioBackendContract` (fades).
  - `audio-javasound`: `JavaSoundBackend` (the two methods); `SoftwareMixer`, `MixVoice` and `VoiceEnd` (package-private).
- Impact on tests and documentation (including Android/DEX compatibility):
  - Tests: decision 10, `MixerGroupExampleTest`, and the device test `AudioTest.testGroupsAndFades` on API 21.
  - Documentation: the feature page, `contracts.md`, `interfaces.md`, `api-index.md`, `engine-features.md`, the roadmap, the agent workflow, the traceability and the batch status.
  - Android/DEX: plain Java (arrays, `String.equals`, `Math.ceil`), checked by the device test of the module.

## Validation

Decided by the agent on 2026-11-27 under the owner's delegation of the batch; reviewed in the pull request of audio batch 7.
