package com.b3dgs.lionengine.example.d_rts.d_ability.entity;

import com.b3dgs.lionengine.example.d_rts.d_ability.Context;
import com.b3dgs.lionengine.example.d_rts.d_ability.EntityType;

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
    protected Building(EntityType id, Context context)
    {
        super(id, context);
        setLayer(0);
    }
}
