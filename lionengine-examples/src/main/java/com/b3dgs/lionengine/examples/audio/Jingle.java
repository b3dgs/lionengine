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
import com.b3dgs.lionengine.audio.VoiceListener;

/**
 * Jingle of {@link JavaSoundExample}: plays its notes one after the other, each at the end of the previous one, on the
 * runtime thread (REQ-AUDIO-002).
 *
 * <p>
 * It is the voice listener of its backend, set by its creator; each note is a little softer than the previous one.
 * </p>
 *
 * @since 11.0.0
 */
public final class Jingle implements VoiceListener
{
    /** The backend. */
    private final AudioBackend backend;
    /** The notes. */
    private final PcmBuffer[] notes;
    /** The gain of the first note. */
    private final double firstGain;
    /** The gain removed at each note. */
    private final double gainStep;
    /** The notes started. */
    private int played;
    /** The voice of the note playing, {@link AudioBackend#NO_VOICE} when none. */
    private int voice;

    /**
     * Creates the jingle; its creator sets it as the voice listener of the backend, then plays the first note.
     *
     * @param backend The backend.
     * @param notes The notes, owned by the jingle.
     * @param firstGain The gain of the first note.
     * @param gainStep The gain removed at each note; the last note keeps a gain in {@code [0, 1]}.
     */
    Jingle(AudioBackend backend, PcmBuffer[] notes, double firstGain, double gainStep)
    {
        this.backend = backend;
        this.notes = notes;
        this.firstGain = firstGain;
        this.gainStep = gainStep;
    }

    /**
     * Returns the notes started.
     *
     * @return The number of notes played so far.
     */
    public int getPlayed()
    {
        return played;
    }

    /**
     * Returns the voice of the note playing.
     *
     * @return The voice, {@link AudioBackend#NO_VOICE} once the jingle is done.
     */
    public int getVoice()
    {
        return voice;
    }

    /**
     * Tells whether the jingle is done.
     *
     * @return {@code true} once the last note ended.
     */
    public boolean isDone()
    {
        return played == notes.length && voice == AudioBackend.NO_VOICE;
    }

    @Override
    public void onVoiceEnded(int ended)
    {
        if (ended == voice)
        {
            next();
        }
    }

    /**
     * Plays the next note, or ends the jingle after the last one.
     */
    void next()
    {
        if (played < notes.length)
        {
            voice = backend.play(notes[played], firstGain - gainStep * played, false);
            played++;
        }
        else
        {
            voice = AudioBackend.NO_VOICE;
        }
    }
}
