package com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.orc;

import com.b3dgs.lionengine.example.d_rts.f_warcraft.Context;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Building;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeEntity;

/**
 * Lumber mill building implementation. This building allows to enable spearman production and spear upgrades.
 */
final class LumbermillOrc
        extends Building
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    LumbermillOrc(Context context)
    {
        super(TypeEntity.lumbermill_orc, context);
    }
}
