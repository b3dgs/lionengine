package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.orc;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.UnitAttacker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeWeapon;

/**
 * Spearman implementation.
 */
final class Spearman
        extends UnitAttacker
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Spearman(Context context)
    {
        super(TypeEntity.spearman, context);
        addWeapon(context, TypeWeapon.spear, 0);
        addSkill(context, 0, TypeSkill.move_orc, 0);
        addSkill(context, 0, TypeSkill.stop_orc, 1);
        addSkill(context, 0, TypeSkill.attack_spear, 2);
    }
}
