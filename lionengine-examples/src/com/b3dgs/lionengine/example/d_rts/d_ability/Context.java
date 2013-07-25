package com.b3dgs.lionengine.example.d_rts.d_ability;

import com.b3dgs.lionengine.example.d_rts.d_ability.entity.FactoryEntity;
import com.b3dgs.lionengine.example.d_rts.d_ability.projectile.FactoryProjectile;
import com.b3dgs.lionengine.example.d_rts.d_ability.weapon.FactoryWeapon;

/**
 * Context container.
 */
public final class Context
{
    /** The map reference. */
    public final Map map;
    /** The factory reference. */
    public final FactoryEntity factoryEntity;
    /** The factory reference. */
    public final FactoryProjectile factoryProjectile;
    /** The factory weapon. */
    public final FactoryWeapon factoryWeapon;
    /** The handler entity reference. */
    public final HandlerEntity handlerEntity;
    /** The handler projectile reference. */
    public final HandlerProjectile handlerProjectile;
    /** The desired fps. */
    public final int desiredFps;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     * @param handlerEntity The handler entity reference.
     * @param handlerProjectile The handler arrow reference.
     * @param desiredFps The the desired fps.
     */
    Context(Map map, HandlerEntity handlerEntity, HandlerProjectile handlerProjectile, int desiredFps)
    {
        this.map = map;
        this.handlerEntity = handlerEntity;
        this.handlerProjectile = handlerProjectile;
        this.desiredFps = desiredFps;
        factoryEntity = new FactoryEntity();
        factoryProjectile = new FactoryProjectile();
        factoryWeapon = new FactoryWeapon();
    }

    /**
     * Assign context to factories.
     */
    public void assignContext()
    {
        factoryEntity.setContext(this);
        factoryWeapon.setContext(this);
    }
}
