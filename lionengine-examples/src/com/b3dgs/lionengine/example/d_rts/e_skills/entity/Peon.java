package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.EntityType;
import com.b3dgs.lionengine.example.d_rts.e_skills.SkillType;

/**
 * Peon implementation.
 */
final class Peon
        extends UnitWorker
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Peon(Context context)
    {
        super(EntityType.PEON, context);
        addSkill(context.factoryEntity, 0, SkillType.MOVE_ORC, 0);
        addSkill(context.factoryEntity, 0, SkillType.STOP_ORC, 1);
        addSkill(context.factoryEntity, 0, SkillType.BUILDING_STANDARD_ORC, 2);
        addSkill(context.factoryEntity, 1, SkillType.BUILD_BARRACKS_ORC, 0);
        addSkill(context.factoryEntity, 1, SkillType.CANCEL_ORC, 1);
    }
}
