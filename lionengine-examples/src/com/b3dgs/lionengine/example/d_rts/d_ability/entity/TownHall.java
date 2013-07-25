package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeEntity;

/**
 * TownHall building implementation. This building allows to create new peon and store resources.
 */
public final class TownHall
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
