package com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile;

import com.b3dgs.lionengine.example.e_shmup.c_tyrian.HandlerEntity;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.Entity;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.projectile.HandlerProjectileGame;

/**
 * Handler projectile.
 */
public final class HandlerProjectile
        extends HandlerProjectileGame<Entity, Projectile>
{
    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     * @param handlerEntity The handler entity reference.
     */
    public HandlerProjectile(CameraGame camera, HandlerEntity handlerEntity)
    {
        super(camera, handlerEntity);
    }
}
