package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.Effect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.FactoryEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.HandlerEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.TypeEffect;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.game.entity.SetupEntityGame;

/**
 * Abstract implementation of an item.
 */
public abstract class EntityItem
        extends Entity
{
    /** Effect factory. */
    private final FactoryEffect factoryEffect;
    /** Effect handler. */
    private final HandlerEffect handlerEffect;

    /**
     * Constructor.
     * 
     * @param type The entity type.
     * @param setup The setup reference.
     * @param map The map reference.
     * @param desiredFps The desired fps.
     * @param factoryEffect The effect factory.
     * @param handlerEffect The effect handler.
     * @param effect The effect type.
     */
    EntityItem(TypeEntity type, SetupEntityGame setup, Map map, int desiredFps, FactoryEffect factoryEffect,
            HandlerEffect handlerEffect, TypeEffect effect)
    {
        super(type, setup, map, desiredFps);
        this.factoryEffect = factoryEffect;
        this.handlerEffect = handlerEffect;
        play(getAnimation(status.getState().getAnimationName()));
    }

    /**
     * Called when the item is taken by the entity.
     * 
     * @param entity The entity.
     */
    private void onTaken(Entity entity)
    {
        final Effect effect = factoryEffect.createEffect(TypeEffect.TAKEN);
        effect.start((int) dieLocation.getX(), (int) dieLocation.getY());
        handlerEffect.add(effect);
    }

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
        kill();
        onTaken(entity);
        destroy();
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
