package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeSkill;

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
        super(TypeEntity.barracks_orc, context);
        addSkill(context.factoryEntity, 0, TypeSkill.produce_grunt, 0);
    }
}
