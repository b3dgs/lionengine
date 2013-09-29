package com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile;

import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Simple projectile, with a linear movement.
 */
final class Bullet
        extends Projectile
{
    /**
     * @see Projectile#Projectile(SetupSurfaceGame, int, int)
     */
    public Bullet(SetupSurfaceGame setup, int id, int frame)
    {
        super(setup, id, frame);
    }
}
