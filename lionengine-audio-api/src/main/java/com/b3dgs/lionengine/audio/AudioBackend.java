/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.audio;

/**
 * Audio backend SPI: plays fully decoded sounds and streamed sounds on voices, with their gain, looping, pause and
 * fades (ADR-0004, ADR-0018, ADR-0027, ADR-0031, ADR-0033, ADR-0065, ADR-0068, ADR-0069, ADR-0071, REQ-AUDIO-001,
 * REQ-AUDIO-007, REQ-AUDIO-009, REQ-AUDIO-011, REQ-AUDIO-012).
 *
 * <p>
 * Implementations: {@code audio-javasound} on desktop, {@code audio-android}, and {@code FakeAudioBackend} of the
 * {@code audio-api} test JAR, which every implementation's contract suite {@code AudioBackendContract} also binds
 * (ADR-0045). The application creates the backend with the inbox of the loop. The engine's audio logic (voice limit and
 * overflow policy, variant selection, playlists) stays in {@code audio-api}; a backend only starts and stops voices and
 * reports their end, and applies the gain, the looping and the pause it is given. Master gain, per-sound gain and the
 * gain of the mixer groups are the engine's: a backend only knows the effective gain of each voice.
 * </p>
 *
 * <p>
 * Controls (ADR-0069):
 * </p>
 * <ul>
 * <li>Gain: a {@code double} in {@code [0, 1]} multiplying the samples of a voice, given at its play and changed with
 * {@link #setGain(int, double)}. The backend reads the gain of a voice once per audio buffer it fills, so a change
 * reaches a playing voice at its next buffer, as a step. Out of range and NaN gains are refused with an
 * {@link IllegalArgumentException}.</li>
 * <li>Looping: a buffer voice played with {@code looping} starts again from its first frame after its last one, with
 * no gap, and never ends by itself, so it posts no end; a buffer of no frame ends at once even when looping. A stream
 * loops inside the engine: a looping stream never returns fewer frames than asked, so the backend needs no loop of
 * its own for it.</li>
 * <li>Pause: {@link #pause(int)} holds a voice where it is and {@link #resume(int)} continues it. A paused voice keeps
 * its voice, so it counts in the voice limit, and is still playing for {@link #isPlaying(int)}; a paused stream is not
 * read. {@link #pauseOutput()} holds every voice at once, for example while the application is in the background
 * (ADR-0031), and {@link #resumeOutput()} continues them; the pause of the output and the pause of a voice are
 * independent, so a voice paused on its own stays paused when the output resumes, and a voice played while the output
 * is paused starts when it resumes.</li>
 * <li>Fade (ADR-0071): {@link #fade(int, double, double)} ramps the gain of a voice linearly, from its gain at the call
 * to a target gain, over a duration in seconds, rounded up to whole audio buffers of the backend (at least one). The
 * gain is constant within a buffer and steps by the same amount from one buffer to the next, the last buffer of the
 * ramp being at the target; once that buffer is played, the backend posts the completion to the inbox, which calls the
 * {@link FadeListener} on the runtime thread. A voice has at most one ramp: a new fade replaces it, from the gain
 * reached, and the replaced one posts nothing. {@link #setGain(int, double)} during a ramp changes its target and
 * keeps its remaining time, so that a change of the master or of a group gain does not cut a fade short. A ramp holds
 * while its voice or the output is paused, and is dropped without completion when its voice ends, is stopped or the
 * backend closes. A fade to {@code 0} does not stop the voice: it plays on, silent, until it is stopped.</li>
 * </ul>
 *
 * <p>
 * A voice plays either a {@link PcmBuffer}, read by copy, or a {@link PcmStream}, whose frames the backend pulls from
 * its own thread as it plays them, until the stream returns fewer frames than asked: that is the end of the voice.
 * Both kinds share the voice numbering, the voice limit, {@link #stop(int)}, {@link #isPlaying(int)} and the end
 * event. The backend reads a stream only from the call to {@link #play(PcmStream, double)} until its voice ends, or
 * until {@link #stop(int)} or {@link #close()} returns, so that the engine may then close what the stream reads. A
 * stream whose read throws an unchecked exception ends its voice there, as if it had returned fewer frames than asked:
 * the end is posted, the stream is not read again, and the backend keeps playing its other voices (ADR-0072). The
 * streams of the engine never throw so: they keep their failure for the runtime thread.
 * </p>
 *
 * <p>
 * A voice is a positive {@code int}, never reused while the backend lives, so playing allocates no handle. When a
 * voice ends by itself, the backend posts one event to the inbox from its own thread; at its delivery the
 * {@link VoiceListener} is called on the runtime thread. A stopped voice posts nothing.
 * </p>
 *
 * <p>
 * Threads: every method is called on the runtime thread. The backend's own threads read the buffers, which are
 * immutable, pull the streams, and only post to the inbox; they never touch simulation state.
 * </p>
 *
 * @since 11.0.0
 */
public interface AudioBackend
{
    /** Voice returned when no voice is free: nothing is played. */
    int NO_VOICE = 0;

    /**
     * Returns the number of voices this backend plays at once.
     *
     * @return The voice limit (positive); the engine keeps its own limit within it.
     */
    int getMaxVoices();

