package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.game.SetupEntityGame;

/**
 * Abstract implementation of an item.
 */
public class EntityItem
        extends Entity
{
    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param map The map reference.
     * @param desiredFps The desired fps.
     */
    public EntityItem(SetupEntityGame setup, Map map, int desiredFps)
    {
        super(setup, map, desiredFps);
        play(getAnimation(status.getState().getAnimationName()));
    }

    @Override
    public void hitBy(Entity entity)
    {
        // Nothing to do
    }

    @Override
    public void hitThat(Entity entity)
    {
        kill();
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
