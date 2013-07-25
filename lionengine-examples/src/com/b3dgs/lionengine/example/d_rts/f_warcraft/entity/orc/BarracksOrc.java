package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.orc;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.BuildingProducer;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * Barracks building implementation. This building allows to create new grunt and spearman.
 */
final class BarracksOrc
        extends BuildingProducer
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    BarracksOrc(Context context)
    {
        super(TypeEntity.barracks_orc, context);
        addSkill(context, 0, TypeSkill.produce_grunt, 0);
        addSkill(context, 0, TypeSkill.produce_spearman, 1);
    }
}
