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
    private static Sc68 sc68;
    /** Music enabled. */
    private static boolean enabled;

    /**
     * Set the enabled state.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public static void setEnabled(boolean enabled)
    {
        SonicArranger.enabled = enabled;
        if (enabled)
        {
            SonicArranger.sc68 = AudioSc68.createSc68Player();
            SonicArranger.sc68.setVolume(40);
        }
    }

    /**
     * Stop player.
     */
    public static void stop()
    {
        if (SonicArranger.enabled)
        {
            SonicArranger.sc68.stop();
        }
    }

    /**
     * Pause player.
     */
    public static void pause()
    {
        if (SonicArranger.enabled)
        {
            SonicArranger.sc68.pause();
        }
    }

    /**
     * Resume player.
     */
    public static void resume()
    {
        if (SonicArranger.enabled)
        {
            SonicArranger.sc68.resume();
        }
    }

    /**
     * Play a music.
     * 
     * @param music The music to play.
     */
    public static void play(Music music)
    {
        if (SonicArranger.enabled)
        {
            SonicArranger.sc68.play(Media.get(AppLionheart.MUSICS_DIR, music.getFilename()));
        }
    }

    /**
     * Terminate player.
     */
    public static void terminate()
    {
        if (SonicArranger.enabled)
        {
            SonicArranger.sc68.free();
        }
    }

    /**
     * Get playing index.
     * 
     * @return The playing index.
     */
    public static int seek()
    {
        if (SonicArranger.enabled)
        {
            return SonicArranger.sc68.seek();
        }
        return -1;
    }

    /**
     * Private constructor.
     */
    private SonicArranger()
    {
        throw new RuntimeException();
    }
}
