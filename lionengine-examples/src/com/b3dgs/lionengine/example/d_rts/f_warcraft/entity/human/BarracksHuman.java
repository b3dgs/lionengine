package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.human;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.BuildingProducer;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Barracks building implementation. This building allows to create new grunt and spearman.
 */
final class BarracksHuman
        extends BuildingProducer
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    BarracksHuman(Context context)
    {
        super(TypeEntity.barracks_human, context);
        addSkill(context, 0, TypeSkill.produce_footman, 0);
        addSkill(context, 0, TypeSkill.produce_archer, 1);
    }
}
