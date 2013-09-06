package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Context;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.FactoryEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.TypeEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;

/**
 * Abstract implementation of an item.
 */
public abstract class EntityItem
        extends Entity
{
    /** Effect factory. */
    private final FactoryEffect factoryEffect;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     * @param type The entity type.
     * @param effect The effect type.
     */
    public EntityItem(Context context, TypeEntity type, TypeEffect effect)
    {
        super(context, type);
        factoryEffect = context.factoryEffect;
        play(getAnimation(status.getState().getAnimationName()));
    }

    /**
     * Called when the item is taken by the entity.
     * 
     * @param entity The entity.
     */
    protected abstract void onTaken(Valdyn entity);

    /*
     * Entity
     */

    @Override
    public void hitBy(Entity entity)
    {
        // Nothing to do
    }

    @Override
    public void hitThat(Entity entity)
    {
        if (!isDead())
        {
            kill();
            onTaken((Valdyn) entity);
            // TODO: Play taken sound
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
        factoryEffect.startEffect(TypeEffect.TAKEN, (int) dieLocation.getX(), (int) dieLocation.getY());
        destroy();
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
