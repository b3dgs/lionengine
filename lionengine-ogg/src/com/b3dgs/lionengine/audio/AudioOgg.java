package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.Media;

/**
 * Handle audio OGG.
 */
public final class AudioOgg
{
    /**
     * Load an OGG music from a file name.
     * 
     * @param media The media file.
     * @return loaded OGG.
     */
    public static Ogg loadOgg(Media media)
    {
        return new OggPlayer(media);
    }

    /**
     * Private constructor.
     */
    private AudioOgg()
    {
        throw new RuntimeException();
    }
}
