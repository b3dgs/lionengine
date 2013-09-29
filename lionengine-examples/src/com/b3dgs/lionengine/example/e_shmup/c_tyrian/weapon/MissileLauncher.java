package com.b3dgs.lionengine.example.e_shmup.c_tyrian.weapon;

import com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.Entity;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.ProjectileType;

/**
 * Pulse cannon implementation.
 */
final class MissileLauncher
        extends Weapon
{
    /**
     * @see Weapon#Weapon(FactoryProjectile, HandlerProjectile)
     */
    MissileLauncher(FactoryProjectile factory, HandlerProjectile handler)
    {
        super(factory, handler);
        setRate(400);
    }

    /*
     * Weapon
     */

    @Override
    protected void launchProjectile(Entity owner)
    {
        addProjectile(ProjectileType.MISSILE, 1, 136, -4.5, 4.5, -10, -10);
        addProjectile(ProjectileType.MISSILE, 1, 151, 4.5, 4.5, 10, -10);

        addProjectile(ProjectileType.MISSILE, 1, 136, -5, 3.75, -10, -12);
        addProjectile(ProjectileType.MISSILE, 1, 151, 5, 3.75, 10, -12);

        addProjectile(ProjectileType.MISSILE, 1, 136, -6, 3.5, -10, -14);
        addProjectile(ProjectileType.MISSILE, 1, 151, 6, 3.5, 10, -14);
    }
}
