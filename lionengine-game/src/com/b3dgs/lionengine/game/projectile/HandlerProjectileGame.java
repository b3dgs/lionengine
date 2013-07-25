package com.b3dgs.lionengine.game.projectile;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;

/**
 * Handle all ship projectiles (the ship represents the main player). Entity projectiles are handled in a separate
 * handler in order to improve performances, avoiding useless checks.
 * 
 * @param <E> The entity type used.
 * @param <P> The projectile type used.
 */
public class HandlerProjectileGame<E extends EntityGame, P extends ProjectileGame<E, ?>>
        extends HandlerEntityGame<P>
{
    /** The entity handler reference. */
    private final HandlerEntityGame<E> handlerEntity;

    /**
     * Create a new player projectile handler.
     * 
     * @param handlerEntity The entity handler reference.
     */
    public HandlerProjectileGame(HandlerEntityGame<E> handlerEntity)
    {
        super();
        this.handlerEntity = handlerEntity;
    }

    /**
     * Update projectiles.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        updateAdd();

        // Update projectiles
        for (final P projectile : list())
        {
            projectile.update(extrp);
            if (projectile.isDestroyed())
            {
                deleteID(projectile.id);
                remove(projectile);
            }
            for (final E entity : handlerEntity.list())
            {
                if (!projectile.isDestroyed() && projectile.getOwner() != entity && projectile.collide(entity))
                {
                    if (!projectile.canHitOnlyTarget() || projectile.canHitOnlyTarget()
                            && entity == projectile.getTarget())
                    {
                        projectile.onHit(entity, projectile.damages.getRandom());
                    }
                }
            }
        }
        updateRemove();
    }

    /**
     * Render projectiles.
     * 
     * @param g The graphics output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraGame camera)
    {
        for (final ProjectileGame<E, ?> projectile : list())
        {
            projectile.render(g, camera);
        }
    }

    @Override
    protected Integer getKey(P object)
    {
        return object.getId();
    }

    /**
     * Delete all projectiles with this id.
     * 
     * @param id The projectiles id to delete.
     */
    private void deleteID(int id)
    {
        if (id > -1)
        {
            for (final P projectile : list())
            {
                if (projectile.id == id)
                {
                    projectile.destroy();
                }
            }
        }
    }
}
