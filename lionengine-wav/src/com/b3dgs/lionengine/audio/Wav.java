package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.Align;

/**
 * Handle sound fx routine. The sound is expected to be short, as it has to be played quickly. It supports the following
 * main controls:
 * <ul>
 * <li>Alignment</li>
 * <li>Volume</li>
 * <li>Channel</li>
 * </ul>
 */
public interface Wav
{
    /** Minimum volume value. */
    int VOLUME_MIN = 0;
    /** Maximum volume value. */
    int VOLUME_MAX = 100;

    /**
     * Set sound alignment.
     * 
     * @param align sound alignment.
     */
    void setAlignment(Align align);

    /**
     * Set the sound volume.
     * 
     * @param volume The volume in percent <code>[{@link #VOLUME_MIN} - {@link #VOLUME_MAX}]</code>.
     */
    void setVolume(int volume);

    /**
     * Play sound immediately until the end, and free resources. Sounds are played in a separated thread. If all
     * channels are used, the sound will not be played.
     */
    void play();

    /**
     * Stop sound. The sound will be stopped, but not deleted.
     */
    void stop();
}
