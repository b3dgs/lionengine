package com.b3dgs.lionengine.example.d_rts.f_warcraft.weapon;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Attacker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeWeapon;

/**
 * Sword weapon implementation.
 */
final class Sword
        extends Weapon
{
    /**
     * Constructor.
     * 
     * @param user The user reference.
     * @param context The context reference.
     */
    Sword(Attacker user, Context context)
    {
        super(TypeWeapon.sword, user, context);
    }

    /*
     * Weapon
     */

    @Override
    public void notifyAttackEnded(int damages, Entity target)
    {
        target.decreaseLife(damages, user);
    }
}
