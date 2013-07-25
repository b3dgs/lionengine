package com.b3dgs.lionengine.example.c_platform.c_entitymap;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;

// Tutorial: Platform Entity Map
// This tutorial explain how to put an entity on a map, and how to handle the collisions.
// It will also show how to use the scrolling effect using the player reference.

/**
 * Program starts here.
 */
public final class AppPlatformEntityMap
{
    /**
     * Private constructor.
     */
    private AppPlatformEntityMap()
    {
        throw new RuntimeException();
    }

    /**
     * Main function.
     * 
     * @param args The arguments.
     */
    public static void main(String args[])
    {
        // Start engine
        Engine.start("Entity on map", Version.create(1, 0, 0), Media.getPath("resources", "platform"));

        // Displays
        final Display internal = new Display(320, 240, 16, 60);
        final Display external = new Display(640, 480, 16, 60);

        // Configuration
        final Config config = new Config(internal, external, true);

        // Loader
        final Loader loader = new Loader(config);
        loader.start(new Scene(loader));
    }
}
