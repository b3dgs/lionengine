package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeSkill;

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
        super(TypeEntity.peon, context);
        addSkill(context.factoryEntity, 0, TypeSkill.move_orc, 0);
        addSkill(context.factoryEntity, 0, TypeSkill.stop_orc, 1);
        addSkill(context.factoryEntity, 0, TypeSkill.building_standard_orc, 2);
        addSkill(context.factoryEntity, 1, TypeSkill.build_barracks_orc, 0);
        addSkill(context.factoryEntity, 1, TypeSkill.cancel_orc, 1);
    }
}
