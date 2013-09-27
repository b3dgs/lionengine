package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.EffectType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.FactoryEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
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
     * @param level The level reference.
     * @param type The entity type.
     * @param effect The effect type.
     */
    public EntityItem(Level level, EntityType type, EffectType effect)
    {
        super(level, type);
        factoryEffect = level.factoryEffect;
        play(getDataAnimation(status.getState().getAnimationName()));
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
        if (!isDead())
        {
            kill();
            onTaken((Valdyn) entity);
        }
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
        factoryEffect
                .startEffect(EffectType.TAKEN, (int) dieLocation.getX() - getWidth() / 2, (int) dieLocation.getY());
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
