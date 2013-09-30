package com.b3dgs.lionengine.example.e_shmup.c_tyrian.projectile;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity.Entity;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.projectile.ProjectileGame;

/**
 * Projectile base implementation.
 */
public abstract class Projectile
        extends ProjectileGame<Entity, Entity>
{
    /**
     * @see ProjectileGame#ProjectileGame(SetupSurfaceGame, int, int)
     */
    Projectile(SetupSurfaceGame setup, int id, int frame)
    {
        super(setup, id, frame);
        setCollision(new CollisionData(10, -4, 4, 4, false));
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
    public void onHit(Entity entity, int damages)
    {
        entity.destroy();
    }

    @Override
    protected void updateMovement(double extrp, double vecX, double vecY)
    {
        moveLocation(extrp, vecX, vecY);
    }
}
