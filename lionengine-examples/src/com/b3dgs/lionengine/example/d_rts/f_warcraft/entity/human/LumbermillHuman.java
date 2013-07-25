package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.human;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Building;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;

/**
 * Lumber mill building implementation. This building allows to enable archer production and bow upgrades.
 */
final class LumbermillHuman
        extends Building
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    LumbermillHuman(Context context)
    {
        super(TypeEntity.lumbermill_human, context);
    }
}
