package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.orc;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.UnitAttacker;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeWeapon;

/**
 * Grunt implementation.
 */
final class Grunt
        extends UnitAttacker
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Grunt(Context context)
    {
        super(TypeEntity.grunt, context);
        addWeapon(context, TypeWeapon.axe, 0);
        addSkill(context, 0, TypeSkill.move_orc, 0);
        addSkill(context, 0, TypeSkill.stop_orc, 1);
        addSkill(context, 0, TypeSkill.attack_axe, 2);
    }
}
