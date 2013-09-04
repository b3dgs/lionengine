package com.b3dgs.lionengine.game.effect;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.HandlerGame;

/**
 * Handle effects.
 * 
 * @param <E> The effect type used.
 */
public abstract class HandlerEffectGame<E extends EffectGame>
        extends HandlerGame<Integer, E>
{
    /**
     * Create a new handler.
     */
    public HandlerEffectGame()
    {
        super();
    }

    /**
     * Main routine, which has to be called in main game loop.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        updateAdd();
        for (final E effect : list())
        {
            effect.update(extrp);
            if (effect.isDestroyed())
            {
                remove(effect);
            }
        }
        updateRemove();
    }

    /**
     * Render the effects.
     * 
     * @param g The graphics output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraGame camera)
    {
        for (final E effect : list())
        {
            effect.render(g, camera);
        }
    }

    /*
     * HandlerGame
     */

    @Override
    protected Integer getKey(E object)
    {
        return object.getId();
    }
}
