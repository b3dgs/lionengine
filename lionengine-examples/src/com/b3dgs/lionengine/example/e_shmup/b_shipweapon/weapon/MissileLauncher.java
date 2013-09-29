package com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon;

import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.ProjectileType;
import com.b3dgs.lionengine.game.entity.EntityGame;

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
    protected void launchProjectile(EntityGame owner)
    {
        addProjectile(ProjectileType.MISSILE, 1, 136, -4, 4, -10, -10);
        addProjectile(ProjectileType.MISSILE, 1, 151, 4, 4, 10, -10);
    }
}
