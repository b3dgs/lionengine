package com.b3dgs.lionengine.example.d_rts.f_warcraft.projectile;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.entity.SetupEntityGame;

/**
 * Arrow projectile implementation.
 */
final class Arrow
        extends Projectile
{
    /**
     * Constructor.
     * 
     * @param setup The entity setup.
     * @param id The projectile id (when a projectile is destroyed, all projectiles with this id are also destroyed).
     * @param frame The projectile tile number (from surface).
     */
    Arrow(SetupEntityGame setup, int id, int frame)
    {
        super(setup, id, frame);
        setCollision(new CollisionData(0, 1, 0, 1));
    }

    /*
     * Projectile
     */

    @Override
    public void onHit(Entity entity, int damages)
    {
        entity.decreaseLife(damages, getOwner().getAttacker());
        destroy(); // Destroy projectile on hit
    }
}
