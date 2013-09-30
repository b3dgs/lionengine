package com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile;

import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.entity.HandlerEntityGame;
import com.b3dgs.lionengine.game.projectile.HandlerProjectileGame;

/**
 * Handler projectile.
 */
public final class HandlerProjectile
        extends HandlerProjectileGame<EntityGame, Projectile>
{
    /**
     * @see HandlerProjectileGame#HandlerProjectileGame(CameraGame, HandlerEntityGame)
     */
    public HandlerProjectile(CameraGame camera, HandlerEntityGame<EntityGame> handlerEntity)
    {
        super(camera, handlerEntity);
    }
}
