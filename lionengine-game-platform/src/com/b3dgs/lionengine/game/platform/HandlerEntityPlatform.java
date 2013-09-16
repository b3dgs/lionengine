package com.b3dgs.lionengine.game.platform;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;

/**
 * Default platform entity handler.
 * 
 * @param <E> Entity type used.
 */
public abstract class HandlerEntityPlatform<E extends EntityPlatform>
        extends HandlerEntityGame<E>
{
    /**
     * Create a new handler.
     */
    public HandlerEntityPlatform()
    {
        super();
    }

    /**
     * Check if entity can be updated.
     * 
     * @param entity The entity to check.
     * @return <code>true</code> if can be updated, <code>false</code> else.
     */
    protected abstract boolean canUpdateEntity(E entity);

    /**
     * Check if entity can be rendered.
     * 
     * @param entity The entity to check.
     * @return <code>true</code> if can be rendered, <code>false</code> else.
     */
    protected abstract boolean canRenderEntity(E entity);

    /**
     * Update this entity ({@link EntityPlatform#update(double)} is already called before).
     * 
     * @param entity The current updating entity.
     * @param extrp The extrapolation value.
     */
    protected abstract void updatingEntity(E entity, double extrp);

    /**
     * Render this entity ({@link EntityPlatform#render(Graphic, CameraPlatform)} is already called before).
     * 
     * @param entity The current rendering entity.
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    protected abstract void renderingEntity(E entity, Graphic g, CameraPlatform camera);

    /**
     * Update all entities.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        // Add entities
        updateAdd();

        // Update
        for (final E entity : list())
        {
            if (canUpdateEntity(entity))
            {
                entity.update(extrp);
                updatingEntity(entity, extrp);
            }
            if (entity.isDestroyed())
            {
                remove(entity);
            }
        }

        // Delete
        updateRemove();
    }

    /**
     * Render all entities.
     * 
     * @param g The graphics output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraPlatform camera)
    {
        for (final E entity : list())
        {
            if (canRenderEntity(entity))
            {
                entity.render(g, camera);
                renderingEntity(entity, g, camera);
            }
        }
    }
}
