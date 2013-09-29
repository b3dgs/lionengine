package com.b3dgs.lionengine.example.e_shmup.b_shipweapon;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

// Tutorial: Shmup Ship Weapon
// This tutorial shows how to handle the weapon for a ship controlled by the player.

/**
 * Program starts here.
 */
public final class AppShmupShipWeapon
{
    /** Application name. */
    public static final String NAME = "Shmup Ship Weapon";
    /** Application version. */
    public static final Version VERSION = Version.create(1, 0, 0);
    /** Resources directory. */
    private static final String RESOURCES = Media.getPath("resources", "shmup");

    /**
     * Main function.
     * 
     * @param argv The arguments.
     */
    public static void main(String[] argv)
    {
        // Start engine
        Engine.start(AppShmupShipWeapon.NAME, AppShmupShipWeapon.VERSION, AppShmupShipWeapon.RESOURCES);

        // Resolution
        final Resolution output = new Resolution(640, 400, 60);

        // Configuration
        final Config config = new Config(output, 16, true);

        // Loader
        final Loader loader = new Loader(config);
        loader.start(new Scene(loader));
    }

    /**
     * Private constructor.
     */
    private AppShmupShipWeapon()
    {
        throw new RuntimeException();
    }
}
