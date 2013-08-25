package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.menu.Menu;

// Tutorial: Warcraft
// This last rts tutorial shows a prototype of a Warcraft, using as much things as possible.

/**
 * Program starts here.
 */
public final class AppWarcraft
{
    /** Program title. */
    public static final String PROGRAM = "Warcraft Remake";
    /** Program version. */
    public static final Version VERSION = Version.create(1, 0, 0);
    /** Program path. */
    public static final String PATH = Media.getPath("resources", "rts");

    /**
     * Main function.
     * 
     * @param args The arguments.
     */
    public static void main(String args[])
    {
        Engine.start(AppWarcraft.PROGRAM, AppWarcraft.VERSION, AppWarcraft.PATH);

        // Configuration
        final Display internal = new Display(320, 200, 16, 60);
        final Display external = new Display(640, 400, 16, 60);
        final Config config = new Config(internal, external, true);

        // Starter
        final Loader loader = new Loader(config);
        ResourcesLoader.load();
        loader.start(new Menu(loader));
    }

    /**
     * Private constructor.
     */
    private AppWarcraft()
    {
        throw new RuntimeException();
    }
}
