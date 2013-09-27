package com.b3dgs.lionengine.example.pong;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

// Tutorial: Pong
// This tutorial will show how to make a simple pong.

/**
 * Program starts here.
 */
public final class AppPong
{
    /**
     * Main function called by the jvm.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        // Start engine
        Engine.start("Pong", Version.create(1, 0, 0), "resources");

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
    private AppPong()
    {
        throw new RuntimeException();
    }
}
