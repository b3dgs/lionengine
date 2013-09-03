package com.b3dgs.lionengine.example.d_rts.b_cursor;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;

// Tutorial: Rts Cursor
// This tutorial will show how to use the rts cursor, in order to interact with the map.
// The following elements are used:
// - MapTileRts
// - TileRts
// - CameraRts
// - CursorRts

/**
 * Program starts here.
 */
public final class AppRtsCursor
{
    /** Program title. */
    public static final String PROGRAM = "Rts Cursor";
    /** Program version. */
    public static final Version VERSION = Version.create(1, 0, 0);
    /** Program path. */
    public static final String PATH = Media.getPath("resources", "rts");

    /**
     * Main function.
     * 
     * @param args The arguments.
     */
    public static void main(String[] args)
    {
        // Start engine
        Engine.start(AppRtsCursor.PROGRAM, AppRtsCursor.VERSION, AppRtsCursor.PATH);

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
    private AppRtsCursor()
    {
        throw new RuntimeException();
    }
}
