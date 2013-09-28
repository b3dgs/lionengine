package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

// Tutorial: Lionheart
// This last platform tutorial will show a quick game example to show as much things as possible.

/**
 * Program starts here.
 */
public final class AppLionheart
{
    /** Application name. */
    public static final String NAME = "Lionheart";
    /** Application version. */
    public static final Version VERSION = Version.create(0, 1, 0);
    /** Sprites directory. */
    public static final String SPRITES_DIR = "sprites";
    /** Levels directory. */
    public static final String LEVELS_DIR = "levels";
    /** Sheets directory. */
    public static final String TILES_DIR = "tiles";
    /** Rasters directory. */
    public static final String RASTERS_DIR = "rasters";
    /** Main entity directory name. */
    public static final String ENTITIES_DIR = "entities";
    /** Main entity directory name. */
    public static final String BACKGROUNDS_DIR = "backgrounds";
    /** Effects directory. */
    public static final String EFFECTS_DIR = "effects";
    /** Musics directory. */
    public static final String MUSICS_DIR = "musics";
    /** Sound fx directory name. */
    public static final String SFX_DIR = "sfx";
    /** Entity configuration file extension. */
    public static final String CONFIG_FILE_EXTENSION = ".xml";
    /** Raster enabled. */
    public static boolean RASTER_ENABLED = false;
    /** Show collision bounds. */
    public static final boolean SHOW_COLLISIONS = false;
    /** Enable sound. */
    private static final boolean ENABLE_SOUND = false;
    /** Resources directory. */
    private static final String RESOURCES = Media.getPath("resources", "platform", "lionheart");

    /**
     * Main function.
     * 
     * @param args The arguments (none).
     */
    public static void main(String[] args)
    {
        Engine.start(AppLionheart.NAME, AppLionheart.VERSION, AppLionheart.RESOURCES);
        Sfx.setEnabled(AppLionheart.ENABLE_SOUND);
        SonicArranger.setEnabled(AppLionheart.ENABLE_SOUND);

        final Resolution output = new Resolution(640, 480, 60);
        final Config config = new Config(output, 16, true);
        final boolean enableEditor = false;
        if (enableEditor)
        {
            AppLionheart.RASTER_ENABLED = false;
            final Editor editor = new Editor();
            editor.start();
        }
        else
        {
            final Loader loader = new Loader(config);
            loader.start(new Scene(loader));
        }
    }

    /**
     * Private constructor.
     */
    private AppLionheart()
    {
        throw new RuntimeException();
    }
}
