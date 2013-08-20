package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.audio.AudioSc68;
import com.b3dgs.lionengine.audio.Sc68;

/**
 * SonicArranger set of functions.
 */
final class SonicArranger
{
    /** Swamp music. */
    public static final Media SWAMP = Media.get("musics", "Swamp.sc68");
    /** Player instance. */
    private static final Sc68 SC68 = AudioSc68.createSc68Player();

    /**
     * Private constructor.
     */
    private SonicArranger()
    {
        throw new RuntimeException();
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
     * Play track from its name.
     * 
     * @param track The track to play.
     */
    public static void play(Media track)
    {
        Verbose.info("Initiating SonicArranger music: ", track.getPath());
        SonicArranger.SC68.setVolume(40);
        SonicArranger.SC68.play(track);
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
}
