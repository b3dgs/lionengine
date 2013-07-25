package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeEntity;

/**
 * TownHall building implementation. This building allows to create new peon and store resources.
 */
final class TownHall
        extends BuildingProducer
        implements Warehouse
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    TownHall(Context context)
    {
        super(TypeEntity.townhall_orc, context);
    }
}
