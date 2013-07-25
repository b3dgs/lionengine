package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeSkill;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeWeapon;

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
        addWeapon(context.factoryWeapon.createWeapon(TypeWeapon.axe, this), 0);
        addSkill(context.factoryEntity, 0, TypeSkill.move_orc, 0);
        addSkill(context.factoryEntity, 0, TypeSkill.stop_orc, 1);
        addSkill(context.factoryEntity, 0, TypeSkill.attack_axe, 2);
    }
}
