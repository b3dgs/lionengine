package com.b3dgs.lionengine.example.a_firstcode;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Theme;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;

// Tutorial: First code
// This tutorial will highlight the minimum required code to initialize properly the engine.
// Each steps are explained, in order to understand what they mean.
// A sequence sample (Scene) will also show how to use the main routine.

/**
 * Program starts here. When you start the jvm, ensure that this main function is called.
 */
public final class AppFirstCode
{
    /**
     * Main function called by the jvm.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        // Start engine (name = "First Code", version = "1.0.0", resources directory = "resources")
        // The Engine is initialized with our parameters:
        // - The name of our program: "First Code"
        // - Our program version: "1.0.0"
        // - The main resources directory, relative to the execution directory: ./resources/
        // This mean that any resources loaded with Media.get(...) will have this directory as prefix
        // - The verbose level
        // - The swing theme (general java appearance)
        Engine.start("First Code", Version.create(1, 0, 0), "resources", Verbose.CRITICAL, Theme.SYSTEM);

        // Configuration reference (native size = 320*240*16 at 60fps)
        // This mean that our native resolution is in 320*240
        // These data are used in case of rendering scaling, if the desired output is different
        // The last value is used to perform the frame rate calculation, corresponding to the native frame rate
        final Display internal = new Display(320, 240, 16, 60);

        // Display configuration (desired = 640*480*16 at 60fps)
        // This is corresponding to the output configuration
        // As our native is in 320*240, the output will be scaled by 2
        // If the current frame rate is lower, the extrapolation value will allow to compensate any data calculation
        final Display external = new Display(640, 480, 16, 60);

        // Final configuration (rendering will be scaled by 2 considering native and desired config)
        // This is the final configuration container, including window mode
        final Config config = new Config(internal, external, true);

        // Program starter, the main thread, setup with our configuration
        // It just needs one sequence reference to start
        final Loader loader = new Loader(config);
        loader.start(new Scene(loader));
    }

    /**
     * Private constructor.
     */
    private AppFirstCode()
    {
        throw new RuntimeException();
    }
}
