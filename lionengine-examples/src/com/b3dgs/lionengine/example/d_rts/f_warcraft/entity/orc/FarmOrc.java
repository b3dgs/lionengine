package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.orc;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Building;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;

/**
 * Farm building implementation. This building allows to increase the population capacity.
 */
final class FarmOrc
        extends Building
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    FarmOrc(Context context)
    {
        super(TypeEntity.farm_orc, context);
    }
}
