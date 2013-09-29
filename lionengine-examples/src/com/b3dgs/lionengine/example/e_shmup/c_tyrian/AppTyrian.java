package com.b3dgs.lionengine.example.e_shmup.c_tyrian;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

// Tutorial: Tyrian
// This tutorial shows how to create a shoot'em'up.

/**
 * Program starts here.
 */
public final class AppTyrian
{
    /** Application name. */
    public static final String NAME = "Tyrian";
    /** Application version. */
    public static final Version VERSION = Version.create(1, 0, 0);
    /** Resources directory. */
    private static final String RESOURCES = Media.getPath("resources", "shmup");

    /**
     * Main function.
     * 
     * @param argv The arguments.
     */
    public static void main(String[] argv)
    {
        // Start engine
        Engine.start(AppTyrian.NAME, AppTyrian.VERSION, AppTyrian.RESOURCES);

        // Resolution
        final Resolution output = new Resolution(640, 400, 60);

        // Configuration
        final Config config = new Config(output, 16, true);

        // Loader
        final Loader loader = new Loader(config);
        loader.start(new Scene(loader));
    }

    /**
     * Private constructor.
     */
    private AppTyrian()
    {
        throw new RuntimeException();
    }
}
