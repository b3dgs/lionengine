package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.Media;

/**
 * Allows to play SonicArranger musics (original Amiga player).
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Sc68 sc68 = AudioSc68.createSc68Player();
 * sc68.setVolume(25);
 * sc68.play(Media.get(&quot;music.sc68&quot;));
 * 
 * Thread.sleep(1000);
 * sc68.pause();
 * Thread.sleep(500);
 * sc68.setVolume(75);
 * sc68.resume();
 * Thread.sleep(1000);
 * Assert.assertTrue(sc68.seek() &gt;= 0);
 * 
 * sc68.stop();
 * sc68.free();
 * </pre>
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
