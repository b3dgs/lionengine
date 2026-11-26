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
 * Audio backend SPI: plays fully decoded sounds and streamed sounds on voices (ADR-0004, ADR-0018, ADR-0027, ADR-0065,
 * ADR-0068, REQ-AUDIO-001, REQ-AUDIO-007, REQ-AUDIO-009).
 *
 * <p>
 * Implementations: {@code audio-javasound} on desktop, {@code audio-android}, and {@code FakeAudioBackend} of the
 * {@code audio-api} test JAR, which every implementation's contract suite {@code AudioBackendContract} also binds
 * (ADR-0045). The application creates the backend with the inbox of the loop. The engine's audio logic (voice limit and
 * overflow policy, variant selection, playlists) stays in {@code audio-api}; a backend only starts and stops voices and
 * reports their end. Pause, resume, gain and looping come with the controls of the audio batch 5.
 * </p>
 *
 * <p>
 * A voice plays either a {@link PcmBuffer}, read by copy, or a {@link PcmStream}, whose frames the backend pulls from
 * its own thread as it plays them, until the stream returns fewer frames than asked: that is the end of the voice.
 * Both kinds share the voice numbering, the voice limit, {@link #stop(int)}, {@link #isPlaying(int)} and the end
 * event. The backend reads a stream only from the call to {@link #play(PcmStream)} until its voice ends, or until
 * {@link #stop(int)} or {@link #close()} returns, so that the engine may then close what the stream reads.
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
     * Starts playing a sound at once, from its first frame, at full gain.
     *
     * @param buffer The sound (never {@code null}).
     * @return The voice playing it, positive; {@link #NO_VOICE} if every voice is busy.
     * @throws IllegalArgumentException If {@code buffer} is {@code null}.
     * @throws IllegalStateException If the backend is closed.
     */
    int play(PcmBuffer buffer);

    /**
     * Starts playing a stream at once, from its next frame, at full gain. The backend pulls its frames from its own
     * thread, in pieces of the size it needs, until the stream returns fewer frames than asked; the voice then ends by
     * itself and posts its end as a buffer voice does.
     *
     * @param stream The stream, read by this voice only (never {@code null}).
     * @return The voice playing it, positive; {@link #NO_VOICE} if every voice is busy, and the stream is not read.
     * @throws IllegalArgumentException If {@code stream} is {@code null}.
     * @throws IllegalStateException If the backend is closed.
     */
    int play(PcmStream stream);

    /**
     * Stops a voice; no effect on a voice that ended, was stopped or does not exist. No end event is posted. Once it
     * returns, the stream of a streamed voice is no longer read.
     *
     * @param voice The voice.
     */
    void stop(int voice);

    /**
     * Tells whether a voice is still playing.
     *
     * @param voice The voice.
     * @return {@code true} until the voice ends or is stopped.
     */
    boolean isPlaying(int voice);

    /**
     * Stops every voice and releases the output; no effect if already closed. Once it returns, no stream is read. End
     * events already posted are still delivered.
     */
    void close();
}
