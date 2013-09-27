package com.b3dgs.lionengine.example.d_rts.a_navmaptile;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

// Tutorial: Rts Navigation MapTile
// This tutorial will show how to navigate inside an rts oriented MapTile.
// The following elements are used:
// - MapTileRts
// - TileRts
// - CameraRts

/**
 * Program starts here.
 */
public final class AppRtsNavMapTile
{
    /** Program title. */
    public static final String PROGRAM = "Rts Map tile navigation";
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
        Engine.start(AppRtsNavMapTile.PROGRAM, AppRtsNavMapTile.VERSION, AppRtsNavMapTile.PATH);

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
    private AppRtsNavMapTile()
    {
        throw new RuntimeException();
    }
}
