package com.b3dgs.lionengine.game.effect;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.HandlerGame;

/**
 * Handle effects.
 * 
 * @param <E> The effect type used.
 */
public class HandlerEffectGame<E extends EffectGame>
        extends HandlerGame<Integer, E>
{
    /** Camera reference. */
    private final CameraGame camera;
    
    /**
     * Create a new handler.
     * 
     * @param camera The camera reference.
     */
    public HandlerEffectGame(CameraGame camera)
    {
        super();
        this.camera = camera;
    }

    /*
     * HandlerGame
     */
    
    @Override
    protected void update(double extrp, E effect)
    {
        effect.update(extrp);
        if (effect.isDestroyed())
        {
            remove(effect);
        }
    }

    @Override
    protected void render(Graphic g, E effect)
    {
        if (camera.isVisible(effect))
        {
            effect.render(g, camera);
        }
    }

    @Override
    protected Integer getKey(E object)
    {
        return object.getId();
    }
}
