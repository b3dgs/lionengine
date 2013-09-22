package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.EntityType;
import com.b3dgs.lionengine.example.d_rts.e_skills.SkillType;
import com.b3dgs.lionengine.example.d_rts.e_skills.WeaponType;

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
        super(EntityType.GRUNT, context);
        addWeapon(context.factoryWeapon.createWeapon(WeaponType.AXE, this), 0);
        addSkill(context.factoryEntity, 0, SkillType.MOVE_ORC, 0);
        addSkill(context.factoryEntity, 0, SkillType.STOP_ORC, 1);
        addSkill(context.factoryEntity, 0, SkillType.ATTACK_AXE, 2);
    }
}
