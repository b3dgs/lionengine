package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.human;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.UnitAttacker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeWeapon;

/**
 * Spearman implementation.
 */
final class Archer
        extends UnitAttacker
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Archer(Context context)
    {
        super(TypeEntity.archer, context);
        addWeapon(context, TypeWeapon.bow, 0);
        addSkill(context, 0, TypeSkill.move_human, 0);
        addSkill(context, 0, TypeSkill.stop_human, 1);
        addSkill(context, 0, TypeSkill.attack_bow, 2);
    }
}
