package com.b3dgs.lionengine.example.c_platform.d_opponent;

import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.HandlerEntityPlatform;

/**
 * Handler implementation. All of our entity will be handled here.
 */
final class HandlerEntity
        extends HandlerEntityPlatform<Entity>
{
    /** Mario reference. */
    private final Mario mario;

    /**
     * Standard constructor.
     * 
     * @param mario The mario reference.
     */
    HandlerEntity(Mario mario)
    {
        super();
        this.mario = mario;
    }

    /*
     * HandlerEntityPlatform
     */

    @Override
    protected boolean canUpdateEntity(Entity entity)
    {
        return true;
    }

    @Override
    protected boolean canRenderEntity(Entity entity)
    {
        return true;
    }

    @Override
    protected void updatingEntity(Entity entity)
    {
        if (!(mario.isDead() || entity.isDead()) && entity.collide(mario))
        {
            // Mario hit entity if coming from its top
            if (mario.isFalling() && mario.getLocationOldY() > entity.getLocationOldY())
            {
                entity.onHurtBy(mario);
                mario.onHitThat(entity);
            }
            else
            {
                mario.onHurtBy(entity);
                entity.onHitThat(mario);
            }
        }
    }

    @Override
    protected void renderingEntity(Entity entity, CameraPlatform camera)
    {
        // Nothing to do
    }
}
