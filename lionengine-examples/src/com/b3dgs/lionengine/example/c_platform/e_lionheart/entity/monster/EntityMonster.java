package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.TypeEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityMover;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;

/**
 * Monster base implementation.
 */
public class EntityMonster
        extends EntityMover
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param type The entity type.
     * @param effect The effect type.
     */
    public EntityMonster(Context context, TypeEntity type, TypeEffect effect)
    {
        super(context, type);
        play(getAnimation(status.getState().getAnimationName()));
    }

    @Override
    protected void updateActions()
    {
        // Nothing to do
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
        destroy();
    }

    @Override
    protected void updateAnimations(double extrp)
    {
        // Nothing to do
    }

}
