package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.human;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Building;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;

/**
 * Farm building implementation. This building allows to increase the population capacity.
 */
final class FarmHuman
        extends Building
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    FarmHuman(Context context)
    {
        super(TypeEntity.farm_human, context);
    }
}
