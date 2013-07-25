package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.human;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.UnitAttacker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeWeapon;

/**
 * Grunt implementation.
 */
final class Footman
        extends UnitAttacker
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Footman(Context context)
    {
        super(TypeEntity.footman, context);
        addWeapon(context, TypeWeapon.sword, 0);
        addSkill(context, 0, TypeSkill.move_human, 0);
        addSkill(context, 0, TypeSkill.stop_human, 1);
        addSkill(context, 0, TypeSkill.attack_sword, 2);
    }
}
