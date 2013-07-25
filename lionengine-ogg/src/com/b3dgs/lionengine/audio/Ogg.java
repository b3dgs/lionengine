package com.b3dgs.lionengine.audio;

/**
 * Handle music routine. A music is an heavy sound, designed to be played once (loop or not).
 */
public interface Ogg
{
    /** Minimum volume value. */
    int VOLUME_MIN = 0;
    /** Maximum volume value. */
    int VOLUME_MAX = 100;

    /**
     * Play music. The music will be played until the end. In case of a loop, music will be played in loop. Music are
     * played in a separated thread.
     * 
     * @param repeat The loop flag.
     */
    void play(boolean repeat);

    /**
     * Set the sound volume.
     * 
     * @param volume The volume in percent <code>[{@link #VOLUME_MIN} - {@link #VOLUME_MAX}]</code>.
     */
    void setVolume(int volume);

    /**
     * Stop music. The music will be stopped, but not deleted.
     */
    void stop();
}
