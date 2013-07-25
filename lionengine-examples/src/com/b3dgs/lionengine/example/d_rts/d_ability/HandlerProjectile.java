package com.b3dgs.lionengine.example.d_rts.d_ability;

import com.b3dgs.lionengine.example.d_rts.d_ability.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.d_ability.projectile.Projectile;
import com.b3dgs.lionengine.game.projectile.HandlerProjectileGame;

/**
 * Projectile handler implementation.
 */
public final class HandlerProjectile
        extends HandlerProjectileGame<Entity, Projectile>
{
    /**
     * Constructor.
     * 
     * @param handlerEntity The entity handler reference.
     */
    HandlerProjectile(HandlerEntity handlerEntity)
    {
        super(handlerEntity);
    }
}
