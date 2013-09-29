package com.b3dgs.lionengine.example.e_shmup.c_tyrian.weapon;

import com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.Entity;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.ProjectileType;

/**
 * Pulse cannon implementation.
 */
final class PulseCannon
        extends Weapon
{
    /**
     * @see Weapon#Weapon(FactoryProjectile, HandlerProjectile)
     */
    PulseCannon(FactoryProjectile factory, HandlerProjectile handler)
    {
        super(factory, handler);
        setRate(100);
    }

    /*
     * Weapon
     */

    @Override
    protected void launchProjectile(Entity owner)
    {
        addProjectile(ProjectileType.BULLET, 1, 1, 0, 5, 0, -3);

        addProjectile(ProjectileType.BULLET, 1, 77, -5, -5, -5, -18);
        addProjectile(ProjectileType.BULLET, 1, 78, 5, -5, 5, -18);
    }
}
