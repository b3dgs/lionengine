package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.Media;

/**
 * Audio factory. Allows to create audio player.
 */
public final class AudioWav
{
    /**
     * Load a sound file <code>(.wav)</code>.
     * 
     * @param media The audio sound media.
     * @return The loaded Sound.
     */
    public static Wav loadWav(Media media)
    {
        return new WavPlayer(media);
    }

    /**
     * Load a sound file <code>(.wav)</code>.
     * 
     * @param media The audio sound media.
     * @param maxSimultaneous The maximum number of simultaneous sounds that can be played at the same time.
     * @return The loaded Sound.
     */
    public static Wav loadWav(Media media, int maxSimultaneous)
    {
        return new WavPlayer(media, maxSimultaneous);
    }

    /**
     * Private constructor.
     */
    private AudioWav()
    {
        throw new RuntimeException();
    }
}
