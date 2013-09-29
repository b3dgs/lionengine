package com.b3dgs.lionengine.example.e_shmup.b_shipweapon.projectile;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.projectile.ProjectileGame;

/**
 * Projectile base implementation.
 */
public abstract class Projectile
        extends ProjectileGame<EntityGame, EntityGame>
{
    /**
     * @see ProjectileGame#ProjectileGame(SetupSurfaceGame, int, int)
     */
    Projectile(SetupSurfaceGame setup, int id, int frame)
    {
        super(setup, id, frame);
    }

    /*
     * ProjectileGame
     */

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        super.render(g, camera);
        if (!camera.isVisible(this))
        {
            destroy();
        }
    }

    @Override
    public void onHit(EntityGame entity, int damages)
    {
        // Nothing to do
    }

    @Override
    protected void updateMovement(double extrp, double vecX, double vecY)
    {
        moveLocation(extrp, vecX, vecY);
    }
}
