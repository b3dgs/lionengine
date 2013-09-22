package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.EntityType;
import com.b3dgs.lionengine.example.d_rts.e_skills.SkillType;

/**
 * Barracks building implementation. This building allows to create new grunt and spearman.
 */
final class Barracks
        extends BuildingProducer
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Barracks(Context context)
    {
        super(EntityType.BARRACKS_ORC, context);
        addSkill(context.factoryEntity, 0, SkillType.PRODUCE_GRUNT, 0);
    }
}
