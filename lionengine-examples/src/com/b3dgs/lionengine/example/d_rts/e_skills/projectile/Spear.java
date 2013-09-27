package com.b3dgs.lionengine.example.d_rts.e_skills.projectile;

import com.b3dgs.lionengine.example.d_rts.e_skills.entity.Entity;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.SetupSurfaceGame;

/**
 * Spear projectile implementation.
 */
final class Spear
        extends Projectile
{
    /**
     * Constructor.
     * 
     * @param setup The entity setup.
     * @param id The projectile id (when a projectile is destroyed, all projectiles with this id are also destroyed).
     * @param frame The projectile tile number (from surface).
     */
    Spear(SetupSurfaceGame setup, int id, int frame)
    {
        super(setup, id, frame);
        setCollision(new CollisionData(0, 1, 0, 1, false));
    }

    /*
     * Projectile
     */

    @Override
    public void onHit(Entity entity, int damages)
    {
        entity.life.decrease(damages);
        destroy(); // Destroy projectile on hit
    }
}
