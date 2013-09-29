package com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon;

import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.ProjectileType;
import com.b3dgs.lionengine.game.entity.EntityGame;

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
    protected void launchProjectile(EntityGame owner)
    {
        addProjectile(ProjectileType.BULLET, 1, 1, 0, 5, 0, -3);

        addProjectile(ProjectileType.BULLET, 1, 77, -5, -5, -5, -18);
        addProjectile(ProjectileType.BULLET, 1, 78, 5, -5, 5, -18);
    }
}
