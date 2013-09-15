package com.b3dgs.lionengine.example.f_network;

import java.util.HashMap;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.HandlerEntityPlatform;

/**
 * Handler implementation. All of our entity will be handled here.
 */
class HandlerEntity
        extends HandlerEntityPlatform<Entity>
{
    /** The mario clients. */
    private final HashMap<Byte, Mario> marioClients;

    /**
     * Standard constructor.
     * 
     * @param marioClients The clients.
     */
    public HandlerEntity(HashMap<Byte, Mario> marioClients)
    {
        super();
        this.marioClients = marioClients;
    }

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
    protected void updatingEntity(Entity entity, double extrp)
    {
        for (final Mario mario : marioClients.values())
        {
            if (!(mario.isDead() || entity.isDead()) && entity.collide(mario))
            {
                // Mario hit entity if coming from its top
                if (mario.getLocationY() - 10 > entity.getLocationY())
                {
                    entity.onHurtBy(mario, 0);
                    mario.onHitThat(entity);
                }
                else
                {
                    mario.onHurtBy(entity, 0);
                    entity.onHitThat(mario);
                }
            }
        }
    }

    @Override
    protected void renderingEntity(Entity entity, Graphic g, CameraPlatform camera)
    {
        // Nothing to do
    }
}
