package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.orc;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.BuildingProducer;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Warehouse;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * TownHall building implementation. This building allows to create new peon and store resources.
 */
final class TownhallOrc
        extends BuildingProducer
        implements Warehouse
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    TownhallOrc(Context context)
    {
        super(TypeEntity.townhall_orc, context);
        addSkill(context, 0, TypeSkill.produce_peon, 0);
    }
}
