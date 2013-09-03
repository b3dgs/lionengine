package com.b3dgs.lionengine.example.tilecollision;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;

// Tutorial: TileCollision
// This tutorial will show how to make a simple tile collision.

/**
 * Program starts here.
 */
public final class AppTileCollision
{
    /**
     * Main function called by the jvm.
     * 
     * @param argv The arguments.
     */
    public static void main(String[] argv)
    {
        // Start engine
        Engine.start("Tile Collision", Version.create(1, 0, 0), Media.getPath("resources", "platform", "mario"));

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
    private AppTileCollision()
    {
        // Nothing to do
    }
}
