package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;

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

    @Override
    public void hitBy(Entity entity)
    {
        // Nothing to do
    }

    @Override
    public void hitThat(Entity entity)
    {
        // Nothing to do
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
