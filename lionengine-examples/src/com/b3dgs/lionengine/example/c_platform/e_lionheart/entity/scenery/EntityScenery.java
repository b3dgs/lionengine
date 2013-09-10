package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityMover;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;

/**
 * Entity scenery base implementation.
 */
public class EntityScenery
        extends Entity
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param type The entity type.
     */
    EntityScenery(Context context, TypeEntity type)
    {
        super(context, type);
    }

    /*
     * Entity
     */

    @Override
    public void updateCollision(int x, int y, int width, int height)
    {
        super.updateCollision(x, y + sprite.getFrameHeight() - height - 6, width, height);
    }

    @Override
    public void hitBy(Entity entity)
    {
        // Nothing to do
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (entity instanceof EntityMover)
        {
            final EntityMover mover = (EntityMover) entity;
            if (!mover.isJumping())
            {
                mover.checkCollisionVertical(Double.valueOf(getLocationY() + sprite.getFrameHeight() + getHeight() - 8));
            }
        }
        if (entity instanceof Valdyn)
        {
            final Valdyn valdyn = (Valdyn) entity;
            if (valdyn.getLocationIntX() < getLocationIntX() + Valdyn.TILE_EXTREMITY_WIDTH)
            {
                valdyn.updateExtremity(true);
            }
            else if (valdyn.getLocationIntX() - getWidth() / 2 > getLocationIntX() + getWidth()
                    - Valdyn.TILE_EXTREMITY_WIDTH)
            {
                valdyn.updateExtremity(false);
            }
        }
    }

    @Override
    protected void updateStates()
    {
        // Nothing to do
    }

    @Override
    protected void updateDead()
    {
        // Nothing to do
    }

    @Override
    protected void updateCollisions()
    {
        // Nothing to do
    }

    @Override
    protected void updateAnimations(double extrp)
    {
        // Nothing to do
    }
}
