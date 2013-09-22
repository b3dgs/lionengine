package com.b3dgs.lionengine.example.d_rts.e_skills.entity;

import com.b3dgs.lionengine.example.d_rts.e_skills.Context;
import com.b3dgs.lionengine.example.d_rts.e_skills.EntityType;

/**
 * Gold mine building implementation. This building allows to extract gold with a worker.
 */
final class GoldMine
        extends Building
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    GoldMine(Context context)
    {
        super(EntityType.GOLD_MINE, context);
    }
}
