package com.b3dgs.lionengine.example.d_rts.e_skills.weapon;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeWeapon;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.Entity;
import com.b3dgs.lionengine.game.rts.ability.attacker.AttackerUsedServices;

/**
 * Axe weapon implementation.
 */
final class Axe
        extends Weapon
{
    /**
     * Constructor.
     * 
     * @param user The user reference.
     * @param context The context reference.
     */
    Axe(AttackerUsedServices<Entity> user, Context context)
    {
        super(TypeWeapon.axe, user, context);
    }

    @Override
    public void notifyAttackEnded(int damages, Entity target)
    {
        target.life.decrease(damages);
    }
}
