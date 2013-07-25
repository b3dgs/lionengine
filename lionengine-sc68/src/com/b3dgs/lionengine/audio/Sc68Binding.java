package com.b3dgs.lionengine.audio;

import com.sun.jna.Library;

/**
 * Sc68 binding interface.
 */
interface Sc68Binding
        extends Library
{
    /**
     * Play sc68.
     * 
     * @param name The track name.
     */
    void SC68Play(String name);

    /**
     * Set volume.
     * 
     * @param volume The value.
     */
    void SC68Volume(int volume);

    /**
     * Pause track.
     */
    void SC68Pause();

    /**
     * Resume track.
     */
    void SC68UnPause();

    /**
     * Stop track.
     */
    void SC68Stop();

    /**
     * Free library.
     */
    void SC68Free();

    /**
     * Get play index.
     * 
     * @return The play index.
     */
    int SC68Seek();
}
