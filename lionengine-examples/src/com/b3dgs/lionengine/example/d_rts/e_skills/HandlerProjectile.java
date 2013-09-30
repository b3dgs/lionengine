package com.b3dgs.lionengine.example.d_rts.e_skills;

import com.b3dgs.lionengine.example.d_rts.e_skills.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.e_skills.projectile.Projectile;
import com.b3dgs.lionengine.game.projectile.HandlerProjectileGame;
import com.b3dgs.lionengine.game.rts.CameraRts;

/**
 * Projectile handler implementation.
 */
public final class HandlerProjectile
        extends HandlerProjectileGame<Entity, Projectile>
{
    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     * @param handlerEntity The entity handler reference.
     */
    HandlerProjectile(CameraRts camera, HandlerEntity handlerEntity)
    {
        super(camera, handlerEntity);
    }
}
