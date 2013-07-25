package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeEntity;

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
    }
}
