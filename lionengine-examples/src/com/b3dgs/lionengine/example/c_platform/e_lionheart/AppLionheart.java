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
public final class AppLionheart
{
    /** Application name. */
    public static final String NAME = "Lionheart";
    /** Application version. */
    public static final Version VERSION = Version.create(1, 0, 0);
    /** Resources directory. */
    private static final String RESOURCES = Media.getPath("resources", "lionheart");
    /** Native display. */
    private static final Display NATIVE_DISPLAY = new Display(320, 240, 16, 60);

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
        Engine.start(AppLionheart.NAME, AppLionheart.VERSION, AppLionheart.RESOURCES);

        final Display external = new Display(640, 480, 16, 60);
        final Config config = new Config(AppLionheart.NATIVE_DISPLAY, external, true);
        final Loader loader = new Loader(config);

        loader.start(new Scene(loader));
    }
}
