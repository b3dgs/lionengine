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
package com.b3dgs.lionengine.examples.audio;

import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.VoiceListener;

/**
 * Generates a beep as PCM samples and plays it on an audio backend, learning its end through the inbox
 * (REQ-AUDIO-001, REQ-AUDIO-009, ADR-0065).
 *
 * <p>
 * Sounds are 16-bit signed PCM, interleaved by frame ({@link PcmFormat}, {@link PcmBuffer}); a decoder turns a file
 * into a buffer, and code can build one directly as here. The backend plays it on a voice, an {@code int}; when the
 * sound ends, the backend posts an event to the inbox, and the {@link VoiceListener} is called on the runtime thread
 * at its delivery. Games will use the engine's audio system of the next batches, which drives the backend; this
 * example shows the layer below it.
 * </p>
 *
 * @since 11.0.0
 */
public final class BeepExample
{
    /** Amplitude of the beep: a quarter of the full scale. */
    private static final double AMPLITUDE = 0.25 * Short.MAX_VALUE;

    /**
     * Private constructor.
     */
    private BeepExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Generates a mono sine beep.
     *
     * @param frequency The frequency in Hz (positive).
     * @param seconds The duration in seconds (not negative).
     * @param sampleRate The sample rate in Hz (positive).
     * @return The sound.
     */
    public static PcmBuffer beep(double frequency, double seconds, int sampleRate)
    {
        final short[] samples = new short[(int) Math.round(seconds * sampleRate)];
        for (int i = 0; i < samples.length; i++)
        {
            samples[i] = (short) Math.round(AMPLITUDE * Math.sin(2.0 * Math.PI * frequency * i / sampleRate));
        }
        return new PcmBuffer(new PcmFormat(sampleRate, PcmFormat.MONO), samples);
    }

    /**
     * Plays a sound and observes its end.
     *
     * @param backend The audio backend.
     * @param sound The sound.
     * @param listener The listener told on the runtime thread when the sound ends.
     * @return The voice playing it, {@link AudioBackend#NO_VOICE} if every voice is busy.
     */
    public static int play(AudioBackend backend, PcmBuffer sound, VoiceListener listener)
    {
        backend.setVoiceListener(listener);
        return backend.play(sound);
    }
}
