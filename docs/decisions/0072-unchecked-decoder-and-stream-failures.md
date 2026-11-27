# ADR-0072 — Unchecked failures of decoders and streams never reach a backend thread

- Status: ACCEPTED
- Date: 2026-11-27
- Decision makers: project owner (review of the audio feature, 2026-11-27: "mixer robustness" chosen among the review options); agent for the details, reversible by a superseding ADR
- Related requirements: `REQ-AUDIO-006`, `REQ-AUDIO-007`, `REQ-AUDIO-008`, `REQ-AUDIO-009`, `REQ-AUDIO-002`
- Open questions resolved: none; none opened
- Supersedes / superseded by: none; completes [ADR-0068](0068-long-sounds-streaming-backend-pull-and-playlists.md), [ADR-0069](0069-audio-controls-gain-looping-pause.md) and [ADR-0070](0070-audio-javasound-desktop-backend.md) (all unchanged)

## Context

The review of the audio feature after batch 7 found that a streamed long sound caught only the checked and expected failures of its source (`IOException`, `AudioDecodingException`, `ResourceException`). Another unchecked exception of a decoder (a bug, a malformed file passing its checks, a provider throwing at the reopening of a loop) went up the backend's thread. On `JavaSoundBackend` that thread is the mixing thread, which has no catch: it died silently, every voice froze, no end nor fade completion was posted again, and a playlist waited for ever. A source returning a frame count outside what was asked corrupted the ring. At the preparation, the same failure leaked the resource stream and escaped as a raw exception, which a playlist did not catch at the delivery of an end. The OGG decoder of the next batch is third-party code, which makes these failures more likely.

## Decision

1. **The stream buffer keeps every failure of its source.** `StreamBuffer` catches any `IOException` or unchecked exception of its source, of the resource and of the reopening of a loop, and a source returning a negative count or more frames than asked (an `AudioDecodingException`). The stream ends after the frames already decoded and keeps the failure for the runtime thread, as ADR-0068 does for the expected failures. The backend's thread never receives it.
2. **Reported as a decoding failure.** `LongSoundLoader` turns an unchecked failure that is neither a `ResourceException` nor an `AudioDecodingException` into an `AudioDecodingException` whose message starts with the key, the original exception kept as its cause. This holds at the opening and the full decoding on the runtime thread (the resource stream closed first) and for the failure of a playing stream reported at its end to the `Playlist` listener, which skips the track as any failing track.
3. **Backend safety net (SPI).** A stream whose read throws an unchecked exception ends its voice there, as if it had returned fewer frames than asked: the end is posted, the stream is not read again, and the backend keeps playing its other voices. `AudioBackend` states it; `FakeAudioBackend` and `SoftwareMixer` implement it; `AudioBackendContract.testStreamThrowing` checks it on every backend. The streams of the engine never throw so, after decision 1; the net covers a stream written by a game or a bug of the mixer reading it.
4. The catches follow the convention of the boundaries of the engine (`SceneManager`, `AsyncRequest`): `// CHECKSTYLE IGNORE LINE: IllegalCatch`.

Reported, not added: the failure caught by the backend safety net is not reported (the SPI has no error hook); a failure of the Java Sound line itself still ends the mixing thread (ADR-0070), an error hook staying a later decision.

## Options considered

- Catch in the stream buffer and the backend (chosen): the engine reports the failure where the game listens, and a backend survives any stream.
- Catch in the backend only (rejected: the failure would be lost, and the playlist would not know the track failed).
- No catch, documenting that decoders must not throw unchecked exceptions (rejected: a third-party codec cannot be trusted to, and one bug silences the whole game).

## Consequences

- Positive consequences: a decoder bug ends one sound and is reported with its key; the other sounds and the mixing thread go on.
- Costs / constraints: every backend implements decision 3; an unexpected exception is wrapped, so its type is found as the cause.
- Impact on modules and APIs: `AudioBackend` (documented rule, no new method); `StreamBuffer`, `LongSoundLoader` (package-private); `SoftwareMixer` (package-private); `FakeAudioBackend` and `AudioBackendContract` in the test JAR.
- Impact on tests and documentation (including Android/DEX compatibility): `DecoderFailureTest` (a source throwing while its playlist track plays, a frame count out of range, failures at the preparation and the full decoding with the stream closed), `AudioBackendContract.testStreamThrowing` on `FakeAudioBackend`, `SoftwareMixer` and `JavaSoundBackend`; the feature page, `contracts.md` and the traceability. Android/DEX: plain Java, no new API.

## Validation

Chosen by the owner in the audio review of 2026-11-27; details decided by the agent; reviewed in the pull request of the review.
