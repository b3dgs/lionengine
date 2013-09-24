package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Audio factory. Allows to create audio player.
 */
public final class AudioMidi
{
    /**
     * Load a midi file <code>(.mid, .midi)</code> and prepare it to be played. A {@link LionEngineException} is thrown
     * if no midi player is available
     * 
     * @param media The audio midi media.
     * @return The loaded midi;
     */
    public static Midi loadMidi(Media media)
    {
        return new MidiPlayer(media);
    }

    /**
     * Private constructor.
     */
    private AudioMidi()
    {
        throw new RuntimeException();
    }
}
