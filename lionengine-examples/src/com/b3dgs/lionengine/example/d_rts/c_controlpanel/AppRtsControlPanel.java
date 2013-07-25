package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;

// Tutorial: Rts Control Panel
// This tutorial will explain the basics of the control panel, in order to interact with entities.
// It will also show how to use the factory.
// The following elements are used:
// - MapTileRts
// - TileRts
// - CameraRts
// - CursorRts
// - ControlPanelModel
// - FactoryEntityGame
// - HandlerEntityRts
// - EntityRts

/**
 * Program starts here.
 */
public final class AppRtsControlPanel
{
    /** Program title. */
    public static final String PROGRAM = "Rts Control panel";
    /** Program version. */
    public static final Version VERSION = Version.create(1, 0, 0);
    /** Program path. */
    public static final String PATH = Media.getPath("resources", "rts");

    /**
     * Private constructor.
     */
    private AppRtsControlPanel()
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
        Engine.start(AppRtsControlPanel.PROGRAM, AppRtsControlPanel.VERSION, AppRtsControlPanel.PATH);

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
