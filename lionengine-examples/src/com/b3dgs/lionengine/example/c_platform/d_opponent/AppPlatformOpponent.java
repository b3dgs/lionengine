package com.b3dgs.lionengine.example.c_platform.d_opponent;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;

// Tutorial: Platform Opponent
// This tutorial will show how to handle multiple entity with different types.
// It will also show how to make them interact with the player.

/**
 * Program starts here.
 */
public class AppPlatformOpponent
{
    /**
     * Main function.
     * 
     * @param argv The arguments.
     */
    public static void main(String argv[])
    {
        // Start engine
        Engine.start("Opponent", Version.create(1, 0, 0), Media.getPath("resources", "platform"));

        // Displays
        final Display internal = new Display(320, 240, 16, 60);
        final Display external = new Display(640, 480, 16, 60);

        // Configuration
        final Config config = new Config(internal, external, true);

        // Loader
        final Loader loader = new Loader(config);
        loader.start(new Scene(loader));
    }

    /**
     * Private constructor.
     */
    private AppPlatformOpponent()
    {
        throw new RuntimeException();
    }
}
