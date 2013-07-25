package com.b3dgs.lionengine.example.c_platform.a_navmaptile;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;

// Tutorial: Platform map tile navigation
// This tutorial will show how to load a map tile and navigate inside it using standard inputs.
// It will also explain how to use the WorldGame class and the LevelRipConverter tool.

/**
 * Program starts here.
 */
public final class AppPlatformNavMapTile
{
    /**
     * Private constructor.
     */
    private AppPlatformNavMapTile()
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
        Engine.start("Platform Map tile navigation", Version.create(1, 0, 0), Media.getPath("resources", "platform"));

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
