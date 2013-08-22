package com.b3dgs.lionengine.example.d_rts.d_ability.projectile;

import com.b3dgs.lionengine.example.d_rts.d_ability.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.d_ability.weapon.Weapon;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.projectile.ProjectileGame;

/**
 * Projectile implementation base.
 */
public abstract class Projectile
        extends ProjectileGame<Entity, Weapon>
{
    /**
     * Constructor.
     * 
     * @param setup The entity setup.
     * @param id The projectile id (when a projectile is destroyed, all projectiles with this id are also destroyed).
     * @param frame The projectile tile number (from surface).
     */
    protected Projectile(SetupEntityGame setup, int id, int frame)
    {
        super(setup, id, frame);
    }

    /*
     * ProjectileGame
     */

    @Override
    protected void updateMovement(double vecX, double vecY, double extrp)
    {
        // Apply a linear movement to the projectile with its vector
        moveLocation(extrp, vecX, vecY);
    }
}
