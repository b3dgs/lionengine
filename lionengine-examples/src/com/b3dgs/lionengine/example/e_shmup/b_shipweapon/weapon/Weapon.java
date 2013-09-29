package com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon;

import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.Projectile;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile.ProjectileType;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.projectile.LauncherProjectileGame;

/**
 * Weapon base implementation.
 */
public abstract class Weapon
        extends LauncherProjectileGame<ProjectileType, EntityGame, EntityGame, Projectile>
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
    protected void launchProjectile(EntityGame owner, EntityGame target)
    {
        // Nothing to do
    }
}
