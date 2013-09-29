package com.b3dgs.lionengine.example.e_shmup.c_tyrian.weapon;

import com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.Entity;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.Projectile;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.ProjectileType;
import com.b3dgs.lionengine.game.projectile.LauncherProjectileGame;

/**
 * Weapon base implementation.
 */
public abstract class Weapon
        extends LauncherProjectileGame<ProjectileType, Entity, Entity, Projectile>
{
    /**
     * Constructor.
     * 
     * @param factory The factory reference.
     * @param handler The handler reference.
     */
    public Weapon(FactoryProjectile factory, HandlerProjectile handler)
    {
        super(factory, handler);
    }

    /*
     * LauncherProjectileGame
     */

    @Override
    protected void launchProjectile(Entity owner, Entity target)
    {
        // Nothing to do
    }
}
