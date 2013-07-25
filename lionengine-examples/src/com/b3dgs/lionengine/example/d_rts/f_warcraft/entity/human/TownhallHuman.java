package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.human;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.BuildingProducer;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Warehouse;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;

/**
 * TownHall building implementation. This building allows to create new peon and store resources.
 */
final class TownhallHuman
        extends BuildingProducer
        implements Warehouse
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    TownhallHuman(Context context)
    {
        super(TypeEntity.townhall_human, context);
        addSkill(context, 0, TypeSkill.produce_peasant, 0);
    }
}
