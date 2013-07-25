package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeEntity;

/**
 * Farm building implementation.
 */
final class Farm
        extends BuildingProducer
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Farm(Context context)
    {
        super(TypeEntity.farm_orc, context);
    }
}
