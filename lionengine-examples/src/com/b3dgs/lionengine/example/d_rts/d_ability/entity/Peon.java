package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeEntity;

/**
 * Peon implementation.
 */
final class Peon
        extends UnitWorker
{
    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    Peon(Context context)
    {
        super(TypeEntity.peon, context);
    }
}
