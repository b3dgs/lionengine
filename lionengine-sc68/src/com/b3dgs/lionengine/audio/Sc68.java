package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.Media;

/**
 * Allows to play SonicArranger musics (original Amiga player).
 */
public interface Sc68
{
    /**
     * Play a music from its id, previously loaded.
     * 
     * @param media The music media.
     */
    void play(Media media);

    /**
     * Set player volume (between 0 and 100, as a percent).
     * 
     * @param volume The music volume [0-100].
     */
    void setVolume(int volume);

    /**
     * Pause a playing music.
     */
    void pause();

    /**
     * Continue to play music after being paused.
     */
    void resume();

    /**
     * Stop a music.
     */
    void stop();

    /**
     * Terminate plugin.
     */
    void free();

    /**
     * Get music progress counter.
     * 
     * @return The music progress counter.
     */
    int seek();
}
