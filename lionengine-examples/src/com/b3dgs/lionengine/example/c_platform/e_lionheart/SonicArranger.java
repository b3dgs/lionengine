package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.audio.AudioSc68;
import com.b3dgs.lionengine.audio.Sc68;

/**
 * Sc68 module binding.
 */
public final class SonicArranger
{
    /** Player instance. */
    private static final Sc68 SC68;

    /**
     * Static init.
     */
    static
    {
        SC68 = AudioSc68.createSc68Player();
        SonicArranger.SC68.setVolume(40);
    }

    /**
     * Stop player.
     */
    public static void stop()
    {
        SonicArranger.SC68.stop();
    }

    /**
     * Pause player.
     */
    public static void pause()
    {
        SonicArranger.SC68.pause();
    }

    /**
     * Resume player.
     */
    public static void resume()
    {
        SonicArranger.SC68.resume();
    }

    /**
     * Play a music.
     * 
     * @param music The music to play.
     */
    public static void play(Music music)
    {
        SonicArranger.SC68.play(Media.get(AppLionheart.MUSICS_DIR, music.getFilename()));
    }

    /**
     * Terminate player.
     */
    public static void terminate()
    {
        SonicArranger.SC68.free();
    }

    /**
     * Get playing index.
     * 
     * @return The playing index.
     */
    public static int seek()
    {
        return SonicArranger.SC68.seek();
    }

    /**
     * Private constructor.
     */
    private SonicArranger()
    {
        throw new RuntimeException();
    }
}