    /**
     * Sets the listener of the voices that end by themselves; it replaces the previous one.
     *
     * @param listener The listener, {@code null} for none.
     */
    void setVoiceListener(VoiceListener listener);

    /**
     * Sets the listener of the fades that reach their end; it replaces the previous one.
     *
     * @param listener The listener, {@code null} for none.
     */
    void setFadeListener(FadeListener listener);

    /**
     * Starts playing a sound at once, from its first frame; while the output is paused, it starts when the output
     * resumes.
     *
     * @param buffer The sound (never {@code null}).
     * @param gain The gain of the voice, in {@code [0, 1]}, applied from its first frame.
     * @param looping {@code true} to start again from the first frame after the last one, so that the voice never ends
     *            by itself (a buffer of no frame ends at once); {@code false} to end after the last frame.
     * @return The voice playing it, positive; {@link #NO_VOICE} if every voice is busy.
     * @throws IllegalArgumentException If {@code buffer} is {@code null}, or the gain is NaN or outside
     *             {@code [0, 1]}.
     * @throws IllegalStateException If the backend is closed.
     */
    int play(PcmBuffer buffer, double gain, boolean looping);

    /**
     * Starts playing a stream at once, from its next frame; while the output is paused, it starts when the output
     * resumes. The backend pulls its frames from its own thread, in pieces of the size it needs, until the stream
     * returns fewer frames than asked; the voice then ends by itself and posts its end as a buffer voice does. A
     * looping stream never returns fewer frames than asked.
     *
     * @param stream The stream, read by this voice only (never {@code null}).
     * @param gain The gain of the voice, in {@code [0, 1]}, applied from its first frame.
     * @return The voice playing it, positive; {@link #NO_VOICE} if every voice is busy, and the stream is not read.
     * @throws IllegalArgumentException If {@code stream} is {@code null}, or the gain is NaN or outside
     *             {@code [0, 1]}.
     * @throws IllegalStateException If the backend is closed.
     */
    int play(PcmStream stream, double gain);

    /**
     * Changes the gain of a voice, from its next audio buffer; no effect on a voice that ended, was stopped or does
     * not exist. A paused voice keeps the gain for its resume. During a fade, the gain becomes the new target of the
     * ramp, which keeps its remaining time (ADR-0071).
     *
     * @param voice The voice.
     * @param gain The gain, in {@code [0, 1]}.
     * @throws IllegalArgumentException If the gain is NaN or outside {@code [0, 1]}, whatever the voice.
     */
    void setGain(int voice, double gain);

    /**
     * Ramps the gain of a voice linearly from its current gain to a target gain, over a duration rounded up to whole
     * audio buffers (at least one); no effect on a voice that ended, was stopped or does not exist. It replaces the
     * ramp of the voice, if any, without completion for the replaced one. Once the last buffer of the ramp is played,
     * at the target gain, the completion is posted to the inbox and the {@link FadeListener} is called at its delivery,
     * on the runtime thread; the voice plays on at the target gain, also at {@code 0}. The ramp holds while the voice
     * or the output is paused, and is dropped without completion if the voice ends, is stopped or the backend closes
     * first.
     *
     * @param voice The voice.
     * @param gain The target gain, in {@code [0, 1]}.
     * @param seconds The duration of the ramp, in seconds, finite and not negative; {@code 0} reaches the target at
     *            the next audio buffer.
     * @throws IllegalArgumentException If the gain is NaN or outside {@code [0, 1]}, or the duration is NaN, infinite
     *             or negative, whatever the voice.
     */
    void fade(int voice, double gain, double seconds);

    /**
     * Pauses a voice where it is; no effect on a paused voice, or on a voice that ended, was stopped or does not
     * exist. The voice keeps its voice and posts no end while paused. Once it returns, the stream of a streamed voice
     * is not read until the voice is resumed.
     *
     * @param voice The voice.
     */
    void pause(int voice);

    /**
     * Resumes a paused voice from where it was paused; no effect on a voice that is not paused, ended, was stopped or
     * does not exist. While the output is paused, the voice continues when the output resumes.
     *
     * @param voice The voice.
     */
    void resume(int voice);

    /**
     * Pauses the output: every voice holds where it is, posts no end and reads no stream, as if each were paused,
     * without changing the pause of each voice; no effect if the output is paused or the backend closed. Once it
     * returns, no stream is read until the output resumes.
     */
    void pauseOutput();

    /**
     * Resumes the output: every voice that is not paused on its own continues; no effect if the output is not paused
     * or the backend closed.
     */
    void resumeOutput();

    /**
     * Stops a voice, paused or not; no effect on a voice that ended, was stopped or does not exist. No end event is
     * posted. Once it returns, the stream of a streamed voice is no longer read.
     *
     * @param voice The voice.
     */
    void stop(int voice);

    /**
     * Tells whether a voice is still playing.
     *
     * @param voice The voice.
     * @return {@code true} until the voice ends or is stopped, also while it is paused.
     */
    boolean isPlaying(int voice);

    /**
     * Stops every voice and releases the output; no effect if already closed. Once it returns, no stream is read. End
     * events already posted are still delivered.
     */
    void close();
}
