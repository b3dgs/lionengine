package com.b3dgs.lionengine.example.d_rts.d_ability;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

// Tutorial: Rts Ability
// This tutorial will show the different kind of ability:
// - Mover (entity that can use pathfinding to move in the map)
// - Producer (produce new entity, build new building)
// - Attacker (attack another entity, melee / distance)
// - Extractor (extract resources with quantity management)
//
// It will also show the basics of weapons / launcher / projectile.

/**
 * Program starts here.
 */
public final class AppRtsAbility
{
    /** Program title. */
    public static final String PROGRAM = "Rts Ability";
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
        Engine.start(AppRtsAbility.PROGRAM, AppRtsAbility.VERSION, AppRtsAbility.PATH);

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
    private AppRtsAbility()
    {
        throw new RuntimeException();
    }
}
