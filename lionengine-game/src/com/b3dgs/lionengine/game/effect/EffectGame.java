package com.b3dgs.lionengine.game.effect;

import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Represents an effect.
 */
public abstract class EffectGame
        extends ConfigurableModel
{
    /** Id used. */
    private static final Set<Integer> IDS = new HashSet<>(16);
    /** Last id used. */
    private static int lastId = 1;

    /**
     * Get the next unused id.
     * 
     * @return The next unused id.
     */
    private static Integer getFreeId()
    {
        while (EffectGame.IDS.contains(Integer.valueOf(EffectGame.lastId)))
        {
            EffectGame.lastId++;
        }
        return Integer.valueOf(EffectGame.lastId);
    }

    /** Effect id. */
    private final Integer id;
    /** Destroyed flag; true will remove it from the handler. */
    private boolean destroy;

    /**
     * Constructor.
     */
    public EffectGame()
    {
        this(null);
    }

    /**
     * Create a new effect from an existing configuration. The configuration will be shared; this will reduce memory
     * usage.
     * 
     * @param configurable The configuration reference.
     */
    public EffectGame(Configurable configurable)
    {
        super(configurable);
        destroy = false;
        id = EffectGame.getFreeId();
        EffectGame.IDS.add(id);
    }

    /**
     * Update the effect.
     * 
     * @param extrp The extrapolation value.
     */
    public abstract void update(double extrp);

    /**
     * Render the effect.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public abstract void render(Graphic g, CameraGame camera);

    /**
     * Get the entity id (unique).
     * 
     * @return The entity id.
     */
    public final Integer getId()
    {
        return id;
    }

    /**
     * Remove entity from handler, and free memory.
     */
    public void destroy()
    {
        destroy = true;
        EffectGame.IDS.remove(getId());
    }

    /**
     * Check if entity is going to be removed.
     * 
     * @return <code>true</code> if going to be removed, <code>false</code> else.
     */
    public boolean isDestroyed()
    {
        return destroy;
    }
}
