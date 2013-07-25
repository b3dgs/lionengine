package com.b3dgs.lionengine.example.d_rts.d_ability.projectile;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeProjectile;
import com.b3dgs.lionengine.example.d_rts.d_ability.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.d_ability.weapon.Weapon;
import com.b3dgs.lionengine.game.projectile.LauncherProjectileGame;

/**
 * Launcher base implementation.
 */
public final class LauncherProjectile
        extends LauncherProjectileGame<TypeProjectile, Entity, Weapon, Projectile>
{
    /** Type projectile. */
    private final TypeProjectile type;
    /** The projectile frame. */
    private int frame;

    /**
     * Constructor.
     * 
     * @param type The projectile type.
     * @param context The context factory.
     */
    public LauncherProjectile(TypeProjectile type, Context context)
    {
        super(context.factoryProjectile, context.handlerProjectile);
        this.type = type;
    }

    /**
     * Set the projectile frame.
     * 
     * @param frame The projectile frame.
     */
    public void setFrame(int frame)
    {
        this.frame = frame;
    }

    @Override
    protected void launchProjectile(Weapon owner)
    {
        // Nothing to do
    }

    @Override
    protected void launchProjectile(Weapon owner, Entity target)
    {
        addProjectile(type, owner.getAttackDamages(), frame, target, 2.5, 0, 0);
    }
}
