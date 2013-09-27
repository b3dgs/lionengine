package com.b3dgs.lionengine.example.b_loaddraw;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

// Tutorial: Load draw
// This tutorial will explain how to use the different kind of images and sprites.
// It will also show how to load external resources from a simple file name.

/**
 * Program starts here.
 */
public final class AppLoadDraw
{
    /**
     * Main function.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        // Start engine
        Engine.start("Load Draw", Version.create(1, 0, 0), Media.getPath("resources", "loaddraw"));

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
    private AppLoadDraw()
    {
        throw new RuntimeException();
    }
}
