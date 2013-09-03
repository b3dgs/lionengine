package com.b3dgs.lionengine.example.d_rts.e_skills;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Version;

// Tutorial: Rts Skills
// This tutorial will show how to link the different entities's ability to the control panel.
// It also explains how to use the Skill class, combined with its factory.

/**
 * Program starts here.
 */
public final class AppRtsSkills
{
    /** Program title. */
    public static final String PROGRAM = "Rts Skills";
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
        Engine.start(AppRtsSkills.PROGRAM, AppRtsSkills.VERSION, AppRtsSkills.PATH);

        // Displays
        final Display internal = new Display(320, 200, 16, 60);
        final Display external = new Display(640, 400, 16, 60);

        // Configuration
        final Config config = new Config(internal, external, true);

        // Loader
        final Loader loader = new Loader(config);
        loader.start(new Scene(loader));
    }

    /**
     * Private constructor.
     */
    private AppRtsSkills()
    {
        throw new RuntimeException();
    }
}
