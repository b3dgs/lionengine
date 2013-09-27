package com.b3dgs.lionengine.example.c_platform.b_entitycontrol;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

// Tutorial: Platform Entity Control
// This tutorial will show how to manage the different state of a controlled entity using keyboard.
// It will also explain how to apply animations depending of its state.

/**
 * Program starts here.
 */
public final class AppPlatformEntityControl
{
    /** Application name. */
    public static final String NAME = "Entity Control";
    /** Application version. */
    public static final Version VERSION = Version.create(1, 0, 0);
    /** Resources directory. */
    private static final String RESOURCES = Media.getPath("resources", "platform", "mario");

    /**
     * Main function.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        // Start engine
        Engine.start(AppPlatformEntityControl.NAME, AppPlatformEntityControl.VERSION,
                AppPlatformEntityControl.RESOURCES);

        // Resolution
        final Resolution output = new Resolution(640, 480, 60);

        // Configuration
        final Config config = new Config(output, 16, true);

        // Loader
        final Loader loader = new Loader(config);
        loader.start(new Scene(loader));
    }

    /**
     * Private constructor.
     */
    private AppPlatformEntityControl()
    {
        throw new RuntimeException();
    }
}
