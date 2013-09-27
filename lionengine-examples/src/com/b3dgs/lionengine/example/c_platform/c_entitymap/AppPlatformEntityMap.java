package com.b3dgs.lionengine.example.c_platform.c_entitymap;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

// Tutorial: Platform Entity Map
// This tutorial explain how to put an entity on a map, and how to handle the collisions.
// It will also show how to use the scrolling effect using the player reference.

/**
 * Program starts here.
 */
public final class AppPlatformEntityMap
{
    /** Application name. */
    public static final String NAME = "Entity on Map";
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
        Engine.start(AppPlatformEntityMap.NAME, AppPlatformEntityMap.VERSION, AppPlatformEntityMap.RESOURCES);

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
    private AppPlatformEntityMap()
    {
        throw new RuntimeException();
    }
}
