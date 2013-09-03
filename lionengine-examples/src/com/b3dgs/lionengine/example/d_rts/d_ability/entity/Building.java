package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.TypeEntity;

/**
 * Abstract building entity implementation.
 */
abstract class Building
        extends Entity
{
    /**
     * Constructor.
     * 
     * @param id The entity type enum.
     * @param context The context reference.
     */
    protected Building(TypeEntity id, Context context)
    {
        super(id, context);
        setLayer(0);
    }
}
