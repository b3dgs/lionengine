package com.b3dgs.lionengine.example.e_shmup.c_tyrian;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.Entity;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.HandlerProjectile;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile.Projectile;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;

/**
 * Handler entity.
 */
public final class HandlerEntity
        extends HandlerEntityGame<Entity>
{
    /**
     * Constructor.
     */
    public HandlerEntity()
    {
        super();
    }

    /**
     * Update the entities.
     * 
     * @param extrp The extrapolation value.
     * @param handlerProjectile The projectile handler.
     */
    public void update(double extrp, HandlerProjectile handlerProjectile)
    {
        updateAdd();
        for (Entity entity : list())
        {
            entity.update(extrp);
            for (Projectile projectile : handlerProjectile.list())
            {
                if (entity.collide(projectile))
                {
                    entity.destroy();
                    remove(entity);
                }
            }
        }
        updateRemove();
    }

    /**
     * Render the entities.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraGame camera)
    {
        for (Entity entity : list())
        {
            entity.render(g, camera);
        }
    }
}
