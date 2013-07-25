package com.b3dgs.lionengine.example.c_platform.e_lionheart;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;

// Tutorial: Lionheart
// This last platform tutorial will show a quick game example to show as much things as possible.

/**
 * Program starts here.
 */
public class AppLionheart
{
    /**
     * Private constructor.
     */
    private AppLionheart()
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
        Engine.start("Lionheart", Version.create(1, 0, 0), Media.getPath("resources", "lionheart"));

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
