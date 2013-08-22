package com.b3dgs.lionengine.example.d_rts.f_warcraft.projectile;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.game.SetupEntityGame;

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
    Spear(SetupEntityGame setup, int id, int frame)
    {
        super(setup, id, frame);
        setCollision(0, 0.2, 0, 0.2);
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
