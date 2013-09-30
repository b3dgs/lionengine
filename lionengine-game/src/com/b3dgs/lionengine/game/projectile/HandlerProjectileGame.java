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
    /** Camera reference. */
    private final CameraGame camera;
    /** The entity handler reference. */
    private final HandlerEntityGame<E> handlerEntity;

    /**
     * Create a new player projectile handler.
     * 
     * @param camera The camera reference.
     * @param handlerEntity The entity handler reference.
     */
    public HandlerProjectileGame(CameraGame camera, HandlerEntityGame<E> handlerEntity)
    {
        super();
        this.camera = camera;
        this.handlerEntity = handlerEntity;
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

    /*
     * HandlerEntityGame
     */

    @Override
    protected void update(double extrp, P projectile)
    {
        projectile.update(extrp);
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
        if (projectile.isDestroyed())
        {
            deleteID(projectile.id);
        }
        super.update(extrp, projectile);
    }
    
    @Override
    protected void render(Graphic g, P projectile)
    {
        if (camera.isVisible(projectile))
        {
            projectile.render(g, camera);
        }
    }
}
