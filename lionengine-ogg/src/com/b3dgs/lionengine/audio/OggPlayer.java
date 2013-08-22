package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;

/**
 * Ogg player implementation.
 */
class OggPlayer
        implements Ogg
{
    /** Music media. */
    private final Media media;
    /** Thread. */
    private OggRoutine routine;
    /** Volume value. */
    private int volume;

    /**
     * Constructor.
     * 
     * @param media music media.
     */
    OggPlayer(Media media)
    {
        Check.notNull(media);
        this.media = media;
        routine = null;
    }

    /*
     * Ogg
     */

    @Override
    public void setVolume(int vol)
    {
        Check.argument(vol >= Ogg.VOLUME_MIN && vol <= Ogg.VOLUME_MAX, "Wrong volume value: ", String.valueOf(vol),
                " [" + Ogg.VOLUME_MIN + "-" + Ogg.VOLUME_MAX + "]");
        volume = vol;
    }

    @Override
    public void play(boolean repeat)
    {
        if (routine == null)
        {
            routine = new OggRoutine(media, repeat);
            routine.setVolume(volume);
            routine.start();
        }
    }

    @Override
    public void stop()
    {
        if (routine != null)
        {
            routine.terminate();
            routine = null;
        }
    }
}
